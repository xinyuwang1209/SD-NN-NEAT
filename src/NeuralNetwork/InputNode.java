package NeuralNetwork;

public class InputNode extends Node{
	double input = 0;
	
	public InputNode(int id) {
		super(id);
		incomingEdges = null;
	}

	@Override
	public void fire(){	//update outgoing edge with current input value for node
		for(Edge e : outgoingEdges){
			e.setActive(true);
			e.setInput(input);
		}
	}
	
	public void setInput(double i){
		input = i;
	}
	
}
