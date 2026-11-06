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

import org.slf4j.Logger;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum LogLevel {
    ERROR, WARN, INFO, DEBUG, TRACE;

    private static final Map<LogLevel, Set<LogLevel>> logLevelSetMap = Map.of(
            ERROR, EnumSet.of(ERROR),
            WARN, EnumSet.of(ERROR, WARN),
            INFO, EnumSet.of(ERROR, WARN, INFO),
            DEBUG, EnumSet.of(ERROR, WARN, INFO, DEBUG),
            TRACE, EnumSet.of(ERROR, WARN, INFO, DEBUG, TRACE)
    );

    public static Set<LogLevel> getMatchingLogLevels(LogLevel logLevel) {
        return logLevelSetMap.get(logLevel);
    }

    public static LogLevel determinateLogLevel(Logger log) {
        if (log.isTraceEnabled()) {
            return TRACE;
        } else if (log.isDebugEnabled()) {
            return DEBUG;
        } else if (log.isInfoEnabled()) {
            return INFO;
        } else if (log.isWarnEnabled()) {
            return WARN;
        } else if (log.isErrorEnabled()) {
            return ERROR;
        }
        return INFO;
    }
}
