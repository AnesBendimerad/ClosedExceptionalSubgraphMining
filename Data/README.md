This folder contains the real world data used in the experiments. These data can be divided into three groups:

**1 - Foursquare venues**: We preprocessed the data provided in this  [page](https://figshare.com/articles/Foursquare_amp_Flickr_activities_in_20_cities/1584973) in order to have an attributed graph structure. The following folders belong to this group: "BarcelonaVenues", "LondonVenues", "LosAngelesVenues", "NewYorkVenues", "ParisVenues", "RomeVenues", "SanFranciscoVenues", "WashingtonVenues".

**2 - San Francisco Crimes**: We preprocessed the data provided in this [page](https://www.kaggle.com/c/sf-crime/data) in order to have an attributed graph structure. This dataset is in the folder "SanFranciscoCrimes"

**3 - San Francisco Crimes and Venues**: This dataset (named "SanFranciscoCrimesAndVenues") is the merge of San Francisco Foursquare venues and San Francisco Crimes into the same graph.

Each dataset is described by three files:

**1 - metaData.txt**: This file specifies information about the graph structure (number of vertices, number of attributes, number of edges, total sum of all the attributes)

**2 - The graph file**: This contains the attributed graph. It is a JSON file that defines the structure of the graph and the vertex descriptions. The listing below is a simple example of a graph file.
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
First, this file presents information about the vertex descriptors (descriptorName and attributesName). Second, it defines the vertices and their attribute values. Third, it specifies the edges of the graph.

**3 - The geographic coordinates file**: In the graph file, each vertex has a unique identifier. The geographic coordinates file specifies for each vertex identifier its location. The location is given as a polygon (a list of latitude longitude points).
