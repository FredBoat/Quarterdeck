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

package com.fredboat.backend.quarterdeck;

import com.fredboat.backend.quarterdeck.info.AppInfo;
import com.fredboat.backend.quarterdeck.info.GitRepoState;
import com.fredboat.backend.quarterdeck.rest.v1.EntityController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by napster on 16.02.18.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { //we handle these ourselves via the DatabaseManager
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        FlywayAutoConfiguration.class
})
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        //just post the info to the console and exit
        if (args.length > 0 &&
                (args[0].equalsIgnoreCase("-v")
                        || args[0].equalsIgnoreCase("--version")
                        || args[0].equalsIgnoreCase("-version"))) {
            System.out.println("Version flag detected. Printing version info, then exiting.");
            System.out.println(getVersionInfo());
            System.out.println("Version info printed, exiting.");
            return;
        }

        System.setProperty("spring.config.name", "quarterdeck");
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(
                event -> {
                    if (event instanceof ApplicationEnvironmentPreparedEvent) {
                        log.info(getVersionInfo());
                    }
                },
                event -> {
                    if (event instanceof ApplicationFailedEvent) {
                        ApplicationFailedEvent failed = (ApplicationFailedEvent) event;
                        log.error("Application failed", failed.getException());
                    }
                }
        );
        springApplication.run(args);
    }

    private static String getVersionInfo() {
        AppInfo appInfo = new AppInfo();
        GitRepoState gitRepoState = new GitRepoState();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss z")
                .withZone(ZoneId.of("Europe/Copenhagen"));

        String buildTime = dtf.format(Instant.ofEpochMilli(appInfo.getBuildTime()));
        String commitTime = dtf.format(Instant.ofEpochMilli(gitRepoState.getCommitTime() * 1000));

        return "\n\n\u001b[36m" +
                "   ___                   _               _           _    \n" +
                "  / _ \\ _   _  __ _ _ __| |_ ___ _ __ __| | ___  ___| | __\n" +
                " | | | | | | |/ _` | '__| __/ _ \\ '__/ _` |/ _ \\/ __| |/ /\n" +
                " | |_| | |_| | (_| | |  | ||  __/ | | (_| |  __/ (__|   < \n" +
                "  \\__\\_\\\\__,_|\\__,_|_|   \\__\\___|_|  \\__,_|\\___|\\___|_|\\_\\\n\n\u001B[0m"

                + "\n\tVersion:        " + appInfo.getVersion()
                + "\n\tBuild:          " + appInfo.getBuildNumber()
                + "\n\tBuild time:     " + buildTime
                + "\n\tBranch:         " + gitRepoState.getBranch()
                + "\n\tCommit:         " + gitRepoState.getCommitIdAbbrev()
                + "\n\tCommit time:    " + commitTime
                + "\n\tJVM:            " + Runtime.version()
                + "\n\tQuarterdeck API " + "v" + com.fredboat.backend.quarterdeck.rest.v0.EntityController.API_VERSION
                + " [deprecated], v" + EntityController.API_VERSION
                + "\n";
    }

}
