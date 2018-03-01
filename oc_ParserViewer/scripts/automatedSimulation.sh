#!/bin/bash

echo ''
echo '###################'
echo 'Started:'
echo '###################'
echo ''

# Simulation Conf.

# Conf1
users[0]=100
users[1]=200
users[2]=400
users[3]=600
users[4]=800
users[5]=1000

# Conf2
#users[0]=800
#users[1]=1000

simulationConfCount=${#users[@]}

# URL, ex. (container and vm): http://10.40.0.2:84 http://146.134.226.151  
url=$1

# Machine, ex.: "vm" or "container"
machineType=$2

# Destiny path result, ex.:/srv/www/htdocs/gatling-results_v5
testPath=$3
mkdir -p $testPath

# Run Times (how many times each simulation conf will run)
runTime=$4

# Ramp time
ramp=$5

for (( i=0; i<$simulationConfCount; i++ )) do

        echo "-----------------------------------"      
        echo "- i:  $i"
        echo "- Ramp:  $ramp"
        echo "- Users: ${users[$i]}"
        echo "- URL:   $url"
        echo "" 

        JAVA_OPTS="-Dramp=$ramp -Dusers=${users[$i]} -Durl=$url"
        path="$testPath/r$ramp/u${users[$i]}/$machineType"
        mkdir -p $path;

        for (( j=1; j<=$runTime; j++ )) do

                # Restart db
                echo "{$j}"
                echo "Restarting DB..."
                ssh admin-cloud1 /etc/init.d/postgresql restart
                ssh admin-cloud1 /home/operator/owncloudDatabaseReset.sh

                # Restart machine
                if [ $machineType = "container" ]
                then
                    echo "Destroying and creating the container...."
                    ssh compute1 'docker stop owncloud-apache-php; docker rm owncloud-apache-php; docker run -tid -p 84:80 --memory="2048m" --memory-swap="2048m" --memory-swappiness="0" --cpus="1" --name="owncloud-apache-php" owncloud-apache-php:v2;';
                elif [ "$machineType" = "vm" ]
                then
                    echo "Restarting VM..."
                    ssh admin-cloud1 'source /root/admin-openrc.sh; openstack server reboot --hard --wait owncloud-v4'

                    echo "Waiting vm getting up:"
                    isDown=true;
                    while $isDown; do

                        if curl -s "$url/owncloud/index.php" | grep "DOCTYPE"
                        then
                                echo "vm is up!"
                                isDown=false;
                        else
                                echo "vm is down... "
                                sleep 3;
                        fi
                    done
                else
                    echo "Machine: ERRO!"
                    break
                fi

                # Run simulation
                echo "Running simulation to : $path"
                JAVA_OPTS="-Dramp=$ramp -Dusers=${users[$i]} -Durl=$url" /home/iuri/gatling/gatling-charts-highcharts-bundle-2.1.6/bin/gatling.sh -s MySimulation -rf $path;
        done
done