package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.BitSetIterator;
import model.Pattern;
import utils.FinalCharacteristic;
import utils.FinalPattern;
import utils.FinalResults;
import utils.Utilities;

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
				// we first produce final patterns
				FinalResults finalResults = new FinalResults();
				finalResults.numberOfPatterns = patternComputer.getPatterns().size();
				finalResults.patterns = new FinalPattern[finalResults.numberOfPatterns];
				for (int i = 0; i < finalResults.numberOfPatterns; i++) {
					Pattern curP = patternComputer.getPatterns().get(i);
					FinalPattern fp = new FinalPattern();
					fp.subgraph = new String[(int) curP.getSubgraphBitSet().cardinality()];
					BitSetIterator iterator = new BitSetIterator(curP.getSubgraphBitSet());
					int cpt, curId = 0;
					while ((cpt = iterator.getNext()) >= 0) {
						fp.subgraph[curId] = patternComputer.getGraph().getVertices()[cpt].getId();
						curId++;
					}
					fp.characteristic = new FinalCharacteristic();
					fp.characteristic.descriptorName = curP.getCharacteristic().getDescriptorMetaData()
							.getDescriptorName();
					fp.characteristic.positiveAttributes = new String[(int) curP.getCharacteristic().getsPlusBitSet()
							.cardinality()];
					iterator = new BitSetIterator(curP.getCharacteristic().getsPlusBitSet());
					curId = 0;
					while ((cpt = iterator.getNext()) >= 0) {
						fp.characteristic.positiveAttributes[curId] = patternComputer.getGraph()
								.getDescriptorsMetaData().getAttributesName()[cpt];
						curId++;
					}
					fp.characteristic.negativeAttributes = new String[(int) curP.getCharacteristic().getsMinusBitSet()
							.cardinality()];
					iterator = new BitSetIterator(curP.getCharacteristic().getsMinusBitSet());
					curId = 0;
					while ((cpt = iterator.getNext()) >= 0) {
						fp.characteristic.negativeAttributes[curId] = patternComputer.getGraph()
								.getDescriptorsMetaData().getAttributesName()[cpt];
						curId++;
					}
					fp.characteristic.score=curP.getCharacteristic().getScore();
					finalResults.patterns[i]=fp;
				}
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				
				try (FileWriter writer = new FileWriter(
						patternComputer.getDesignPoint().getResultFolderPath() + "/retrievedPatterns.json")) {
					gson.toJson(finalResults, writer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//BufferedWriter resultFile = new BufferedWriter(new FileWriter(
				//		patternComputer.getDesignPoint().getResultFolderPath() + "/retrievedPatterns.json"));
				//writeResultAsJSon(patternComputer, resultFile);
				//resultFile.close();
			}
			// we write statistics
			//

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
