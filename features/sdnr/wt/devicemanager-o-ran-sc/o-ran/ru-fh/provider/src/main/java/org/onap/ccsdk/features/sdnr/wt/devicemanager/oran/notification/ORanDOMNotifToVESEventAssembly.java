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
package org.onap.ccsdk.features.sdnr.wt.devicemanager.oran.notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.service.VESCollectorService;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader.Domain;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader.Priority;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.NamedHashMap;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.NotificationFields;
import org.onap.ccsdk.features.sdnr.wt.netconfnodestateservice.NetconfDomAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ORanDOMNotifToVESEventAssembly {

    private static final Logger log = LoggerFactory.getLogger(ORanDOMNotifToVESEventAssembly.class);
    private static final Domain VES_EVENT_DOMAIN = Domain.NOTIFICATION;
    private static final String VES_EVENTTYPE = "ORAN_notification";
    private static final Priority VES_EVENT_PRIORITY = Priority.NORMAL;
    private NetconfDomAccessor netconfDomAccessor;
    private VESCollectorService vesProvider;

    public ORanDOMNotifToVESEventAssembly(@NonNull NetconfDomAccessor netconfDomAccessor,
            @NonNull VESCollectorService vesCollectorService) {
        this.netconfDomAccessor = Objects.requireNonNull(netconfDomAccessor);
        this.vesProvider = Objects.requireNonNull(vesCollectorService);
    }

    // VES CommonEventHeader fields
    public CommonEventHeader createVESCommonEventHeader(Instant time, String notificationTypeName, int sequenceNo) {
        String eventId = notificationTypeName + "-" + Long.toUnsignedString(sequenceNo);
        return new CommonEventHeader()
                .withDomain(VES_EVENT_DOMAIN)
                .withEventName(notificationTypeName)
                .withEventType(VES_EVENTTYPE)
                .withPriority(VES_EVENT_PRIORITY)
                .withEventId(eventId)
                .withStartEpochMicrosec(time.toEpochMilli() * 1000.0)
                .withLastEpochMicrosec(time.toEpochMilli() * 1000.0)
                .withNfVendorName("ORAN")
                .withReportingEntityId(vesProvider.getConfig().getReportingEntityId())
                .withReportingEntityName(vesProvider.getConfig().getReportingEntityName())
                .withSequence(sequenceNo)
                .withSourceId("ORAN")
                .withSourceName(netconfDomAccessor.getNodeId().getValue());

    }

    // Notification fields
    public NotificationFields createVESNotificationFields(Map<String, String> xPathFields,
            String notificationTypeName) {
        StringBuffer buf = new StringBuffer();
        Iterator<Entry<String, String>> it = xPathFields.entrySet().iterator();
        var mappedPathFields = new org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.HashMap();
        while (it.hasNext()) {
            Entry<String, String> pair = it.next();
            buf.append("\n" + pair.getKey() + " = " + pair.getValue());
            mappedPathFields.setAdditionalProperty(pair.getKey(), pair.getValue());
        }
        log.debug("Resultlist({}):{}", xPathFields.size(), buf);
        return new NotificationFields()
                .withChangeType(notificationTypeName)
                .withChangeIdentifier(netconfDomAccessor.getNodeId().getValue())
                .withArrayOfNamedHashMap(
                        List.of(new NamedHashMap().withName(notificationTypeName).withHashMap(mappedPathFields)));

    }
}
