package Evolution;

import java.util.ArrayList;

import NeuralNetwork.Edge;
import NeuralNetwork.HiddenNode;
import NeuralNetwork.InputNode;
import NeuralNetwork.Layer;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.Node;
import NeuralNetwork.OutputNode;

public class NEATNetwork extends NeuralNetwork{
	
	ArrayList<NodeGene> nodeGeneList = new ArrayList<NodeGene>();
	ArrayList<ConnectGene> connectGeneList = new ArrayList<ConnectGene>();
	int currentFitness = 0;
	int highestInnovationNumber = 0;
	int speciesID = 0;
	
	public NEATNetwork(InputNode[] inputNodes, OutputNode[] outputNodes){
		int nodeNumber = 1;			//actual node/innovation count kept in NEAT
		int innovationNumber = 1;
		
		for(InputNode in : inputNodes){	//Add all inputs to input layer and make node gene for each
			in.setID(nodeNumber);
			addInputNode(in);
			nodeGeneList.add(new NodeGene(in.getID(), NodeGene.NodeType.SENSOR));
			nodeNumber++;
		}
		
		for(OutputNode out : outputNodes){	//Add all outputs to output layer and make node gene for each
			out.setID(nodeNumber);
			addOutputNode(out);
			nodeGeneList.add(new NodeGene(out.getID(), NodeGene.NodeType.OUTPUT));
			nodeNumber++;
		}
		
		for(InputNode in : getInputNodes()){	//Connect all input to all output to form the initial minimal NN
			for(OutputNode out : getOutputNodes()){
				Edge e = new Edge(1);
				in.addOutGoingEdge(e);
				out.addIncomingEdge(e);
				e.setNode1(in);
				e.setNode2(out);
				connectGeneList.add(new ConnectGene(in, out, e.getWeight(), innovationNumber));
				innovationNumber++;
			}
		}
		highestInnovationNumber = innovationNumber;
	}
	
	public int getSpeciesID(){
		return speciesID;
	}
	
	public int getHighestInnovationNumber(){
		return highestInnovationNumber;
	}
	
	public ArrayList<NodeGene> getNodeGeneList(){
		return nodeGeneList;
	}
	
	public ArrayList<ConnectGene> getConnectGeneList(){
		return connectGeneList;
	}
	
	public void addNodeBetween(HiddenNode n, Node n1, Node n2){
		for(Edge e : n1.getOutgoingEdges()){	//verify that there is a pre-existing connection between n1 and n2	
			if(e.getNode2().equals(n2)){
				//_____________________________________________________________________________
				
				if(n1 instanceof InputNode && n2 instanceof OutputNode){
					addHiddenNode(n, 0);										//add the hiddenNode to the first hidden Layer
					connectNodesAndAddGenes(n, n1, n2);							//connects nodes and updates connectionGenes
				}else if(n1 instanceof InputNode){								//n2 must be a hidden node
					addHiddenNode(n, getHiddenNodeLayerDepth((HiddenNode)n2));	//place n in the same layer as n2
					moveHiddenSubTreeDeeper(n2, true);							//move n2 and all of its connected outgoing nodes 1 layer deeper
					connectNodesAndAddGenes(n, n1, n2);							//connects nodes and updates connectionGenes
				}else{															//n1 must be a hidden node, n2 irrelevant
					addHiddenNode(n, getHiddenNodeLayerDepth((HiddenNode)n1)+1);//place n one layer deeper than n1
					moveHiddenSubTreeDeeper(n1, false);							//move all of n1's connected outgoing nodes 1 layer deeper
					connectNodesAndAddGenes(n, n1, n2);							//connects nodes and updates connectionGenes
				}
				//_____________________________________________________________________________
			}
		}
	}
	
	public void addConnection(HiddenNode n1, HiddenNode n2){
		connectNodesAndAddGenes(n1, n2);	//connect n1 -> n2 and add genes
	}
	
	private void connectNodesAndAddGenes(Node n1, Node n2){
		connectNodesAndAddGenes(null, n1, n2);
	}
	
	private void connectNodesAndAddGenes(HiddenNode n, Node n1, Node n2){
		if(n == null){																	//connect n1 -> n2
			connectNodes(n1, n2);														//Connect the nodes together
			connectGeneList.add(new ConnectGene(n1, n2, 1, NEAT.innovationNumber));		//add connection gene
			NEAT.innovationNumber++;
		}else{																			//connect n1 ->  n -> n2
			nodeGeneList.add(new NodeGene(NEAT.nodeNumber, NodeGene.NodeType.HIDDEN));	//Add the node gene
			NEAT.nodeNumber++;
			updateEnableConnectionGene(n1, n2, false);									//disable the connection between n1 and n2
			connectNodes(n1, n);														//Connect the nodes together
			connectGeneList.add(new ConnectGene(n1, n, 0, NEAT.innovationNumber));		//add connection gene
			NEAT.innovationNumber++;
			connectNodes(n, n2);
			connectGeneList.add(new ConnectGene(n, n2, getConnectGeneWeight(n1, n2), NEAT.innovationNumber));	
			NEAT.innovationNumber++;
		}
		
		highestInnovationNumber = NEAT.innovationNumber++; //NOTE: may need to refactor for threadsafe access
	}
	
	private void updateEnableConnectionGene(Node n1, Node n2, boolean enable){	//NOTE: also disables the NN connection
		for(Edge e : n1.getOutgoingEdges())		//disable connection between n1 and n2
			if(e.getNode2().equals(n2))
				e.setActive(enable);
		for(ConnectGene cg : connectGeneList)
			if(cg.getIn().equals(n1) && cg.getOut().equals(n2))
				cg.setEnabled(enable);
	}
	
	public double getConnectGeneWeight(Node n1, Node n2){
		for(ConnectGene cg : connectGeneList)
			if(cg.getIn().equals(n1) && cg.getOut().equals(n2))
				return cg.getWeight();
		
		return 0;
	}
}