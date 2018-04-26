package Evolution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import NeuralNetwork.Edge;
import NeuralNetwork.HiddenNode;
import NeuralNetwork.InputNode;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.Node;
import NeuralNetwork.OutputNode;

public class NEATNetwork extends NeuralNetwork implements Serializable{
	ArrayList<NodeGene> nodeGeneList = new ArrayList<NodeGene>();
	ArrayList<ConnectGene> connectGeneList = new ArrayList<ConnectGene>();
	double currentFitness = 0;
	int highestInnovationNumber = 0;
	int generationsAlive = 0;
	
	int numInputNodes = 0;
	int numOutputNodes = 0;
	
	public NEATNetwork(){	//used when creating a copy
		this(null, null);
	}
	
	public NEATNetwork(InputNode[] inputNodes, OutputNode[] outputNodes){
		if(inputNodes == null || outputNodes == null)
			return;
		
		numInputNodes = inputNodes.length;
		numOutputNodes = outputNodes.length;
		
		int nodeNumber = 1;
		
		for(InputNode in : inputNodes){	//Add all inputs to input layer and make node gene for each
			in.setID(nodeNumber);
			addInputNode(in);
			nodeGeneList.add(new NodeGene(in, NodeGene.NodeType.SENSOR));
			nodeNumber++;
		}
		
		for(OutputNode out : outputNodes){	//Add all outputs to output layer and make node gene for each
			out.setID(nodeNumber);
			addOutputNode(out);
			nodeGeneList.add(new NodeGene(out, NodeGene.NodeType.OUTPUT));
			nodeNumber++;
		}
	}
	
	public int getGenerationsAlive(){
		return generationsAlive;
	}
	
	public int incGenerationsAlive(){
		generationsAlive++;
		return generationsAlive;
	}
	
	public void sortConnectGeneList(){
		Collections.sort(connectGeneList, new Comparator<ConnectGene>(){
			public int compare(ConnectGene cg1, ConnectGene cg2){
				return Double.compare(cg1.getInnovationNumber(), cg2.getInnovationNumber());
			}
		});
	}
	
	public double getCurrentFitness(){
		return currentFitness;
	}
	
	public void setCurrentFitness(double fitness){
		currentFitness = fitness;
	}
	
	public NEATNetwork createCopyFromGenes(){												//pass the networks used during crossover to copy node depth in the hidden layers 
		NEATNetwork NN = new NEATNetwork();
		
		for(NodeGene ng : nodeGeneList){													//copy NodeGenes and add nodes
			if(ng.getNode() instanceof InputNode){
				InputNode n = new InputNode(ng.getNode().getID());
				NN.nodeGeneList.add(new NodeGene(n, ng.getNodeType()));
				NN.addInputNode(n);
			}
			
			if(ng.getNode() instanceof HiddenNode){
				HiddenNode n = new HiddenNode(ng.getNode().getID());
				NN.nodeGeneList.add(new NodeGene(n, ng.getNodeType()));
				NN.addHiddenNode(n,  getHiddenNodeLayerDepth((HiddenNode)ng.getNode()));
			}
			
			if(ng.getNode() instanceof OutputNode){
				OutputNode n = new OutputNode(ng.getNode().getID());
				NN.nodeGeneList.add(new NodeGene(n, ng.getNodeType()));
				NN.addOutputNode(n);
			}
		}
		
		for(ConnectGene cg : connectGeneList){												//copy ConnectGenes and add connections
			Edge e = new Edge(cg.getWeight());
			e.setEnabled(cg.isEnabled());
			
			for(NodeGene ng : NN.nodeGeneList){
				if(cg.getInNode().getID() == ng.getNode().getID()){
					ng.getNode().addOutgoingEdge(e);
					e.setNode1(ng.getNode());
				}
				
				if(cg.getOutNode().getID() == ng.getNode().getID()){
					ng.getNode().addIncomingEdge(e);
					e.setNode2(ng.getNode());
				}
			}
			
			NN.connectGeneList.add(new ConnectGene(e.getNode1(), e.getNode2(), e, cg.isEnabled(), cg.getInnovationNumber()));
		}
		
		return NN;
	}
	
	public void debugPrintNetworkFromNodeGenes(){
		debugPrintNetworkFromNodeGenes(this);
	}
	
	public void debugPrintNetworkFromNodeGenes(NEATNetwork NN){
		//debug to print copied network structure
		System.out.println("\nBEGIN_DEBUG________________________________________\n");
		for(NodeGene ng : NN.getNodeGeneList()){
			if(ng.getNode() instanceof OutputNode)
				continue;
			
			if(ng.getNode() instanceof HiddenNode)
				System.out.println("[NodeID: " + ng.getNode().getID() + "] [Depth: " + NN.getHiddenNodeLayerDepth((HiddenNode)ng.getNode()) + "] " + ng.getNode());
			else
				System.out.println("[NodeID: " + ng.getNode().getID() + "] " + ng.getNode());
			for(Edge e : ng.getNode().getOutgoingEdges()){
				for(ConnectGene cg : NN.getConnectGeneList()){
					if(e.equals(cg.getEdge()))
						System.out.println("[ENABLED: " + cg.isEnabled() + "] [WEIGHT: " + e.getWeight() + "] " + e);
				}
				if(e.getNode2() instanceof HiddenNode)
					System.out.println("[NodeID: " + e.getNode2().getID() + "] [Depth: " + NN.getHiddenNodeLayerDepth((HiddenNode)e.getNode2()) + "] " + e.getNode2());
				else
					System.out.println("[NodeID: " + e.getNode2().getID() + "] " + e.getNode2());
				
			}
			System.out.println();
		}
		System.out.println("END_DEBUG__________________________________________\n");
	}
	
