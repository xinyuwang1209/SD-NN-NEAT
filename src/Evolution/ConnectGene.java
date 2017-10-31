package Evolution;

import NeuralNetwork.Node;

public class ConnectGene {
	Node inNode;
	Node outNode;
	double weight;
	Boolean enabled;
	int innovationNumber;
	
	public ConnectGene(Node in, Node out, double w, int innov){
		this(in, out, w, true, innov);
	}
	
	public ConnectGene(Node in, Node out, double w, Boolean enable, int innov){
		inNode = in;
		outNode = out;
		weight = w;
		enabled = enable;
		innovationNumber = innov;
	}
	
	Node getIn(){
		return inNode;
	}
	
	Node getOut(){
		return outNode;
	}
	
	double getWeight(){
		return weight;
	}
	
	void setWeight(double w){
		weight = w;
	}
	
	boolean isEnabled(){
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
	
}
