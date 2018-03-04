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

package com.fredboat.quarterdeck.backend.config;

import fredboat.db.DatabaseManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import space.npstr.sqlsauce.DatabaseWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by napster on 16.02.18.
 *
 * Mirrors the fredboat tree of the application.yaml
 */
@ConfigurationProperties
@Configuration
@EnableConfigurationProperties
public class AppConfiguration {

    private final Security security = new Security();

    @Bean
    public Security getSecurity() {
        return security;
    }

    public static class Security {

        private List<Admin> admins = new ArrayList<>();

        public List<Admin> getAdmins() {
            return admins;
        }

        public void setAdmins(List<Admin> admins) {
            this.admins = admins;
        }

        public static class Admin {
            private String name = "";
            private String pass = "";

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPass() {
                return pass;
            }

            public void setPass(String pass) {
                this.pass = pass;
            }
        }
    }
}
