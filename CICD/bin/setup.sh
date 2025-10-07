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

#!/bin/bash

# Log output of each command executed in the script for easier debugging
#exec > >(stdbuf -oL awk '{ print strftime("[%Y-%m-%d %H:%M:%S]"), $0; fflush(); }' | tee -a setup.log) 2>&1

# 7. Dump the log into the Github window. How ?


DOCKER_NETWORK_SUB="172.18.0.0/16"
DOCKER_NETWORK_GW="172.18.0.1"
DOCKER_NETWORK_NAME="CICD-Network"
CUSTOM_O_RU_MPLANE_DC_FILE="docker-compose-o-ru-mplane.yaml"
CUSTOM_O_RU_MPLANE_SSH_CALLHOME="ietf-netconf-server-ssh-callhome.json"
CUSTOM_O_RU_MPLANE_TLS_CALLHOME="ietf-netconf-server-tls-callhome.json"
CUSTOM_O_RU_MPLANE_TLS_NON_CALLHOME="ietf-netconf-server-tls-listen.json"
SDNR_COMPONENTS_DC_FILE="docker-compose-sdnr-mariadb.yaml"
# Code everything relative to the BINDIR
BINDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOTDIR="$(cd "${BINDIR}/.." && pwd)"
CONFIGDIR=${ROOTDIR}/config
TMPDIR=${ROOTDIR}/tmp
LOGDIR=${ROOTDIR}/log
LOGFILE=testsuite.log

log() {
    echo "$1" | tee -a ${LOGDIR}/${LOGFILE}
}

cleanup() {
    log "## Deleting any running/stopped docker containers ##\n"
    container_names=$(docker ps --format {{.Names}})
    if [[ "$container_names" != "" ]]; then
        docker rm -f $(docker ps -aq)
    fi

    log "## Checking if docker network with name - ${DOCKER_NETWORK_NAME} - exists ##\n"
    docker_nw=$(docker network ls --filter name=^${DOCKER_NETWORK_NAME}$ --format {{.Name}})
    if [[ "$docker_nw" != "" ]]; then
        log "## Docker network with name ${DOCKER_NETWORK_NAME} exists. Deleting it ...##\n"
        docker network rm ${DOCKER_NETWORK_NAME}
    else
        log "## Docker network with name ${DOCKER_NETWORK_NAME} DOES NOT exist ##"
    fi

    if [[ -d ${TMPDIR} ]]; then
        log "## Deleting tmp directory ..##\n"
        sudo rm -rf ${TMPDIR}
    fi

    if [[ -f ${LOGDIR}/setup.log ]]; then
        rm ${LOGDIR}/setup.log
    fi
}

build_pynts_images() {
    log "## Start Building PyNTS ... ##\n"
    log "## Cloning PyNTS simulator from https://github.com/o-ran-sc/sim-o1-ofhmp-interfaces ##\n"
    git clone https://github.com/o-ran-sc/sim-o1-ofhmp-interfaces.git ${TMPDIR}/sim-o1-ofhmp-interfaces
    cd ${TMPDIR}/sim-o1-ofhmp-interfaces
    make
    if [ $? = 0 ]; then
        log "\n## Build of PyNTS Successful ##\n"
        log "## Following docker images were built ##\n"
        docker images | grep pynts
    else
        log "\n### Build failed ###\n"
        exit 1
    fi
}

create_docker_network() {
    log "\n## Creating docker network - ${DOCKER_NETWORK_NAME} ##\n"
    docker network create --subnet ${DOCKER_NETWORK_SUB} --gateway ${DOCKER_NETWORK_GW} ${DOCKER_NETWORK_NAME}
}

start_pynts() {
    log "## Overwriting file - docker-compose-o-ru-mplane.yaml - with custom file ##\n"
    cp ${CONFIGDIR}/${CUSTOM_O_RU_MPLANE_DC_FILE} ${TMPDIR}/sim-o1-ofhmp-interfaces
    cp ${CONFIGDIR}/${CUSTOM_O_RU_MPLANE_SSH_CALLHOME} ${TMPDIR}/sim-o1-ofhmp-interfaces/o-ru-mplane/data
    cp ${CONFIGDIR}/${CUSTOM_O_RU_MPLANE_TLS_CALLHOME} ${TMPDIR}/sim-o1-ofhmp-interfaces/o-ru-mplane/data
    cp ${CONFIGDIR}/${CUSTOM_O_RU_MPLANE_TLS_NON_CALLHOME} ${TMPDIR}/sim-o1-ofhmp-interfaces/o-ru-mplane/data
    log "## Starting PyNTS simulator ##\n"
    docker compose -f ${TMPDIR}/sim-o1-ofhmp-interfaces/${CUSTOM_O_RU_MPLANE_DC_FILE} up -d 
}

start_sdnr_components() {
    log "## Starting SDNR Components (MARIADB, SDNR, SDNC-WEB) ##\n"
    log "## Copying certs directory to $TMPDIR ##\n"
    cp -r ${CONFIGDIR}/certs ${TMPDIR}
    log "## Copying sdnr directory to $TMPDIR ##\n"
    cp -r ${CONFIGDIR}/sdnr ${TMPDIR}
    log "## Copying sdnc-web directory to $TMPDIR ##\n"
    cp -r ${CONFIGDIR}/sdnc-web ${TMPDIR}
    log "## Copying docker compose file to $TMPDIR ##\n"
    cp ${CONFIGDIR}/${SDNR_COMPONENTS_DC_FILE} ${TMPDIR}
    cd ${TMPDIR}
    docker-compose -f ${TMPDIR}/${SDNR_COMPONENTS_DC_FILE} --env-file ${CONFIGDIR}/.env up -d 
    cd ${ROOTDIR}
}

start_test_suite() {
    python3 ${ROOTDIR}/src/Main.py --logFile ${LOGDIR}/${LOGFILE}
}

#Main
log "## Starting CI/CD ##\n"
cleanup
mkdir $TMPDIR
build_pynts_images
create_docker_network
start_sdnr_components
start_pynts
sleep 20
start_test_suite
