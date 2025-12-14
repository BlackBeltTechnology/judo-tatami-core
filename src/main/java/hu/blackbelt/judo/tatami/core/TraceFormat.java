package hu.blackbelt.judo.tatami.core;

/*-
 * #%L
 * Judo :: Tatami :: Core
 * %%
 * Copyright (C) 2018 - 2024 BlackBelt Technology
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

/**
 * Supported trace file formats for transformation traces.
 */
public enum TraceFormat {

    /**
     * Legacy Epsilon ETL XMI format.
     * Uses pseudo EMF metamodel with sourceUri and targetUri attributes.
     */
    ETL_XMI(".xmi"),

    /**
     * Zeta JSON format.
     * Uses JSON with traceEntries array containing source/target element info.
     */
    ZETA_JSON(".json");

    private final String fileExtension;

    TraceFormat(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Returns the typical file extension for this format.
     *
     * @return file extension including the dot (e.g., ".json")
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Detects the trace format from a filename based on its extension.
     *
     * @param filename the filename to analyze
     * @return the detected format, defaults to ETL_XMI if unknown
     */
    public static TraceFormat detect(String filename) {
        if (filename == null) {
            return ETL_XMI;
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".json")) {
            return ZETA_JSON;
        }
        return ETL_XMI;
    }

    /**
     * Detects the trace format from a file path.
     *
     * @param path the file path to analyze
     * @return the detected format, defaults to ETL_XMI if unknown
     */
    public static TraceFormat detectFromPath(java.nio.file.Path path) {
        if (path == null) {
            return ETL_XMI;
        }
        return detect(path.getFileName().toString());
    }

    /**
     * Detects the trace format from a URI.
     *
     * @param uri the URI to analyze
     * @return the detected format, defaults to ETL_XMI if unknown
     */
    public static TraceFormat detectFromUri(org.eclipse.emf.common.util.URI uri) {
        if (uri == null) {
            return ETL_XMI;
        }
        return detect(uri.lastSegment());
    }
}
