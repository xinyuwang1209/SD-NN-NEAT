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
			if(e.isEnabled()){		//only fire the edge if its enabled
				e.setActive(true);
				e.setInput(input);
			}else{
				e.setActive(false);
			}
		}
	}
	
	public void setInput(double i){
		input = i;
	}
}
