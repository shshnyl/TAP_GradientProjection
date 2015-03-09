package trafficnetwork;

import java.util.ArrayList;
import java.util.HashSet;

public class Path {
	protected int [] nodeIds;
	private double flow = 0.0;
	private double travelTime = 0.0;
	
	// this function is just for testing purpose
	public int countOfNodes() {
		return nodeIds.length;
	}
	// this function is just for testing purpose
	public void printAllNodeIds() {
		String output = new String();
		for (int i = 0; i < nodeIds.length; i++) {
			output += (nodeIds[i]) + " ";
		}
		System.out.println(output);
	}
	// this function is just for testing purpose
	public double getFlow() {
		return this.flow;	
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
		this.clearFlow2Link();
		this.flow = Math.max(0, flow);
		this.assignFlow2Link();
	}
	
	public double shiftFlow(double diffFlow) { // + for increase, - for decrease
		this.clearFlow2Link();
		double old_flow = flow;
		flow = Math.max(flow + diffFlow, 0);
		this.assignFlow2Link();
		return flow - old_flow;
	}
	
	private void assignFlow2Link() { // 
		for (int i = 1; i < nodeIds.length; i++) {
			Network.getLink(nodeIds[i - 1], nodeIds[i]).assignPathFlow(flow);
		}
	}
	
	private void clearFlow2Link() {
		for (int i = 1; i < nodeIds.length; i++) {
			Network.getLink(nodeIds[i - 1], nodeIds[i]).clearPathFlow(flow);
		}
	}
	
	public void calcTravelTime() { // calculate and return the travel time
		travelTime = 0.0;
		for (int i = 1; i < nodeIds.length; i++) {
			travelTime += Network.getLink(nodeIds[i - 1], nodeIds[i]).calcTravelTime();
		}
	}
	
	public double getTravelTime() {
		return travelTime;
	}
	
	public double calcSumDerivative2ShortestPaths(ShortestPath shortestpath) { // calc sum of derivative TT of not common links
		HashSet<String> lookupTable = shortestpath.getLinkLookupTable();
		double result = shortestpath.getSumDerivativeTT(); 
		Link curr_link = null;
		for (int i = 1; i < nodeIds.length; i++) {
			curr_link = Network.getLink(nodeIds[i - 1], nodeIds[i]);
			if (lookupTable.contains(curr_link.linkId)) { // common part
				result -= curr_link.calcDerivativeTT();
			}
			else { // not common part
				result += curr_link.calcDerivativeTT();;
			}
		}
		
		return result;
	}
	

}
