package algorithm;

import java.io.PrintWriter;
import java.util.Iterator;

import trafficnetwork.Network;
import trafficnetwork.Path;
import trafficnetwork.ShortestPath;

public class GradientProjection {

	public static void main(String[] args) {
		try {
			Network sf1 = new Network("sf.1");
			int oriId = 8, desId = 16, numIteration = 15; double demand = 1000;

			// Initialize the paths, assign all demand on the shortest
			Path tmp_sp = sf1.generateShortestPath(oriId, desId);
			tmp_sp.assignFlow(demand);
			int i = 0;
			// start a loop
			while (i++ < numIteration) {
				// update travel time
				Iterator<Path> it = Network.paths.values().iterator();
				while (it.hasNext()) {
					Path temp = it.next();
					temp.getTravelTime();
				}
				// Calculate Shortest Path and add it into path set(if different)
				Path shortest_p = sf1.generateShortestPath(oriId, desId);

				//gradient algorithm
				it = Network.paths.values().iterator();
				double sum = 0;
				while (it.hasNext()) {
					Path tmp = it.next();
					if (tmp == shortest_p) {
						continue;
					}
					
					//System.out.println("shortest_path "); shortest_p.printAllNodeIds();
					//System.out.println("tmp "); tmp.printAllNodeIds();

					double tmpVal = tmp.getSumDerivative2ShortestPaths((ShortestPath) shortest_p);
					double diffflow = (tmp.travelTime - shortest_p.travelTime) / tmpVal;
					sum += tmp.shiftFlow(-diffflow);
				}

				shortest_p.shiftFlow(-sum);
			}

			System.out.println(Network.paths.size());
			
			//System.out.println("The output flows are: ");
			Iterator<Path> itr = Network.paths.values().iterator();
			while (itr.hasNext()) {
				Path path = itr.next();
				//System.out.println(path.getHashCode());
				System.out.println("flow: " + path.getFlow());
			}
			
		} catch (Exception e) {
			System.out.println("Error!");
			System.out.println(e.getMessage());
		}
	}

}