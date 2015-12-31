/**
 * Copyright (c) 2015 IBM Cloudant. All rights reserved.
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

package uk.co.dx13.restingplace.requests;

import org.junit.Test;
import uk.co.dx13.restingplace.Client;
import uk.co.dx13.restingplace.Database;
import uk.co.dx13.restingplace.requests.response.ResponseMappingDelegate;
import uk.co.dx13.restingplace.requests.response.ResponseStringDelegate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Tests for GetDocumentRequest
 */
public class GetDocumentRequestTests {

    private class ResultHolder<T> {
        T result;
    }

    private class Animal {
        public String _id;
        public String _rev;
        public String latin_name;
    }

    @Test public void testGetStringDocSync() throws URISyntaxException {

        Client c = new Client(new URI("http://localhost:5984/"));
        Database d = c.database("animaldb");

        final ResultHolder<String> rh = new ResultHolder<String>();
        GetDocumentRequest request = new GetDocumentRequest();
        request.setDocId("aardvark");
        request.setDelegate(new ResponseStringDelegate() {
            @Override
            public void receiveStringResponse(CloudantRequest request, int statusCode,
                                              Map<String, String> headers, String body) {
                rh.result = body;
            }
        });
        d.executeRequest(request);
        assertEquals("JSON received not as expected", rh.result, "{\"_id\":\"aardvark\",\"_rev\":\"3-fe45a3e06244adbe7ba145e74e57aba5\",\"min_weight\":40,\"max_weight\":65,\"min_length\":1,\"max_length\":2.2000000000000001776,\"latin_name\":\"Orycteropus afer\",\"wiki_page\":\"http://en.wikipedia.org/wiki/Aardvark\",\"class\":\"mammal\",\"diet\":\"omnivore\"}\n");
    }

    @Test public void testGetMappedHashMapDocSync() throws URISyntaxException {

        Client c = new Client(new URI("http://localhost:5984/"));
        Database d = c.database("animaldb");

        final ResultHolder<HashMap> rh = new ResultHolder<HashMap>();
        GetDocumentRequest request = new GetDocumentRequest();
        request.setDocId("aardvark");
        request.setDelegate(new ResponseMappingDelegate<HashMap>() {
            @Override
            public Class<HashMap> getClazz() {
                return HashMap.class;
            }

            @Override
            public void receiveObjectResponse(CloudantRequest request, int statusCode,
                                              Map<String, String> headers, HashMap body) {
                rh.result = body;
            }
        });
        d.executeRequest(request);
        assertEquals("_id wrong", "aardvark", rh.result.get("_id"));
        assertEquals("_rev wrong", "3-fe45a3e06244adbe7ba145e74e57aba5", rh.result.get("_rev"));
        assertEquals("min_weight wrong", 40.0, rh.result.get("min_weight"));
        assertEquals("max_weight wrong", 65.0, rh.result.get("max_weight"));
    }

    @Test public void testGetMappedAnimalDocSync() throws URISyntaxException {

        Client c = new Client(new URI("http://localhost:5984/"));
        Database d = c.database("animaldb");

        final ResultHolder<Animal> rh = new ResultHolder<Animal>();
        GetDocumentRequest request = new GetDocumentRequest();
        request.setDocId("aardvark");
        request.setDelegate(new ResponseMappingDelegate<Animal>() {
            @Override
            public Class<Animal> getClazz() {
                return Animal.class;
            }

            @Override
            public void receiveObjectResponse(CloudantRequest request, int statusCode,
                                              Map<String, String> headers, Animal body) {
                rh.result = body;
            }
        });
        d.executeRequest(request);
        assertEquals("_id wrong", "aardvark", rh.result._id);
        assertEquals("_rev wrong", "3-fe45a3e06244adbe7ba145e74e57aba5", rh.result._rev);
        assertEquals("latin_name wrong", "Orycteropus afer", rh.result.latin_name);
    }

    @Test public void testGetStringDocAsync() throws URISyntaxException {

        Client c = new Client(new URI("http://localhost:5984/"));
        Database d = c.database("animaldb");
        final CountDownLatch requestComplete = new CountDownLatch(1);

        final ResultHolder<String> rh = new ResultHolder<String>();
        GetDocumentRequest request = new GetDocumentRequest();
        request.setDocId("aardvark");
        request.setDelegate(new ResponseStringDelegate() {
            @Override
            public void receiveStringResponse(CloudantRequest request, int statusCode,
                                              Map<String, String> headers, String body) {
                rh.result = body;

                // Allow the test thread to wake up so that we can
                // check this is running asynchronously
                try {
                    Thread.sleep(100);  // ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                requestComplete.countDown();
            }
        });
        d.executeAsyncRequest(request);

        // Here we shouldn't yet have a result
        assertNull("Request result has been set", rh.result);

        // Wait for the operation to complete.
        try {
            requestComplete.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            /* do nothing */
        }
        assertEquals("JSON received not as expected", rh.result, "{\"_id\":\"aardvark\",\"_rev\":\"3-fe45a3e06244adbe7ba145e74e57aba5\",\"min_weight\":40,\"max_weight\":65,\"min_length\":1,\"max_length\":2.2000000000000001776,\"latin_name\":\"Orycteropus afer\",\"wiki_page\":\"http://en.wikipedia.org/wiki/Aardvark\",\"class\":\"mammal\",\"diet\":\"omnivore\"}\n");
    }

}
