# oc_loadtester 0.7

## Description
The oc_loadtester allow to perform custom Gatling simulations to generate some load on a single or multiple ownCloud servers. The oc_loadtester was tested for Ubuntu 14.04,  Gatling 2.1.6, ownCloud 7.0.4, PostgreSQL 9.3.6 and Java 7.

The simulation was written in Scala (localizated in folder ./scala). The API (./scala/OwnCloudSimulation.scala) is used by custom scenarios (for example: ./scala/MySimulation01.scala and ./scala/MySimulation02.scala)

The script for multiple ownCloud simulation (./scripts/OC_Gatling_Launcher.sh) modifies the original [script](https://github.com/gatling/gatling/blob/416fb4364d25085bb207121d8b87e05836e8abb3/src/sphinx/cookbook/code/GatlingScalingOut.sh)

## Instalation
[TODO]

## Acknowledgements
This development has been funded by [FINEP](http://www.finep.gov.br), the Brazilian Innovation Agency.
