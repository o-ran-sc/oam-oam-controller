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

from typing import Optional
from pydantic import BaseModel, Field

# Class to validate the Test plan data (Ex: testplan.json)
class Input (BaseModel):
   req_type: str = Field(..., alias="reqType")
   req_endpoint: str = Field(..., alias="reqEndPoint")
   req_payload_file: str = Field(..., alias="reqPayloadFile")

class Validation (BaseModel):
   req_type: str = Field(..., alias="reqType")
   req_endpoint: str = Field(..., alias="reqEndPoint")
   criteria: object

class Cleanup (BaseModel):
   req_type: str = Field(..., alias="reqType")
   req_endpoint: str = Field(..., alias="reqEndPoint")

class TestPlan(BaseModel):
   description: str
   plan_id: str = Field(..., alias="plan-id")
   input: Optional[Input] = None
   exp_resp_code: Optional[int] = Field(None, alias="expectedRespCode")
   validation: Validation = Field(..., alias="validation")
   cleanup: Optional[Cleanup] = None

   def get_validate_url(self):
      return self.validation.req_endpoint
   
   def get_validate_req_type(self):
      return self.validation.req_type

   def get_cleanup_url(self):
      return self.cleanup.req_endpoint
   
   def get_cleanup_req_type(self):
      return self.cleanup.req_type
   
   def get_input_url(self):
      return self.cleanup.req_endpoint
   
   def get_input_req_type(self):
      return self.cleanup.req_type
   
   def get_input_payload_file(self):
      return self.input.req_payload_file

   def get_input(self):
      return self.input