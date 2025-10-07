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

#Entry point of the test suite

from TestPlanModel import TestPlan
from TestPlanRunner import TestPlanRunner
from pydantic import BaseModel
import json
import os
import sys
import argparse
from pathlib import Path
from SdnrConfigModel import SdnrConfig
from TestSuiteConfig import TestSuiteConfig
import logging

logger = logging.getLogger(__name__)
parser = argparse.ArgumentParser(description="Test Suite")
parser.add_argument("--logFile", type=str, required=True, help="log file name including path")
successCnt: int = 0
failedCnt: int = 0

class Main:
    testsuite_config: TestSuiteConfig
    sdnrconfig: SdnrConfig
    

    def __init__(self, logFile: str):
        print("Initializing Test Suite ...")
        logging.basicConfig (
        level=logging.DEBUG,
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
        handlers=[
            logging.FileHandler(logFile),
            logging.StreamHandler()
        ]
    )
        self.load_testsuite_config()
        self.load_sdnr_config()

    def load_sdnr_config(self) -> None:
        sdnrconfig_path = "config/sdnrconfig.json"
        self.sdnrconfig = SdnrConfig.model_validate(json.loads(Path(sdnrconfig_path).read_text()))

    def load_testsuite_config(self) -> None:
        testsuiteconfig_path = "config/testsuiteconfig.json"
        self.testsuite_config = TestSuiteConfig.model_validate(json.loads(Path(testsuiteconfig_path).read_text()))

    def start(self) -> None:
        # Iterate through test plans
        for _, dirs, _ in os.walk(self.testsuite_config.testplan_dir):
            dirs.sort()
            for dir in dirs:
                logger.info("Executing Test Plan : %s", dir)
                for file in os.listdir(self.testsuite_config.testplan_dir+"/"+dir):
                    if "plan" in file: 
                        self.executeTest(dir, file)
    
    def executeTest(self, subdir: str, testplan_file: str):
        global successCnt, failedCnt
        logger.info("\n======== BEGIN TEST: Test Dir - %s ========", subdir)
        # 1. Load test plan
        testplan_path = "testplans/" + subdir + "/" + testplan_file
        data = json.loads(Path(testplan_path).read_text())
        test_plan = TestPlan.model_validate(data)

        # 2. Execute Plan
        testplan_runner = TestPlanRunner(self.testsuite_config, self.sdnrconfig, test_plan)
        (create_result, validate_result, cleanup_result) = testplan_runner.run()
        if (create_result and validate_result and cleanup_result):
            successCnt += 1
        else:
            failedCnt += 1

        # 3. Print test result
        logger.info("---------------------------------------------------------------------------")
        logger.info("|  Test %s Result - Execution - %s, Validation - %s, Cleanup - %s  |", 
                    subdir, create_result, validate_result, cleanup_result)
        logger.info("---------------------------------------------------------------------------")
        logger.info("======== END TEST: Test Dir - %s.  ========\n", subdir)

#Main
if __name__ == '__main__':
    args = parser.parse_args()
    main = Main(args.logFile)
    main.start()
    logger.info ("Test Execution Summary - Success = %d Failed = %d", successCnt, failedCnt)
    if (failedCnt > 0):
        logger.info("TEST Suite Execution Failed")
        sys.exit(1)
    else:
        logger.info("TEST Suite Execution Success")
        sys.exit(0)
