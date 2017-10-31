package Evolution;

public class NodeGene {
	enum NodeType{
		SENSOR, HIDDEN, OUTPUT
	}
	
	int nodeNumber;
	NodeType nodeType;
	
	public NodeGene(int num, NodeType type){
		nodeNumber = num;
		nodeType = type;
	}
	
	int getNodeNumber(){
		return nodeNumber;
	}
	
	NodeType getNodeType(){
		return nodeType;
	}
	
}
