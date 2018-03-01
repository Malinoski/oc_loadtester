# oc_ParserViewer

### Description

The oc_ParserViewer tool allow to generate data and charts from [oc_loadtester](https://github.com/Malinoski/oc_loadtester) through a specific scenario. The scenario has 4 physical nodes, the first has the gatling executor, second has a ownCloud Docker container, the third has a ownCloud OpenStack virtual machine, and the last has a database for file data storage. 

This tool was tested on macOS Sierra 10.12.6, gnuplot 5.2 patchlevel 2 and Java SE 1.8.

### 1 Generate data

Execute the Shell Script [automatedSimulation.sh](https://github.com/Malinoski/oc_loadtester/blob/master/oc_ParserViewer/scripts/automatedSimulation.sh) to generate gatling data (html, json, etc). Ex.: 

`./simulation.sh http://146.134.226.151 vm /srv/www/htdocs/gatling-results_v8 30 60;`

`./simulation.sh http://10.40.0.2:84 container /srv/www/htdocs/gatling-results_v8 30 60`

Each example above executes 30 times the [basic simulation](https://github.com/Malinoski/oc_loadtester/blob/master/gatling/examples/MySimulation.scala) with the following configuration:

```
Ramp		|Users
60		|100
60		|200
60		|400
60		|600
60		|800
60		|1000
```

Note: 100/200/400/600/800/1000 users are hard coded in the Shell Script.

### 2 Parse the data

Execute the Java file [OcDataGenerator.java](https://github.com/Malinoski/oc_loadtester/blob/master/oc_ParserViewer/src/malinoski/OcDataGenerator.java) to generate the processed CSV file from the early step.

### 3 Generate the gnuplot charts

Execute the Gnupot Scripts for average success requests ([averageSuccessRequests.gnu](https://github.com/Malinoski/oc_loadtester/blob/master/oc_ParserViewer/gnuplot/averageSuccessRequests.gnu)) and  average time response ([averageTimeResponse.gnu](https://github.com/Malinoski/oc_loadtester/blob/master/oc_ParserViewer/gnuplot/averageTimeResponse.gnu))

Average success requests sample:
`mac:gnuplot iuri$ gnuplot averageTimeResponse.gnu`
![alt text](https://github.com/Malinoski/oc_loadtester/blob/master/oc_ParserViewer/data/averageSuccess.svg)

Average time response sample:
`mac:gnuplot iuri$ gnuplot averageTimeResponse.gnu`
![alt text](https://github.com/Malinoski/oc_loadtester/blob/master/oc_ParserViewer/data/averageTimeResponse.svg)

### Acknowledgements
This development has been funded by [FINEP](http://www.finep.gov.br), the Brazilian Innovation Agency.
