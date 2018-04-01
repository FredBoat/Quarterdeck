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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by napster on 18.02.18.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final DocsConfig docsConfig;

    protected SecurityConfiguration(DocsConfig docsConfig) {
        this.docsConfig = docsConfig;
    }

    private String[] getAuthWhitelist() {
        List<String> antMatchers = new ArrayList<>();
        // metrics
        antMatchers.add("/metrics"); // these should rather be handled by nginx whitelisting (or other proxy choices)

        if (this.docsConfig.isOpen()) {
            // spring rest docs
            antMatchers.add("/docs/**");

            // swagger ui
            antMatchers.addAll(Arrays.asList(
                    "/swagger-resources/**",
                    "/", // does a redirect
                    "/swagger-ui.html",
                    "/v2/api-docs",
                    "/webjars/**"));
        }

        return antMatchers.toArray(new String[0]);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (this.docsConfig.isOpen()) {
            http.cors().disable();
        }
        http.csrf().disable().authorizeRequests() // todo enable csrf when doing proper authorization for end users
                .antMatchers(getAuthWhitelist()).permitAll()
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, AppConfiguration.Security security) throws Exception {
        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryAuth = auth.inMemoryAuthentication();
        for (AppConfiguration.Security.Admin admin : security.getAdmins()) {
            if (admin.getPass().isEmpty()) {
                throw new RuntimeException("Admin " + admin.getName() + " configured with empty pass.");
            }
            //we are treating the pass as tokens right now so using the noop encoder is fine
            inMemoryAuth.withUser(admin.getName()).password("{noop}" + admin.getPass()).roles("ADMIN", "USER");
        }
    }
}
