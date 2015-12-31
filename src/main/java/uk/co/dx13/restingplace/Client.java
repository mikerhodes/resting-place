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

import uk.co.dx13.restingplace.http.HttpRequestExecutor;
import uk.co.dx13.restingplace.requests.CloudantRequest;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A database client. Holds connection settings and allows executing requests using those settings.
 */
public class Client {

    private final URI instanceURI;

    public Client(URI instanceURI) {
        this.instanceURI = instanceURI;
    }

    private URI getInstanceURI() {
        return this.instanceURI;
    }

    /**
     * Returns an object that can be used to make requests to a database.
     * @param name the name of the database
     * @return A DatabaseNew object which can be used to make requests to the database.
     */
    public Database database(String name) {
        return new Database(this, name);
    }

    protected void executeRequest(CloudantRequest request) {
        request.setCloudantRootUrl(getInstanceURI());
        try {
            new HttpRequestExecutor(request).executeSync();
        } catch (URISyntaxException e) {
            /* We should have guaranteed the URL is correct, so convert to a runtime exception. */
            throw new RuntimeException("Invalid URI constructed for request");
        }
    }

    public void executeAsyncRequest(CloudantRequest request) {
        request.setCloudantRootUrl(getInstanceURI());
        new HttpRequestExecutor(request).executeAsync();
    }

}
