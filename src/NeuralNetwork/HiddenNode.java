package NeuralNetwork;

public class HiddenNode extends Node{
	
	public HiddenNode(int id){
		super(id);
	}
	
	public int getNodeID(){
		return nodeID;
	}
	
	@Override
	public void fire(){
		double sigmod = sigmod(sumOfIncomingEdges());	//normalize the sum
		if(sigmod >= Math.abs(fireThreshold)){	//if we fire set all output edges to active and update their inputs
			for(Edge e : outgoingEdges){
				e.setInput(sigmod);
				e.setActive(true);
			}
		}else{								//if we don't fire set all output edges to be inactive
			for(Edge e : outgoingEdges){
				e.setActive(false);
			}
		}
	}
}
