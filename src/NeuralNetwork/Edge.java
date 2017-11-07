package NeuralNetwork;

public class Edge {
	double weight;
	boolean active = false; //set true when neuron fires on edge else false
	boolean enabled = true;
	double input = 0;
	Node node1;
	Node node2;
	
	public Edge(double w){	//Every edge must have an initial weight
		weight = w;
	}
	
	public void setWeight(double w){
		weight = w;
	}
	
	public double getWeight(){
		return weight;
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
	
	public void setNode1(Node n){
		node1 = n;
	}
	
	public void setNode2(Node n){
		node2 = n;
	}
	
	public Node getNode1(){
		return node1;
	}
	
	public Node getNode2(){
		return node2;
	}
	
	public void setEnabled(Boolean enable){
		enabled = enable;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
}
