# oc_ParserViewer

### Description

The oc_ParserViewer tool allow to generate data and charts from [oc_loadtester](https://github.com/Malinoski/oc_loadtester) through a specific scenario. The scenario has 4 physical nodes, the first has the gatling executor, second has a ownCloud Docker container, the third has a ownCloud OpenStack virtual machine, and the last has a database for file data storage. 

This tool was tested on macOS Sierra 10.12.6, gnuplot 5.2 patchlevel 2 and Java SE 1.8.

### 1 Generate the data

Execute the Shell Script [automatedSimulation.sh](https://github.com/Malinoski/oc_loadtester/blob/master/oc_ParserViewer/scripts/automatedSimulation.sh) to generate gatling data (html, json, etc), ex.: 

`/path/to/simulation.sh [container-url] container /path/to/gatling-result 30 120;`

`/path/to/simulation.sh [vm url] vm /path/to/gatling-result 30 120`

Each example above executes 30 times the [basic simulation](https://github.com/Malinoski/oc_loadtester/blob/master/gatling/examples/MySimulation.scala), in the same directory (`/path/to/gatling-result`) with the following configuration (hard coded in automatedSimulation.sh):

```
Ramp		|Users
60		|100
60		|200
60		|400
60		|600
60		|800
60		|1000
```

### 2 Generate the gnuplot charts

To generate gnuplot charts from previous datas, is necessary generate some CSV files.

#### 2.1 Generate CSV files

Execute the Java file [OcDataGenerator.java](https://github.com/Malinoski/oc_loadtester/blob/master/oc_ParserViewer/src/malinoski/OcDataGenerator.java) to generate the processed CSV files. For example, create an executable jar and execute with the necessary parameters:

`java -jar oc_loader.jar /path/to/gatling-result`

The result are:

`data/gatling-result-brute.csv`

`data/gatling-result-processed-cont.csv`

`data/gatling-result-processed-vm.csv`

The first contains the main information of each simulation, the second and third contains averages and pattern deviations.

#### 2.2 Generate gnuplot charts

Configure the gnuplot script with the processed CSV files, and execute then, ex.:

`gnuplot successRequests.gnu`

`gnuplot timeResponse.gnu`

The result will be like these images:
![alt text](https://github.com/Malinoski/oc_loadtester/blob/master/oc_ParserViewer/data/ro30ra10us100-200-400-600-800-1000-successRequest.png)
![alt text](https://github.com/Malinoski/oc_loadtester/blob/master/oc_ParserViewer/data/ro30ra10us100-200-400-600-800-1000-timeResponse.png)

### Acknowledgements
This development has been funded by [FINEP](http://www.finep.gov.br), the Brazilian Innovation Agency.
