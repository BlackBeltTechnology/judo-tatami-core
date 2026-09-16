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

import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Interface for loading and saving transformation traces in various formats.
 *
 * <p>Implementations handle specific formats such as ETL XMI or Zeta JSON.</p>
 *
 * @see EtlTraceLoader
 * @see ZetaTraceLoader
 */
public interface TraceLoader {

    /**
     * Loads trace entries from an input stream.
     *
     * @param input the input stream containing trace data
     * @param resourcesToResolve ResourceSets used to resolve element references
     * @return list of loaded trace entries
     * @throws TraceLoadException if loading fails (malformed data, unresolved references)
     */
    List<TraceEntry> loadTrace(InputStream input, List<ResourceSet> resourcesToResolve)
            throws TraceLoadException;

    /**
     * Saves trace entries to an output stream.
     *
     * @param entries the trace entries to save
     * @param output the output stream to write to
     * @throws TraceSaveException if saving fails
     */
    void saveTrace(List<TraceEntry> entries, OutputStream output)
            throws TraceSaveException;
}
