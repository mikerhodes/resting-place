/**
 * Copyright (c) 2015 Mike Rhodes. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package uk.co.dx13.restingplace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * A few useful Stream utilities.
 */
public class StreamUtils {

    private static final int bufferSize = 1024;

    public static String toString(final InputStream is) {
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in;
        try {
            in = new InputStreamReader(is, "UTF-8");
            while (true) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
        }
        catch (UnsupportedEncodingException ex) {
        /* ... */
        }
        catch (IOException ex) {
        /* ... */
        }
        return out.toString();
    }

    public static byte[] toByteArray(final InputStream is) {
        final byte[] buffer = new byte[bufferSize];
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while (true) {
                int rsz = is.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                baos.write(buffer, 0, rsz);
            }
        }
        catch (UnsupportedEncodingException ex) {
        /* ... */
        }
        catch (IOException ex) {
        /* ... */
        }
        return baos.toByteArray();
    }

}
