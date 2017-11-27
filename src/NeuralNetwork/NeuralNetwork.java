package NeuralNetwork;

import java.util.ArrayList;

public class NeuralNetwork{
	protected ArrayList<Layer<HiddenNode>> hiddenLayers = new ArrayList<Layer<HiddenNode>>();
	protected Layer<InputNode> inputLayer = new Layer<InputNode>();
	protected Layer<OutputNode> outputLayer = new Layer<OutputNode>();
	
	public void addInputNode(InputNode in){
		inputLayer.add(in);
	}
	
	public void addOutputNode(OutputNode on){
		outputLayer.add(on);
	}
	
	public void addHiddenLayer(){
		hiddenLayers.add(new Layer<HiddenNode>());
	}
	
	public void addHiddenNode(HiddenNode hn, int layerDepth){
		while(hiddenLayers.size()-1 < layerDepth) //keep adding layers until valid depth
			addHiddenLayer();
		
		hiddenLayers.get(layerDepth).add(hn);
	}
	
	public void connectNodes(Node n1, Node n2){
		Edge e = new Edge(1);
		connectNodes(n1, n2, e);
	}
	
	public void connectNodes(Node n1, Node n2, Edge e){	//adds connection between n1 and n2
		
		for(InputNode n : inputLayer.getNodeList()){
			if(n.equals(n1)){
				e.setNode1(n1);
				n.addOutgoingEdge(e);
			}
		}
		
		for(Layer<HiddenNode> l : hiddenLayers){
			for(Node n : l.getNodeList()){
				if(n.equals(n1)){
					e.setNode1(n1);
					n.addOutgoingEdge(e);
				}
				if(n.equals(n2)){
					e.setNode2(n2);
					n.addIncomingEdge(e);
				}
			}
		}
		
		for(OutputNode n : outputLayer.getNodeList()){
			if(n.equals(n2)){
				e.setNode2(n2);
				n.addIncomingEdge(e);
			}
		}
	}
	
	public int getHiddenNodeLayerDepth(HiddenNode hn){
		for(int i=0; i<hiddenLayers.size(); i++)
			for(HiddenNode n : hiddenLayers.get(i).getNodeList())
				if(n.equals(hn))
					return i;
		return -1;
	}
	
	public void moveHiddenNodeDeeper(HiddenNode hn){
		for(int i=0; i<hiddenLayers.size(); i++){
			for(HiddenNode n : hiddenLayers.get(i).getNodeList()){
				if(hn.equals(n) && i<hiddenLayers.size()-1){			//if there's a layer above the current layer move it there
					hiddenLayers.get(i).remove(n);
					hiddenLayers.get(i+1).add(n);
					return;
				}else if(hn.equals(n)){									//else add a new layer and move the node there
					hiddenLayers.add(new Layer<HiddenNode>());
					hiddenLayers.get(i).remove(n);
					hiddenLayers.get(i+1).add(n);
					return;
				}
			}
		}
	}
	
	public void moveHiddenSubTreeDeeper(HiddenNode n, Boolean includeRoot){		//recursively move a sub tree of hidden nodes 1 layer deeper	
		if(includeRoot)
			moveHiddenNodeDeeper(n);
		
		for(Edge e : n.getOutgoingEdges())
			if(e.getNode2() instanceof HiddenNode)
				moveHiddenSubTreeDeeper((HiddenNode)e.getNode2(), true);
	}
	
	public ArrayList<Layer<HiddenNode>> getHiddenLayers(){
		return hiddenLayers;
	}
	
	public ArrayList<InputNode> getInputNodes(){
		return inputLayer.getNodeList();
	}
	
	public ArrayList<OutputNode> getOutputNodes(){
		return outputLayer.getNodeList();
	}
	
	public void execute(){
		for(InputNode n : inputLayer.getNodeList()){	//update input values
			n.fire();
		}
		
		for(Layer<HiddenNode> l : hiddenLayers){
			for(Node n : l.getNodeList()){
				n.fire();
			}
		}
		
		for(OutputNode n : outputLayer.getNodeList()){
			n.fire();
		}
	}
}
