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
	public static HashMap<String, Path> paths = new HashMap<String, Path>();
	
	public Network(String filename) {
		try{
			this.readFile(filename);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void assignAllPathFlow(double flow[]) { // assign an arbitrary flow value
		// exclude illegal input argument
		if (flow.length != paths.size()) throw new IllegalArgumentException();
		// legal input
		int i = 0;
		Iterator<Path> itr = paths.values().iterator();
		while (itr.hasNext()) {
			itr.next().assignFlow(flow[i]);
			i++;
		}
	}
	
	public void shiftAllPathFlow(double diffflow[]) { // shift from the original value
		// exclude illegal input argument
		if (diffflow.length != paths.size()) throw new IllegalArgumentException();
		// legal input
		int i = 0;
		Iterator<Path> itr = paths.values().iterator();
		while (itr.hasNext()) {
			itr.next().shiftFlow(diffflow[i]);
			i++;
		}
	}
	
	public void clearAllPathFlow() { // assign 0 flow value to all the path
		Iterator<Path> itr = paths.values().iterator();
		while (itr.hasNext()) {
			itr.next().assignFlow(0);
		}
	}
	
	public ShortestPath generateShortestPath(int oriId, int desId) { // find the shortest path in terms of travel time
		HashSet<Integer> visitedNodes = new HashSet<Integer>();
		ArrayList<Double> nodeDist = new ArrayList<Double>();
		ArrayList<Integer> prevNodeIds = new ArrayList<Integer>();
		int frontierId = 0, neighborId = 0;
		double frontierDist, neighborDist;
		// init
		for (int i = 0; i < Network.numNodes; i++) {
			nodeDist.add(Double.MAX_VALUE);
			prevNodeIds.add(0);
		}
		nodeDist.set(oriId - 1, 0.0);
		
		// dijkstra
		while (visitedNodes.size() < Network.numNodes) {
			// picking the shortest one from the node
			frontierId = 0; frontierDist = Double.MAX_VALUE;
			for (int i = 0; i < Network.numNodes; i++) {
				if (!visitedNodes.contains(i + 1) && nodeDist.get(i) < frontierDist) {
					frontierId = i + 1;
					frontierDist = nodeDist.get(i);
				}
			}
			visitedNodes.add(frontierId);
			// if it is the destination
			if (frontierId == desId) 
				break;
			// otherwise
			// update neighbors
			Iterator<Integer> itr = Network.getNode(frontierId).getNextNodesIterator();
			while (itr.hasNext()) {
				neighborId = itr.next();
				neighborDist = frontierDist + Network.getLink(frontierId, neighborId).calcTravelTime();	
				if (visitedNodes.contains(neighborId)) continue;
				
				double old_neighborDist = nodeDist.get(neighborId - 1);
				if (neighborDist < old_neighborDist) {
					nodeDist.set(neighborId - 1, neighborDist);
					prevNodeIds.set(neighborId - 1, frontierId);
				}
			}
		}

		// generate and add the path
		int id = desId;
		ArrayList<Integer> nodeIds = new ArrayList<Integer>(); 
		while (id != 0) {
			nodeIds.add(0, id);
			id = prevNodeIds.get(id - 1);
		}
		
		ShortestPath path = null;
		String pathId = getPathHashCode(nodeIds);
		if (paths.containsKey(pathId)) { // 
			path = (ShortestPath) paths.get(pathId);
		}
		else {
			path = new ShortestPath(nodeIds);
			paths.put(pathId, path);
		}
		//path.travelTime = nodeDist.get(desId - 1);
		return path;
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
	
	static public double getObjectiveFunc() {
		double result = 0.0;
		Iterator<Link> itr = links.values().iterator();
		while (itr.hasNext()) {
			result += itr.next().calcIntegrationTT();
		}
		return result;
	}
	
	static public String getLinkHashCode(int node1, int node2) {
		return Integer.toString(node1) + "_" + Integer.toString(node2);
	}
	
	static public String getPathHashCode(ArrayList<Integer> nodeIds) {
		String result = new String();
		for (int i = 0; i <nodeIds.size(); i++) {
			result += nodeIds.get(i);
		}
		return result;
	}
	
	static public Node getNode(int node) {
		return nodes[node - 1];
	}
	
	static public Link getLink(int node1, int node2) {
		return links.get(getLinkHashCode(node1, node2));
	}
	
	
}
