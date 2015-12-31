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

import uk.co.dx13.restingplace.requests.DatabaseRequest;

/**
 * Simple Database client. Create using a Client object.
 */
public class Database {

    private final String databaseName;

    private final Client client;

    private final Deserializer deserializer;
    private final Serializer serializer;

    public Database(Client client, String databaseName) {
        this.serializer = new Serializer();
        this.deserializer = new Deserializer();
        this.databaseName = databaseName;
        this.client = client;
    }

    public void executeRequest(DatabaseRequest request) {
        request.setDatabaseName(this.databaseName);
        client.executeRequest(request);
    }

    public void executeAsyncRequest(DatabaseRequest request) {
        request.setDatabaseName(this.databaseName);
        client.executeAsyncRequest(request);
    }
}
