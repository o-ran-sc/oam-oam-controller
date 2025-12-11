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
package org.onap.ccsdk.features.sdnr.wt.devicemanager.oran.vesmapper;

import java.time.Instant;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.oran.util.ORanDMDOMUtility;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.oran.util.ORanDeviceManagerQNames;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.service.VESCollectorService;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader.Domain;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.CommonEventHeader.Priority;
import org.onap.ccsdk.features.sdnr.wt.devicemanager.types.ves.PnfRegistrationFields;
import org.onap.ccsdk.features.sdnr.wt.netconfnodestateservice.NetconfAccessor;
import org.opendaylight.yangtools.yang.data.api.schema.MapEntryNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ORanRegistrationToVESpnfRegistrationMapper {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ORanRegistrationToVESpnfRegistrationMapper.class);
    //CommonEventHeader fields
    private static final Domain VES_EVENT_DOMAIN = Domain.PNF_REGISTRATION;
    private static final String VES_EVENTTYPE = "NetConf Callhome Registration";
    private static final Priority VES_EVENT_PRIORITY = Priority.NORMAL;

    private final VESCollectorService vesProvider;
    private final NetconfAccessor netconfAccessor;

    private Integer sequenceNo;


    public ORanRegistrationToVESpnfRegistrationMapper(NetconfAccessor netconfAccessor,
            VESCollectorService vesCollectorService) {
        this.netconfAccessor = netconfAccessor;
        this.vesProvider = vesCollectorService;

        this.sequenceNo = 0;
    }

    public CommonEventHeader mapCommonEventHeader(MapEntryNode component) {
        return new CommonEventHeader()
                .withDomain(VES_EVENT_DOMAIN)
                .withEventId(netconfAccessor.getNodeId().getValue())
                .withEventName(netconfAccessor.getNodeId().getValue())
                .withEventType(VES_EVENTTYPE)
                .withPriority(VES_EVENT_PRIORITY)
                .withStartEpochMicrosec(Instant.now().toEpochMilli() * 1000.0)
                .withLastEpochMicrosec(Instant.now().toEpochMilli() * 1000.0)
                .withNfVendorName(
                        ORanDMDOMUtility.getLeafValue(component,
                                ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_MFG_NAME))
                .withReportingEntityId(vesProvider.getConfig().getReportingEntityId())
                .withReportingEntityName(vesProvider.getConfig().getReportingEntityName())
                .withSequence(sequenceNo++)
                .withSourceId(
                        ORanDMDOMUtility.getLeafValue(component, ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_UUID)
                                != null
                                ? ORanDMDOMUtility.getLeafValue(component,
                                ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_UUID)
                                : netconfAccessor.getNodeId().getValue())
                .withSourceName(netconfAccessor.getNodeId().getValue());

    }

    public PnfRegistrationFields mapPNFRegistrationFields(MapEntryNode component) {
        return new PnfRegistrationFields()

                .withModelNumber(ORanDMDOMUtility.getLeafValue(component,
                        ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_MFG_NAME))
                .withOamV4IpAddress(netconfAccessor.getNetconfNode().getHost().getIpAddress().getIpv4Address() != null
                        ? netconfAccessor.getNetconfNode().getHost().getIpAddress().getIpv4Address().getValue()
                        : null)
                .withOamV6IpAddress(netconfAccessor.getNetconfNode().getHost().getIpAddress().getIpv6Address() != null
                        ? netconfAccessor.getNetconfNode().getHost().getIpAddress().getIpv6Address().getValue()
                        : null)
                .withSerialNumber(ORanDMDOMUtility.getLeafValue(component,
                        ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_SER_NUM))
                .withVendorName(ORanDMDOMUtility.getLeafValue(component,
                        ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_MFG_NAME))
                .withSoftwareVersion(
                        ORanDMDOMUtility.getLeafValue(component, ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_SW_REV))
                .withUnitType(
                        ORanDMDOMUtility.getLeafValue(component, ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_ALIAS))
                .withUnitFamily(
                        ORanDMDOMUtility.getLeafValue(component, ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_CLASS))
                .withManufactureDate(ORanDMDOMUtility.getLeafValue(component,
                        ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_MFG_DATE) != null
                        ? ORanDMDOMUtility.getLeafValue(component,
                        ORanDeviceManagerQNames.IETF_HW_COMPONENT_LIST_MFG_DATE)
                        : "Unknown");
        //vesPnfFields.setLastServiceDate(component.getLastChange());
    }
}
