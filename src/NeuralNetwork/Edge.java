package NeuralNetwork;

public class Edge {
	double weight;
	boolean active = false; //set true when neuron fires on edge else false
	double input = 0;
	Node node;
	
	public Edge(double w){	//Every edge must have an initial weight
		weight = w;
	}
	
	public void setWeight(double w){
		weight = w;
	}
	
	public double getOutput(){ //output = w*x
		return input*weight;
	}
	
	public void setInput(double i){
		input = i;
	}
	
	public void setActive(boolean a){
		active = a;
	}
	
	public boolean isActive(){
		return active;
	}
}
