package mainpackage;

import controller.GraphBuilder;
import controller.IPatternSampler;
import controller.MeasureComputer;
import controller.PatternSampler;
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
		graph = new GraphBuilder(designPoint.getInputFilePath()).build();
		// System.out.println(graph.getVertices().length);
		IPatternSampler computer = new PatternSampler(designPoint, new MeasureComputer(graph));
		computer.samplePatterns();
		ResultsWriter.writeResults(computer, designPoint.isWriteFoundPatterns());

	}

}
