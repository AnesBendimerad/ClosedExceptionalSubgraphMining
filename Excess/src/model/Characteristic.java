package model;

import java.util.HashMap;

import org.apache.lucene.util.OpenBitSet;

public class Characteristic {
	private DescriptorMetaData descriptorMetaData;
	
	private OpenBitSet sPlusBitSet;
	private OpenBitSet sMinusBitSet;
	private HashMap<Integer,Double> scoreByAttribute;
	private double score;
	private boolean empty;
	public DescriptorMetaData getDescriptorMetaData() {
		return descriptorMetaData;
	}
	public Characteristic(DescriptorMetaData descriptorMetaData, OpenBitSet sPlusBitSet, OpenBitSet sMinusBitSet,
			double score,HashMap<Integer,Double> scoreByAttribute) {
		this.descriptorMetaData = descriptorMetaData;
		this.sPlusBitSet = sPlusBitSet;
		this.sMinusBitSet = sMinusBitSet;
		this.score = score;
		this.scoreByAttribute=scoreByAttribute;
		empty=false;
	}
	
	public Characteristic(Characteristic clonedCharacteristic){
		sPlusBitSet=new OpenBitSet(clonedCharacteristic.getDescriptorMetaData().getAttributesName().length);
		sMinusBitSet=new OpenBitSet(clonedCharacteristic.getDescriptorMetaData().getAttributesName().length);
		sPlusBitSet.or(clonedCharacteristic.getsPlusBitSet());
		sMinusBitSet.or(clonedCharacteristic.getsMinusBitSet());
	}
	public boolean isEmpty() {
		return empty;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	//public void setsPlus(HashSet<Integer> sPlus) {
	//	this.sPlus = sPlus;
	//}
	//public void setsMinus(HashSet<Integer> sMinus) {
	//	this.sMinus = sMinus;
	//}
	public void setsPlusBitSet(OpenBitSet sPlusBitSet) {
		this.sPlusBitSet = sPlusBitSet;
	}
	public void setsMinusBitSet(OpenBitSet sMinusBitSet) {
		this.sMinusBitSet = sMinusBitSet;
	}
	public void setScoreByAttribute(HashMap<Integer, Double> scoreByAttribute) {
		this.scoreByAttribute = scoreByAttribute;
	}
	public void setScore(double score) {
		this.score = score;
	}
	
	public HashMap<Integer, Double> getScoreByAttribute() {
		return scoreByAttribute;
	}

	public void addValueToScore(double value){
		score+=value;
	}
	public void addValueToScoreByAttribute(int attribute,double value){
		scoreByAttribute.put(attribute, scoreByAttribute.get(attribute)+value);
	}
	
//	public HashSet<Integer> getsPlus() {
//		return sPlus;
//	}
//
//	public HashSet<Integer> getsMinus() {
//		return sMinus;
//	}
	
	public OpenBitSet getsPlusBitSet() {
		return sPlusBitSet;
	}
	public OpenBitSet getsMinusBitSet() {
		return sMinusBitSet;
	}
	
	public double getScore() {
		return score;
	}

	public String toJson(String tabulation) {
		String jsonString = tabulation + "{\n";
		jsonString += tabulation + "\t\"descriptorName\" : \"" + descriptorMetaData.getDescriptorName() + "\",\n";
		jsonString += tabulation + "\t\"positiveAttributes\" : [";
		boolean firstInsert = true;
		
		int i = 0;
		int k;
		boolean continu = true;
		
		while (continu) {
			k = sPlusBitSet.nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				if (firstInsert) {
					firstInsert = false;
				} else {
					jsonString += ",";
				}
				jsonString += "\"" + descriptorMetaData.getAttributesName()[k] + "\"";
				i=k+1;
			}
		}
		jsonString += "],\n";
		jsonString += tabulation + "\t\"negativeAttributes\" : [";
		firstInsert = true;
		i=0;
		continu=true;
		while (continu) {
			k = sMinusBitSet.nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				if (firstInsert) {
					firstInsert = false;
				} else {
					jsonString += ",";
				}
				jsonString += "\"" + descriptorMetaData.getAttributesName()[k] + "\"";
				i=k+1;
			}
		}
		jsonString += "],\n";
		jsonString += tabulation + "\t\"score\" : " + score + "\n";
		jsonString += tabulation + "}";
		return jsonString;
	}
}
