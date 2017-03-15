package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.lucene.util.OpenBitSet;

import model.Characteristic;
import model.DesignPoint;
import model.Graph;
import model.Pattern;
import model.Statistics;
import model.Vertex;

public class PatternSampler implements IPatternSampler {
	private DesignPoint designPoint;
	private Graph graph;
	private MeasureComputer measureComputer;
	private ArrayList<Pattern> patterns;
	private Pattern[] patternsSingletonBased;
	private double[] probabilityDistribution;
	private Statistics statistics;
	private Random random = new Random();
	private OpenBitSet intermediateSet1;
	private OpenBitSet intermediateSet2;
	OpenBitSet candidates;
	OpenBitSet rest;
	long startTime;
	long stopTime;
	public PatternSampler(DesignPoint designPoint, MeasureComputer measureComputer) {
		this.designPoint = designPoint;
		this.graph = measureComputer.getGraph();
		this.measureComputer = measureComputer;
		statistics = new Statistics();
		intermediateSet1 = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
		intermediateSet2 = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
		rest = new OpenBitSet(graph.getVertices().length);
		candidates = new OpenBitSet(graph.getVertices().length);
	}

	public DesignPoint getDesignPoint() {
		return designPoint;
	}

	public Graph getGraph() {
		return graph;
	}

	public ArrayList<Pattern> getPatterns() {
		return patterns;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public void samplePatterns() {
		patterns = new ArrayList<>();
		statistics.nbRecursiveCalls = 0;
		statistics.nbPatternVerified = 0;
		startTime = System.currentTimeMillis();
		int nbContrastedVertices = measureComputer.processVertexMeasures(designPoint);
		// calculate closed generators (based on singleton subgraphs) with their
		// scores
		createPatternsSingletonBased(nbContrastedVertices);

		// while loop that calls the method sampleOnePattern
		boolean continu = true;
		while (continu) {
			Pattern p = sampleOnePattern();
			statistics.nbPatternVerified++;
			if (p.getCharacteristic().getScore() >= designPoint.getDelta()
					&& p.getSubgraphBitSet().cardinality() >= designPoint.getSigma()
					&& p.getCharacteristic().getScore() > 0) {
				if (p.getCharacteristic().getsPlusBitSet().cardinality() > 0
						|| p.getCharacteristic().getsMinusBitSet().cardinality() > 0) {
					if (!isReallyValid(p)){
						throw new RuntimeException(" not really valid");
					}
					patterns.add(p);
				}

			}
			stopTime = System.currentTimeMillis();
			if (stopTime - startTime >= designPoint.getSamplingTimeInMS()) {
				continu = false;
				statistics.patternMiningTimeInMS = stopTime - startTime;
			}
		}
		if (designPoint.isRemoveRepetition()) {
			long interCount=0;
			ArrayList<Pattern> cleanedPatterns = new ArrayList<>();
			for (Pattern p : patterns) {
				boolean cleaned = true;
				for (Pattern cP : cleanedPatterns) {
					interCount=OpenBitSet.intersectionCount(p.getSubgraphBitSet(), cP.getSubgraphBitSet());
					
					if (interCount==p.getSubgraphBitSet().cardinality() && interCount==cP.getSubgraphBitSet().cardinality()) {
						cleaned = false;
						break;
					}
				}
				if (cleaned) {
					cleanedPatterns.add(p);
				}
			}
			patterns = cleanedPatterns;
		}
	}

	Pattern sampleOnePattern() {
		Pattern p = generatePatternFromBegin();
		int currentSize;
		do {
			currentSize = (int) p.getSubgraphBitSet().cardinality();
			p = sampleFromNextOfP(p);
			stopTime = System.currentTimeMillis();
			if (stopTime - startTime >= designPoint.getSamplingTimeInMS()) {
				break;
			}

		} while (p.getSubgraphBitSet().cardinality() > currentSize);
		return p;
	}

	private Pattern generatePatternFromBegin() {
		double randValue = random.nextDouble();
		int i = 0;
		while (i < patternsSingletonBased.length && probabilityDistribution[i] < randValue) {
			i++;
		}
		return new Pattern(patternsSingletonBased[i], graph.getVertices().length);
	}

	private Pattern sampleFromNextOfP(Pattern p) {

		candidates.clear(0, graph.getVertices().length);
		candidates.or(p.getNeighbors());
		candidates.andNot(p.getSubgraphBitSet());
		Pattern[] candidatesPatterns = new Pattern[(int) candidates.cardinality() + 1];
		double[] candidatesScores = new double[(int) candidates.cardinality() + 1];

		candidatesPatterns[0] = p;
		candidatesScores[0] = p.getCharacteristic().getScore();
		int curIndex = 1;
		int i = 0;
		int k;
		boolean continu = true;
		double totalScore = candidatesScores[0];
		while (continu) {
			k = candidates.nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				stopTime = System.currentTimeMillis();
				if (stopTime - startTime >= designPoint.getSamplingTimeInMS()) {
					return p;
				}
				Pattern candPat = new Pattern(p, graph.getVertices().length);
				addVertexAndClosePattern(candPat, graph.getVertices()[k]);
				candidatesPatterns[curIndex] = candPat;
				candidatesScores[curIndex] = candPat.getCharacteristic().getScore();
				totalScore += candPat.getCharacteristic().getScore();
				curIndex++;
				i = k + 1;
				if (candPat.getSubgraphBitSet().cardinality() >= designPoint.getSigma()) {
					if (candPat.getCharacteristic().getScore() >= designPoint.getDelta()
							&& candPat.getCharacteristic().getScore() > 0) {
						if (candPat.getCharacteristic().getsPlusBitSet().cardinality() > 0
								|| candPat.getCharacteristic().getsMinusBitSet().cardinality() > 0) {
							if (!isReallyValid(candPat)){
								HashSet<String> verticesIdsOfP=new HashSet<>();
								int a=0;
								int b;
								boolean continu2=true;
								while (continu2){
									b=p.getSubgraphBitSet().nextSetBit(a);
									if (b<0){
										continu2=false;
									}
									else {
										verticesIdsOfP.add(graph.getVertices()[b].getId());
										a=b+1;
									}
								}
								throw new RuntimeException(" not really valid");
							}
							patterns.add(candPat);
						}

					}
				}
			}
		}
		double randValue = random.nextDouble();
		i = 0;
		continu = true;
		double currentValue = 0;
		double res = 0;
		while (continu) {
			currentValue += candidatesScores[i];
			res = currentValue / totalScore;
			if (res < randValue) {
				i++;
			} else {
				continu = false;
			}
		}
		return candidatesPatterns[i];

	}

