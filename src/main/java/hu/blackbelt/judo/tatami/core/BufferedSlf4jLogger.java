package hu.blackbelt.judo.tatami.core;

/*-
 * #%L
 * Judo :: Tatami :: Core
 * %%
 * Copyright (C) 2018 - 2023 BlackBelt Technology
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Slf4j
public class BufferedSlf4jLogger implements Logger, Closeable {

    Collection<LogEntry> logEntries;
    Logger logger;

    public static class LogEntry {
        LogLevel logLevel;
        String message;
        Throwable throwable;

        public LogEntry(LogLevel logLevel, String message, Throwable throwable) {
            this.logLevel = logLevel;
            this.message = message;
            this.throwable = throwable;
        }

        public void sendToLogger(Logger logger) {
            if (logLevel == LogLevel.ERROR) {
                logger.error(message, throwable);
            } else if (logLevel == LogLevel.WARN) {
                logger.warn(message, throwable);
            } else if (logLevel == LogLevel.INFO) {
                logger.info(message, throwable);
            } else if (logLevel == LogLevel.DEBUG) {
                logger.debug(message, throwable);
            } else if (logLevel == LogLevel.TRACE) {
                logger.trace(message, throwable);
            }
        }
    }

    private Set<LogLevel> currentLevels = Set.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO);

    public BufferedSlf4jLogger(Logger logger) {
        logEntries = new ArrayList<>();
        LogLevel logLevel = LogLevel.determinateLogLevel(logger);
        this.logger = logger;
        setLoglevels(logLevel);
    }

    public BufferedSlf4jLogger(LogLevel logLevel) {
        logEntries = new ArrayList<>();
        this.logger = log;
        setLoglevels(logLevel);
    }

    private void setLoglevels(LogLevel logLevel) {
        currentLevels = LogLevel.getMatchingLogLevels(logLevel);
    }

    @Override
    public String getName() {
        return "Buffered Slf4j Logger";
    }

    @Override
    public boolean isTraceEnabled() {
        return currentLevels.contains(LogLevel.TRACE);
    }

    @Override
    public void trace(String msg) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, msg, null));
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, String.format(format, arg), null));
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, String.format(format, arguments), null));
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, msg, t));
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.TRACE);
    }

    @Override
    public void trace(Marker marker, String msg) {
        trace(msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        trace(format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        trace(format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        trace(format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        trace(msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return currentLevels.contains(LogLevel.DEBUG);
    }

    @Override
    public void debug(String msg) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, msg, null));
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, String.format(format, arg), null));
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, String.format(format, arguments), null));
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, msg, t));
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.DEBUG);
    }

    @Override
    public void debug(Marker marker, String msg) {
        debug(msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        debug(format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        debug(format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        debug(format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        debug(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return currentLevels.contains(LogLevel.INFO);
    }

    @Override
    public void info(String msg) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, msg, null));
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, String.format(format, arg), null));
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, String.format(format, arguments), null));
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, msg, t));
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.INFO);
    }

    @Override
    public void info(Marker marker, String msg) {
        info(msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        info(format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        info(format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        info(format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        info(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return currentLevels.contains(LogLevel.WARN);
    }

    @Override
    public void warn(String msg) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, msg, null));
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, String.format(format, arg), null));
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, String.format(format, arguments), null));
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, msg, t));
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.WARN);
    }

    @Override
    public void warn(Marker marker, String msg) {
        warn(msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        warn(format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        warn(format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        warn(format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        warn(msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return currentLevels.contains(LogLevel.ERROR);
    }

    @Override
    public void error(String msg) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, msg, null));
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, String.format(format, arg), null));
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, String.format(format, arguments), null));
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, msg, t));
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.ERROR);
    }

    @Override
    public void error(Marker marker, String msg) {
        error(msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        error(format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        error(format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        error(format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        error(msg, t);
    }

    public synchronized void flush() {
        sendToLogger();
        logEntries.clear();
    }

    @Override
    public String toString() {
        return "BufferedSlf4jLogger{logger=" + logger.getName() + "}";
    }

    public void sendToLogger(final Logger logger) {
        logEntries.forEach(e -> e.sendToLogger(logger));
    }

    public void sendToLogger() {
        Logger loggerF = this.logger == null ? log : this.logger;
        logEntries.forEach(e -> e.sendToLogger(loggerF));
    }

    @Override
    public void close() throws IOException {
        this.flush();
    }
}
