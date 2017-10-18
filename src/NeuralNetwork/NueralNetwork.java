package NeuralNetwork;

import java.util.ArrayList;

public class NueralNetwork {
	ArrayList<Layer<HiddenNode>> hiddenLayers = new ArrayList<Layer<HiddenNode>>();
	Layer<InputNode> inputLayer = new Layer<InputNode>();
	Layer<OutputNode> outputLayer = new Layer<OutputNode>();

	public NueralNetwork() {
		
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
