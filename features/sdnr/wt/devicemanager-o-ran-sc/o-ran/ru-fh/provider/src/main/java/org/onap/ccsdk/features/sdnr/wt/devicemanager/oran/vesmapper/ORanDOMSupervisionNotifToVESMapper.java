/*
 * ============LICENSE_START=======================================================
 * ONAP : ccsdk features
 * ================================================================================
 * Copyright (C) 2023 highstreet technologies GmbH Intellectual Property.
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
package org.onap.ccsdk.features.sdnr.wt.devicemanager.oran.vesmapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.Instant;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.oran.util.ORanDMDOMUtility;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.service.VESCollectorService;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader.Domain;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader.Priority;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.Data;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.StndDefinedFields;
import org.opendaylight.mdsal.dom.api.DOMNotification;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;

/*
 * {
    "event": {
        "stndDefinedFields": {
            "schemaReference": "https://gerrit.o-ran-sc.org/r/gitweb?p=scp/oam/modeling.git;a=blob_plain;f=data-model/yang/published/o-ran/ru-fh/o-ran-supervision.yang#components/schemas/ofhm-event-stream",
            "stndDefinedFieldsVersion": "1.0",
            "data": {
                "ietf:notification": {
                    "eventTime": "2023-06-28T07:28:55.098Z",
                    "o-ran-supervision:supervision-notification": {
                        "session-id": 999999
                    }
                }
            }
        },
        "commonEventHeader": {
            "domain": "stndDefined",
            "eventType": "o-ran-supervision:supervision-notification",
            "eventId": "pnf2_o-ran-supervision:supervision-notification_fed2ab31f6e1da56",
            "eventName": "stndDefined_o-ran-supervision:supervision-notification",
            "sequence": 1687937335098,
            "priority": "Low",
            "reportingEntityId": "c2b7d6e9-ee35-459a-ab8e-717a6fc1fde6",
            "reportingEntityName": "flows",
            "sourceId": "378e9904-6d39-40ea-9994-0596fe2235a3",
            "sourceName": "O-RAN-SC-OAM-Test-Component-01",
            "startEpochMicrosec": 1687937335098000,
            "lastEpochMicrosec": 1687937335098000,
            "nfNamingCode": "pnf2",
            "nfVendorName": "O-RAN-SC-OAM-Project",
            "timeZoneOffset": "+00:00",
            "stndDefinedNamespace": "o-ran-supervision:supervision-notification",
            "version": "4.1",
            "vesEventListenerVersion": "7.2.1"
        }
    }
}
 */
public class ORanDOMSupervisionNotifToVESMapper {

    private static final Domain VES_EVENT_DOMAIN = Domain.STND_DEFINED;
    private static final String VES_EVENTTYPE = "o-ran-supervision:supervision-notification";
    private static final Priority VES_EVENT_PRIORITY = Priority.LOW;
    private static final String O_RU_SUPERVISION_SCHEMA_REFERENCE =
            "https://gerrit.o-ran-sc.org/r/gitweb?p=scp/oam/modeling.git;a=blob_plain;f=data-model/yang/published/o-ran/ru-fh/o-ran-supervision.yang#components/schemas/ofhm-event-stream";
    private final VESCollectorService vesProvider;
    private final String notifName;
    private final String nodeIdString;
    //Initialized during registration
    private String mfgName;
    private String uuid;
    private String modelName;

    public ORanDOMSupervisionNotifToVESMapper(NodeId nodeId, VESCollectorService vesCollectorService,
            String notifName) {
        this.nodeIdString = nodeId.getValue();
        this.vesProvider = vesCollectorService;
        this.notifName = notifName;
    }

    public void setMfgName(String mfgName) {
        this.mfgName = mfgName;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public CommonEventHeader mapCommonEventHeader(DOMNotification notification, Instant eventTime,
            int sequenceNo) {
        String eventId = notifName + "-" + Long.toUnsignedString(sequenceNo);
        return new CommonEventHeader()
                .withDomain(VES_EVENT_DOMAIN)
                .withEventName(VES_EVENT_DOMAIN + "_" + VES_EVENTTYPE)
                .withEventType(VES_EVENTTYPE)
                .withPriority(VES_EVENT_PRIORITY)
                .withEventId(eventId)
                .withStartEpochMicrosec(eventTime.toEpochMilli() * 1000.0)
                .withLastEpochMicrosec(eventTime.toEpochMilli() * 1000.0)
                .withNfVendorName(mfgName)
                .withReportingEntityId(vesProvider.getConfig().getReportingEntityId())
                .withReportingEntityName(vesProvider.getConfig().getReportingEntityName())
                .withSequence(sequenceNo)
                .withSourceId(uuid)
                .withSourceName(nodeIdString);

    }

    public StndDefinedFields mapStndDefinedFields(Instant eventTimeInstant) {
        return new StndDefinedFields()
                .withSchemaReference(URI.create(O_RU_SUPERVISION_SCHEMA_REFERENCE))
                .withData(getSupervisionData(eventTimeInstant));
    }

    private Data getSupervisionData(Instant eventTimeInstant) {
        ORanSupervisionNotification oruSuperNotif = new ORanSupervisionNotification();
        oruSuperNotif.setSessionId(
                999999); // Hardcoded due to limitation in NTS Simulator. Ideally should be NETCONF Session ID

        IetfNotification ietfNotif = new IetfNotification();
        ietfNotif.setOranSupervisionNotif(oruSuperNotif);
        ietfNotif.setEventTime(ORanDMDOMUtility.getDateAndTimeOfInstant(eventTimeInstant).getValue());

        return new Data().withAdditionalProperty("ietf:notification", ietfNotif);
    }

}

class IetfNotification {

    String eventTime;
    @JsonProperty("o-ran-supervision:supervision-notification")
    ORanSupervisionNotification oranSupervisionNotif;

    public IetfNotification() {
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public ORanSupervisionNotification getOranSupervisionNotif() {
        return oranSupervisionNotif;
    }

    public void setOranSupervisionNotif(ORanSupervisionNotification oranSupervisionNotif) {
        this.oranSupervisionNotif = oranSupervisionNotif;
    }

}


class ORanSupervisionNotification {

    @JsonProperty("session-id")
    int sessionId;

    public ORanSupervisionNotification() {
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
