/**
 * ============LICENSE_START========================================================================
 * ONAP : ccsdk feature sdnr wt odlux
 * =================================================================================================
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property. All rights reserved.
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

import { combineActionHandler } from '../../../../framework/src/flux/middleware';

import { DetailsHandler, DetailsStoreState } from './detailsHandler';
import { MapHandler, MapState } from './mapHandler';
import { SearchHandler, searchState } from './searchHandler';
import { connectivityState, ConnectivityHandler } from './connectivityHandler';
import { SettingsHandler, SettingsState } from './settingsHandler';
import { ManagementHandler, ManagementState } from './sitedocManagementHandler';
import { FilterHandler, FilterState } from './filterHandler';

export type INetworkAppStoreState = {
  details: DetailsStoreState;
  map: MapState;
  search: searchState;
  filter: FilterState;
  connectivity: connectivityState;
  settings: SettingsState;
  sitedocManagement: ManagementState;
};

declare module '../../../../framework/src/store/applicationStore' {
  interface IApplicationStoreState {
    network: INetworkAppStoreState;
  }
}

const appHandler = {
  details: DetailsHandler,
  map: MapHandler,
  search: SearchHandler,
  filter: FilterHandler,
  connectivity: ConnectivityHandler,
  settings: SettingsHandler,
  sitedocManagement: ManagementHandler,
};

export const networkmapRootHandler = combineActionHandler<INetworkAppStoreState>(appHandler);
export default networkmapRootHandler;