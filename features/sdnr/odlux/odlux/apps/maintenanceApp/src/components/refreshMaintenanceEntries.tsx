/**
 * ============LICENSE_START========================================================================
 * ONAP : ccsdk feature sdnr wt odlux
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
import React, { FC } from 'react';

import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';

import { useApplicationDispatch } from '../../../../framework/src/flux/connect';

import { maintenanceEntriesReloadAction } from '../handlers/maintenanceEntriesHandler';

export enum RefreshMaintenanceEntriesDialogMode {
  None = 'none',
  RefreshMaintenanceEntriesTable = 'RefreshMaintenanceEntriesTable',
}

type DialogSettings = {
  dialogTitle: string;
  dialogDescription: string;
  applyButtonText: string;
  cancelButtonText: string;
  enableMountIdEditor: boolean;
  enableUsernameEditor: boolean;
  enableExtendedEditor: boolean;
};

const settings: { [key: string]: DialogSettings } = {
  [RefreshMaintenanceEntriesDialogMode.None]: {
    dialogTitle: '',
    dialogDescription: '',
    applyButtonText: '',
    cancelButtonText: '',
    enableMountIdEditor: false,
    enableUsernameEditor: false,
    enableExtendedEditor: false,
  },
  [RefreshMaintenanceEntriesDialogMode.RefreshMaintenanceEntriesTable]: {
    dialogTitle: 'Do you want to refresh Maintenance Entries?',
    dialogDescription: '',
    applyButtonText: 'Yes',
    cancelButtonText: 'Cancel',
    enableMountIdEditor: true,
    enableUsernameEditor: true,
    enableExtendedEditor: true,
  },
};

type RefreshMaintenanceEntriesDialogComponentProps = {
  mode: RefreshMaintenanceEntriesDialogMode;
  onClose: () => void;
};

const RefreshMaintenanceEntriesDialogComponent: FC<RefreshMaintenanceEntriesDialogComponentProps> = (props) => {
  const dispatch = useApplicationDispatch();
  const refreshMaintenanceEntries = () => dispatch(maintenanceEntriesReloadAction);
  const setting = settings[props.mode];

  const onRefresh = () => {
    refreshMaintenanceEntries();
    props.onClose();
  };

  const onCancel = () => {
    props.onClose();
  };

  return (
    <Dialog open={props.mode !== RefreshMaintenanceEntriesDialogMode.None}>
      <DialogTitle id="form-dialog-title" aria-label={`${setting.dialogTitle.replace(/ /g, '-').toLowerCase()}-dialog`}>{setting.dialogTitle}</DialogTitle>
      <DialogContent>
        <DialogContentText>
          {setting.dialogDescription}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button aria-label="dialog-confirm-button" onClick={onRefresh} color="inherit">{setting.applyButtonText}</Button>
        <Button aria-label="dialog-cancel-button" onClick={onCancel} color="secondary">{setting.cancelButtonText}</Button>
      </DialogActions>
    </Dialog>
  );
};

export const RefreshMaintenanceEntriesDialog = RefreshMaintenanceEntriesDialogComponent;
export default RefreshMaintenanceEntriesDialog;
