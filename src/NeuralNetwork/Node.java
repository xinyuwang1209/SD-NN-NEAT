package NeuralNetwork;

import java.util.ArrayList;

public abstract class Node {
	protected static final double e = 2.71828;
	protected static final double fireThreshold  = 0.75;
	
	int nodeID;
	ArrayList<Edge> incomingEdges = new ArrayList<Edge>();
	ArrayList<Edge> outgoingEdges = new ArrayList<Edge>();
	
	public Node(int id){
		nodeID = id;
	}
	
	public void addIncomingEdge(Edge e){
		incomingEdges.add(e);
	}
	public void addOutGoingEdge(Edge e){
		outgoingEdges.add(e);
	}
	
	protected double sigmod(double x){		//sigmod function taken from MIT's NEAT paper
		return 1/(1+Math.pow(e, -4.9*x));	//(1/(e^(-4.9x)))
	}
	
	protected double sumOfIncomingEdges(){
		double sum = 0;
		for(Edge e : incomingEdges){		//sum up the outputs of all active input nodes
			if(e.isActive())
				sum += e.getOutput();
		}
		return sum;
	}
	
	public abstract void fire();
}
