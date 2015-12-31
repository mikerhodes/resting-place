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

package uk.co.dx13.restingplace.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.co.dx13.restingplace.exceptions.CouchDbException;
import uk.co.dx13.restingplace.http.connection.DefaultHttpUrlConnectionFactory;
import uk.co.dx13.restingplace.http.connection.Http;
import uk.co.dx13.restingplace.http.connection.HttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Executes a HTTP request defined by a HttpRequestTemplate and calls back into the template with the response.
 */
public class HttpRequestExecutor {

    private final Gson gson;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 6, 500, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());

//    private HttpConnection.HttpUrlConnectionFactory factory =
//            (OkHttpClientHttpUrlConnectionFactory.isOkUsable())
//                    ? new OkHttpClientHttpUrlConnectionFactory()
//                    : new DefaultHttpUrlConnectionFactory();

    private final HttpConnection.HttpUrlConnectionFactory factory = new DefaultHttpUrlConnectionFactory();

    private final HttpRequestTemplate request;

    public HttpRequestExecutor(HttpRequestTemplate request) {
        this.gson = new GsonBuilder().create();
        this.request = request;
    }

    public void executeAsync() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    executeSync();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Execute a HTTP request and handle common error cases.
     *
     * @return the executed HttpConnection
     * @throws CouchDbException for HTTP error codes or if an IOException was thrown
     */
    public void executeSync() throws URISyntaxException {

        URI rootUri = request.httpRootUrl();
        String path = request.httpPath();
        List<Param> queryParameters = request.httpQueryParameters();
        String body = request.httpRequestBody();
        String method = request.httpMethod();

        String queryString = "";
        if (queryParameters != null && queryParameters.size() > 0) {
            StringBuilder queryBuilder = new StringBuilder("?");
            for (int i = 0; i < queryParameters.size(); i++) {
                if (i > 0) {
                    queryBuilder.append("&");
                }
                queryBuilder.append(queryParameters.get(i).toURLEncodedString());
            }
            queryString = queryBuilder.toString();
        }

        URI uri = new URI(String.format("%s/%s%s", rootUri.toString(), path, queryString));
        HttpConnection connection = Http.connect(method, uri, "application/json");
        if (body != null) {
            connection.setRequestBody(body);
        }

        //set our HttpUrlFactory on the connection
        connection.connectionFactory = factory;

        // all CouchClient requests want to receive application/json responses
        connection.requestProperties.put("Accept", "application/json");

        // first try to execute our request and get the input stream with the server's response
        // we want to catch IOException because HttpUrlConnection throws these for non-success
        // responses (eg 404 throws a FileNotFoundException) but we need to map to our own
        // specific exceptions
        try {
            connection = connection.execute();
            int code = connection.getConnection().getResponseCode();
            InputStream responseBody;

            if (code / 100 == 2) { // success [200,299]
                responseBody = connection.responseAsInputStream();
            } else {
                responseBody = connection.getConnection().getErrorStream();
            }

            try {
                this.request.receiveStreamResponse(code, null, responseBody);
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
                connection.disconnect();
            }

        } catch (IOException ioe) {
            throw new CouchDbException("Error retrieving server response", ioe);
        }
    }
}
