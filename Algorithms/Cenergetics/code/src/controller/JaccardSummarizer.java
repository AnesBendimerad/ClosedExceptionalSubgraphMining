package controller;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.lucene.util.OpenBitSet;

import model.DesignPoint;
import model.Pattern;

public class JaccardSummarizer {

	public static int countToSummarise=10000;
	
	public static ArrayList<Pattern> getSummary(ArrayList<Pattern> initialPatterns,DesignPoint designPoint){
		ArrayList<Pattern> summary=new ArrayList<>();
		int i=0;
		while (i<initialPatterns.size() && ((countToSummarise==-1) || i<designPoint.maxNbPatternsToMineInSummary)){
			if (i%10000==0){
				System.out.println("summary, i="+i);
			}
			Pattern currentP=initialPatterns.get(i);
			boolean covered=false;
			for (int j=summary.size()-1;j>=0;j--){
				Pattern p =summary.get(j);
				if (isJaccardThresholdExceeded(currentP, p,designPoint)){
					covered=true;
					break;
				}
			}
			if (!covered){
				summary.add(currentP);
			}
			i++;
		}
		return summary;
	}

	public static boolean isJaccardThresholdExceeded(Pattern p1, Pattern p2,DesignPoint designPoint){
		double interSize=OpenBitSet.intersectionCount(p1.getSubgraphBitSet(), p2.getSubgraphBitSet());
		double p1Size=p1.getSubgraphBitSet().cardinality();
		double p2Size=p2.getSubgraphBitSet().cardinality();
		if ((interSize/(p1Size+p2Size-interSize))>=designPoint.jaccardValue){
			return true;
		}
		else {
			return false;
		}
	}
	

}