	private void createPatternsSingletonBased(int nbContrastedVertices) {
		// we create patterns singleton based, and we calculate sampling
		// probabilities
		patternsSingletonBased = new Pattern[nbContrastedVertices];
		probabilityDistribution = new double[nbContrastedVertices];
		double totalScore = 0;
		int cpt = 0;
		for (Vertex v : graph.getVertices()) {
			Pattern p = new Pattern();
			addVertexAndClosePattern(p, v);
			if (p.getCharacteristic().getScore() > 0) {
				totalScore += p.getCharacteristic().getScore();
				patternsSingletonBased[cpt] = p;
				if (p.getSubgraphBitSet().cardinality() >= designPoint.getSigma()) {
					if (p.getCharacteristic().getScore() >= designPoint.getDelta()
							&& p.getCharacteristic().getScore() > 0) {
						if (p.getCharacteristic().getsPlusBitSet().cardinality() > 0
								|| p.getCharacteristic().getsMinusBitSet().cardinality() > 0) {
							if (!isReallyValid(p)){
								throw new RuntimeException(" not really valid");
							}
							patterns.add(p);
						}
					}
				}
				cpt++;
			}
		}
		double curSum = 0;
		for (int i = 0; i < nbContrastedVertices; i++) {
			curSum += patternsSingletonBased[i].getCharacteristic().getScore();
			probabilityDistribution[i] = curSum / totalScore;
		}
	}
	private boolean isReallyValid(Pattern p){
//		OpenBitSet inters=new OpenBitSet(graph.getVertices().length);
//		for (int i=0;i<graph.getDescriptorsMetaData().getAttributesName().length;i++){
//			if (!p.getCharacteristic().getsPlusBitSet().get(i)){
//				inters.clear(0,graph.getVertices().length);
//				inters.or(p.getSubgraphBitSet());
//				inters.and(measureComputer.getPositiveCharacteristics()[i]);
//				if (inters.cardinality()==p.getSubgraphBitSet().cardinality()){
//					return false;
//				}
//			}
//			if (!p.getCharacteristic().getsMinusBitSet().get(i)){
//				inters.clear(0,graph.getVertices().length);
//				inters.or(p.getSubgraphBitSet());
//				inters.and(measureComputer.getNegativeCharacteristics()[i]);
//				if (inters.cardinality()==p.getSubgraphBitSet().cardinality()){
//					return false;
//				}
//			}
//			
//		}
//		
//
//		// calculate allCandidates
//		OpenBitSet allCandidates = new OpenBitSet(graph.getVertices().length);
//		allCandidates.set(0, graph.getVertices().length);
//		int i = 0;
//		int k;
//		boolean continu = true;
//		while (continu) {
//			k = p.getCharacteristic().getsPlusBitSet().nextSetBit(i);
//			if (k < 0) {
//				continu = false;
//			} else {
//				allCandidates.and(measureComputer.getPositiveCharacteristics()[k]);
//				i = k + 1;
//			}
//		}
//		i = 0;
//		continu = true;
//		while (continu) {
//			k = p.getCharacteristic().getsMinusBitSet().nextSetBit(i);
//			if (k < 0) {
//				continu = false;
//			} else {
//				allCandidates.and(measureComputer.getNegativeCharacteristics()[k]);
//				i = k + 1;
//			}
//		}
//		
//		rest.clear(0, graph.getVertices().length);
//		
//		i=0;
//		continu=true;
//		while (continu){
//			k=p.getSubgraphBitSet().nextSetBit(i);
//			if (k<0){
//				continu=false;
//			}
//			else {
//				rest.or(graph.getVertices()[k].getNeighborsBitSet());
//				
//				i=k+1;
//			}
//		}
//		
//		rest.and(allCandidates);
//		rest.andNot(p.getSubgraphBitSet());
//		if (rest.cardinality()>0){
//			return false;
//		}
//		else {
//			return true;
//		}
		return true;
	}

