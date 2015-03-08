package trafficnetwork;

import java.util.ArrayList;
import java.util.HashSet;

public class ShortestPath extends Path {
	
	private HashSet<String> linkIdLookupTable;
	private double sumDerivativeTT;
	
	public ShortestPath(int [] nodeIds) {
		super(nodeIds);
		this.createLinkLookupTable();
		this.calcSumDerivativeTT();
	}
	
	public ShortestPath(int [] nodeIds, double flow) {
		super(nodeIds, flow);
		this.createLinkLookupTable();
		this.calcSumDerivativeTT();
	}
	
	public ShortestPath(Path path) {
		super(path.nodeIds);
		this.createLinkLookupTable();
		this.calcSumDerivativeTT();
	}
	
	private void calcSumDerivativeTT() { // sum of the first derivative of link time 
		Link curr_link = null;
		sumDerivativeTT = 0.0;
		for (int i = 1; i < nodeIds.length; i++) {
			curr_link = Network.getLink(nodeIds[i - 1], nodeIds[i]);
			sumDerivativeTT += curr_link.calcDerivativeTT();
		}
	}
	
	private void createLinkLookupTable() {
		linkIdLookupTable = new HashSet<String>();
		for (int i = 1; i < nodeIds.length; i++) {
			linkIdLookupTable.add(Network.getLinkHashCode(nodeIds[i - 1], nodeIds[i]));
		}
	}
	
	public HashSet<String> getLinkLookupTable() {
		return this.linkIdLookupTable;
	}
	
	public double getSumDerivativeTT() { // sum of the first derivative of link time 
		return this.sumDerivativeTT;
	}
}
