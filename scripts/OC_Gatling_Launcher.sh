#!/bin/bash
##################################################################################################################
#Gatling scale out/cluster run script:
#Before running this script some assumptions are made:
#1) Public keys were exchange inorder to ssh with no password promot (ssh-copy-id on all remotes)
#2) Check  read/write permissions on all folders declared in this script.
#3) Gatling installation (GATLING_HOME variable) is the same on all hosts
#4) Assuming all hosts has the same user name (if not change in script)
##################################################################################################################

#Assuming same user name for all hosts
USER_NAME='iuri'

#Remote hosts list
HOSTS=(10.0.1.43 10.0.1.49)

#Assuming all Gatling installation in same path (with write permissions)
GATLING_HOME=/home/iuri/gatling-charts-highcharts-bundle-2.1.6
GATLING_SIMULATIONS_DIR=$GATLING_HOME/user-files/simulations
GATLING_RUNNER=$GATLING_HOME/bin/gatling.sh

#Change to your simulation class name
SIMULATION_NAME=MySimulation01
SIMULATION_DIR=$GATLING_SIMULATIONS_DIR/OwnCloudSimulation/gatling/

#No need to change this
GATLING_REPORT_DIR=$GATLING_HOME/results/
GATHER_REPORTS_DIR=$GATLING_HOME/reports/

### Cleaning local host

echo "# Cleaning previous runs from localhost"
rm -rf $GATHER_REPORTS_DIR
mkdir $GATHER_REPORTS_DIR
rm -rf $GATLING_REPORT_DIR

## Cleaning remote hosts

for HOST in "${HOSTS[@]}"
do
	echo "# Cleaning previous runs from host: $HOST"
	ssh -n -f $USER_NAME@$HOST "sh -c 'rm -rf $GATLING_REPORT_DIR' "
	ssh -n -f $USER_NAME@$HOST "sh -c 'rm -f $GATLING_HOME/run.log' "
done

for HOST in "${HOSTS[@]}"
do
	echo "# Cleaning previous simulations from host: $HOST"
	ssh -n -f $USER_NAME@$HOST "sh -c 'rm -rf $GATLING_SIMULATIONS_DIR/*' "
done

### Copying simulations to remote hosts

for HOST in "${HOSTS[@]}"
do
	echo "# Copying simulations to host: $HOST"
	scp -r $SIMULATION_DIR* $USER_NAME@$HOST:$GATLING_SIMULATIONS_DIR
done

### Run simulations on remote hosts

for HOST in "${HOSTS[@]}"
do
	echo "# Running simulation on host: $HOST"
ssh -n -f $USER_NAME@$HOST "sh -c 'JAVA_OPTS=\"-DbaseUrl=http://${HOST}\" nohup $GATLING_RUNNER -nr -s $SIMULATION_NAME > $GATLING_HOME/run.log 2>&1 &'"
done

### Waiting simulations finish on remoteHosts

check_process() {
	# $1 process name
	# $2 remote host
	# $3 remote user
	# echo "$ts: checking $1"
	printf "."
	# [ "$1" = "" ]  && return 0
	# [ "$2" = "" ]  && return 0
	# [ "$3" = "" ]  && return 0
	if [ `ssh $3@$2 pgrep -n $1` ] 
	then 
		return 1 # not found
	else 
		printf " process finished."
		return 0 # found
	fi
}

check_report() {
	echo ""
	printf "."
	result=$(ssh $2@$1 "if [ -d $GATLING_REPORT_DIR ] ; then echo 0 ; else echo 1 ; fi")
	if [ $result = 1 ] 
	then
		return 1 # not found
	else 	
		printf " results finished."
		return 0 # found
	fi
}

for HOST in "${HOSTS[@]}"
do
	printf "# Waiting for simulation to finish on host $HOST  "
	# timestamp
	# ts=`date +%T`
	# printf "$ts "  
	echo
	while [ 1 ]; do
		check_process "gatling.sh" $HOST $USER_NAME			
		[ $? -eq 0 ]  && break        
  		sleep 1
	done  
	while [ 1 ]; do
		check_report $HOST $USER_NAME			
		[ $? -eq 0 ]  && break        
  		sleep 1
	done
	echo
	echo "Simulation on host $HOST finished!" 


done

### Get results from remote hosts

mkdir ${GATHER_REPORTS_DIR}reports
for HOST in "${HOSTS[@]}"
do  
	echo "# Gathering result file from host: $HOST"
	ssh -n -f $USER_NAME@$HOST "sh -c 'ls -t $GATLING_REPORT_DIR | head -n 1 | xargs -I {} mv ${GATLING_REPORT_DIR}{} ${GATLING_REPORT_DIR}report'"  
	scp $USER_NAME@$HOST:${GATLING_REPORT_DIR}report/simulation.log ${GATHER_REPORTS_DIR}reports/simulation-$HOST.log
done

### Aggregating simulation on local host
mv $GATHER_REPORTS_DIR $GATLING_REPORT_DIR
echo "# Aggregating simulations"
$GATLING_RUNNER -ro reports

### Show results

#using macOSX
#open ${GATLING_REPORT_DIR}reports/index.html

#using ubuntu
google-chrome ${GATLING_REPORT_DIR}reports/index.html
