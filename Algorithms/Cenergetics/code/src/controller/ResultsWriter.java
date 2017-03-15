package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import model.Pattern;

public class ResultsWriter {
	public static void createFolder(String folderPath) {

		if (!Files.exists(Paths.get(folderPath), LinkOption.NOFOLLOW_LINKS)) {
			File f = new File(folderPath);
			f.mkdir();
			f.setExecutable(true);
			f.setReadable(true);
			f.setWritable(true);
		} else {
			throw new RuntimeException("outputFolder already exists");
		}
	}

	public static void writeResults(PatternComputer patternComputer, boolean writeFoundPatterns) {
		// we assume that resultFolder is already created
		try {
			// we write results
			if (writeFoundPatterns) {
				BufferedWriter resultFile = new BufferedWriter(new FileWriter(
						patternComputer.getDesignPoint().getResultFolderPath() + "/retrievedPatterns.json"));
				writeResultAsJSon(patternComputer, resultFile);
				resultFile.close();
			}
			// we write statistics
			BufferedWriter resultIndicatorFile = new BufferedWriter(
					new FileWriter(patternComputer.getDesignPoint().getResultFolderPath() + "/statistics.txt"));
			resultIndicatorFile.write("nbRecursiveCalls:" + patternComputer.getStatistics().nbRecursiveCalls + "\n");
			resultIndicatorFile.write("nbVerifiedPatterns:" + patternComputer.getStatistics().nbPatternVerified + "\n");
			resultIndicatorFile
					.write("patternMiningTimeInMS: " + patternComputer.getStatistics().patternMiningTimeInMS + "\n");
			resultIndicatorFile.write("nbRetrievedPatterns: " + patternComputer.getPatterns().size() + "\n");
			resultIndicatorFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeResultAsJSon(PatternComputer patternComputer, BufferedWriter resultFile)
			throws IOException {
		resultFile.write("{\n");
		resultFile.write("\t\"numberOfPatterns\" :" + patternComputer.getPatterns().size() + ",\n");
		resultFile.write("\t\"patterns\" : [\n");
		boolean firstInsert = true;
		for (Pattern pattern : patternComputer.getPatterns()) {
			if (firstInsert) {
				firstInsert = false;
			} else {
				resultFile.write(",\n");
			}
			resultFile.write(pattern.toJson("\t\t", patternComputer.getGraph()));
		}
		resultFile.write("\n\t]\n}");
	}
}
