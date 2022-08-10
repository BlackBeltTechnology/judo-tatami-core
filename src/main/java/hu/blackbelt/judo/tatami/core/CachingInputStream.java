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
import java.io.IOException;
import java.io.InputStream;

public class CachingInputStream extends BufferedInputStream {
    public CachingInputStream(InputStream source) {
        super(new PostCloseProtection(source));
        super.mark(Integer.MAX_VALUE);
    }

    @Override
    public synchronized void close() throws IOException {
        if (!((PostCloseProtection) in).decoratedClosed) {
            in.close();
        }
        super.reset();
    }

    private static class PostCloseProtection extends InputStream {
        private volatile boolean decoratedClosed = false;
        private final InputStream source;

        public PostCloseProtection(InputStream source) {
            this.source = source;
        }

        @Override
        public int read() throws IOException {
            return decoratedClosed ? -1 : source.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return decoratedClosed ? -1 : source.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return decoratedClosed ? -1 : source.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return decoratedClosed ? 0 : source.skip(n);
        }

        @Override
        public int available() throws IOException {
            return source.available();
        }

        @Override
        public void close() throws IOException {
            decoratedClosed = true;
            source.close();
        }

        @Override
        public void mark(int readLimit) {
            source.mark(readLimit);
        }

        @Override
        public void reset() throws IOException {
            source.reset();
        }

        @Override
        public boolean markSupported() {
            return source.markSupported();
        }
    }
}
