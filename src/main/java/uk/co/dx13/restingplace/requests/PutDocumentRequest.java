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
import java.util.ArrayList;
import java.util.List;

/**
 * Upload a document to Cloudant.
 */
public class PutDocumentRequest extends DatabaseRequest {

    private String encodedDocId;
    private String documentBody;
    private String revisionId;
    private int writeQuorum = -1;

    public void setDocId(String value) {
        try {
            this.encodedDocId = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            /* UTF-8 should always be supported, so swallow exception */
            e.printStackTrace();
        }
    }

    public void setDocumentBody(String value) {
        this.documentBody = value;
    }

    public void setRevisionId(String value) {
        this.revisionId = value;
    }

    public void setWriteQuorum(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Write quorum cannot be set less than zero");
        }
        this.writeQuorum = value;
    }

    public String httpPath() {
        return this.encodedDatabaseName + "/" + this.encodedDocId;
    }

    public String httpMethod() {
        return "PUT";
    }

    public List<Param> httpQueryParameters() {
        List<Param> queryParameters = new ArrayList<Param>();
        queryParameters.add(new Param("rev", this.revisionId));
        if (this.writeQuorum > -1) {
            queryParameters.add(new Param("w", this.writeQuorum));
        }
        return queryParameters;
    };

    public String httpRequestBody() { return this.documentBody; }
}
