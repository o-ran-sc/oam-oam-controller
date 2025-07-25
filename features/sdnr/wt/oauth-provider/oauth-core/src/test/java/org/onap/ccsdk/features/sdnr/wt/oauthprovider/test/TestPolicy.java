/*
 * ============LICENSE_START=======================================================
 * ONAP : ccsdk features
 * ================================================================================
 * Copyright (C) 2021 highstreet technologies GmbH Intellectual Property.
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
 *
 */
package org.onap.ccsdk.features.sdnr.wt.oauthprovider.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.ccsdk.features.sdnr.wt.oauthprovider.data.OdlPolicy;

public class TestPolicy {

    private static final String PATH_1 = "/p1/**";

    @Test
    public void testPolicyAllowAll() {
        OdlPolicy p = OdlPolicy.allowAll(PATH_1);
        assertTrue(p.getMethods().isGet());
        assertTrue(p.getMethods().isPost());
        assertTrue(p.getMethods().isPut());
        assertTrue(p.getMethods().isDelete());
        assertTrue(p.getMethods().isPatch());
        assertEquals(PATH_1,p.getPath());
    }

    @Test
    public void testPolicyDenyAll() {
        OdlPolicy p = OdlPolicy.denyAll(PATH_1);
        assertFalse(p.getMethods().isGet());
        assertFalse(p.getMethods().isPost());
        assertFalse(p.getMethods().isPut());
        assertFalse(p.getMethods().isDelete());
        assertFalse(p.getMethods().isPatch());
        assertEquals(PATH_1,p.getPath());
    }

}
