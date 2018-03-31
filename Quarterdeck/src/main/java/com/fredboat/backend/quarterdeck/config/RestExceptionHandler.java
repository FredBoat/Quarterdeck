/*
 * MIT License
 *
 * Copyright (c) 2016-2018 The FredBoat Org https://github.com/FredBoat/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.fredboat.backend.quarterdeck.config;

import com.fredboat.backend.quarterdeck.parsing.LanguageParseException;
import com.fredboat.backend.quarterdeck.parsing.NumberParseException;
import com.fredboat.backend.quarterdeck.parsing.ParseCastException;
import com.fredboat.backend.quarterdeck.parsing.RepeatModeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Created by napster on 21.03.18.
 *
 * We try to enhance Spring's exception handling by returning an {@link ErrorMessage} object with a helpful message,
 * as well as debug information.
 *
 * This is also the place to add {@link ExceptionHandler}s for our own Exceptions
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);


    // ################################################################################
    // ##                        Enhancing Spring exceptions
    // ################################################################################


    //intercept the method to inject an error message in case it is missing
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        Object response = body;
        if (response == null) {
            response = buildUndocumentedErrorMessage(status, request);
        }
        return super.handleExceptionInternal(ex, response, headers, status, request);
    }

    //just like the super method, just without the unnecessary warn level logging and a better message
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatus status,
                                                                         WebRequest request) {
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }

        String message = ex.getMethod() + " is not supported by this endpoint. Check the headers for the supported methods.";
        return handleExceptionInternal(ex, buildErrorMessage(status, message, request), headers, status, request);
    }

    //show a better message for borked path variables
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status,
                                                        WebRequest request) {
        if (ex instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException e = (MethodArgumentTypeMismatchException) ex;
            String message = String.format("The parameter \"%s\" needs to be of type \"%s\", which your provided value \"%s\" is not.",
                    e.getName(), e.getRequiredType(), e.getValue());
            return super.handleExceptionInternal(ex, buildErrorMessage(status, message, request), headers, status, request);
        }
        return super.handleTypeMismatch(ex, headers, status, request);
    }


    // ################################################################################
    // ##                        Handling our own Exceptions
    // ################################################################################


    @ExceptionHandler({ParseCastException.class, LanguageParseException.class, NumberParseException.class,
            RepeatModeParseException.class})
    public ResponseEntity<Object> handleParseException(Exception e, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorMessage message = buildErrorMessage(status, e.getMessage(), request);
        return handleExceptionInternal(e, message, new HttpHeaders(), status, request);
    }

    //actually log worthy exceptions. catch-all for all internal, non-spring exceptions happening.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalException(Exception e, WebRequest request) {
        log.error("Uncaught exception bubbled up", e);

        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    // ################################################################################
    // ##                        ErrorMessage defaults and creation
    // ################################################################################


    private static final String UNDOCUMENTED_EXCEPTION_MESSAGE = "OOPSIE WOOPSIE!! Uwu We made a fucky wucky!! A " +
            "wittle fucko boingo! The code monkeys at our headquarters are working VEWY HAWD to fix this!" +
            "\n\nOr in other words: Whatever you did, we don't have a comprehensive and secure error message for you " +
            "yet. You should probably give the status code of this response a good look, as well as the headers, and " +
            "maybe try again later." +
            "\n\nIf this isn't going away, and you are not a machine but an actual human, please lend us a hand and " +
            "tell us what request you are trying to do on our issue tracker over at https://github.com/FredBoat/Backend" +
            "\nThanks!";


    private ErrorMessage buildUndocumentedErrorMessage(HttpStatus status, WebRequest request) {
        return buildErrorMessage(status, UNDOCUMENTED_EXCEPTION_MESSAGE, request);
    }

    /**
     * Create an error message showing the status code and the error message along with a bit of debug information.
     */
    private ErrorMessage buildErrorMessage(HttpStatus status, String message, WebRequest request) {
        String msg = message;
        msg += "\n\nYou are user: " + (request.getRemoteUser() == null ? "anonymous" : request.getRemoteUser());
        if (request instanceof ServletWebRequest) {
            ServletWebRequest req = (ServletWebRequest) request;
            msg += "\nYour request was: " + req.getHttpMethod() + " " + req.getRequest().getRequestURI();
        } else {
            msg += "\nYour request was: " + request.getDescription(true);
        }
        return new ErrorMessage(status.value(), msg);

    }

    /**
     * An error message showing a status code and a message to the requester.
     */
    @SuppressWarnings("unused")
    private static class ErrorMessage {

        private int status;
        private String developerMessage;

        public ErrorMessage(int status, String developerMessage) {
            this.status = status;
            this.developerMessage = developerMessage;
        }

        public String getDeveloperMessage() {
            return this.developerMessage;
        }

        public void setDeveloperMessage(String developerMessage) {
            this.developerMessage = developerMessage;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
