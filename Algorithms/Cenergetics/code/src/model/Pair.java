package model;

public class Pair implements Comparable<Pair> {
	public int index;
	public double score;
	
	public Pair(){
		
	}
	public Pair(int index, double score) {
		this.index = index;
		this.score = score;
	}


	@Override
	public int compareTo(Pair o) {
		if (score>o.score){
			return 1;
		}
		else if (score<o.score){
			return -1;
		}
		return 0;
	}
	
	
}
