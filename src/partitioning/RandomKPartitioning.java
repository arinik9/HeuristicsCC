package partitioning;

import myUtils.Clustering;
import myUtils.StdRandom;

public class RandomKPartitioning {
	int n;
	int k;
	int[] membership;
	double[][] adjMat;
	
	public RandomKPartitioning(int n_, int k_, double[][] adjMat_){
		n = n_;
		k = k_;
		membership = new int[n];
		adjMat = adjMat_;
	}
	
	public Clustering generateRandomPartition(){
		Clustering c = null;

		//================================
		// Form k cluster examplars
		// ===============================
		// System.out.println("k" + k);

		int[] clusterExamplars = new int[k];
		for(int j=0; j<k; j++)
			clusterExamplars[j] = -1; // init
		
		// StdRandom.setSeed(12345);
		int ci = StdRandom.uniform(n);
		clusterExamplars[0] = ci;
		membership[ci] = 1; // cluster ids start from 1
		
		// pick k-1 cluster examplars at random, where their positive neighborhood is not exactly the same
		for(int i=1; i<k; i++){

			int newExampler = pickDiffClusterExampler(clusterExamplars);

			clusterExamplars[i] = newExampler;
			membership[newExampler] = i+1;
		}


		for(int i=0; i<n; i++){

			int examplar = assignVertexToClusterExamplar(i, clusterExamplars);
			membership[i] = membership[examplar];
		}
		
		c = new Clustering(membership, 0); // id=0 (it is not important)
		c.computeImbalance(adjMat);
		return(c);
	}
	

	
	public int pickDiffClusterExampler(int[] clusterExamplars){
		int ci = -1;
		boolean alreadyFound = true;
		int it = 0;
		int MAX_TRIAL = 10;
		while(alreadyFound){

			alreadyFound = false;
			ci = StdRandom.uniform(n);
			for(int j=0; j<k; j++){
				if(ci == clusterExamplars[j]){ // clusterExamplars[j]!=-1 && 
					alreadyFound = true;
					break;
				}
			}
			// check if 'ci' has the same neighbors with the existing cluster examplars or not
			if(!alreadyFound){
				
				if(it >= MAX_TRIAL) // after some trial iteration, if we cannot find a perfect candidate, we choose it randomly
					break;
					
				for(int j=0; j<k; j++){
					if(clusterExamplars[j]!=-1 && IsNeighborIdentical(ci,clusterExamplars[j])){
						alreadyFound = true;
						break;
					}
				}
			}
			it++;
		}
		return(ci);
	}
	
		
	
	public boolean IsNeighborIdentical(int u, int v){
		boolean identical = true;
		boolean anyCommonEdge = false;
		int nbCommonEdge = 0;
		for(int i=0; i<n; i++){ // iterate over vertices other than u and v
			if(i!=u && i!=v && adjMat[u][i]!=0.0 && adjMat[v][i]!=0.0){ // ensure that both u and v have an edge with other vertex i
				anyCommonEdge = true;
				nbCommonEdge++;
				if(adjMat[u][i]*adjMat[v][i]<0.0){ // if this is negative, then one has neg edge and other has pos edge
					return(false);
				}
			}
		}
		return(identical && nbCommonEdge>2); // if there is not any common edge, then it returns false
	}
	
	
	
	
	// choose the cluster examplar based on positive neigborhood
	public int assignVertexToClusterExamplar(int u, int[] clusterExamplars){
		for(int j=0; j<k; j++){
			if(u == clusterExamplars[j])
				return(clusterExamplars[j]);
		}
		
		// ===============================
		
		int choiceExamplar = clusterExamplars[0]; // in case there is no improvement, pick the first one
		int bestPosCount = -1;
		for(int j=0; j<k; j++){
			int currExamplar = clusterExamplars[j];
			int posCount = 0;
			for(int i=0; i<n; i++){
				if(i!=u && i!=currExamplar && adjMat[u][i]>0.0 && adjMat[currExamplar][i]>0.0){
					posCount++;
				}
			}
			if(posCount>bestPosCount){
				bestPosCount = posCount;
				choiceExamplar = currExamplar;
			}
		}
		return(choiceExamplar);
	}
	
}
