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
 *
 */
package org.onap.ccsdk.features.sdnr.wt.devicemanager.oran.yangspecs;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.onap.ccsdk.features.sdnr.wt.netconfnodestateservice.Capabilities;
import org.onap.ccsdk.features.sdnr.wt.netconfnodestateservice.NetconfDomAccessor;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.common.QNameModule;
import org.opendaylight.yangtools.yang.common.Revision;
import org.opendaylight.yangtools.yang.common.XMLNamespace;

public class OranHardware extends YangModule {

    public static final String NAMESPACE = "urn:o-ran:hardware:1.0";
    public static final QNameModule ORANHW_2019_03_28 =
            QNameModule.of(XMLNamespace.of(NAMESPACE), Revision.of("2019-03-28"));
    public static final QNameModule ORANHW_2024_04_15 =
            QNameModule.of(XMLNamespace.of(NAMESPACE), Revision.of("2024-04-15"));
    private static final List<QNameModule> MODULES = Arrays.asList(ORANHW_2019_03_28, ORANHW_2024_04_15);

    OranHardware(NetconfDomAccessor netconfDomAccessor, QNameModule module) {
        super(netconfDomAccessor, module);
    }

    public QName getModuleQName() {
        return getQName("o-ran-hardware");
    }

    public QName getComponentQName() {
        return getQName("O-RAN-HW-COMPONENT");
    }


    /**
     * Get specific instance, depending on capabilities
     *
     * @param netconfDomAccessor
     * @return
     */
    public static Optional<OranHardware> getModule(NetconfDomAccessor netconfDomAccessor) {
        Capabilities capabilities = netconfDomAccessor.getCapabilites();
        for (QNameModule module : MODULES) {
            if (capabilities.isSupportingNamespaceAndRevision(module)) {
                return Optional.of(new OranHardware(netconfDomAccessor, module));
            }
        }
        return Optional.empty();
    }

}
