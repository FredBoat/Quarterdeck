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

import com.fredboat.backend.quarterdeck.config.property.DocsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by napster on 31.03.18.
 * <p>
 * We use this to serve the docs under /docs and /swagger-ui.html
 */
@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    private final DocsConfig docsConfig;

    public StaticResourceConfiguration(DocsConfig docsConfig) {
        this.docsConfig = docsConfig;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        String redirectPath = "/swagger-ui.html";
        String basePath = this.docsConfig.getBasePath();
        if (!basePath.isEmpty()) {
            redirectPath = basePath + redirectPath;
        }
        registry.addRedirectViewController("/", redirectPath);
    }
}
