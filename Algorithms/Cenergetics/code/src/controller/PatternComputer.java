package controller;

import java.util.ArrayList;

import org.apache.lucene.util.OpenBitSet;

import model.DesignPoint;
import model.Graph;
import model.NewIndicesByClosure;
import model.Pattern;
import model.Statistics;
import model.UBValue;
import model.Vertex;

public class PatternComputer {
	private DesignPoint designPoint;
	private Graph graph;
	private MeasureComputer measureComputer;
	private ArrayList<Pattern> patterns;
	private Statistics statistics;

	public PatternComputer(DesignPoint designPoint, MeasureComputer measureComputer) {
		this.designPoint = designPoint;
		this.graph = measureComputer.getGraph();
		this.measureComputer = measureComputer;
		statistics = new Statistics();
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

	public void retrievePatterns() {
		patterns = new ArrayList<>();
		statistics.nbRecursiveCalls = 0;
		statistics.nbPatternVerified=0;
		long startTime = System.currentTimeMillis();
		measureComputer.processVertexMeasures(designPoint);
		OpenBitSet candidatesVertices = new OpenBitSet(graph.getVertices().length);
		candidatesVertices.set(0, graph.getVertices().length);
		OpenBitSet sPlus = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
		OpenBitSet sMinus = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
		OpenBitSet sPlusCand = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
		OpenBitSet sMinusCand = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
		sPlusCand.set(0, graph.getDescriptorsMetaData().getAttributesName().length);
		sMinusCand.set(0, graph.getDescriptorsMetaData().getAttributesName().length);
		characEnumeration(sPlus, sMinus, candidatesVertices, sPlusCand, sMinusCand, measureComputer.getInitUbValue());
		long stopTime = System.currentTimeMillis();
		statistics.patternMiningTimeInMS = stopTime - startTime;

	}

	private void characEnumeration(OpenBitSet oldSPlus, OpenBitSet oldSMinus, OpenBitSet oldCandidatesVertices,
			OpenBitSet oldSPlusCand, OpenBitSet oldSMinusCand, UBValue ubValue) {
		statistics.nbRecursiveCalls++;

		//if (statistics.nbRecursiveCalls % 10000 == 0) {
		//	System.out.println("nb calls : " + statistics.nbRecursiveCalls);
		//}
		if (designPoint.isActivateUB()) {
			if (ubValue.getValue() < designPoint.getDelta()) {
				return;
			}
		}
		if (oldCandidatesVertices.cardinality()<designPoint.getSigma()){
			return;
		}

		OpenBitSet candidatesVertices = new OpenBitSet(graph.getVertices().length);
		int indice = choseCandidateToAdd(oldCandidatesVertices, oldSPlusCand, oldSMinusCand, candidatesVertices);

		if (indice >= 0 && indice < graph.getDescriptorsMetaData().getAttributesName().length) {
			int iPlus = indice;
			// candidatesVertices.or(oldCandidatesVertices);
			OpenBitSet sPlus = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
			OpenBitSet sMinus = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
			sPlus.or(oldSPlus);
			sMinus.or(oldSMinus);
			// we add iPlus to sPlus and prune
			sPlus.fastSet(iPlus);
			// candidatesVertices.and(measureComputer.getPositiveCharacteristics()[iPlus]);
			ArrayList<UBValue> ubValuesPerCC = null;
			UBValue ubValueSecondCall = null;
			if (designPoint.isActivateUB()) {
				ubValuesPerCC = new ArrayList<>();
				ubValueSecondCall = new UBValue(true);
				// update ubValueSecondCall with removedVertices
				OpenBitSet prunedCandidates=new OpenBitSet(graph.getVertices().length);
				prunedCandidates.or(oldCandidatesVertices);
				prunedCandidates.andNot(candidatesVertices);
				int i=0;
				int k;
				boolean continu=true;
				while (continu){
					k=prunedCandidates.nextSetBit(i);
					if (k<0){
						continu=false;
					}
					else {
						double partToAdd=ubValue.getValuePerVertex().get(k);
						double partToRemove=graph.getVertices()[k].getAttributeDescriptorScore(iPlus);
						if (partToRemove>0){
							ubValueSecondCall.getValuePerVertex().put(k, partToAdd-partToRemove);
							ubValueSecondCall.incrementTotalValue(partToAdd-partToRemove);
						}
						else {
							ubValueSecondCall.getValuePerVertex().put(k, partToAdd);
							ubValueSecondCall.incrementTotalValue( partToAdd);
						}
						i=k+1;
					}
				}
			}
			
			
			ArrayList<OpenBitSet> connectedComponents = extractConnectedSubgraphs(ubValue, candidatesVertices,
					ubValuesPerCC, ubValueSecondCall, indice);
			int currentIndex = 0;
			for (OpenBitSet connectedComponent : connectedComponents) {
				statistics.nbPatternVerified++;
				OpenBitSet closedSPlus = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
				OpenBitSet closedSMinus = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
				NewIndicesByClosure indices = new NewIndicesByClosure();
				boolean closureGood = closeConnectedComponent(sPlus, sMinus, connectedComponent, oldSPlusCand,
						oldSMinusCand, closedSPlus, closedSMinus, indices);
				if (closureGood) {
					OpenBitSet sPlusCand = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
					OpenBitSet sMinusCand = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
					sPlusCand.or(oldSPlusCand);
					sMinusCand.or(oldSMinusCand);
					sPlusCand.andNot(closedSPlus);
					sMinusCand.andNot(closedSMinus);
					if (designPoint.isActivateUB()) {
						characEnumeration(closedSPlus, closedSMinus, connectedComponent, sPlusCand, sMinusCand,
								ubValuesPerCC.get(currentIndex));
					} else {
						characEnumeration(closedSPlus, closedSMinus, connectedComponent, sPlusCand, sMinusCand, null);
					}
				}
				currentIndex++;
			}
			oldSPlusCand.fastClear(iPlus);
			characEnumeration(oldSPlus, oldSMinus, oldCandidatesVertices, oldSPlusCand, oldSMinusCand,
					ubValueSecondCall);
			oldSPlusCand.fastSet(iPlus);

		} else if (indice >= graph.getDescriptorsMetaData().getAttributesName().length) {
			int iMinus = indice - graph.getDescriptorsMetaData().getAttributesName().length;
			OpenBitSet sPlus = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
			OpenBitSet sMinus = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
			sPlus.or(oldSPlus);
			sMinus.or(oldSMinus);
			// we add iPlus to sPlus and prune
			sMinus.fastSet(iMinus);
			// candidatesVertices.and(measureComputer.getNegativeCharacteristics()[iMinus]);
			ArrayList<UBValue> ubValuesPerCC = null;
			UBValue ubValueSecondCall = null;
			if (designPoint.isActivateUB()) {
				ubValuesPerCC = new ArrayList<>();
				ubValueSecondCall = new UBValue(true);
				
				// update ubValueSecondCall with removedVertices
				OpenBitSet prunedCandidates=new OpenBitSet(graph.getVertices().length);
				prunedCandidates.or(oldCandidatesVertices);
				prunedCandidates.andNot(candidatesVertices);
				int i=0;
				int k;
				boolean continu=true;
				while (continu){
					k=prunedCandidates.nextSetBit(i);
					if (k<0){
						continu=false;
					}
					else {
						double partToAdd=ubValue.getValuePerVertex().get(k);
						double partToRemove=graph.getVertices()[k].getAttributeDescriptorScore(iMinus);
						if (partToRemove<0){
							ubValueSecondCall.getValuePerVertex().put(k, partToAdd+partToRemove);
							ubValueSecondCall.incrementTotalValue(partToAdd+partToRemove);
						}
						else {
							ubValueSecondCall.getValuePerVertex().put(k, partToAdd);
							ubValueSecondCall.incrementTotalValue( partToAdd);
						}
						i=k+1;
					}
				}
			}
			int currentIndex = 0;
			ArrayList<OpenBitSet> connectedComponents = extractConnectedSubgraphs(ubValue, candidatesVertices,
					ubValuesPerCC, ubValueSecondCall, indice);
			for (OpenBitSet connectedComponent : connectedComponents) {
				statistics.nbPatternVerified++;
				OpenBitSet closedSPlus = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
				OpenBitSet closedSMinus = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
				NewIndicesByClosure indices = new NewIndicesByClosure();
				boolean closureGood = closeConnectedComponent(sPlus, sMinus, connectedComponent, oldSPlusCand,
						oldSMinusCand, closedSPlus, closedSMinus, indices);
				if (closureGood) {
					OpenBitSet sPlusCand = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
					OpenBitSet sMinusCand = new OpenBitSet(graph.getDescriptorsMetaData().getAttributesName().length);
					sPlusCand.or(oldSPlusCand);
					sMinusCand.or(oldSMinusCand);
					sPlusCand.andNot(closedSPlus);
					sMinusCand.andNot(closedSMinus);
					if (designPoint.isActivateUB()) {
						characEnumeration(closedSPlus, closedSMinus, connectedComponent, sPlusCand, sMinusCand,
								ubValuesPerCC.get(currentIndex));
					} else {
						characEnumeration(closedSPlus, closedSMinus, connectedComponent, sPlusCand, sMinusCand, null);
					}
				}
				currentIndex++;
			}
			oldSMinusCand.fastClear(iMinus);
			characEnumeration(oldSPlus, oldSMinus, oldCandidatesVertices, oldSPlusCand, oldSMinusCand,
					ubValueSecondCall);
			oldSMinusCand.fastSet(iMinus);
		} else {
			if (oldCandidatesVertices.cardinality() >= designPoint.getSigma()) {
				double score = getScore(oldCandidatesVertices, oldSPlus, oldSMinus);
				//double score = ubValue.getValue();
				if (score >= designPoint.getDelta()) {
					addSolution(oldCandidatesVertices, oldSPlus, oldSMinus, score);
				}
			}
		}
	}

	private int choseCandidateToAdd(OpenBitSet oldCandidatesVertices, OpenBitSet oldSPlusCand, OpenBitSet oldSMinusCand,
			OpenBitSet candidatesVertices) {
		if (!designPoint.isActivateFailFirstPrinciple()) {
			int indice = oldSPlusCand.nextSetBit(0);
			if (indice >= 0) {
				candidatesVertices.or(oldCandidatesVertices);
				candidatesVertices.and(measureComputer.getPositiveCharacteristics()[indice]);
				return indice;
			}
			indice = oldSMinusCand.nextSetBit(0);
			if (indice >= 0) {
				candidatesVertices.or(oldCandidatesVertices);
				candidatesVertices.and(measureComputer.getNegativeCharacteristics()[indice]);
				indice += graph.getDescriptorsMetaData().getAttributesName().length;
				return indice;
			} else {
				return -1;
			}
		} else {
			OpenBitSet currentCand = new OpenBitSet(graph.getVertices().length);
			int minIntersection = (int) oldCandidatesVertices.cardinality();
			int choosenIndex = -1;
			int curIntersect;
			int i = 0;
			int k;
			boolean continu = true;
			while (continu) {
				k = oldSPlusCand.nextSetBit(i);
				if (k < 0) {
					continu = false;
				} else {
					currentCand.clear(0, graph.getVertices().length);
					currentCand.or(oldCandidatesVertices);
					currentCand.and(measureComputer.getPositiveCharacteristics()[k]);
					curIntersect = (int) currentCand.cardinality();
					if (curIntersect < minIntersection) {
						minIntersection = curIntersect;
						choosenIndex = k;
						candidatesVertices.clear(0, graph.getVertices().length);
						candidatesVertices.or(currentCand);
					}
					i = k + 1;
				}
			}
			i = 0;
			continu = true;
			while (continu) {
				k = oldSMinusCand.nextSetBit(i);
				if (k < 0) {
					continu = false;
				} else {
					currentCand.clear(0, graph.getVertices().length);
					currentCand.or(oldCandidatesVertices);
					currentCand.and(measureComputer.getNegativeCharacteristics()[k]);
					curIntersect = (int) currentCand.cardinality();
					if (curIntersect < minIntersection) {
						minIntersection = curIntersect;
						choosenIndex = k + graph.getDescriptorsMetaData().getAttributesName().length;
						candidatesVertices.clear(0, graph.getVertices().length);
						candidatesVertices.or(currentCand);
					}
					i = k + 1;
				}
			}
			return choosenIndex;
		}
	}

	private void addSolution(OpenBitSet subgraph, OpenBitSet sPlus, OpenBitSet sMinus, double score) {
		patterns.add(new Pattern(subgraph, graph.getDescriptorsMetaData(), sPlus, sMinus, score, null));

	}

	private double getScore(OpenBitSet subgraph, OpenBitSet sPlus, OpenBitSet sMinus) {
		double score = 0;
		int i = 0;
		int k;
		boolean continu = true;
		while (continu) {
			k = subgraph.nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				score += getScoreOfVertex(graph.getVertices()[k], sPlus, sMinus);
				i = k + 1;
			}
		}
		return score;

	}

