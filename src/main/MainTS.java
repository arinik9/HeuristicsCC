package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import myUtils.Clustering;
import partitioning.RelocationHeuristicWithTabuSearch;
import partitioning.RelocationHeuristicWithVNS;


/**
* source: Brusco and Doreian. Partitioning signed networks using relocation heuristics, tabu search, and variable neighborhood search. 2019
* https://www.sciencedirect.com/science/article/pii/S0378873318302260
*
*/
public class MainTS {

	
	static String tempFile = "temp.txt";
	
	/**
	 * 
	 * Input parameters:
	 * <ul>
	 * <li> inFile (String): Input file path. </li>
	 * <li> outDir (String): Output directory path. Default "." 
	 * 		(i.e. the current directory). </li>
	 * <li> tilim (Integer): Time limit in seconds. </li>
	 * </ul>
	 * 

	 * 
	 * @param args  (Not used in this program. Instead, user parameters are obtained
	 * 	 through ant properties. See the buil.xml for more details).
	 * 
	 * @throws FileNotFoundException.
	 */
	public static void main(String[] args) throws FileNotFoundException {
		int tilim = 300; // in seconds
		String inputFilePath = "";
		String outputDirPath = ".";

		if( !System.getProperty("inputFilePath").equals("${inputFilePath}") )
			inputFilePath = System.getProperty("inputFilePath");
		else {
			System.out.println("inputFilePath is not specified. Exit");
			return;
		}

		if( !System.getProperty("outDir").equals("${outDir}") )
			outputDirPath = System.getProperty("outDir");
		
		// Those 4 options are  available with cutting plane approach
		if(!System.getProperty("tilim").equals("${tilim}") )
			tilim = Integer.parseInt(System.getProperty("tilim"));
		
		
		System.out.println("===============================================");
		System.out.println("inputFilePath: " + inputFilePath);
		System.out.println("outputDirPath: " + outputDirPath);
		System.out.println("tilim: " + tilim + "s");
		System.out.println("===============================================");
		
		
		//StdRandom.setSeed(12345);


		new File(outputDirPath).mkdirs();
		
		double[][] adjMat = createAdjMatrixFromInput(inputFilePath);

		int tabuCapacity = 10;
		RelocationHeuristicWithTabuSearch rh = new RelocationHeuristicWithTabuSearch(adjMat, tabuCapacity);
		Clustering Cinit = rh.solveRelocationHeuristic(tilim/2);
		System.out.println("relocation heuristic finished");
		Clustering Cres = rh.solveTabuSearch(Cinit, tilim/2);
		System.out.println("tabu search finished");
		System.out.println("imbalance: " + Cres.getImbalance());
		System.out.println(Cres);
		
		//String fileName = "membership0.txt";
        String fileName = "sol.txt";
		Cres.writeMembership(outputDirPath, fileName);
		
		System.out.println("end");
	}
	
	
	
	
	
	
	
	
	
	/**
	 * This method reads input graph file, then stocks it as weighted adjacency matrix, 
	 * finally writes the graph in lower triangle format into a temp file.
	 * 
	 * @param filename  input graph filename
	 * @return 
	 */
	//private static double[][] createAdjMatrixFromInput(String fileName, boolean isReducedTriangleConstraints) {
	private static double[][] createAdjMatrixFromInput(String fileName) {
		
		  double[][] weightedAdjMatrix = null;
		  int n = -1;
		// =====================================================================
		// read input graph file
		// =====================================================================
		try{
		  InputStream  ips=new FileInputStream(fileName);
		  InputStreamReader ipsr=new InputStreamReader(ips);
		  BufferedReader   br=new
		  BufferedReader(ipsr);
		  String ligne;
		  
		  ligne = br.readLine();
		  
		  /* Get the number of nodes from the first line */
		  n = Integer.parseInt(ligne.split("\t")[0]);
		  

		  weightedAdjMatrix = new double[n][n];
		  if(weightedAdjMatrix[0][0] != 0.0d)
			  System.out.println("Main: Error default value of doubles");
		  
		  /* For all the other lines */
		  while ((ligne=br.readLine())!=null){
			  
			  String[] split = ligne.split("\t");
			  
			  if(split.length >= 3){
				  int i = Integer.parseInt(split[0]);
				  int j = Integer.parseInt(split[1]);
				  double v = Double.parseDouble(split[2]);
				  weightedAdjMatrix[i][j] = v;
				  weightedAdjMatrix[j][i] = v;
			  }
			  else
				  System.err.println("All the lines of the input file must contain three values" 
						+ " separated by tabulations"
						+ "(except the first one which contains two values).\n"
				  		+ "Current line: " + ligne);
		  }
		  br.close();
		}catch(Exception e){
		  System.out.println(e.toString());
		  n = -1;
		}
		// end =================================================================
		
		
		
//		
//		// 2nd phase =============================================================
//		// put negative epsilon values  when links are missing between nodes
//		if(isReducedTriangleConstraints && weightedAdjMatrix != null && n!=-1){
//			double w = (float) -1/n;
//			for(int i=1; i<weightedAdjMatrix.length; i++){
//				for(int j=0; j<i; j++){
//					//System.out.println("i:"+i+", j:"+j+", w:"+weightedAdjMatrix[i][j]);
//					if(weightedAdjMatrix[i][j] == 0.0d){
//						//System.out.println("i:"+i+", j:"+j+", w:"+weightedAdjMatrix[i][j]);
//						weightedAdjMatrix[i][j] = w;
//						weightedAdjMatrix[j][i] = w;
//					}
//				}
//			}
//		}
//		// end 2nd phase =================================================================
//
//
//		for(int i=1; i<weightedAdjMatrix.length; i++){
//			for(int j=0; j<i; j++){
//				//System.out.println("i:"+i+", j:"+j+", w:"+weightedAdjMatrix[i][j]);
//			}
//		}

		
//		// =====================================================================
//		// write into temp file (in lower triangle format)
//		// =====================================================================
//		if(weightedAdjMatrix != null){
//			 try{
//			     FileWriter fw = new FileWriter(tempFile, false);
//			     BufferedWriter output = new BufferedWriter(fw);
//
//			     for(int i = 1 ; i < weightedAdjMatrix.length ; ++i){
//			    	 String s = "";
//			    	 
//			    	 for(int j = 0 ; j < i ; ++j) // for each line, iterate over columns
//			    		 s += weightedAdjMatrix[i][j] + " ";
//
//			    	 s += "\n";
//			    	 output.write(s);
//			    	 output.flush();
//			     }
//			     
//			     output.close();
//			 }
//			 catch(IOException ioe){
//			     System.out.print("Erreur in reading input file: ");
//			     ioe.printStackTrace();
//			 }
//
//		}
//		// end =================================================================

		return(weightedAdjMatrix);
	}

}
