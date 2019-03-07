package controller;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.lucene.util.OpenBitSet;

import model.BitSetIterator;
import model.DesignPoint;
import model.Graph;
import model.Pattern;

public class DisjunctiveWraccSummarizer {

	

	public static ArrayList<Pattern> getSummary(Graph graph,ArrayList<Pattern> inputPatterns, DesignPoint designPoint) {
		ArrayList<Pattern> initialPatterns=new ArrayList<>(inputPatterns);
		boolean[][] alreadyCovered=new boolean[graph.getVertices().length][graph.getDescriptorsMetaData().getAttributesName().length];
		for (int i=0;i<alreadyCovered.length;i++) {
			for (int j=0;j<alreadyCovered[i].length;j++) {
				alreadyCovered[i][j]=false;
			}
		}
		
		ArrayList<Pattern> summary = new ArrayList<>();
		while (summary.size()<designPoint.nbSummarizedPatterns && initialPatterns.size()>0) {
			int i=getBest(graph,initialPatterns,designPoint,alreadyCovered,summary.size());
			Pattern p=initialPatterns.get(i);
			if (p.getCharacteristic().getScore()<designPoint.getDelta()) {
				break;
			}
			initialPatterns.remove(i);
			summary.add(p);
			addPatternToModel(p,alreadyCovered);
		}
		
		return summary;
	}
	
	private static void addPatternToModel(Pattern p,boolean[][] alreadyCovered) {
		BitSetIterator iterator=new BitSetIterator(p.getSubgraphBitSet());
		int curIt=0;
		while ((curIt=iterator.getNext())>=0) {
			int curIt2=0;
			BitSetIterator iterator2=new BitSetIterator(p.getCharacteristic().getsPlusBitSet());
			while ((curIt2=iterator2.getNext())>=0) {
				alreadyCovered[curIt][curIt2]=true;
			}
			iterator2=new BitSetIterator(p.getCharacteristic().getsPlusBitSet());
			while ((curIt2=iterator2.getNext())>=0) {
				alreadyCovered[curIt][curIt2]=true;
			}
		}
	}
	
	private static int getBest(Graph graph,ArrayList<Pattern> initialPatterns,DesignPoint designPoint, boolean[][] alreadyCovered,int curUpdId) {
		while (true) {
			Pattern p=initialPatterns.get(0);
			if (p.updateId==curUpdId) {
				break;
			}
			else {
				initialPatterns.remove(0);
				updateScorePattern(p, alreadyCovered, graph, curUpdId);
				int index=Collections.binarySearch(initialPatterns,p,Collections.reverseOrder());
				if (index<0) {
					index*=-1;
					index--;
				}
				initialPatterns.add(index, p);
			}
		}
		return 0;
	}
	
	public static void updateScorePattern(Pattern pattern, boolean[][] alreadyCovered,Graph graph,int curUpdId) {
		// already implemented
		double newScore=0;
		BitSetIterator iterator=new BitSetIterator(pattern.getSubgraphBitSet());
		int curIt=0;
		while ((curIt=iterator.getNext())>=0) {
			int curIt2=0;
			BitSetIterator iterator2=new BitSetIterator(pattern.getCharacteristic().getsPlusBitSet());
			while ((curIt2=iterator2.getNext())>=0) {
				if (!alreadyCovered[curIt][curIt2]) {
					newScore+=graph.getVertices()[curIt].getAttributeDescriptorScore(curIt2);
				}
			}
			iterator2=new BitSetIterator(pattern.getCharacteristic().getsPlusBitSet());
			while ((curIt2=iterator2.getNext())>=0) {
				if (!alreadyCovered[curIt][curIt2]) {
					newScore-=graph.getVertices()[curIt].getAttributeDescriptorScore(curIt2);
				}
			}
		}
		pattern.getCharacteristic().setScore(newScore);
		pattern.updateId=curUpdId;
	}
	
	

	public static boolean isJaccardThresholdExceeded(Pattern p1, Pattern p2, DesignPoint designPoint) {
		double interSize = OpenBitSet.intersectionCount(p1.getSubgraphBitSet(), p2.getSubgraphBitSet());
		double p1Size = p1.getSubgraphBitSet().cardinality();
		double p2Size = p2.getSubgraphBitSet().cardinality();
		if ((interSize / (p1Size + p2Size - interSize)) >= designPoint.jaccardValue) {
			return true;
		} else {
			return false;
		}
	}

}
