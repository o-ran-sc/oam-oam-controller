/*
 * ============LICENSE_START=======================================================
 * ONAP : ccsdk features
 * ================================================================================
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
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
package org.onap.ccsdk.features.sdnr.wt.devicemanager.util;

import java.time.Instant;
import lombok.AllArgsConstructor;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.service.VESCollectorCfgService;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.VESMessage;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader.Domain;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader.Priority;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.Event;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.NotificationFields;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev241009.ConnectionOper.ConnectionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class MountpointStateVESMessageFormatter {

    private static final Logger LOG = LoggerFactory.getLogger(MountpointStateVESMessageFormatter.class);

    public static final String NODE_CHANGE_LEAVE_CONNECTED = "Unmounted";
    public static final String NODE_CHANGE_REMOVED = "Removed";
    static int sequenceNo = 0;
    private final String reportingEntityId;
    private final String reportingEntityName;

    public MountpointStateVESMessageFormatter(VESCollectorCfgService cfg) {
        this(cfg.getReportingEntityId(), cfg.getReportingEntityName());
    }

    private static void incrSequenceNo() {
        sequenceNo++;
    }

    private int getSequenceNo() {
        return sequenceNo;
    }

    public VESMessage createVESMessage(String nodeId, ConnectionStatus connectionStatus) {
        return this.createVESMessage(nodeId, connectionStatus, java.time.Clock.systemUTC().instant());
    }
    public VESMessage createVESMessage(String nodeId, String connectionStatus) {
        return this.createVESMessage(nodeId, connectionStatus, java.time.Clock.systemUTC().instant());
    }
    public VESMessage createVESMessage(String nodeId, ConnectionStatus connectionStatus, Instant timestamp) {
        return this.createVESMessage(nodeId, connectionStatus.toString(), timestamp);
    }

    public VESMessage createVESMessage(String nodeId, String connectionStatus, Instant timestamp) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("format to VES from {}, {}, {}", nodeId, connectionStatus, timestamp);
        }

        MountpointStateVESMessageFormatter.incrSequenceNo();

        CommonEventHeader vesCommonEventHeader = createVESCommonEventHeader(nodeId, connectionStatus.toString(),
                timestamp);
        NotificationFields vesNotificationFields = createVESNotificationFields(nodeId, connectionStatus.toString());

        return VESMessage.builder().event(new Event().withCommonEventHeader(vesCommonEventHeader)
                .withNotificationFields(vesNotificationFields)).build();

    }

    private NotificationFields createVESNotificationFields(String nodeId, String connectionStatus) {
        return new NotificationFields()
                .withChangeIdentifier(nodeId)
                .withChangeType(Constants.VES_CHANGETYPE)
                .withNewState(connectionStatus);
    }

    private CommonEventHeader createVESCommonEventHeader(String nodeId, String connectionStatus, Instant timestamp) {

        return new CommonEventHeader()
                .withDomain(Domain.NOTIFICATION)
                .withEventId(nodeId + "_" + connectionStatus + "_" + getSequenceNo())
                .withEventName(nodeId + "_" + connectionStatus + "_" + getSequenceNo())
                .withSourceName(nodeId)
                .withPriority(Priority.NORMAL)
                .withReportingEntityId(this.reportingEntityId)
                .withReportingEntityName(this.reportingEntityName)
                .withSequence(getSequenceNo())
                .withLastEpochMicrosec(timestamp.toEpochMilli() * 1000.0)
                .withStartEpochMicrosec(timestamp.toEpochMilli() * 1000.0);

    }
}
