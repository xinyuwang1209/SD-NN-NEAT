package NeuralNetwork;

public class OutputNode extends Node{
	public OutputNode(int id) {
		super(id);
		outgoingEdges = null;	//Output nodes don't have outgoing edges
	}

	@Override
	public void fire(){	//if incoming edge fires activate output
		double sigmod = sigmod(sumOfIncomingEdges());
		for(Edge e: incomingEdges){
			if(sigmod >= Math.abs(fireThreshold)) {
				System.out.println("FIRED");
				return;
			}
		}
		System.out.println("failure");
	}
}
