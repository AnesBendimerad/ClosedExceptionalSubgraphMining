package mainpackage;

import controller.GraphBuilder;
import controller.MeasureComputer;
import controller.PatternComputer;
import controller.RepFactorGraphBuilder;
import controller.ResultsWriter;
import model.DesignPoint;
import model.Graph;
import utils.Utilities;

public class MainClass {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("you must specify one parameter : parameters File path");
		}
		// System.out.println("parameters file path : "+args[0]);
		DesignPoint designPoint = new DesignPoint();
		Utilities.readParametersFromFile(designPoint, args[0]);
		// ResultsWriter.createFolder(designPoint.getResultFolderPath());
		Graph graph =null;
		if (designPoint.getReplicationFactor() == 1) {
			graph = new GraphBuilder(designPoint.getInputFilePath()).build();
		}
		else {
			graph=new RepFactorGraphBuilder(designPoint.getInputFilePath()).build(designPoint.getReplicationFactor());
		}
		// System.out.println(graph.getVertices().length);
		PatternComputer computer = new PatternComputer(designPoint, new MeasureComputer(graph));
		computer.retrievePatterns();
		ResultsWriter.writeResults(computer, designPoint.isWriteFoundPatterns());

	}

}
