package partitioning;

import myUtils.Clustering;
import myUtils.StdRandom;

public class SimAn {

	int n;
	double[][] adjMat;
	
	public SimAn(double[][] adjMat_){
		n = adjMat_.length;
		adjMat = adjMat_;
	}
	
	
	// this is a synchrone method. When we change the cluster of vertex, we update immediately the underlying partition
	public Clustering solveSimAn(){
//		long startTime = System.currentTimeMillis();
//		double t = 0.0;

		int MAX_NB_STEPS = 1000;
		double Tmax = n/(-4*Math.log(0.8));
		double Tlimit = 0.1;
		
		// CREATE AN INITIAL PARTITION
		int[] membership = new int[n];
		for(int i=0; i<n; i++)
			membership[i] = i+1; // each cluster is in different cluster
		Clustering Cbest = new Clustering(membership, 0); // id=0 (it is not important)
		Cbest.computeImbalance(adjMat);
		
		
		double Tcurr = Tmax;
//		while(Tcurr>Tlimit && t<tilim){
		while(Tcurr>Tlimit){
			Clustering Ccurr = new Clustering(Cbest);
			
			for(int step=0; step<MAX_NB_STEPS; step++){
				for(int v=0; v<n; v++){
					int sourceClusterId = Ccurr.membership[v];
					int nbClusters = Ccurr.getNbCluster();
					
					int newClusterId = sourceClusterId;
					if(nbClusters>1){
						while(newClusterId == sourceClusterId)
							newClusterId = StdRandom.uniform(1,nbClusters+2); // pick a random number between [1,nbClusters+1] >> +1 for empty cluster
					
						double balChange = Ccurr.calculateBalanceChange(v, newClusterId, adjMat); // positive values mean that there is an improvement
						
						boolean update = false;
						if(balChange>0) // if there is an improvement
							update = true;
						else if(balChange<0) { // otherwise, accept this worsening with some probability in terms of the current temperature
							double[] probabilities = new double[2];
							probabilities[0] = Math.exp(balChange/Tcurr); // probability for update
							probabilities[1] = 1 - probabilities[0]; // probability for no update
							if(StdRandom.discrete(probabilities) == 0)
								update = true;
						}
						else {
							// do nothing if balChange = 0
						}
							
						if(update){
							Ccurr.changeClusterOfNode(v, newClusterId, true);
							Ccurr.setImbalance(Ccurr.getImbalance()-balChange);
						}
					}
				}
				
//				long endTime = System.currentTimeMillis();
//				t = (float) (endTime-startTime)/1000;
			}
			
			if(Ccurr.getImbalance() < Cbest.getImbalance())
				Cbest = new Clustering(Ccurr);
			
			// ===========================================================
			// SA gets very time consuming for large graphs.
			// normally, we need to estimate the convergence time in function of n and t,
			// but I could not not find any theoretical finding. So, I use this code block to decrease the running time
			// depending on the value of n
			
			if(this.n<=500)
				Tcurr = (float) (Tcurr*0.98);
			else if(this.n>500 && this.n<=1000)
				Tcurr = (float) (Tcurr*0.95);
			else if(this.n>1000 && this.n<=2000)
				Tcurr = (float) (Tcurr*0.90);
			else if(this.n>2000 && this.n<=4000)
				Tcurr = (float) (Tcurr*0.85);
			else if(this.n>4000 && this.n<=10000)
				Tcurr = (float) (Tcurr*0.80);
			else if(this.n>10000 && this.n<=20000)
				Tcurr = (float) (Tcurr*0.75);
			else if(this.n>20000 && this.n<=50000)
				Tcurr = (float) (Tcurr*0.7);
			else
				Tcurr = (float) (Tcurr*0.6);
			// ===========================================================
		}
		
		return(Cbest);
	}
	
}
