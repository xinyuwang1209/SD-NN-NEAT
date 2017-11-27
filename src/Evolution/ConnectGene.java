package Evolution;

import NeuralNetwork.Edge;
import NeuralNetwork.Node;

public class ConnectGene {
	Node inNode;
	Node outNode;
	Edge edge;
	Boolean enabled;
	int innovationNumber;
	
	public ConnectGene(Node in, Node out, Edge e, int innov){
		this(in, out, e, true, innov);
	}
	
	public ConnectGene(Node in, Node out, Edge e, Boolean enable, int innov){
		inNode = in;
		outNode = out;
		edge = e;
		enabled = enable;
		innovationNumber = innov;
	}
	
	Node getInNode(){
		return inNode;
	}
	
	Node getOutNode(){
		return outNode;
	}
	
	Edge getEdge(){
		return edge;
	}
	
	double getWeight(){
		return edge.getWeight();
	}
	
	void setWeight(double w){
		edge.setWeight(w);
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	void setEnabled(Boolean enable){
		enabled = enable;
	}
	
	void toogleEnable(){
		enabled = !enabled;
	}
	
	int getInnovationNumber(){
		return innovationNumber;
	}
	
	void setInnovationNumber(int iv){
		innovationNumber = iv;
	}
}
