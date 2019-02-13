package controller;

import org.apache.lucene.util.OpenBitSet;

import model.DesignPoint;
import model.Graph;
import model.UBValue;
import model.Vertex;

public class MeasureComputer {
	private Graph graph;
	private double[] totalDescriptorsValues;
	private double descriptorsTotalSum;
	// for each characteristic in each distribution, this variable contains an
	// openBitSet indicating wether the vertex with the corresponding index
	// has positive value for this characteristic
	private OpenBitSet[] positiveCharacteristics;
	// for each characteristic in each distribution, this variable contains an
	// openBitSet indicating wether the vertex with the corresponding index
	// has negative value for this characteristic
	private OpenBitSet[] negativeCharacteristics;

	private UBValue initUbValue;

	public MeasureComputer(Graph graph) {
		this.graph = graph;
		positiveCharacteristics = new OpenBitSet[graph.getDescriptorsMetaData().getAttributesName().length];
		negativeCharacteristics = new OpenBitSet[graph.getDescriptorsMetaData().getAttributesName().length];
		for (int j = 0; j < graph.getDescriptorsMetaData().getAttributesName().length; j++) {
			positiveCharacteristics[j] = new OpenBitSet(graph.getVertices().length);
			negativeCharacteristics[j] = new OpenBitSet(graph.getVertices().length);
		}
	}

	public Graph getGraph() {
		return graph;
	}

	public void processVertexMeasures(DesignPoint designPoint) {
		if (designPoint.isActivateUB()) {
			initUbValue = new UBValue(true);
		}
		totalDescriptorsValues = new double[graph.getDescriptorsMetaData().getAttributesName().length];
		double descriptorTotalSum = 0;
		for (int attIndex = 0; attIndex < graph.getDescriptorsMetaData().getAttributesName().length; attIndex++) {
			double attributeTotalSum = 0;
			for (Vertex v : graph.getVertices()) {
				attributeTotalSum += v.getDescriptorValue(attIndex);
			}
			totalDescriptorsValues[attIndex] = attributeTotalSum;
			descriptorTotalSum += attributeTotalSum;
		}
		descriptorsTotalSum = descriptorTotalSum;
		// process measure for each vertex
		for (int i = 0; i < graph.getVertices().length; i++) {
			Vertex v = graph.getVertices()[i];
			double totalScoreOfVertex = 0;
			double[] scores = new double[graph.getDescriptorsMetaData().getAttributesName().length];
			for (int attIndex = 0; attIndex < graph.getDescriptorsMetaData().getAttributesName().length; attIndex++) {
				double expectedV=0;
				if (v.getDescriptorTotal() == 0) {
					scores[attIndex] = 0;
				} else {
					expectedV=v.getDescriptorTotal()*totalDescriptorsValues[attIndex] / descriptorsTotalSum;
					scores[attIndex] = ((v.getDescriptorValue(attIndex) / v.getDescriptorTotal())
							- ((totalDescriptorsValues[attIndex]) / (descriptorsTotalSum)))
							* (((double) (v.getDescriptorTotal())) / ((double) (descriptorsTotalSum)));
					if ((!designPoint.isActivateSMinus()) && (scores[attIndex] < 0)) {
						scores[attIndex] = 0;
					}
					if ((!designPoint.isActivateSPlus()) && (scores[attIndex] > 0)) {
						scores[attIndex] = 0;
					}
				}
				if (scores[attIndex] > 0 && v.getDescriptorValue(attIndex)>expectedV*designPoint.getPlusRatio()) {
					positiveCharacteristics[attIndex].fastSet(i);
					totalScoreOfVertex += scores[attIndex];
				} else {
					positiveCharacteristics[attIndex].fastClear(i);
				}
				if (scores[attIndex] < 0 && v.getDescriptorValue(attIndex)<expectedV*designPoint.getMinusRatio()) {
					negativeCharacteristics[attIndex].fastSet(i);
					totalScoreOfVertex -= scores[attIndex];
				} else {
					negativeCharacteristics[attIndex].fastClear(i);
				}
			}
			v.setDescriptorsScores(scores);
			if (designPoint.isActivateUB()) {
				initUbValue.incrementTotalValue(totalScoreOfVertex);
				initUbValue.getValuePerVertex().put(v.getIndexInGraph(), totalScoreOfVertex);
			}
		}
	}

	public UBValue getInitUbValue() {
		return initUbValue;
	}

	public OpenBitSet[] getPositiveCharacteristics() {
		return positiveCharacteristics;
	}

	public OpenBitSet[] getNegativeCharacteristics() {
		return negativeCharacteristics;
	}

}
