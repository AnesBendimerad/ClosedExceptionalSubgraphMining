package controller;

import java.util.ArrayList;

import org.apache.lucene.util.OpenBitSet;

import model.DesignPoint;
import model.Graph;
import model.Pattern;

public class CoverageManager {
	
	
	public static boolean isSpaceCovered(Graph graph,Pattern pattern, OpenBitSet candidatesVertices,
			ArrayList<Pattern> patterns, DesignPoint designPoint) {
		if (pattern.getSubgraphBitSet() == null || pattern.getSubgraphBitSet().isEmpty()) {
			return false;
		}
		
		// We first calculate Sm(X U Y)
		OpenBitSet sXUYPlusSet=new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
		sXUYPlusSet.or(pattern.getCharacteristic().getsPlusBitSet());
		OpenBitSet sXUYMinusSet=new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
		sXUYMinusSet.or(pattern.getCharacteristic().getsMinusBitSet());
		int k;
		int i=0;
		boolean continu=true;
		while (continu) {
			k = candidatesVertices.nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				if (!sXUYPlusSet.isEmpty()){
					sXUYPlusSet.and(graph.getVertices()[k].getPositiveAttributesBitSet());
				}
				if (!sXUYMinusSet.isEmpty()){
					sXUYMinusSet.and(graph.getVertices()[k].getNegativeAttributesBitSet());
				}
				if (sXUYPlusSet.isEmpty() && sXUYMinusSet.isEmpty()){
					break;
				}
				i=k+1;
			}
		}
		for (Pattern retPattern : patterns) {
			if (isRetPatternCoversSpace(graph,pattern, candidatesVertices, retPattern, designPoint,sXUYPlusSet,sXUYPlusSet)) {
				return true;
			}
		}
		return false;

	}

	private static boolean isRetPatternCoversSpace(Graph graph,Pattern pattern, OpenBitSet candidatesVertices,
			Pattern retPattern, DesignPoint designPoint,OpenBitSet sXUYPlusSet,OpenBitSet sXUYMinusSet) {
		// we first test vertices coverage:
		double XInterKrSet=OpenBitSet.intersectionCount(pattern.getSubgraphBitSet(), retPattern.getSubgraphBitSet());
		
		double YDifKrSet=OpenBitSet.andNotCount(candidatesVertices, retPattern.getSubgraphBitSet());
		double vCoverage=((double)XInterKrSet)/((double)(pattern.getSubgraphBitSet().cardinality()+YDifKrSet));
		if (vCoverage<designPoint.getMinCov()){
			return false;
		}
		// we second test characteristics coverage:
		OpenBitSet SDifSrPlusSet=new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
		SDifSrPlusSet.or(pattern.getCharacteristic().getsPlusBitSet());
		OpenBitSet SDifSrMinusSet=new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
		SDifSrMinusSet.or(pattern.getCharacteristic().getsMinusBitSet());
		
		SDifSrPlusSet.andNot(retPattern.getCharacteristic().getsPlusBitSet());
		
		SDifSrMinusSet.andNot(retPattern.getCharacteristic().getsMinusBitSet());
		
		double SDifSrSize=(double)(SDifSrPlusSet.cardinality()+SDifSrMinusSet.cardinality());
		if (SDifSrSize==0){
			return true;
		}
		double SmAndSrPlusSet=OpenBitSet.intersectionCount(sXUYPlusSet, retPattern.getCharacteristic().getsPlusBitSet());
		double SmAndSrMinusSet=OpenBitSet.intersectionCount(sXUYMinusSet, retPattern.getCharacteristic().getsMinusBitSet());
		double numerator = (double)(SmAndSrPlusSet+SmAndSrMinusSet);
		
		SDifSrPlusSet.or(sXUYPlusSet);
		SDifSrMinusSet.or(sXUYMinusSet);
		double denominator =(double)(SDifSrPlusSet.cardinality()+SDifSrMinusSet.cardinality()); 
		double sCoverage=numerator/denominator;
		if (sCoverage<designPoint.getMinCov()){
			return false;
		}
		return true;
	}

	public static boolean isPatternCovered(Graph graph,Pattern pattern, ArrayList<Pattern> patterns, DesignPoint designPoint) {
		if (pattern.getSubgraphBitSet() == null || pattern.getSubgraphBitSet().isEmpty()) {
			return false;
		}
		for (Pattern retPattern : patterns) {
			if (isRetPatternCoversPattern(graph,pattern, retPattern, designPoint)) {
				throw new RuntimeException("probleme");
				//return true;
			}
		}
		return false;
	}

	private static boolean isRetPatternCoversPattern(Graph graph,Pattern pattern, Pattern retPattern, DesignPoint designPoint) {
		double subgraphSet=OpenBitSet.intersectionCount(pattern.getSubgraphBitSet(), retPattern.getSubgraphBitSet());
		double vCoverage=((double)subgraphSet)/((double)pattern.getSubgraphBitSet().cardinality());
		if (vCoverage<designPoint.getMinCov()){
			return false;
		}
		double sPlusIntersectionSet=OpenBitSet.intersectionCount(pattern.getCharacteristic().getsPlusBitSet(),retPattern.getCharacteristic().getsPlusBitSet());
		double sMinusIntersectionSet=OpenBitSet.intersectionCount(pattern.getCharacteristic().getsMinusBitSet(),retPattern.getCharacteristic().getsMinusBitSet());
		double sCoverage=((double)(sPlusIntersectionSet+sMinusIntersectionSet))/
				((double)(pattern.getCharacteristic().getsPlusBitSet().cardinality()+pattern.getCharacteristic().getsMinusBitSet().cardinality()));
		if (sCoverage<designPoint.getMinCov()){
			return false;
		}
		return true;
		
	}
}








