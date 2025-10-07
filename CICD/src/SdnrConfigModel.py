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

from pydantic import BaseModel, Field
import base64

class SdnrCredentials(BaseModel):
    sdnr_username: str = Field(..., alias="sdnrUsername")
    sdnr_password: str = Field(..., alias="sdnrPassword")
    
class SdnrConfig(BaseModel):
    sdnr_host: str = Field(..., alias="sdnrHost")
    sdnr_port: int = Field(..., alias="sdnrPort")
    basic_auth: bool = Field(..., alias="basicAuth")
    credentials: SdnrCredentials
    #TODO: Enhance for Token based Authentication

    def get_auth_token(self) -> str :
        # Return base64 encoded value if authBasic or token if oauth
        if self.basic_auth:
            auth_bytes = (self.credentials.sdnr_username + ":" + self.credentials.sdnr_password).encode("utf-8")
            auth_encoded = base64.b64encode(auth_bytes)
            return auth_encoded.decode("utf-8") #return string
        
    def get_sdnr_headers(self) -> dict:
        headers = {
            'accept': 'application/json',
            'authorization': 'Basic ' + self.get_auth_token()
        }
        return headers

    def get_sdnr_url(self) -> str:
        return "http://" + self.sdnr_host + ":" + str(self.sdnr_port)