################################################################################
# Copyright 2025 highstreet technologies GmbH
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from TestPlanModel import TestPlan
import json
import requests
from pathlib import Path
from SdnrConfigModel import SdnrConfig
from TestSuiteConfig import TestSuiteConfig
import logging
from typing import Tuple
import time

logger = logging.getLogger("TestPlanRunner")

class TestPlanRunner():
    testplan: TestPlan
    sdnrconfig: SdnrConfig
    testsuite_cfg: TestSuiteConfig
    create_success: bool = False
    validate_success: bool = False
    cleanup_success: bool = False

    def __init__(self, testsuite_config: TestSuiteConfig, sdnrconfig: SdnrConfig, testplan: TestPlan):
        self.testplan = testplan
        self.sdnrconfig = sdnrconfig
        self.testsuite_cfg = testsuite_config 
                
    def run(self) -> Tuple[bool, bool, bool]:
        self.create_test()
        if (self.create_success):
            self.validate_test()
        self.cleanup()
        return (self.create_success, self.validate_success, self.cleanup_success)

    def create_test(self):
        logger.info("Creating Test - START")
        logger.info("Test Description = %s",self.testplan.description)
        logger.info("Test Plan Input = %s", self.testplan.get_input())
        if (self.testplan.get_input() is not None):
            logger.info("Request Type - %s",self.testplan.get_input_req_type())

            payload_file = "testplans/"+self.testplan.get_input_payload_file()
            payload = json.loads(Path(payload_file).read_text())
            logger.info("Payload = %s", payload)
            logger.info(self.sdnrconfig.get_sdnr_url() + self.testplan.get_input_url())
            logger.info(self.sdnrconfig.get_sdnr_headers())
            response = requests.put(self.sdnrconfig.get_sdnr_url() + self.testplan.get_input_url(), 
                                    verify=True, 
                                    data=json.dumps(payload), 
                                    headers=self.sdnrconfig.get_sdnr_headers())
            
            if (response.status_code == requests.codes.created):
                logger.info("Request Successful - Response code = %s", response.status_code)
                self.create_success = True
                time.sleep(10)
            else:
                logger.info("Creation failed - Reason - %s",response.status_code)
                logger.info("Error Message - %s", response.json())
            logger.info("Creating Test - END")
        else:
            logger.info("Nothing to CREATE, Proceeding for VALIDATION")
            self.create_success = True

    def validate_test(self) -> bool:
        logger.info("Validating test - START")
        # 1. get validation type and url
        url = self.testplan.get_validate_url()
        type = self.testplan.get_validate_req_type()
        if type == "GET":
            criteria = self.testplan.validation.criteria
            for key in criteria.keys():
                response = requests.get(self.sdnrconfig.get_sdnr_url() + url + key, 
                                verify=True, 
                                headers=self.sdnrconfig.get_sdnr_headers())
                if (response.status_code == requests.codes.OK):
                    logger.info("Response - %s", response.json())
                    #TODO: Hard coding for now. 
                    if response.json()["netconf-node-topology:connection-status"] == criteria[key]:
                        logger.info("VALIDATION SUCCESS")
                        self.validate_success = True
                    else:
                        logger.info("VALIDATION FAILED")
              
        logger.info("Validating test - END")


    def cleanup(self):
        logger.info("**** Cleanup - BEGIN ****")
        logger.info("self.cleanup = %s", self.testplan.cleanup)
        if (self.testplan.cleanup is not None):
            cleanup_obj = self.testplan.cleanup
            if cleanup_obj.req_type == "DELETE":
                response = requests.delete(self.sdnrconfig.get_sdnr_url() + self.testplan.get_cleanup_url(),
                                        verify=True,
                                        headers=self.sdnrconfig.get_sdnr_headers())
                if (response.status_code == requests.codes.NO_CONTENT):
                    print("Cleanup successful")
                    self.cleanup_success = True
                else:
                    logger.info("Cleanup failed")

            logger.info("**** Cleanup - END ****")
        else:
            logger.info("Nothing to Clean")
            self.cleanup_success = True

        