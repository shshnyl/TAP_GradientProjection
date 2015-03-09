package algorithm;

import java.util.Iterator;

import trafficnetwork.Network;
import trafficnetwork.Path;
import trafficnetwork.ShortestPath;

public class GradientProjection {

	public static void main(String[] args) {
		try {
			Network sf1 = new Network("sf.1");
			Iterator<Path> itr = null;
			int oriId = 8, desId = 16, numIteration = 2500; double demand = 40;

			// Initialize the paths, assign all demand on the shortest
			ShortestPath shortestpath = sf1.generateShortestPath(oriId, desId);
			shortestpath.assignFlow(demand); shortestpath.calcTravelTime(); 
			// start a loop
			for (int i = 0; i < numIteration; i++) {
				// Calculate Shortest Path and add it into path set(if different)
				shortestpath = sf1.generateShortestPath(oriId, desId);
				shortestpath.calcTravelTime(); 
				//gradient algorithm
				double sum = 0;
				itr = Network.paths.values().iterator();
				while (itr.hasNext()) {
					ShortestPath tmp = (ShortestPath) itr.next();
					if (tmp == shortestpath) {
						continue;
					}
					
					double diffflow = 
							(tmp.getTravelTime() - shortestpath.getTravelTime()) 
							/ tmp.calcSumDerivative2ShortestPaths(shortestpath);
					
					sum += tmp.shiftFlow(-diffflow);
				}
				shortestpath.shiftFlow(-sum);
				// update travel time
				itr = Network.paths.values().iterator();
				while (itr.hasNext()) {
					itr.next().calcTravelTime();
				}
			}

			System.out.println(Network.paths.size());
			
			System.out.println("The output flows are: ");
			itr = Network.paths.values().iterator();
			while (itr.hasNext()) {
				Path path = itr.next();
				path.printAllNodeIds();
				System.out.println("flow: " + path.getFlow());
				System.out.println("time: " + path.getTravelTime());
			}
			
		} catch (Exception e) {
			System.out.println("Error!");
			System.out.println(e.getMessage());
		}
	}

}