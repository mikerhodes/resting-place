/**
 * Copyright (c) 2015 Mike Rhodes. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package uk.co.dx13.restingplace.requests.response;

import uk.co.dx13.restingplace.requests.CloudantRequest;

import java.util.Map;

/**
 * Implement and assign to an operation to receive an object deserialised from the JSON.
 */
public interface ResponseMappingDelegate<T> {

    /** Returns the class to map response body to. */
    Class<T> getClazz();

    /** Called with the request's response. */
    void receiveObjectResponse(CloudantRequest request, int statusCode,
                               Map<String, String> headers, T body);
}
