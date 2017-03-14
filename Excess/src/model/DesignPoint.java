package model;

public class DesignPoint {
	public static final double DELTA_DEFAULT_VALUE = 0.005;
	public static final double MIN_COV_DEFAULT_VALUE = 0.8;
	public static final int SIGMA_DEFAULT_VALUE = 1;
	public static final boolean COVERAGE_DEFAULT_VALUE= false;
	public static final boolean SMINUS_DEFAULT_VALUE = true;
	public static final boolean SPLUS_DEFAULT_VALUE = true;
	

	private double delta = DELTA_DEFAULT_VALUE;
	private double minCov = MIN_COV_DEFAULT_VALUE;
	private int sigma = SIGMA_DEFAULT_VALUE;
	private boolean activateCoverage = COVERAGE_DEFAULT_VALUE;
	private boolean activateSMinus = SMINUS_DEFAULT_VALUE;
	private boolean activateSPlus = SPLUS_DEFAULT_VALUE;
	private String resultFolderPath;
	private String inputFilePath;
	private boolean writeFoundPatterns=true;
	private boolean removeRepetition=false;
	private long samplingTimeInMS=100;
	
	public void setSamplingTimeInMS(long samplingTimeInMS) {
		this.samplingTimeInMS = samplingTimeInMS;
	}
	public long getSamplingTimeInMS() {
		return samplingTimeInMS;
	}
	public boolean isRemoveRepetition() {
		return removeRepetition;
	}
	public void setRemoveRepetition(boolean removeRepetition) {
		this.removeRepetition = removeRepetition;
	}
	
	public boolean isWriteFoundPatterns() {
		return writeFoundPatterns;
	}
	public void setWriteFoundPatterns(boolean writeFoundPatterns) {
		this.writeFoundPatterns = writeFoundPatterns;
	}
	public double getDelta() {
		return delta;
	}
	public void setDelta(double delta) {
		this.delta = delta;
	}
	public double getMinCov() {
		return minCov;
	}
	public void setMinCov(double minCov) {
		this.minCov = minCov;
	}
	public int getSigma() {
		return sigma;
	}
	public void setSigma(int sigma) {
		this.sigma = sigma;
	}
	public boolean isActivateCoverage() {
		return activateCoverage;
	}
	public void setActivateCoverage(boolean activateCoverage) {
		this.activateCoverage = activateCoverage;
	}
	public boolean isActivateSMinus() {
		return activateSMinus;
	}
	public void setActivateSMinus(boolean activateSMinus) {
		this.activateSMinus = activateSMinus;
	}
	public boolean isActivateSPlus() {
		return activateSPlus;
	}
	public void setActivateSPlus(boolean activateSPlus) {
		this.activateSPlus = activateSPlus;
	}
//	public boolean isPruneWithConnection() {
//		return pruneWithConnection;
//	}
//	public void setPruneWithConnection(boolean pruneWithConnection) {
//		this.pruneWithConnection = pruneWithConnection;
//	}
	public String getResultFolderPath() {
		return resultFolderPath;
	}
	public void setResultFolderPath(String resultFolderPath) {
		this.resultFolderPath = resultFolderPath;
	}
	public String getInputFilePath() {
		return inputFilePath;
	}
	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}
	
	
	
	
}
