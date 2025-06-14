/*
 * ============LICENSE_START========================================================================
 * ONAP : ccsdk feature sdnr wt
 * =================================================================================================
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property. All rights reserved.
 * =================================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * ============LICENSE_END==========================================================================
 */
package org.onap.ccsdk.features.sdnr.wt.websocketmanager2.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.ccsdk.features.sdnr.wt.websocketmanager.WebSocketManagerProvider;
import org.opendaylight.mdsal.binding.api.RpcProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.ObjectCreationNotification;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.ProblemNotification;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.ProblemNotificationBuilder;
import org.opendaylight.yangtools.binding.Notification;
import org.osgi.service.http.HttpService;

public class WebsockerProviderTest extends Mockito {

    @Test
    public void test() {
        RpcProviderService rpcProviderServiceMock = mock(RpcProviderService.class);
        HttpService httpService = mock(HttpService.class);

        try (WebSocketManagerProvider provider = new WebSocketManagerProvider();) {
            provider.init();
            provider.onBindService(httpService);
            provider.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTypeAssertion() {

        Notification problemNotification = new ProblemNotificationBuilder().build();
        assertTrue(WebSocketManagerProvider.assertNotificationType(problemNotification, ProblemNotification.QNAME));
        assertFalse(
                WebSocketManagerProvider.assertNotificationType(problemNotification, ObjectCreationNotification.QNAME));

    }

}
