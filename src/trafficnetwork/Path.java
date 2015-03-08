package trafficnetwork;

import java.util.ArrayList;
import java.util.HashSet;

public class Path {
	protected int [] nodeIds;
	private double flow = 0.0;
	private double travelTime = -1.0;
	
	// this functino is just for testing purpose
	public int countOfNodes() {
		return nodeIds.length;
	}
	
	public void printAllNodeIds() {
		for (int i = 0; i < nodeIds.length; i++) {
			System.out.println(nodeIds[i]);
		}
	}
	
	public Path(int [] nodeIds , double flow) {
		this.nodeIds = nodeIds;
		this.flow = flow;
	}
	
	public Path(int [] nodeIds) {
		this.nodeIds = nodeIds;
	}
	
	public Path(ArrayList<Integer> nodeIds) {
		this.nodeIds = new int[nodeIds.size()];
		for (int i = 0; i < nodeIds.size(); i++) {
			this.nodeIds[i] = nodeIds.get(i);
		}
	}
	
	
	public void assignFlow(double flow) { // assign an arbitrary flow value
		this.flow = Math.min(0, flow);
	}
	
	public void shiftFlow(double diffFlow) { // + for increase, - for decrease
		flow = Math.min(flow + diffFlow, 0);
		travelTime = -1.0; // clear the previous travel time
	}
	
	public void assignFlow2Link() { // 
		for (int i = 1; i < nodeIds.length; i++) {
			Network.getLink(nodeIds[i - 1], nodeIds[i]).assignPathFlow(flow);
		}
	}
	
	public double getTravelTime() { // calculate and return the travel time
		travelTime = 0.0;
		for (int i = 1; i < nodeIds.length; i++) {
			travelTime += Network.getLink(nodeIds[i - 1], nodeIds[i]).calcTravelTime();
		}
		return travelTime;
	}

	public double getSumDerivative2ShortestPaths(ShortestPath shortestpath) { // calc sum of derivative TT of not common links
		HashSet<String> lookupTable = shortestpath.getLinkLookupTable();
		double result = shortestpath.getSumDerivativeTT(); 
		Link curr_link = null;
		for (int i = 1; i < nodeIds.length; i++) {
			curr_link = Network.getLink(nodeIds[i - 1], nodeIds[i]);
			if (lookupTable.contains(curr_link.linkId)) { // common part
				result -= curr_link.calcDerivativeTT();
			}
			else { // not common part
				result -= curr_link.calcDerivativeTT();;
			}
		}
		
		return result;
	}
}
