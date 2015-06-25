#!/bin/bash
GATLING_HOME=/home/iuri/gatling-charts-highcharts-bundle-2.1.6
GATLING_SIMULATIONS_DIR=$GATLING_HOME/user-files/simulations
GATLING_RUNNER=$GATLING_HOME/bin/gatling.sh

#Change to your simulation class name
SIMULATION_NAME='OwnCloudSimulation'

#No need to change this
GATLING_REPORT_DIR=$GATLING_HOME/results/
GATHER_REPORTS_DIR=$GATLING_HOME/reports/

echo $GATLING_REPORT_DIR/reports/simulation.log
result=$(ssh iuri@10.0.1.43  "if [ -f $GATLING_REPORT_DIR/reports/simulation.log ] ; then echo 0 ; else echo 1 ; fi")
if [ $result = 1 ]
	then echo "found"
	else echo "NOT found"
fi


