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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by napster on 21.03.18.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static final String UNCAUGHT_EXCEPTION_MESSAGE = "OOPSIE WOOPSIE!! Uwu We made a fucky wucky!! A wittle " +
            "fucko boingo! The code monkeys at our headquarters are working VEWY HAWD to fix this!\n\nOr in other words: " +
            "Whatever you did, we don't have a comprehensive and secure error message for you yet.\nPlease lend us a " +
            "hand and tell us how exactly you ended up here on our issue tracker over at https://github.com/FredBoat/Backend" +
            "\nThanks!";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e, ServletWebRequest request) {
        log.error("Caught unhandled RuntimeException. Please add en ExceptionHandler for it that gives comprehensive "
                + "and secure feedback to the caller of the API about what went wrong.", e);

        String message = UNCAUGHT_EXCEPTION_MESSAGE
                + "\n\nYou are user: " + (request.getRemoteUser() == null ? "anonymous" : request.getRemoteUser())
                + "\nYour request was: " + request.getHttpMethod() + " " + request.getRequest().getRequestURI();

        return handleError(request, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> handleError(WebRequest request, String message, HttpStatus status) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        Object response;
        String contentType = request.getHeader("Content-Type");
        if (contentType != null && contentType.contains("application/json")) {
            headers.add("Content-Type", "application/json");
            response = new ErrorMessage(status.value(), message);
        } else {
            headers.add("Content-Type", "text/plain");
            response = "Status " + status.value() + " " + status.getReasonPhrase() + "\n" + message;
        }

        return new ResponseEntity<>(response, headers, status);
    }

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
