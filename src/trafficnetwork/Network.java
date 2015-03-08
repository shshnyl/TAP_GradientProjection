package trafficnetwork;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;


public class Network {
	static public int numNodes;
	static public int numLinks;
	static private Node[] nodes;
	static private HashMap<String, Link> links;
	
	// 
	private Path[] paths = null;
	public ShortestPath shortestpath = null;
	
	public Network(String filename) {
		try{
			this.readFile(filename);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public Path[] generatePaths(int oriId, int desId) { // generate all the paths from ori to des
		// free those memory
		paths = null; 
		// DFS from the destination and travel backwards
		HashSet<Integer> visitedNodeIds = new HashSet<Integer>(); // remember this is a "global" lookup table 
		ArrayList<ArrayList<Integer> > arrayOfNodeIds = this.generatePathsHelper(oriId, desId, visitedNodeIds);
		paths = new Path[arrayOfNodeIds.size()];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = new Path(arrayOfNodeIds.get(i));
		}
		return paths;
	}
	
	private ArrayList<ArrayList<Integer> > generatePathsHelper(int oriId, int desId, HashSet<Integer> visitedNodeIds) {
		if (visitedNodeIds.contains(desId)) // a cycle is created
			return null; 
		if (desId == oriId) { // reach the end
			ArrayList<Integer> tmp = new ArrayList<Integer>(); tmp.add(desId);
			ArrayList<ArrayList<Integer> > result = new ArrayList<ArrayList<Integer> >(); result.add(tmp);
			return result;
		}
		else { // try all prev neighbors
			// marked as visited
			visitedNodeIds.add(desId); 
			
			// add all path from origin to prev neighbors
			Iterator<Integer> itr = nodes[desId - 1].getPrevNodesIterator();
			ArrayList<ArrayList<Integer> > result = new ArrayList<ArrayList<Integer> >()
					, sub_result = null;
			while (itr.hasNext()) {
				sub_result = this.generatePathsHelper(oriId, itr.next(), visitedNodeIds);
				if (sub_result != null) 
					result.addAll(sub_result);
			}
			
			// append self to the result
			Iterator<ArrayList<Integer> > result_itr = result.iterator();
			while (result_itr.hasNext()) {
				result_itr.next().add(desId);
			}
			
			// mark as unvisited
			visitedNodeIds.remove(desId); 
			// return
			return result;
		}
	}
	
	public void assignAllPathFlow(double flow[]) { // assign an arbitrary flow value
		// exclude illegal input argument
		if (flow.length != paths.length) throw new IllegalArgumentException();
		// legal input
		for (int i = 0; i < paths.length; i++) {
			paths[i].assignFlow(flow[i]);
		}
	}
	
	public void shiftAllPathFlow(double diffflow[]) { // shift from the original value
		// exclude illegal input argument
		if (diffflow.length != paths.length) throw new IllegalArgumentException();
		// legal input
		for (int i = 0; i < paths.length; i++) {
			paths[i].shiftFlow(diffflow[i]);
		}
	}
	
	public void clearAllPathFlow() { // assign 0 flow value to all the path
		for (int i = 0; i < paths.length; i++) {
			paths[i].assignFlow(0);
		}
	}
	
	public Path generateShortestPath() { // find the shortest path in terms of travel time
		// exclude cases with no paths
		if (this.paths.length == 0) return null;
		// clear link flows
		Iterator<Link> itr = links.values().iterator();
		while (itr.hasNext()){
			itr.next().clearLinkFlow();
		}
		// assign travel flow
		for (int i = 0; i < this.paths.length; i++) {
			paths[i].assignFlow2Link();
		}
		// update travel time & find shortest path
		int minIndex = 0; double minTT = paths[0].getTravelTime();
		for (int i = 1; i < this.paths.length; i++) {
			double TT = paths[i].getTravelTime();
			if (minTT < TT) {
				minTT = TT;
				minIndex = i;
			}
		}
		// create new shortest path
		shortestpath = new ShortestPath(paths[minIndex]);
		return paths[minIndex];
	}

	private void readFile(String filename) throws IOException { // read file
		URL url = Network.class.getClassLoader().getResource(filename);
		Scanner textScanner = new Scanner(new File(url.getPath()));
		// number of nodes
		numNodes = (textScanner.nextInt()); 
		nodes = new Node[numNodes];
		// for each node
		for (int i = 0; i < numNodes; i++) {
			nodes[i] = new Node(i + 1);
			// let them gone with the wind
			textScanner.nextInt();
			textScanner.nextInt();
		}
		// number of links
		links = new HashMap<String, Link>();
		numLinks =(textScanner.nextInt()); 
		// for each link
		int node1 = 0, node2 = 0;
		double capacity = 0, length = 0, ffs = 0;
		for (int i = 0; i < numLinks; i++) {
			node1 = textScanner.nextInt();
			node2 = textScanner.nextInt();
			capacity = textScanner.nextDouble();
			length = textScanner.nextDouble();
			ffs = textScanner.nextDouble();
			links.put(getLinkHashCode(node1, node2), new Link(node1, node2, capacity, length, ffs));
			getNode(node1).addNeighbor(node2, true);
			getNode(node2).addNeighbor(node1, false);
		}
		// close
		textScanner.close();
	}
	
	static public String getLinkHashCode(int node1, int node2) {
		return Integer.toString(node1) + "_" + Integer.toString(node2);
	}
	
	static public Node getNode(int node) {
		return nodes[node - 1];
	}
	
	static public Link getLink(int node1, int node2) {
		return links.get(getLinkHashCode(node1, node2));
	}
	
	
}
