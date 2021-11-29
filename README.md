# HeuristicsCC

(Meta)Heuristic methods for the Correlation Clustering problem

* Copyright 2020-21 Nejat Arınık

*HeuristicsCC* is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation. For source availability and license information see the file `LICENCE`

* GitHub repo: https://github.com/arinik9/HeuristicsCC
* Contact: Nejat Arınık <arinik9@gmail.com>


-----------------------------------------------------------------------



# Description

This program proposes several (meta)heuristic methods for the Correlation Clustering problem. The implemented heuristic methods are as follows:

* **SA:** Simulated Annealing ([reference paper](https://doi.org/10.1088/1751-8113/42/34/345003))
* **TS:** Tabu Search ([reference paper](https://doi.org/10.1016/j.socnet.2018.08.007))
* **VNS:** Variable Neighborhood Search ([reference paper](https://doi.org/10.1016/j.socnet.2018.08.007))

See Chapter 2 in *[Arınık'21]* for more details.



# Use
1. Install [ant](https://ant.apache.org/)
2. Compile the heuristic of interest:
   1. Simulated Annealing: ``` ant -buildfile build-sa.xml compile jar ```
   2. Tabu Search: ``` ant -buildfile build-ts.xml compile jar``` 
   3. Variable Neighborhood Search: ``` ant -buildfile build-vns.xml compile jar``` 
3. Run the heuristic of interest (check the following scripts for more details about how to run):
   1. Simulated Annealing: ``` ./run-sa.sh``` 
   2. Tabu Search: ``` ./run-ts.sh``` 
   3. Variable Neighborhood Search: ``` ./run-vns.sh``` 



## References
* **[Arınık'21]** N. Arınık, [*Multiplicity in the Partitioning of Signed Graphs*](https://www.theses.fr/2021AVIG0285). PhD thesis in Avignon Université (2021).