package NeuralNetwork;

public class OutputNode extends Node{
	Boolean fired = false;
	public OutputNode(int id) {
		super(id);
		outgoingEdges = null;	//Output nodes don't have outgoing edges
	}

	@Override
	public void fire(){	//if incoming edge fires activate output
		double sigmod = sigmod(sumOfIncomingEdges());
		
		if(sigmod >= Math.abs(fireThreshold)){
			fired = true;
		}else{
			fired = false;
		}
	}
	
	public boolean checkFired(){
		return fired;
	}
}
