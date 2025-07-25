/*
 * ============LICENSE_START=======================================================
 * ONAP : ccsdk features
 * ================================================================================
 * Copyright (C) 2025 highstreet technologies GmbH Intellectual Property.
 * All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.ccsdk.features.sdnr.wt.common.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.ccsdk.features.sdnr.wt.common.Resources;

public class TestResources {

    @Test
    public void test(){
        var file1 = Resources.getUrlForRessource(this.getClass(),"testpom.xml");
        assertNotNull(file1);
        var file2 = Resources.getFileContent(this.getClass(),"testpom.xml");
        assertNotNull(file2);

    }
}
