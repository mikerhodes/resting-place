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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import uk.co.dx13.restingplace.Deserializer;
import uk.co.dx13.restingplace.exceptions.CouchDbException;
import uk.co.dx13.restingplace.exceptions.DocumentConflictException;
import uk.co.dx13.restingplace.exceptions.NoDocumentException;
import uk.co.dx13.restingplace.exceptions.PreconditionFailedException;
import uk.co.dx13.restingplace.http.HttpRequestTemplate;
import uk.co.dx13.restingplace.requests.response.ResponseMappingDelegate;
import uk.co.dx13.restingplace.requests.response.ResponseStreamDelegate;
import uk.co.dx13.restingplace.requests.response.ResponseStringDelegate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

/**
 * Generic Cloudant database request.
 */
public abstract class CloudantRequest implements HttpRequestTemplate {

    private URI cloudantRootUrl;
    private ResponseStringDelegate stringDelegate;
    private ResponseStreamDelegate streamDelegate;
    private ResponseMappingDelegate mappingDelegate;
    private final Gson gson;

    private final int BUFFER_SIZE = 1024;

    protected CloudantRequest() {
        this.gson = new GsonBuilder().create();
    }

    public URI httpRootUrl() {
        return cloudantRootUrl;
    }

    public void setCloudantRootUrl(URI value) {
        this.cloudantRootUrl = value;
    }

    public void receiveStreamResponse(int statusCode,
                                      Map<String, String> headers,
                                      InputStream body) {

        if (statusCode / 100 == 2) { // success [200,299]

            if (this.streamDelegate != null) {
                this.streamDelegate.receiveStreamResponse(this, statusCode, headers, body);
            }

            if (this.stringDelegate != null) {
                this.stringDelegate.receiveStringResponse(
                        this, statusCode, headers, slurp(body, BUFFER_SIZE));
            }

            if (this.mappingDelegate != null) {
                // TODO Can we be clever and set the type of `o` correctly to avoid the unchecked warning?
                String json = slurp(body, BUFFER_SIZE);
                final Object o = new Deserializer().deserialize(json, this.mappingDelegate.getClazz());
                //noinspection unchecked
                this.mappingDelegate.receiveObjectResponse(this, statusCode, headers, o);
            }

        } else {
            CouchDbException ex;
            switch (statusCode) {
                case HttpURLConnection.HTTP_NOT_FOUND: //404
                    ex = new NoDocumentException("Not Found");
                    break;
                case HttpURLConnection.HTTP_CONFLICT: //409
                    ex = new DocumentConflictException("Conflict");
                    break;
                case HttpURLConnection.HTTP_PRECON_FAILED: //412
                    ex = new PreconditionFailedException("Precondition Failed");
                    break;
                default:
                    ex = new CouchDbException("Error", statusCode);
            }

            //if there is an error stream try to deserialize into the typed exception
            if (body != null) {
                Class<? extends CouchDbException> exceptionClass = ex.getClass();
                try {
                    ex = gson.fromJson(new InputStreamReader(body),
                            exceptionClass);
                } catch (JsonParseException e) {
                    //suppress and just throw ex momentarily
                }
            }
            //set the status code for the cases where we may not have already
            ex.setStatusCode(statusCode);
            throw ex;
        }
    }

    public static String slurp(final InputStream is, final int bufferSize) {
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

    public void setDelegate(ResponseMappingDelegate value) {
        if (this.streamDelegate != null || this.stringDelegate != null) {
            throw new IllegalStateException("Cannot set more than one response delegate");
        }
        this.mappingDelegate = value;
    }

    public void setDelegate(ResponseStringDelegate value) {
        if (this.streamDelegate != null || this.mappingDelegate != null) {
            throw new IllegalStateException("Cannot set more than one response delegate");
        }
        this.stringDelegate = value;
    }

    public void setDelegate(ResponseStreamDelegate value) {
        if (this.stringDelegate != null || this.mappingDelegate != null) {
            throw new IllegalStateException("Cannot set more than one response delegate");
        }
        this.streamDelegate = value;
    }
}
