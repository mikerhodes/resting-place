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

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Implement this interface for HttpRequestExecutor to be able to execute the request
 */
public interface HttpRequestTemplate {

    URI httpRootUrl();
    String httpPath();
    String httpMethod();
    List<Param> httpQueryParameters();
    String httpRequestBody();

    /**
     * Receive response.
     * @param statusCode Status code of the operation
     * @param headers Map of headers
     * @param body InputStream of body. Callee should *not* close.
     */
    void receiveStreamResponse(int statusCode, Map<String, String> headers, InputStream body);

}
