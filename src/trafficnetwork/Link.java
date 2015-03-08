package trafficnetwork;

public class Link {
	// nodes
	final public int inNodeId;
	final public int outNodeId;
	final public String linkId;
	// attrs
	final private double capacity;
	// BPR parameters
	final private double t0;
	final private double n = 4;
	final private double alpha = 0.15;
	// flow assigned
	public double flow;
	
	public Link(int node1, int node2, double capacity, double length, double ffs) {
		// final data members
		this.inNodeId = node1;
		this.outNodeId = node2;
		this.linkId = Network.getLinkHashCode(node1, node2);
		this.capacity = capacity;
		// zero init flow
		this.flow = 0;
		this.t0 = length / ffs;
	}
	
	public void assignLinkFlow(double flow) { // assign link with certain flow
		this.flow = flow;
	}
	
	public void clearLinkFlow() { 
		this.flow = 0;
	}

	public void assignPathFlow(double pathflow) { // assign link with certain flow from path
		this.flow += pathflow;
	}
	
	public void clearPathFlow(double pathflow) { 
		this.flow = Math.min(this.flow - pathflow, 0);
	}
	
	public double calcTravelTime(double flow) { // given the flow and calculate tt
		this.flow = flow;
		return this.calcTravelTime();
	}
	
	public double calcTravelTime() { // calculate tt with the flow
		return t0 * (1 + alpha * Math.pow(flow / capacity, n));
	}
	
	public double calcDerivativeTT() { // first derivative 
		if (flow == 0) 
			return 0;
		else
			return (alpha * t0 * n / capacity) * Math.pow(flow / capacity, n - 1);
	}
	
}
