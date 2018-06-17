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

import com.fredboat.backend.quarterdeck.db.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.npstr.sqlsauce.DatabaseConnection;
import space.npstr.sqlsauce.DatabaseWrapper;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Created by napster on 23.02.18.
 * <p>
 * Provides database related beans. Also see {@link Database}
 */
@Configuration
public class DatabaseConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private final Database database;

    @Nullable
    private volatile DatabaseWrapper cacheWrapper;
    private final Object cacheWrapperInitLock = new Object();

    public DatabaseConfiguration(Database database) {
        this.database = database;
    }

    @Bean
    public DatabaseWrapper mainDbWrapper() {
        return new DatabaseWrapper(createMainDatabaseConnection());
    }

    private DatabaseConnection createMainDatabaseConnection() {
        final DatabaseConnection connection = createADatabaseConnection(this.database::getMainDbConn);
        if (connection == null) {
            throw new NullPointerException("The main database is not supposed to ever return a null connection. "
                    + "Did the method contract change?");
        }
        return connection;
    }

    //with this bean being nullable, it cannot be reliably created / cached by the spring application context - inject
    //this configuration class instead and call this method, which employs double checked locking.
    @Nullable
    public DatabaseWrapper getCacheDbWrapper() {
        DatabaseWrapper singleton = this.cacheWrapper;
        if (singleton == null) {
            synchronized (this.cacheWrapperInitLock) {
                singleton = this.cacheWrapper;
                if (singleton == null) {
                    DatabaseConnection cacheDbConn = createCacheDatabaseConnection();
                    this.cacheWrapper = singleton =
                            cacheDbConn == null
                                    ? null
                                    : new DatabaseWrapper(cacheDbConn);
                }
            }
        }
        return singleton;
    }

    @Nullable
    private DatabaseConnection createCacheDatabaseConnection() {
        return createADatabaseConnection(this.database::getCacheDbConn);
    }


    @CheckForNull //depending on what kind of method was passed as the supplier.
    private DatabaseConnection createADatabaseConnection(Supplier<DatabaseConnection> method) {
        //attempt to connect to the database a few times
        // this is relevant in a dockerized environment because after a reboot there is no guarantee that the db
        // container will be started before the fredboat one
        int dbConnectionAttempts = 0;

        do {
            try {
                return method.get();
            } catch (Exception e) {
                log.info("Could not connect to database. Retrying in a moment...", e);
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException ex) {
                    log.warn("Interrupted while creating a database connection. Returning...", ex);
                    Thread.currentThread().interrupt();
                }
            }
        } while (dbConnectionAttempts++ < 10 && !Thread.interrupted());

        String message = "Could not establish database connection. Is everything configured correctly? " +
                "If this is the first run on a slow machine the database may need more time to be initiated. Exiting...";
        log.error(message);
        // Exiting may lead to a restart of this container (depending on the user's configuration), giving the
        // application more attempts at connecting to the database, which may really just be having a very slow
        // initiation (arm machines, raspis etc).
        System.exit(1);
        throw new InvalidConfigurationException(message);
    }
}
