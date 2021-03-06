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

package uk.co.dx13.restingplace.exceptions;

import java.io.IOException;

/**
 * Generic Couch exception.
 */
public class CouchDbException extends RuntimeException {
    private int statusCode = -1;

    public CouchDbException(String error, int statusCode) {
        super(error);
        this.statusCode = statusCode;
    }

    public CouchDbException(String error, IOException ioe) {
        super(error, ioe);
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
