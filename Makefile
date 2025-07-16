# Copyright 2025 highstreet technologies USA Corp.
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

## help:                Show the help.
.PHONY: help
help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "Targets:"
	@fgrep "##" Makefile | fgrep -v fgrep | sed 's/##/ -/g'


## feature:             Build all features 
.PHONY: feature
feature:
	mvn clean install -f parents/pom.xml
	mvn clean install -f features/sdnr/wt/pom.xml
	mvn clean install -f features/sdnr/odlux/pom.xml

## dist:                Build images
.PHONY: dist
dist:
	mvn clean install -f distribution/oam-controller/pom.xml
	mvn clean install -f distribution/oam-controller-web/pom.xml

## all:                 Build features and images
.PHONY: all
all: feature dist