	private double getScoreOfVertex(Vertex vertex, OpenBitSet sPlus, OpenBitSet sMinus) {
		double score = 0;
		int i = 0;
		int k;
		boolean continu = true;
		while (continu) {
			k = sPlus.nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				if (vertex.getAttributeDescriptorScore(k) > 0) {
					score += vertex.getAttributeDescriptorScore(k);
				} else {
					throw new RuntimeException("incoherence error");
				}

				i = k + 1;
			}
		}
		i = 0;
		continu = true;
		while (continu) {
			k = sMinus.nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				if (vertex.getAttributeDescriptorScore(k) < 0) {
					score -= vertex.getAttributeDescriptorScore(k);
				} else {
					throw new RuntimeException("incoherence error");
				}

				i = k + 1;
			}
		}
		return score;
	}

	private boolean closeConnectedComponent(OpenBitSet sPlus, OpenBitSet sMinus, OpenBitSet connectedComponent,
			OpenBitSet oldSPlusCand, OpenBitSet oldSMinusCand, OpenBitSet closedSPlus, OpenBitSet closedSMinus,
			NewIndicesByClosure indices) {
		closedSPlus.or(sPlus);
		closedSMinus.or(sMinus);
		int sizeOfCC = (int) connectedComponent.cardinality();
		for (int i = 0; i < graph.getDescriptorsMetaData().getAttributesName().length; i++) {
			if (!sPlus.get(i) && !sMinus.get(i)) {
				if (OpenBitSet.intersectionCount(measureComputer.getPositiveCharacteristics()[i],
						connectedComponent) == sizeOfCC) {
					if (!oldSPlusCand.get(i)) {
						return false;
					} else {
						closedSPlus.fastSet(i);
					}
				} else if (OpenBitSet.intersectionCount(measureComputer.getNegativeCharacteristics()[i],
						connectedComponent) == sizeOfCC) {
					if (!oldSMinusCand.get(i)) {
						return false;
					} else {
						closedSMinus.fastSet(i);
					}
				}
			}
		}
		return true;
	}

	private ArrayList<OpenBitSet> extractConnectedSubgraphs(UBValue originalUB, OpenBitSet candidates,
			ArrayList<UBValue> ubValuesPerCC, UBValue ubValueSecondCall, int indice) {
		OpenBitSet rest = new OpenBitSet(graph.getVertices().length);
		rest.union(candidates);
		boolean continu = true;
		ArrayList<OpenBitSet> connectedComponents = new ArrayList<>();
		while (continu) {
			int nextOne = rest.nextSetBit(0);
			if (nextOne < 0) {
				continu = false;
			} else {
				UBValue ubOfCC = null;
				if (designPoint.isActivateUB()) {
					ubOfCC = new UBValue(true);
					double ubVertexValue = originalUB.getValuePerVertex().get(nextOne);
					ubOfCC.getValuePerVertex().put(nextOne, ubVertexValue);
					ubOfCC.incrementTotalValue(ubVertexValue);

					if (indice < graph.getDescriptorsMetaData().getAttributesName().length) {
						// positive :
						double partToRemove = graph.getVertices()[nextOne].getAttributeDescriptorScore(indice);
						if (partToRemove > 0) {
							ubValueSecondCall.getValuePerVertex().put(nextOne, ubVertexValue - partToRemove);
							ubValueSecondCall.incrementTotalValue(ubVertexValue - partToRemove);
						} else {
							ubValueSecondCall.getValuePerVertex().put(nextOne, ubVertexValue);
							ubValueSecondCall.incrementTotalValue(ubVertexValue);
						}
					} else {
						// negative :
						double partToRemove = graph.getVertices()[nextOne].getAttributeDescriptorScore(
								indice - graph.getDescriptorsMetaData().getAttributesName().length);
						if (partToRemove < 0) {
							ubValueSecondCall.getValuePerVertex().put(nextOne, ubVertexValue + partToRemove);
							ubValueSecondCall.incrementTotalValue(ubVertexValue + partToRemove);
						} else {
							ubValueSecondCall.getValuePerVertex().put(nextOne, ubVertexValue);
							ubValueSecondCall.incrementTotalValue(ubVertexValue);
						}
					}
				}

				OpenBitSet found = new OpenBitSet(graph.getVertices().length);
				found.fastSet(nextOne);
				rest.fastClear(nextOne);
				exploreDepthFirst(originalUB, nextOne, found, rest, ubOfCC, ubValueSecondCall, indice);
				if (designPoint.isActivateUB()) {
					ubValuesPerCC.add(ubOfCC);
				}
				connectedComponents.add(found);
			}
		}
		return connectedComponents;
	}

	private void exploreDepthFirst(UBValue originalUB, int currentIndex, OpenBitSet found, OpenBitSet rest,
			UBValue ubOfCC, UBValue ubValueSecondCall, int indice) {
		for (Integer neighborIndex : graph.getVertices()[currentIndex].getSetOfNeighborsId()) {
			if (rest.get(neighborIndex)) {
				if (designPoint.isActivateUB()) {
					double ubVertexValue = originalUB.getValuePerVertex().get(neighborIndex);
					ubOfCC.getValuePerVertex().put(neighborIndex, ubVertexValue);
					ubOfCC.incrementTotalValue(ubVertexValue);

					if (indice < graph.getDescriptorsMetaData().getAttributesName().length) {
						// positive :
						double partToRemove = graph.getVertices()[neighborIndex].getAttributeDescriptorScore(indice);
						if (partToRemove > 0) {
							ubValueSecondCall.getValuePerVertex().put(neighborIndex, ubVertexValue - partToRemove);
							ubValueSecondCall.incrementTotalValue(ubVertexValue - partToRemove);
						} else {
							ubValueSecondCall.getValuePerVertex().put(neighborIndex, ubVertexValue);
							ubValueSecondCall.incrementTotalValue(ubVertexValue);
						}
					} else {
						// negative :
						double partToRemove = graph.getVertices()[neighborIndex].getAttributeDescriptorScore(
								indice - graph.getDescriptorsMetaData().getAttributesName().length);
						if (partToRemove < 0) {
							ubValueSecondCall.getValuePerVertex().put(neighborIndex, ubVertexValue + partToRemove);
							ubValueSecondCall.incrementTotalValue(ubVertexValue + partToRemove);
						} else {
							ubValueSecondCall.getValuePerVertex().put(neighborIndex, ubVertexValue);
							ubValueSecondCall.incrementTotalValue(ubVertexValue);
						}
					}
				}
				found.fastSet(neighborIndex);
				rest.fastClear(neighborIndex);
				exploreDepthFirst(originalUB, neighborIndex, found, rest, ubOfCC, ubValueSecondCall, indice);
			}
		}
	}

}
