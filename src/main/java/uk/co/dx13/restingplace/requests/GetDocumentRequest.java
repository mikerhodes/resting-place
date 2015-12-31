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

import uk.co.dx13.restingplace.http.Param;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Get a document from Cloudant.
 */
public class GetDocumentRequest extends DatabaseRequest {

    private String encodedDocId;

    public void setDocId(String value) {
        try {
            this.encodedDocId = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            /* UTF-8 should always be supported, so swallow exception */
            e.printStackTrace();
        }
    }

    public String httpPath() {
        return this.encodedDatabaseName + "/" + this.encodedDocId;
    }

    public String httpMethod() {
        return "GET";
    }

    public List<Param> httpQueryParameters() { return null; };

    public String httpRequestBody() { return null; }
}
