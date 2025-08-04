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

.PHONY: help parents features-wt features-odlux dist all

## help:                Show the help.
help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "Targets:"
	@fgrep "##" Makefile | fgrep -v fgrep | sed 's/##/ -/g'

## parents:				Build parents
parents:
	mvn clean install -f parents/pom.xml -s settings.xml

## features-wt:         Build wt features
features-wt:
	mvn clean install -f features/sdnr/wt/pom.xml -s settings.xml

## features-odlux:      Build odlux features
features-odlux:
	mvn clean install -f features/sdnr/odlux/pom.xml -s settings.xml

## dist:                Build images
dist:
	mvn clean install -f distribution/oam-controller/pom.xml -s settings.xml
	mvn clean install -f distribution/oam-controller-web/pom.xml -s settings.xml

## deploy				Deploy artifacts to some artifact repository like nexus, jfrog, github packages etc.
deploy:
	mvn clean deploy -f parents/pom.xml -s settings.xml
	mvn clean deploy -f features/sdnr/wt/pom.xml -s settings.xml
	mvn clean deploy -f features/sdnr/odlux/pom.xml -s settings.xml
	mvn clean deploy -f distribution/oam-controller/pom.xml -s settings.xml

## all:                 Build features and images
build: parents features-wt features-odlux dist

