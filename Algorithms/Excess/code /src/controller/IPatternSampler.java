package controller;

import java.util.ArrayList;

import model.DesignPoint;
import model.Graph;
import model.Pattern;
import model.Statistics;

public interface IPatternSampler {
	
	DesignPoint getDesignPoint();
	Statistics getStatistics();
	ArrayList<Pattern> getPatterns();
	Graph getGraph();
	void samplePatterns();

}
