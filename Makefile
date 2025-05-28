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

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------
ACTIVATE = . .venv/bin/activate

# -------------------------------------------------------------------------
# Phony targets
# -------------------------------------------------------------------------
.PHONY: system-check docs hello help

# -------------------------------------------------------------------------
# [Install & Setup]
# -------------------------------------------------------------------------

install: install-pip system-check ## [Install] Install necessary tools

venv: ## [Install] Set up Python virtual environment
	@echo "## Creating Python virtual environment..."
	python3 -m venv .venv

install-pip: ## [Install] Install Python dependencies
	@echo "## Installing Python dependencies..."
	$(ACTIVATE) && pip install -r docs/requirements-docs.txt

docs: ## [Docs] Shows the docker status
	@echo "Gererate READ_THE_DOCS output"
	tox -e docs,docs-linkcheck


# -------------------------------------------------------------------------
# [System]
# -------------------------------------------------------------------------

docker-status: ## [System] Shows the docker status
	@docker ps -a --format "table {{.Names}}\t{{.Image}}\t{{.Status}}"

system-check: hello ## [System] System overview
	@echo "## System Overview:"
	@df -hT
	@free -h
	@grep -c ^processor /proc/cpuinfo
	@cat /etc/os-release | grep -i pretty
	@docker version
	@docker compose version
	@git --version
	@python3 --version
	@go version
	@timedatectl
	@echo "Host: ${HOSTNAME}"

# -------------------------------------------------------------------------
# [Help]
# -------------------------------------------------------------------------

TEXT_FILE := banner.txt
hello: ## [Help] Displays a banner
	@echo "Reading lines from $(TEXT_FILE):"
	@while IFS= read -r line; do \
		echo "$$line"; \
	done < $(TEXT_FILE)

help: hello ## [Help] Show this help
	@echo ""
	@echo "Available Make targets:"
	@awk 'BEGIN {FS = ":.*?## "}; /^[a-zA-Z_-]+:.*?## / { \
		split($$2, parts, "] "); \
		group = substr(parts[1], 2); \
		desc = parts[2]; \
		if (group != last_group) { \
			printf "\n\033[1m[%s]\033[0m\n", group; \
			last_group = group; \
		} \
		printf "  \033[36m%-25s\033[0m %s\n", $$1, desc \
	}' $(MAKEFILE_LIST)
	@echo ""
