package mainpackage;

import java.util.ArrayList;

import controller.GraphBuilder;
import controller.JaccardSummarizer;
import controller.MeasureComputer;
import controller.PatternComputer;
import controller.RepFactorGraphBuilder;
import controller.ResultsWriter;
import model.DesignPoint;
import model.Graph;
import model.Pattern;
import utils.Utilities;

public class MainClass {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("you need to specify one parameter : parameters File path");
		}
		// System.out.println("parameters file path : "+args[0]);
		DesignPoint designPoint = new DesignPoint();
		Utilities.readParametersFromFile(designPoint, args[0]);
		ResultsWriter.createFolder(designPoint.getResultFolderPath());
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
		
		ArrayList<Pattern> summary=null;
		if (designPoint.summarize){
			summary=JaccardSummarizer.getSummary(computer.getPatterns(), designPoint);
		}
		
		ResultsWriter.writeResults(computer, designPoint.isWriteFoundPatterns(),summary);

	}

}
