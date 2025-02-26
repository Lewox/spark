/*
 * Copyright 2016 - Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.route;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test the HttpMethod.
 */
public class HttpMethodTest {

    @Test
    public void testSupportedHttpMethod() {
        HttpMethod get = HttpMethod.get;
        HttpMethod method = HttpMethod.get(get.name());

        assertEquals(get, method);
    }

    @Test
    public void testNotSupportedHttpMethod() {
        HttpMethod method = HttpMethod.get("lock");

        assertEquals(HttpMethod.unsupported, method);
    }

}
