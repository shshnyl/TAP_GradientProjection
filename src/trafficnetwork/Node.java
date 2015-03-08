package trafficnetwork;

import java.util.ArrayList;
import java.util.Iterator;

public class Node {
	
	final private int nodeId;
	final private ArrayList<Integer> prevNodes;
	final private ArrayList<Integer> nextNodes;
	
	public Node(int id) {
		this.nodeId = id;
		this.prevNodes = new ArrayList<Integer>();
		this.nextNodes = new ArrayList<Integer>();
	}
	
	public void addNeighbor(int nodeId, boolean isNext) {
		if (isNext) {
			nextNodes.add(nodeId);
		}
		else {
			prevNodes.add(nodeId);
		}
	}
	
	public Iterator<Integer> getPrevNodesIterator() {
		return prevNodes.iterator();
	}
	
	public Iterator<Integer> getNextNodesIterator() {
		return nextNodes.iterator();
	}
}
