package Evolution;

import NeuralNetwork.Node;

public class NodeGene {
	enum NodeType{
		SENSOR, HIDDEN, OUTPUT
	}
	
	Node node;
	NodeType nodeType;
	
	public NodeGene(Node n, NodeType type){
		node = n;
		nodeType = type;
	}
	
	Node getNode(){
		return node;
	}
	
	NodeType getNodeType(){
		return nodeType;
	}
	
}
