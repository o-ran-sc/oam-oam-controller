# Abstract class which defines the common structure for all REST requests
from abc import ABC, abstractmethod

class ApiRequest():
    baseurl: str
    endpoint: str
    headers: dict[str, str] = {}
    query_params: dict[str, str]
    request_body: str

    @abstractmethod
    def __init__(self, baseurl: str, endpoint: str):
        self.baseurl = baseurl
        self.endpoint = endpoint

    @abstractmethod
    def execute(self):
        pass

    def add_header(self, key: str, value: str):
        self.headers[key] = value

    def add_query_param(self, key: str, value: str):
        self.query_params[key] = value

    def set_request_body(self, request_body: str):
        self.request_body = request_body