	public void addNodeGene(NodeGene ng){
		nodeGeneList.add(ng);
	}
	
	public void addConnectGene(ConnectGene cg){
		connectGeneList.add(cg);
	}
	
	public ArrayList<NodeGene> getNodeGeneList(){
		return nodeGeneList;
	}
	
	public ArrayList<ConnectGene> getConnectGeneList(){
		return connectGeneList;
	}
	
	public void updateEdgeWeight(Edge edge, double value){
		for(ConnectGene cg : connectGeneList)
			if(cg.getEdge().equals(edge))
				cg.getEdge().setWeight(value);	//ConnectGene contain the instance of Edge used in the NueralNetwork so updating the CG updates the NN weight
	}
	
	public int getHighestInnovationNumber(){
		return highestInnovationNumber;
	}
	
	public void addNodeBetween(HiddenNode n, Node n1, Node n2){
		Boolean valid = false;
		for(Edge e : n1.getOutgoingEdges())													//verify that there is a pre-existing connection between n1 and n2
			if(e.getNode2().equals(n2) && e.isEnabled())
				valid = true;
		
		if(valid){																			//Add the node if their is a valid pre-existing connection between n1 and n2
		//_____________________________________________________________________________
			
			if(n1 instanceof InputNode && n2 instanceof OutputNode){
				addHiddenNode(n, 0);														//add the hiddenNode to the first hidden Layer
				connectNodesAndAddGenes(n, n1, n2);											//connects nodes and updates connectionGenes
			}else if(n1 instanceof InputNode && n2 instanceof HiddenNode){					//n2 must be a hidden node
				addHiddenNode(n, getHiddenNodeLayerDepth((HiddenNode)n2));					//place n in the same layer as n2
				moveHiddenSubTreeDeeper((HiddenNode)n2, true);								//move n2 and all of its connected outgoing nodes 1 layer deeper
				connectNodesAndAddGenes(n, n1, n2);											//connects nodes and updates connectionGenes
			}else if(n1 instanceof HiddenNode && n2 instanceof HiddenNode){					//n1 must be a hidden node
				addHiddenNode(n, getHiddenNodeLayerDepth((HiddenNode)n1)+1);				//place n one layer deeper than n1
				if(getHiddenNodeLayerDepth(n) == getHiddenNodeLayerDepth((HiddenNode) n2))	//if n is in the same layer as n2 move n2 one layer deeper
					moveHiddenSubTreeDeeper((HiddenNode)n2, true);							//move n2's and all of its connected outgoing nodes 1 layer deeper
				connectNodesAndAddGenes(n, n1, n2);											//connects nodes and updates connectionGenes
			}else if(n1 instanceof HiddenNode && n2 instanceof OutputNode){
				addHiddenNode(n, getHiddenNodeLayerDepth((HiddenNode)n1)+1);				//place n one layer deeper than n1
				connectNodesAndAddGenes(n, n1, n2);											//connects nodes and updates connectionGenes
			}else{
				System.out.println("Unhandled addNodeBetween Case");
			}
		//_____________________________________________________________________________
		}
	}
	
	public void addConnection(Node n1, Node n2){
		connectNodesAndAddGenes(null, n1, n2);	//connect n1 -> n2 and add genes
	}
	
	private void connectNodesAndAddGenes(HiddenNode n, Node n1, Node n2){
		if(n == null){																	//connect n1 -> n2
			Edge e;
			if(0.5 > Math.random())														//50/50 chance on positive or negative connection
				e = new Edge(1);
			else
				e = new Edge(-1);
			connectNodes(n1, n2, e);													//Connect the nodes together
			connectGeneList.add(new ConnectGene(n1, n2, e, NEAT.innovationNumber));		//add connection gene
			NEAT.innovationNumber++;
		}else{																			//connect n1 ->  n -> n2
			Edge e1 = new Edge(1);
			Edge e2 = new Edge(getConnectGeneWeight(n1, n2));
			
			nodeGeneList.add(new NodeGene(n, NodeGene.NodeType.HIDDEN));				//Add the node gene
			updateEnableConnectionGene(n1, n2, false);									//disable the connection between n1 and n2
			
			connectNodes(n1, n, e1);													//n1 -> n
			connectGeneList.add(new ConnectGene(n1, n, e1, NEAT.innovationNumber));		//add connection gene
			NEAT.innovationNumber++;
			
			connectNodes(n, n2, e2);													//n -> n2
			connectGeneList.add(new ConnectGene(n, n2, e2, NEAT.innovationNumber));		//add connection gene
			NEAT.innovationNumber++;
		}
		
		highestInnovationNumber = NEAT.innovationNumber++; 								//NOTE: may need to refactor for threadsafe access
	}
	
	private void updateEnableConnectionGene(Node n1, Node n2, boolean enable){	//NOTE: also disables the NN connection
		for(Edge e : n1.getOutgoingEdges())		//disable connection between n1 and n2
			if(e.getNode2().equals(n2))
				e.setActive(enable);
		for(ConnectGene cg : connectGeneList)
			if(cg.getInNode().equals(n1) && cg.getOutNode().equals(n2))
				cg.setEnabled(enable);
	}
	
	public double getConnectGeneWeight(Node n1, Node n2){
		for(ConnectGene cg : connectGeneList)
			if(cg.getInNode().equals(n1) && cg.getOutNode().equals(n2))
				return cg.getWeight();
		
		return 0;
	}

}