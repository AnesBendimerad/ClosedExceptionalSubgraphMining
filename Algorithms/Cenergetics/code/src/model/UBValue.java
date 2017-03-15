package model;

import java.util.HashMap;

public class UBValue {
	private double value;
	private HashMap<Integer,Double> valuePerVertex;

	public UBValue(boolean initValues){
		if (initValues){
			value=0;
			valuePerVertex=new HashMap<>();
		}
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public HashMap<Integer, Double> getValuePerVertex() {
		return valuePerVertex;
	}
	
	public void incrementTotalValue(double valueToAdd){
		value+=valueToAdd;
	}
	public void incrementValuePerVertex(int vertexIndex,double value){
		if (valuePerVertex.containsKey(vertexIndex)){
			valuePerVertex.put(vertexIndex, valuePerVertex.get(vertexIndex)+value);
		}
		else {
			valuePerVertex.put(vertexIndex,value);
		}
	}
	

}
