package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import model.DesignPoint;

public class Utilities {
	
	public static void readParametersFromFile(DesignPoint designPoint,String parametersFilePath) {
		HashSet<String> requiredParameters=new HashSet<>();
		requiredParameters.add("inputFilePath");
		requiredParameters.add("resultFolderPath");
		try {
			BufferedReader parametersFile = new BufferedReader(new FileReader(new File(parametersFilePath)));
			String line;
			while ((line = parametersFile.readLine()) != null) {
				String[] elements = line.split("=");
				switch (elements[0]) {
				case "delta":
					// threshold
					designPoint.setDelta(Double.parseDouble(elements[1]));
					break;
				case "mincov":
					designPoint.setMinCov(Double.parseDouble(elements[1]));
					break;
				case "sigma":
					// minSizeSubGraph					
					designPoint.setSigma(Integer.parseInt(elements[1]));
					break;
				case "samplingTimeInMS":
					// minSizeSubGraph					
					designPoint.setSamplingTimeInMS(Integer.parseInt(elements[1]));
					break;
					
				case "removeRepetition":
					designPoint.setRemoveRepetition(Boolean.parseBoolean(elements[1]));
					break;
				case "writeFoundPatterns":
					designPoint.setWriteFoundPatterns(Boolean.parseBoolean(elements[1]));
					break;
				case "activateCoverage":
					designPoint.setActivateCoverage(Boolean.parseBoolean(elements[1]));
					break;
				case "activateSMinus":
					designPoint.setActivateSMinus(Boolean.parseBoolean(elements[1]));
					break;
				case "activateSPlus":
					designPoint.setActivateSPlus(Boolean.parseBoolean(elements[1]));
					break;
				case "inputFilePath":
					requiredParameters.remove("inputFilePath");
					designPoint.setInputFilePath(elements[1]);
					break;
				case "resultFolderPath":
					requiredParameters.remove("resultFolderPath");
					designPoint.setResultFolderPath(elements[1]);
					break;
				default:
					throw new RuntimeException("this parameter is unknown : " + elements[0]);
				}
			}
			parametersFile.close();
			if (requiredParameters.size()>0){
				throw new RuntimeException("Some required parameters are missing");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
