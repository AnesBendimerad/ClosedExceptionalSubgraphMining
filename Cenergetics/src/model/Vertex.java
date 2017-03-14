package model;

import java.util.HashSet;

import org.apache.lucene.util.OpenBitSet;

public class Vertex {
	private String id;
	private int indexInGraph;
	private double[] descriptorsValues = null;
	private double descriptorsTotal =0;
	private double[] descriptorsScores = null;
	private OpenBitSet neighborsBitSet;
	private OpenBitSet positiveAttributesBitSet;
	private OpenBitSet negativeAttributesBitSet;
	private HashSet<Integer> setOfNeighborsId;
	//private HashSet<Integer> positiveAttributes;
	//private HashSet<Integer> negativeAttributes;

	public Vertex(String id, double[] attributesValues) {
		this.id = id;
		this.descriptorsValues = attributesValues;		
		double sum = 0;
		for (int j = 0; j < attributesValues.length; j++) {
			sum += attributesValues[j];
		}
		descriptorsTotal = sum;
		setOfNeighborsId = new HashSet<>();
		
	}
	public Vertex(String id, int indexInGraph,double [] descriptorsValues,double descriptorsTotal,HashSet<Integer> neighborsSet){
		this.id=id;
		this.indexInGraph=indexInGraph;
		this.descriptorsValues=descriptorsValues;
		this.descriptorsTotal=descriptorsTotal;
		this.setOfNeighborsId=neighborsSet;
	}
	//public HashSet<Integer> getPositiveAttributes() {
	//	return positiveAttributes;
	//}
	//public HashSet<Integer> getNegativeAttributes() {
	//	return negativeAttributes;
	//}
	public void setIndexInGraph(int indexInGraph) {
		this.indexInGraph = indexInGraph;
	}

	public int getIndexInGraph() {
		return indexInGraph;
	}

	public HashSet<Integer> getSetOfNeighborsId() {
		return setOfNeighborsId;
	}

	public void setupNeighborsIds(int sizeOfGraph) {
		neighborsBitSet = new OpenBitSet(sizeOfGraph);
		for (int neighborId : setOfNeighborsId) {
			neighborsBitSet.fastSet(neighborId);
		}
	}
	public OpenBitSet getNeighborsBitSet() {
		return neighborsBitSet;
	}
	public String getId() {
		return id;
	}

	public double getDescriptorValue( int attributeIndex) {
		return descriptorsValues[attributeIndex];
	}

	public double getDescriptorTotal() {
		return descriptorsTotal;
	}

	public void setDescriptorsScores(double[] scores) {
		descriptorsScores = scores;
		//positiveAttributes=new HashSet<>();
		//negativeAttributes=new HashSet<>();
		positiveAttributesBitSet=new OpenBitSet(scores.length);
		negativeAttributesBitSet=new OpenBitSet(scores.length);
		for (int i=0;i<scores.length;i++){
			if (scores[i]>0){
				//positiveAttributes.add(i);
				positiveAttributesBitSet.fastSet(i);
			}
			if (scores[i]<0){
				//negativeAttributes.add(i);
				negativeAttributesBitSet.fastSet(i);
			}
		}
	}

	public OpenBitSet getPositiveAttributesBitSet() {
		return positiveAttributesBitSet;
	}
	public OpenBitSet getNegativeAttributesBitSet() {
		return negativeAttributesBitSet;
	}
	
	public double getAttributeDescriptorScore( int attributeId) {
		return descriptorsScores[attributeId];
	}

	@Override
	public boolean equals(Object obj) {
		return id.equals(((Vertex) obj).getId());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
