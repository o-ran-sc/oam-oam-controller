# Operation and Maintenance (OAM) Controller

This repository contains code artifctes related to an OAM Controller, which
terminates the O-RAN Alliance Interfaces O1 and OpenFronthaul Management Plane.

## Get stated

Update your ubuntu 24.04 system and clone the OAM Controller repository.

```bash
sudo apt update
sudo apt upgrade -y
sudo apt install make
sudo apt install apache2-utils
mkdir -p ~/workspace && cd ~/workspace

## Get an overview of the system
git clone "https://gerrit.o-ran-sc.org/r/oam/oam-controller" && (cd "oam-controller" && mkdir -p `git rev-parse --git-dir`/hooks/ && curl -Lo `git rev-parse --git-dir`/hooks/commit-msg https://gerrit.o-ran-sc.org/r/tools/hooks/commit-msg && chmod +x `git rev-parse --git-dir`/hooks/commit-msg)
cd oam-controller
make system-check
make install
source .venv/bin/activate
```

## Build

// to be updated

## Deploy

// to be updated

## Documentation

The project documentation link is:
https://docs.o-ran-sc.org/projects/o-ran-sc-oam/en/latest/index.html?highlight=Operation%20and%20mainetance

To generate the documentation the project must be cloned and the following
command must be executed:
```
make docs
```

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

# Important Info
- oam-controller is using parts from onap ccsdk project
- "Fork" reference is ccsdk-parent 3.1.1: https://gerrit.onap.org/r/gitweb?p=ccsdk/parent.git;a=tag;h=ef4a562efe380089eb8fd34e768119f8124a506c

# Github Workflows
- Github workflows will be used instead of Jenkins for CI/CD
- 2 Github workflows are added for building the code
- GERRIT_PROJECT needs special handling due to the repo naming convention
- Test commit to check GHA triggering
- Test commit to check GHA triggering
