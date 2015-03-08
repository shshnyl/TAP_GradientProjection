package algorithm;

import java.io.PrintWriter;
import java.util.Iterator;

import trafficnetwork.Network;
import trafficnetwork.Path;

public class GradientProjection {

	public static void main(String[] args) {
		try {
			Network sf1 = new Network("sf.2");
			
			int oriId = 1, desId = 4;

			//Iterator<Integer> itr= Network.getNode(20).getPrevNodesIterator();
			//while (itr.hasNext()) {
				//System.out.println(itr.next());
			//}
			
			// step 1: get all the paths, which are constant for every OD pair
			Path[] paths= sf1.generatePaths(oriId, desId);
			System.out.println("Number of paths: " + paths.length);
			for (int i = 0; i < paths.length; i++) {
				System.out.println("Path" +  i);
				paths[i].printAllNodeIds();
				//System.out.println(paths[i].countOfNodes());
			}
			// setp 2: init flow on each link
			/*
			double flow[] = new double [paths.length];
			double diffflow[] = new double [paths.length];
			boolean certaincondition = false;
			sf1.assignAllPathFlow(flow);
			// step 3: start a loop
			while (certaincondition) {
				// step 3.1 generate the current shortest path
				sf1.generateShortestPath();
				// step 3.2 gradient algorithm
				for (int i = 0; i < paths.length; i++) { // the shortest path will return 0, which means no change in flow
					diffflow[i] = 
							(paths[i].getTravelTime() - sf1.shortestpath.getTravelTime()) 
							/ paths[i].getSumDerivative2ShortestPaths(sf1.shortestpath);
				}
				
				
				// step 3.3
				sf1.shiftAllPathFlow(diffflow); // note here we automatically apply projection inside the path class
			}
			// step 4: output?
			*/
			
		} catch (Exception e) {
			;
		}
	}

}
