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

package uk.co.dx13.restingplace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Deserialise a JSON document into a POJO.
 */
public class Deserializer {

    private final Gson gson;

    public Deserializer() {
        this.gson = new GsonBuilder().create();
    }

    /**
     * @param json The JSON string to deserialise
     * @return Object of type T
     */
    public <T> T deserialize(String json, Class<T> classType) throws
            JsonSyntaxException {
        return gson.fromJson(json, classType);
    }

}
