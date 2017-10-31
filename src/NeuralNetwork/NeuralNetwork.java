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
		try{
			hiddenLayers.get(layerDepth).add(hn);
		}catch(Exception e){						//keep adding layers until valid depth
			addHiddenLayer();
			addHiddenNode(hn, layerDepth);
		}
	}
	
	public void connectNodes(Node n1, Node n2){	//adds connection between n1 and n2
		Edge e = new Edge(1);
		
		for(InputNode n : inputLayer.getNodeList()){
			if(n.equals(n1)){
				e.setNode1(n1);
				n.addOutGoingEdge(e);
			}
		}
		
		for(Layer<HiddenNode> l : hiddenLayers){
			for(Node n : l.getNodeList()){
				if(n.equals(n1)){
					e.setNode1(n1);
					n.addOutGoingEdge(e);
				}
				if(n.equals(n2)){
					e.setNode2(n2);
					n.addOutGoingEdge(e);
				}
			}
		}
		
		for(OutputNode n : outputLayer.getNodeList()){
			if(n.equals(n2)){
				e.setNode2(n2);
				n.addOutGoingEdge(e);
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
				if(hn.equals(n) && i<hiddenLayers.size()-1){	//if there's a layer above the current layer move it there
					hiddenLayers.get(i).remove(n);
					hiddenLayers.get(i+1).add(n);
					return;
				}else{											//else add a new layer and move the node there
					hiddenLayers.add(new Layer<HiddenNode>());
					hiddenLayers.get(i).remove(n);
					hiddenLayers.get(i+1).add(n);
					return;
				}
			}
		}
	}
	
	public void moveHiddenSubTreeDeeper(Node n, Boolean includeRoot){		//recursively move a sub tree of hidden nodes 1 layer deeper
		for(Edge e : n.getOutgoingEdges()){
			if(e.getNode2() instanceof HiddenNode){
				moveHiddenSubTreeDeeper(e.getNode2(), true);
				if(includeRoot)
					moveHiddenNodeDeeper((HiddenNode)e.getNode2());
			}
		}
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
		for(InputNode n : inputLayer.getNodeList()){
			//update input values
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
