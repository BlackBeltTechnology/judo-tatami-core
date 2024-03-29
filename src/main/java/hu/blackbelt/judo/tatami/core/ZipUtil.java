package hu.blackbelt.judo.tatami.core;

/*-
 * #%L
 * Judo :: Tatami :: Core
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This utility compresses a list of files to standard ZIP format file. It is able to compresses all
 * sub files and sub directories, recursively.
 */
public class ZipUtil {
    /**
     * A constants for buffer size used to read/write data.
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Compresses a collection of files to a destination zip file.
     *
     * @param listFiles A collection of files and directories
     * @param destZipFile The path of the destination zip file
     * @throws FileNotFoundException if file not found
     * @throws IOException if IO exception occurs
     */
    public void compressFiles(List<File> listFiles, String destZipFile)
            throws IOException {

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile))) {

            for (File file : listFiles) {
                if (file.isDirectory()) {
                    addFolderToZip(file, file.getName(), zos);
                } else {
                    addFileToZip(file, zos);
                }
            }

            zos.flush();
        }
    }

    /**
     * Adds a directory to the current zip output stream.
     *
     * @param folder the directory to be added
     * @param parentFolder the path of parent directory
     * @param zos the current zip output stream
     * @throws FileNotFoundException if file not found
     * @throws IOException if IO exception occurs
     */
    private void addFolderToZip(File folder, String parentFolder, ZipOutputStream zos)
            throws FileNotFoundException, IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }

            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));

            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                byte[] bytesIn = new byte[BUFFER_SIZE];
                int read;
                while ((read = bis.read(bytesIn)) != -1) {
                    zos.write(bytesIn, 0, read);
                }
            }

            zos.closeEntry();

        }
    }

    /**
     * Adds a file to the current zip output stream.
     *
     * @param file the file to be added
     * @param zos the current zip output stream
     * @throws FileNotFoundException if file not found
     * @throws IOException if IO exception occurs
     */
    private static void addFileToZip(File file, ZipOutputStream zos) throws FileNotFoundException,
            IOException {
        zos.putNextEntry(new ZipEntry(file.getName()));

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read;
            while ((read = bis.read(bytesIn)) != -1) {
                zos.write(bytesIn, 0, read);
            }
        }

        zos.closeEntry();
    }
}
