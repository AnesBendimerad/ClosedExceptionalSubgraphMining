package model;

import java.util.HashMap;

import org.apache.lucene.util.OpenBitSet;

public class Pattern {
	private OpenBitSet subgraphBitSet;
	private OpenBitSet neighbors;
	private Characteristic characteristic;

//	public Pattern(OpenBitSet subgraphBitSet, DescriptorMetaData descriptorMetaData, OpenBitSet sPlusSet,
//			OpenBitSet sMinusSet, double score,HashMap<Integer,Double> scoreByAttribute) {
//		this.subgraphBitSet = subgraphBitSet;
//		characteristic = new Characteristic(descriptorMetaData, sPlusSet, sMinusSet, score,scoreByAttribute);
//	}
	public OpenBitSet getNeighbors() {
		return neighbors;
	}
	public Pattern(Pattern pattern,int sizeOfVertices){
		if (pattern.getSubgraphBitSet()==null){
			this.subgraphBitSet=null;
			this.characteristic=null;
			this.neighbors=null;
		}
		else {
			this.subgraphBitSet=new OpenBitSet(sizeOfVertices);
			subgraphBitSet.or(pattern.getSubgraphBitSet());
			this.neighbors=new OpenBitSet(sizeOfVertices);
			this.neighbors.or(pattern.getNeighbors());
			OpenBitSet sPluSet=new OpenBitSet(pattern.getCharacteristic().getDescriptorMetaData().getAttributesName().length);
			sPluSet.or(pattern.getCharacteristic().getsPlusBitSet());
			OpenBitSet sMinusSet=new OpenBitSet(pattern.getCharacteristic().getDescriptorMetaData().getAttributesName().length);
			sMinusSet.or(pattern.getCharacteristic().getsMinusBitSet());
			HashMap<Integer,Double> scoreByAttribute=new HashMap<>(pattern.getCharacteristic().getScoreByAttribute());
			this.characteristic=new Characteristic(pattern.getCharacteristic().getDescriptorMetaData(), sPluSet, sMinusSet, pattern.getCharacteristic().getScore(), scoreByAttribute);
		}
	}

	public Pattern(){
		subgraphBitSet=null;
		characteristic=null;
		neighbors=null;
	}
	
	//public void setSubgraph(HashSet<Integer> subgraph) {
	//	this.subgraph = subgraph;
	//}
	public void setNeighbors(OpenBitSet neighbors) {
		this.neighbors = neighbors;
	}
	
	public void setSubgraphBitSet(OpenBitSet subgraphBitSet) {
		this.subgraphBitSet = subgraphBitSet;
	}
	public OpenBitSet getSubgraphBitSet() {
		return subgraphBitSet;
	}
	
	public void setCharacteristic(Characteristic characteristic) {
		this.characteristic = characteristic;
	}
	
	//public HashSet<Integer> getSubgraph() {
	//	return subgraph;
	//}
	public Characteristic getCharacteristic() {
		return characteristic;
	}

	public String toJson(String tabulation,Graph graph) {
		String jsonString = tabulation + "{\n";
		jsonString += tabulation + "\t\"subgraph\" : [";
		boolean firstInsert = true;
		
		int i = 0;
		int k;
		boolean continu = true;
		
		while (continu) {
			k = subgraphBitSet.nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				if (firstInsert) {
					firstInsert = false;
				} else {
					jsonString += ",";
				}
				Vertex v=graph.getVertices()[k];
				jsonString += "\"" + v.getId() + "\"";
				i=k+1;
			}
		}
		
		jsonString += "],\n";
		jsonString += tabulation + "\t\"characteristic\" : \n";
		jsonString += characteristic.toJson(tabulation + "\t");
		jsonString += "\n";
		jsonString += tabulation + "}";
		return jsonString;
	}
}
