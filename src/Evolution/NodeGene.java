package Evolution;

import java.io.Serializable;

import NeuralNetwork.Node;

public class NodeGene implements Serializable{
	private static final long serialVersionUID = -3484116647023789024L;

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
