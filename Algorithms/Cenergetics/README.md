## Introduction
The algorithm CENERGETICs is the complete approach of closed exceptional attributed sub-graph mining (Section 3-1).  

The folder "Release" contains the runnable JAR file. This can be directly executed. The folder "Code" contains the source code of the algorithm.  The compilation of this algorithm requires some libraries which are provided in "Code/lib".

## Inputs of CENERGETICs
CENERGETICs requires two input files. These files must be in the same repository as the runnable JAR. These files are described in what follows.

**1 - The graph file**: This contains the graph the algorithm will mine. It is a JSON file that defines the structure of the graph and the vertex descriptions. The listing below is a simple example of a graph file.
```json
{
	"descriptorName": "FoursquareVenues",	
	"attributesName": ["Health","Tourism","Store","Food","Industry"],
	"vertices" : [
		{
			"vertexId" : "V1",
			"descriptorsValues" :[12,5,4,1,4]
		},
		{
			"vertexId" : "V2",
			"descriptorsValues" :[12,4,4,6,6]
		},
		{
			"vertexId" : "V3",
			"descriptorsValues" :[4,13,6,6,5]
		}
	],
	"edges" : [
		{
			"vertexId" :"V1",
			"connected_vertices" : ["V2","V3"]
		},
		{
			"vertexId" :"V2",
			"connected_vertices" : ["V1"]
		}
	]
}
```

**2 - Parameters file**: This file contains the values of the algorithm parameters. We will suppose that it is named "parameters.txt". The listing below shows an example of a file that specifies the parameters. If a parameter is not specified, the algorithm will use its default value. 
```
inputFilePath=NYCFoursquareGraph.json
resultFolderPath=results
delta=0.01
sigma=1
activateUB=true
activateSMinus=false
activateSPlus=true
activateFailFirstPrinciple=true
```
These parameters are explained in what follows:
- inputFilePath: The path of the graph file.
- resultFolderPath: The path of the results folder
- delta: The WRAcc threshold.
- sigma: The subgraph size threshold.
- activateUB: This determines whether the upper bound UB is enabled or not.
- activateSMinus: Whether we mine negative characteristics.
- activateSPlus: Whether we mine positive characteristics.
- activateCharacFailFirstPrinciple: Whether the algorithm uses the First Fail Principle in the generation of characteristics or not.

## How to launch ENERGETICS
In order to execute the algorithm, the files provided in "Release" can be used. Then, the following command line can be used in this folder:
```
java -jar ENERGETICS.jar parameters.txt
```

So we must specify the parameters file path in the command line.

## The result files
When the program finishes the execution, it produces two files.

**1 - statistics.txt**: This file contains some information about the execution, as the execution time.

**2 - retrievedPatternsFile.json**: This is a JSON file that contains the retrieved patterns. The listing below shows an example of "retrievedPatternsFile.json".

```json
{
	"numberOfPatterns" :2,
	"patterns" : [
		{
			"subgraph" : ["V1","V2"],
			"characteristic" : 
			{
				"descriptorName" : "San Francisco Crimes",
				"positiveAttributes" : ["Vandalism","larceny"],
				"negativeAttributes" : ["Vehicle theft"],
				"score" : 0.0154
			}
		},
		{
			"subgraph" : ["V3"],
			"characteristic" : 
			{
				"descriptorName" : "Place Types",
				"positiveAttributes" : ["larceny"],
				"negativeAttributes" : [],
				"score" : 0.0134
			}
		},
	]
}
```