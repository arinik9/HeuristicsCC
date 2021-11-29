package partitioning;


import myUtils.Clustering;
import myUtils.StdRandom;



public class RelocationHeuristicWithTabuSearch {
	int n;
	double[][] adjMat;
	int[] tabuList;
	int tabuCapacity;
	
	public RelocationHeuristicWithTabuSearch(double[][] adjMat_, int tabuCapacity_){
		n = adjMat_.length;
		adjMat = adjMat_;
		tabuCapacity = tabuCapacity_;
		tabuList = new int[n];
		resetTabuList();
	}
	
	
	public void resetTabuList(){
		for(int i=0; i<n; i++)
			tabuList[i] = 0;
	}
	
	
	public void decrementTabuList(){
		for(int i=0; i<n; i++)
			if(tabuList[i] > 0)
				tabuList[i]--;
	}
	
	
	public Clustering solveRelocationHeuristic(double tilim){
		
		long startTime = System.currentTimeMillis();
		Clustering Cbest = null;
		double bestImbValue = Double.MAX_VALUE;
		double t = 0.0;
		
		while(t<tilim){
			//System.out.println(t);

			int kmax = 20;
			if(n<kmax)
				kmax = n/2;
			int k = StdRandom.uniform(2,kmax+1); // pick a random number from [2,kmax]
			
			RandomKPartitioning kPartitioning = new RandomKPartitioning(n, k, adjMat);
			Clustering Ccurr = kPartitioning.generateRandomPartition();
			//System.out.println(Ccurr);

			Ccurr.computeImbalance(adjMat);
			boolean improv = false;
			
			// BEGIN RELOCATION HEURISTIC 
			while(!improv && t<tilim){
				improv = true;
				
				for(int v=0; v<n; v++){
					int sourceClusterId = Ccurr.membership[v];
					int nbClusters = Ccurr.getNbCluster();
					
					for(int newClusterId=1; newClusterId<=(nbClusters+1); newClusterId++){ // +1 for an empty cluster
						if(newClusterId!=sourceClusterId && newClusterId<=(Ccurr.getNbCluster()+1)){ // Ccurr is updated during the iteration
							double balChange = Ccurr.calculateBalanceChange(v, newClusterId, adjMat); // positive values mean that 
							if(balChange>0){
								Ccurr.changeClusterOfNode(v, newClusterId, true);
								Ccurr.setImbalance(Ccurr.getImbalance()-balChange);
								sourceClusterId = newClusterId;
								improv = false;
							}
						}
					}
				}
				
				long endTime = System.currentTimeMillis();
				t = (float) (endTime-startTime)/1000;
			}
			// END RELOCATION HEURISTIC
			
			
			if(Ccurr.getImbalance() < bestImbValue){
				Cbest = new Clustering(Ccurr);
				bestImbValue = Ccurr.getImbalance();
			}
			
			long endTime = System.currentTimeMillis();
			t = (float) (endTime-startTime)/1000;

		}

		return(Cbest);
	}
	
	
	
	
	
	public Clustering solveTabuSearch(Clustering Cinit, double tilim){
		Clustering Cbest = new Clustering(Cinit);
		Cbest.computeImbalance(adjMat);
		Clustering Ccurr = new Clustering(Cinit);
		Clustering Cnext = null;
		double t = 0; // in seconds
		long startTime = System.currentTimeMillis();
		
		while(t<tilim){
			//System.out.println(t);

			////////////////////////////////////////////////////////////////////////////
			// find a new clustering with best improvement or smallest worsening
			////////////////////////////////////////////////////////////////////////////
			
			double bestImbChange = -Double.MAX_VALUE; // positive is better
			int bestVertex = 0;
			for(int v=0; v<n; v++){
				if(tabuList[v] == 0){
					ClusterSearchResult result = pickBestTargetCluster(v, Ccurr, true); // best improvement or smallest worsening
					int newClusterId = result.getNewClusterId();
					double imbChange = result.getImbChange(); // a positive value indicates there is an improvement
					
					if(newClusterId != -1 && bestImbChange<imbChange){ // if there is any cluster which improves the current imbalance
						Cnext = new Clustering(Ccurr);
						Cnext.changeClusterOfNode(v, newClusterId, true);
						Cnext.setImbalance(Cnext.getImbalance()-imbChange);
						
						bestVertex = v;
						bestImbChange = imbChange;
					}
				}
			}
			Ccurr = new Clustering(Cnext);
			
			////////////////////////////////////////////////////////////////////////////
			// perform operations regarding tabu list
			////////////////////////////////////////////////////////////////////////////
			tabuList[bestVertex] = tabuCapacity;
			
			if((Cnext.getImbalance() < Cbest.getImbalance())){
				Cbest = new Clustering(Cnext);
				resetTabuList();
			}
			else {
				decrementTabuList();
			}
			
			long endTime = System.currentTimeMillis();
			t = (float) (endTime-startTime)/1000;
		}
		
		return(Cbest);
	}
	
	
	
	// best target cluster based on best improvement (or smallest worsening)
	public ClusterSearchResult pickBestTargetCluster(int v, Clustering C, boolean isCriteriaSmallestWorsening){
		int sourceClusterId = C.membership[v];
		int targetClusterClusterId = -1;
		double bestbalChange = 0.0;
		if(isCriteriaSmallestWorsening)
			bestbalChange = -Double.MAX_VALUE;
		
		for(int newClusterId=1; newClusterId<=(C.getNbCluster()+1); newClusterId++){ // +1 for an empty cluster
			if(newClusterId!=sourceClusterId){
				double balChange = C.calculateBalanceChange(v, newClusterId, adjMat); // positive values mean that 
				if(balChange>bestbalChange){
					bestbalChange = balChange;
					targetClusterClusterId = newClusterId;
				}
			}
		}
		
		ClusterSearchResult result = new ClusterSearchResult(targetClusterClusterId, bestbalChange);
		return(result);
	}
	

	
	
	public class ClusterSearchResult {
		int newClusterId;
		double imbChange;
		
		ClusterSearchResult(int newClusterId_, double imbChange_){
			newClusterId = newClusterId_;
			imbChange = imbChange_;
		}
		
		public int getNewClusterId(){
			return(newClusterId);
		}
		
		public double getImbChange(){
			return(imbChange);
		}
		
	}
}