	private void addVertexAndClosePattern(Pattern pattern, Vertex vertex) {
		if (pattern.getSubgraphBitSet() == null) {
			OpenBitSet subgraph = new OpenBitSet(graph.getVertices().length);
			subgraph.fastSet(vertex.getIndexInGraph());
			OpenBitSet neighbors = new OpenBitSet(graph.getVertices().length);
			neighbors.or(vertex.getNeighborsBitSet());

			OpenBitSet sPlusBitSet = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
			OpenBitSet sMinusBitSet = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
			sPlusBitSet.or(vertex.getPositiveAttributesBitSet());
			sMinusBitSet.or(vertex.getNegativeAttributesBitSet());
			double score = 0;
			HashMap<Integer, Double> scoreByAttributes = new HashMap<>();
			for (int i = 0; i < graph.getDescriptorsMetaData().getAttributesName().length; i++) {
				if (vertex.getAttributeDescriptorScore(i) > 0) {
					scoreByAttributes.put(i, vertex.getAttributeDescriptorScore(i));
					score += vertex.getAttributeDescriptorScore(i);
				}
				if (vertex.getAttributeDescriptorScore(i) < 0) {
					scoreByAttributes.put(i, vertex.getAttributeDescriptorScore(i));
					score -= vertex.getAttributeDescriptorScore(i);
				}
			}
			Characteristic charac = new Characteristic(graph.getDescriptorsMetaData(), sPlusBitSet, sMinusBitSet, score,
					scoreByAttributes);
			pattern.setCharacteristic(charac);
			pattern.setSubgraphBitSet(subgraph);
			pattern.setNeighbors(neighbors);
		} else {
			pattern.getSubgraphBitSet().fastSet(vertex.getIndexInGraph());
			pattern.getNeighbors().or(vertex.getNeighborsBitSet());
			intermediateSet1.clear(0, graph.getDescriptorsMetaData().getAttributesName().length);
			intermediateSet2.clear(0, graph.getDescriptorsMetaData().getAttributesName().length);
			intermediateSet1.or(pattern.getCharacteristic().getsPlusBitSet());
			intermediateSet2.or(pattern.getCharacteristic().getsMinusBitSet());
			intermediateSet1.andNot(vertex.getPositiveAttributesBitSet());
			intermediateSet2.andNot(vertex.getNegativeAttributesBitSet());
			int i = 0;
			int k;
			boolean continu = true;
			double scoreToRemove = 0;
			while (continu) {
				k = intermediateSet1.nextSetBit(i);
				if (k < 0) {
					continu = false;
				} else {
					scoreToRemove = pattern.getCharacteristic().getScoreByAttribute().get(k);
					pattern.getCharacteristic().addValueToScore(-scoreToRemove);
					pattern.getCharacteristic().getScoreByAttribute().remove(k);
					i = k + 1;
				}
			}
			i = 0;
			continu = true;
			while (continu) {
				k = intermediateSet2.nextSetBit(i);
				if (k < 0) {
					continu = false;
				} else {
					scoreToRemove = pattern.getCharacteristic().getScoreByAttribute().get(k);
					pattern.getCharacteristic().addValueToScore(+scoreToRemove);
					pattern.getCharacteristic().getScoreByAttribute().remove(k);
					i = k + 1;
				}
			}
			pattern.getCharacteristic().getsPlusBitSet().and(vertex.getPositiveAttributesBitSet());
			pattern.getCharacteristic().getsMinusBitSet().and(vertex.getNegativeAttributesBitSet());
			if (pattern.getCharacteristic().getsPlusBitSet().cardinality() == 0) {
				if (pattern.getCharacteristic().getsMinusBitSet().cardinality() == 0) {
					pattern.getCharacteristic().setScore(0);
					return;
				}
			}
			i = 0;
			continu = true;
			double scoreToAdd = 0;
			while (continu) {
				k = pattern.getCharacteristic().getsPlusBitSet().nextSetBit(i);
				if (k < 0) {
					continu = false;
				} else {
					scoreToAdd = vertex.getAttributeDescriptorScore(k);
					pattern.getCharacteristic().addValueToScore(scoreToAdd);
					pattern.getCharacteristic().addValueToScoreByAttribute(k, scoreToAdd);
					i = k + 1;
				}
			}
			i = 0;
			continu = true;
			while (continu) {
				k = pattern.getCharacteristic().getsMinusBitSet().nextSetBit(i);
				if (k < 0) {
					continu = false;
				} else {
					scoreToAdd = vertex.getAttributeDescriptorScore(k);
					pattern.getCharacteristic().addValueToScore(-scoreToAdd);
					pattern.getCharacteristic().addValueToScoreByAttribute(k, scoreToAdd);
					i = k + 1;
				}
			}

		}
		

		// blabla
		int i = 0;
		int k;
		boolean continu = true;

		rest.clear(0, graph.getVertices().length);
		rest.or(pattern.getNeighbors());
		rest.andNot(pattern.getSubgraphBitSet());

		// calculate allCandidates
		OpenBitSet allCandidates = new OpenBitSet(graph.getVertices().length);
		allCandidates.set(0, graph.getVertices().length);
		i = 0;
		continu = true;
		while (continu) {
			k = pattern.getCharacteristic().getsPlusBitSet().nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				allCandidates.and(measureComputer.getPositiveCharacteristics()[k]);
				i = k + 1;
			}
		}
		i = 0;
		continu = true;
		while (continu) {
			k = pattern.getCharacteristic().getsMinusBitSet().nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				allCandidates.and(measureComputer.getNegativeCharacteristics()[k]);
				i = k + 1;
			}
		}
		rest.and(allCandidates);

		while (rest.cardinality() != 0) {
			int curId = rest.nextSetBit(0);
			rest.fastClear(curId);
			Vertex curVertex = graph.getVertices()[curId];
			pattern.getSubgraphBitSet().fastSet(curId);
			pattern.getNeighbors().or(curVertex.getNeighborsBitSet());
			rest.or(curVertex.getNeighborsBitSet());
			rest.and(allCandidates);
			rest.andNot(pattern.getSubgraphBitSet());
			i = 0;
			continu = true;
			while (continu) {
				k = pattern.getCharacteristic().getsPlusBitSet().nextSetBit(i);
				if (k < 0) {
					continu = false;
				} else {
					pattern.getCharacteristic().addValueToScore(curVertex.getAttributeDescriptorScore(k));
					pattern.getCharacteristic().addValueToScoreByAttribute(k, curVertex.getAttributeDescriptorScore(k));
					i = k + 1;
				}
			}
			i = 0;
			continu = true;
			while (continu) {
				k = pattern.getCharacteristic().getsMinusBitSet().nextSetBit(i);
				if (k < 0) {
					continu = false;
				} else {
					pattern.getCharacteristic().addValueToScore(-curVertex.getAttributeDescriptorScore(k));
					pattern.getCharacteristic().addValueToScoreByAttribute(k, curVertex.getAttributeDescriptorScore(k));
					i = k + 1;
				}
			}
		}

	}
}
