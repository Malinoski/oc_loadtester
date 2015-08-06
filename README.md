# oc_loadtester 0.8.2

## Description
The oc_loadtester tool allows customized [Gatling](https://github.com/gatling) simulations to generate load on a single or multiple [ownCloud](https://owncloud.org) servers. 
This tool was tested on Ubuntu 14.04,  Gatling 2.1.6, PostgreSQL 9.3.6, Java 7 and the following ownCloud versions: 5.0.18, 7.0.4, 8.0.3 and 8.1.0.

Some simulation scenarios are provided [here](https://github.com/Malinoski/oc_loadtester/tree/master/gatling/examples).

The script for multiple ownCloud servers (see [here](https://github.com/Malinoski/oc_loadtester/tree/master/scripts)) modifies the original  [script](https://github.com/gatling/gatling/blob/416fb4364d25085bb207121d8b87e05836e8abb3/src/sphinx/cookbook/code/GatlingScalingOut.sh) provided at the Gatling GitHub repository.

### 1 How to use for a single ownCloud server

The oc_loadtester is executed in the same host where ownCloud and Gatling are installed.

**1.1** Create a new user in ownCloud server (in the examples below, the user `testuser` was used). This user will be used by oc_loadtester.

**1.2** Install Gatling, eg.:

`# wget https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/2.1.6/gatling-charts-highcharts-bundle-2.1.6-bundle.zip`  

`# unzip gatling-charts-highcharts-bundle-2.1.6-bundle.zip`

**1.3** Install the oc_loadtester, eg.:

`# wget https://github.com/Malinoski/oc_loadtester/archive/master.zip`  

`# unzip master.zip`

**1.4** Put the scala code folder in Gatling simulation folder, eg.:  

`# cp -r oc_loadtester-master/gatling gatling-charts-highcharts-bundle-2.1.6/user-files/simulations`  

**1.5** Run the oc_loadtester from the ownCloud server host, eg.:  

`# cd gatling-charts-highcharts-bundle-2.1.6`  

`# ./bin/gatling.sh -s MySimulation`  

### 2 How to use for multiple ownCloud servers

The oc_loadtester is executed from a local host and the ownCloud servers are installed in remote hosts. Gatling is installed in the local and remote hosts. When the oc_loadtester is executed, the script (OC_Gatling_Launcher.sh) sends the simulation (all scala code) to each Gatling in remote hosts and executes the simulations remotely. The script waits for the simulations to finish, gets the results and generates the final results through Gatling.

**2.1** Assuming all hosts have the same user name (if not change the script!)

**2.2** Public keys were exchanged in order for ssh to work with no password prompt (ssh-copy-id on all remote hosts)

**2.3** Gatling installation (`GATLING_HOME` and variable) is the same on all hosts

**2.4** Check read/write permissions on all folders declared in this script

**2.5** Assuming all ownCloud servers share the same database and storage server

**2.6** Create a new user in ownCloud server (in the examples below, the user `testuser` was used). This user will be used by oc_loadtester.

**2.7** Install Gatling in each remote host

**2.7** In the local host:

- Install Gatling (see section **1.2**)

- Install oc_loadtester (see section **1.3**)

- Put the Scala code folder in Gatling simulation folder (see section **1.4**)

- Configure the required variables in the `OC_Gatling_Launcher.sh` script: `HOSTS`, `USER_NAME`, `USER_NAME` and `SIMULATION_NAME`
 
**2.8** Run the oc_loadtester from local host, eg.:

`# ./oc_loadtester-master/scripts/OC_Gatling_Launcher.sh`

## Acknowledgements
This development has been funded by [FINEP](http://www.finep.gov.br), the Brazilian Innovation Agency.
