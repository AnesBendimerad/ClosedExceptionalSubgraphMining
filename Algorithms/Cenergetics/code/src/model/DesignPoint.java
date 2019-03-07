package model;

public class DesignPoint {
	public static final double DELTA_DEFAULT_VALUE = 0.005;
	public static final double MIN_COV_DEFAULT_VALUE = 0.8;
	public static final int SIGMA_DEFAULT_VALUE = 1;
	public static final boolean UB_DEFAULT_VALUE = true;
	public static final boolean FAIL_FIRST_PRINCIPLE_DEFAULT_VALUE = true;
	public static final boolean COVERAGE_DEFAULT_VALUE= false;
	public static final boolean COVERAGE_PRUNING_DEFAULT_VALUE = false;
	public static final boolean SMINUS_DEFAULT_VALUE = true;
	public static final boolean SPLUS_DEFAULT_VALUE = true;
	public static final boolean CLOSED_PATTERNS_DEFAULT_VALUE= true;
	//public static final boolean PRUNE_WITH_CONNECTION_DEFAULT_VALUE = false;
	public static final boolean PRUNE_WITH_ANTI_MONOTONY_DEFAULT_VALUE = false;
	

	private double delta = DELTA_DEFAULT_VALUE;
	private double minCov = MIN_COV_DEFAULT_VALUE;
	private int sigma = SIGMA_DEFAULT_VALUE;
	private boolean activateUB = UB_DEFAULT_VALUE;
	private boolean activateCoverage = COVERAGE_DEFAULT_VALUE;
	private boolean activateCoveragePruning = COVERAGE_PRUNING_DEFAULT_VALUE;
	private boolean activateSMinus = SMINUS_DEFAULT_VALUE;
	private boolean activateSPlus = SPLUS_DEFAULT_VALUE;
	//private boolean pruneWithConnection = PRUNE_WITH_CONNECTION_DEFAULT_VALUE;
	private boolean pruneWithAntiMonotony=PRUNE_WITH_ANTI_MONOTONY_DEFAULT_VALUE;
	private boolean activateFailFirstPrinciple=FAIL_FIRST_PRINCIPLE_DEFAULT_VALUE;
	private boolean activateClosedPatterns=CLOSED_PATTERNS_DEFAULT_VALUE;
	private String resultFolderPath;
	private String inputFilePath;
	private boolean writeFoundPatterns=true;
	private int replicationFactor=1;
	
	public SummaryType summarize=SummaryType.disjunctiveWracc;
	
	public double jaccardValue=0.6;
	private double plusRatio=1;
	private double minusRatio=1;
	public int maxNbPatternsToMineInSummary=100000;
	public int nbSummarizedPatterns=1000;
	
	public void setMinusRatio(double plusRatio) {
		this.minusRatio = plusRatio;
	}
	public void setPlusRatio(double minusRatio) {
		this.plusRatio = minusRatio;
	}
	public double getMinusRatio() {
		return minusRatio;
	}
	public double getPlusRatio() {
		return plusRatio;
	}
	
	public void setReplicationFactor(int replicationFactor) {
		this.replicationFactor = replicationFactor;
	}
	public int getReplicationFactor() {
		return replicationFactor;
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
	public boolean isActivateUB() {
		return activateUB;
	}
	public void setActivateUB(boolean activateUB) {
		this.activateUB = activateUB;
	}
	public boolean isActivateCoverage() {
		return activateCoverage;
	}
	public void setActivateCoverage(boolean activateCoverage) {
		this.activateCoverage = activateCoverage;
	}
	public boolean isActivateCoveragePruning() {
		return activateCoveragePruning;
	}
	public void setActivateCoveragePruning(boolean activateCoveragePruning) {
		this.activateCoveragePruning = activateCoveragePruning;
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
	public boolean isPruneWithAntiMonotony() {
		return pruneWithAntiMonotony;
	}
	public void setPruneWithAntiMonotony(boolean pruneWithAntiMonotony) {
		this.pruneWithAntiMonotony = pruneWithAntiMonotony;
	}
	public boolean isActivateFailFirstPrinciple() {
		return activateFailFirstPrinciple;
	}
	public void setActivateFailFirstPrinciple(boolean activateFailFirstPrinciple) {
		this.activateFailFirstPrinciple = activateFailFirstPrinciple;
	}
	public boolean isActivateClosedPatterns() {
		return activateClosedPatterns;
	}
	public void setActivateClosedPatterns(boolean activateClosedPatterns) {
		this.activateClosedPatterns = activateClosedPatterns;
	}
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
