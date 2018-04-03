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

import com.fasterxml.classmate.TypeResolver;
import com.fredboat.backend.quarterdeck.config.property.DocsConfig;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import fredboat.definitions.Language;
import fredboat.definitions.RepeatMode;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import static springfox.documentation.builders.PathSelectors.ant;

/**
 * Created by napster on 01.04.18.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private final TypeResolver typeResolver;
    private final DocsConfig docsConfig;

    public SwaggerConfiguration(TypeResolver typeResolver, DocsConfig docsConfig) {
        this.typeResolver = typeResolver;
        this.docsConfig = docsConfig;
    }

    @Bean
    public Docket v1Api() {
        return baseDocket()
                .groupName("v1")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.fredboat.backend.quarterdeck.rest.v1"))
                .paths(ant("/v1/guilds/**"))
                .build()
                .additionalModels(this.typeResolver.resolve(Language.class), this.typeResolver.resolve(RepeatMode.class))
                .securitySchemes(List.of(new BasicAuth("test")))
                .securityContexts(List.of(securityContext()))
                ;
    }

    // todo this seems not getting picked up, investigate. possible gson issue.
    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .displayOperationId(false)
                .defaultModelRendering(ModelRendering.MODEL)
                .docExpansion(DocExpansion.FULL)
                .build();
    }

    private Docket baseDocket() {
        Docket baseDocket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metadata())
                .produces(Set.of(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .consumes(Set.of(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .directModelSubstitute(DiscordSnowflake.class, String.class)
                .useDefaultResponseMessages(false);

        String host = this.docsConfig.getHost();
        if (!host.isEmpty()) {
            baseDocket.host(host);
            baseDocket.protocols(Set.of("https"));
        } else {
            //probably hosted on localhost, allow to use http for test queries
            baseDocket.protocols(Set.of("https", "http"));
        }
        String basePath = this.docsConfig.getBasePath();
        if (!basePath.isEmpty()) {
            baseDocket.pathMapping(basePath);
        }
        return baseDocket;
    }

    private static SecurityContext securityContext() {
        AuthorizationScope[] authScopes = new AuthorizationScope[1];
        authScopes[0] = new AuthorizationScopeBuilder()
                .scope("admin")
                .description("Full Access")
                .build();

        return SecurityContext.builder()
                .securityReferences(List.of(SecurityReference.builder()
                        .reference("test")
                        .scopes(authScopes)
                        .build()))
                .forPaths(PathSelectors.ant("**"))
                .build();
    }

    private static ApiInfo metadata() {
        try {
            return new ApiInfoBuilder()
                    .title("Quarterdeck - FredBoat Backend API")
                    .description(IOUtils.resourceToString("/docs/api_description.md", Charset.forName("UTF-8")))
                    .version("v1")
                    .contact(new Contact("The FredBoat Org", "https://github.com/FredBoat", ""))
                    .license("MIT")
                    .licenseUrl("https://github.com/FredBoat/Backend/blob/dev/LICENSE")
                    .termsOfServiceUrl("https://fredboat.com/docs/terms")
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
