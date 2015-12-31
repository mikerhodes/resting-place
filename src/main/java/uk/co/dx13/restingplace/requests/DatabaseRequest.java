/**
 * Copyright (c) 2015 Mike Rhodes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package uk.co.dx13.restingplace.requests;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Generic Cloudant database request.
 */
public abstract class DatabaseRequest extends CloudantRequest {

    protected String encodedDatabaseName;

    public void setDatabaseName(String value) {
        try {
            this.encodedDatabaseName = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            /* UTF-8 should always be supported, so swallow exception */
            e.printStackTrace();
        }
    }

}
