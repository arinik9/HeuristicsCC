package partitioning;

import myUtils.Clustering;
import myUtils.StdRandom;

public class RelocationHeuristicWithVNS {

	int n;
	double[][] adjMat;
	
	public RelocationHeuristicWithVNS(double[][] adjMat_){
		n = adjMat_.length;
		adjMat = adjMat_;
	}
	
	
	public Clustering solveRelocationHeuristicWithVNS(double tilim){
		
		long startTime = System.currentTimeMillis();
		double t = 0.0;
		// variables of VNS
		double ymin = 0.005;
		double ymax = 0.2;
		double ystep = 0.005;
		double ypert = ymin;
		
		///////////////////////////////////////////////////////////
		// CREATE INITIAL PARTITION
		int kmax = 20;
		if(n<kmax)
			kmax = n/2;
		int k = StdRandom.uniform(2,kmax+1); // pick a random number from [2,kmax]
		RandomKPartitioning kPartitioning = new RandomKPartitioning(n, k, adjMat);
		Clustering Cbest = kPartitioning.generateRandomPartition();
		//System.out.println(Cbest);
		Cbest.computeImbalance(adjMat);
			
		
		while(t<tilim){
			boolean improv = false;
			Clustering Ccurr = new Clustering(Cbest);
			
			///////////////////////////////////////////////////////////
			// PERTURBATION 
			for(int v=0; v<n; v++){
				int sourceClusterId = Ccurr.membership[v];
				int nbClusters = Ccurr.getNbCluster();
				double rnd = StdRandom.uniform();
				if(rnd < ypert){
					int newClusterId = sourceClusterId;
					if(nbClusters>1){
						while(newClusterId == sourceClusterId)
							newClusterId = StdRandom.uniform(1,nbClusters+2); // pick a random number between [1,nbClusters+1]
						double balChange = Ccurr.calculateBalanceChange(v, newClusterId, adjMat); // positive values mean that there is an improvement
						Ccurr.changeClusterOfNode(v, newClusterId, true);
						Ccurr.setImbalance(Ccurr.getImbalance()-balChange);
					}
					else 
						break;
				}
			}
			
			///////////////////////////////////////////////////////////
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
			///////////////////////////////////////////////////////////

			
			if(Ccurr.getImbalance() < Cbest.getImbalance()){
				Cbest = new Clustering(Ccurr);
				ypert = ymin;
			} else {
				ypert += ystep;
				if(ypert>ymax)
					ypert = ymin;
			}
			
			long endTime = System.currentTimeMillis();
			t = (float) (endTime-startTime)/1000;
		}
		
		return(Cbest);
	}
	
	
}
