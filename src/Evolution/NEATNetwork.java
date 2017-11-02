package Evolution;

import java.util.ArrayList;

import NeuralNetwork.Edge;
import NeuralNetwork.HiddenNode;
import NeuralNetwork.InputNode;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.Node;
import NeuralNetwork.OutputNode;

public class NEATNetwork extends NeuralNetwork{
	
	ArrayList<NodeGene> nodeGeneList = new ArrayList<NodeGene>();
	ArrayList<ConnectGene> connectGeneList = new ArrayList<ConnectGene>();
	double currentFitness = 0;
	int highestInnovationNumber = 0;
	
	public NEATNetwork(){	//used when creating a copy
		this(null, null);
	}
	
	public NEATNetwork(InputNode[] inputNodes, OutputNode[] outputNodes){
		if(inputNodes == null || outputNodes == null)
			return;
		
		int nodeNumber = 1;			//actual node/innovation count kept in NEAT
		int innovationNumber = 1;
		
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
		
		for(InputNode in : getInputNodes()){	//Connect all input to all output to form the initial minimal NN
			for(OutputNode out : getOutputNodes()){
				Edge e = new Edge(1);
				in.addOutgoingEdge(e);
				out.addIncomingEdge(e);
				e.setNode1(in);
				e.setNode2(out);
				connectGeneList.add(new ConnectGene(in, out, e, innovationNumber));
				innovationNumber++;
			}
		}
		highestInnovationNumber = innovationNumber;
	}
	
	public double getCurrentFitness(){
		return currentFitness;
	}
	
	public void setCurrentFitness(double fitness){
		currentFitness = fitness;
	}
	
	public NEATNetwork createCopyFromConnectGenes(){
		NEATNetwork NN = new NEATNetwork();

		for(ConnectGene cg : connectGeneList){															//copy ConnectGene and copy NeuralNetwork
			Node n1;
			Node n2;
			Edge e;
			
			if(cg.getInNode() instanceof InputNode){													//make a new copy of n1
				n1 = new InputNode(cg.getInNode().getID());
			}else if(cg.getInNode() instanceof HiddenNode){
				n1 = new HiddenNode(cg.getInNode().getID());
			}else{
				return null;	//ERROR
			}
			
			for(ConnectGene NNcg : NN.getConnectGeneList())												//check if n1 has already been copied
				if(NNcg.getInNode().getID() == n1.getID())
					n1 = NNcg.getInNode();
			
			if(cg.getOutNode() instanceof HiddenNode){													//make a new copy of n2
				n2 = new HiddenNode(cg.getInNode().getID());
			}else if(cg.getOutNode() instanceof OutputNode){
				n2 = new OutputNode(cg.getInNode().getID());
			}else{
				return null;	//ERROR
			}
			
			for(ConnectGene NNcg : NN.getConnectGeneList())												//check if n2 has already been copied
				if(NNcg.getOutNode().getID() == n2.getID())
					n2 = NNcg.getOutNode();
			
			e = new Edge(cg.getWeight());																//create a new edge
			e.setActive(cg.getEdge().isActive());
			e.setNode1(n1);
			e.setNode2(n2);
			
			n1.addOutgoingEdge(e);																		//add edge to the node's edge lists
			n2.addIncomingEdge(e);
			
			if(n1 instanceof InputNode){																//insert n1 into proper layer
				NN.addInputNode((InputNode)n1);
			}else if(n1 instanceof HiddenNode){
				NN.addHiddenNode((HiddenNode)n1, getHiddenNodeLayerDepth((HiddenNode)cg.getInNode()));
			}else{
				return null;	//ERROR
			}
			
			if(n2 instanceof HiddenNode){																//insert n1 into proper layer
				NN.addHiddenNode((HiddenNode)n2, getHiddenNodeLayerDepth((HiddenNode)cg.getOutNode()));
			}else if(n2 instanceof OutputNode){
				NN.addOutputNode((OutputNode)n2);
			}else{
				return null;	//ERROR
			}
			
			NN.addConnectGene(new ConnectGene(n1, n2, e, cg.isEnabled(), cg.getInnovationNumber()));	//copy and add ConnectGene to to new NN
		}
		
		for(ConnectGene NNcg : NN.getConnectGeneList()){												//copy NodeGenes
			Boolean repeat1 = false;
			Boolean repeat2 = false;
			for(NodeGene NNng : NN.getNodeGeneList()){
				if(NNng.getNode() == NNcg.getInNode())
					repeat1 = true;
				if(NNng.getNode() == NNcg.getInNode())
					repeat2 = true;
			}
			
			if(!repeat1){
				if(NNcg.getInNode() instanceof InputNode)
					NN.addNodeGene(new NodeGene(NNcg.getInNode(), NodeGene.NodeType.SENSOR));
				if(NNcg.getInNode() instanceof HiddenNode)
					NN.addNodeGene(new NodeGene(NNcg.getInNode(), NodeGene.NodeType.HIDDEN));
				if(NNcg.getInNode() instanceof OutputNode)
					NN.addNodeGene(new NodeGene(NNcg.getInNode(), NodeGene.NodeType.OUTPUT));
					
			}
			
			if(!repeat2){
				if(NNcg.getInNode() instanceof InputNode)
					NN.addNodeGene(new NodeGene(NNcg.getOutNode(), NodeGene.NodeType.SENSOR));
				if(NNcg.getInNode() instanceof HiddenNode)
					NN.addNodeGene(new NodeGene(NNcg.getOutNode(), NodeGene.NodeType.HIDDEN));
				if(NNcg.getInNode() instanceof OutputNode)
					NN.addNodeGene(new NodeGene(NNcg.getOutNode(), NodeGene.NodeType.OUTPUT));
			}
				
		}
		
		return NN;
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
	
	public void addConnection(Node n1, Node n2){
		connectNodesAndAddGenes(null, n1, n2);	//connect n1 -> n2 and add genes
	}
	
	private void connectNodesAndAddGenes(HiddenNode n, Node n1, Node n2){
		Edge e1 = new Edge(1);
		Edge e2 = new Edge(getConnectGeneWeight(n1, n2));
		if(n == null){																	//connect n1 -> n2
			connectNodes(n1, n2);														//Connect the nodes together
			connectGeneList.add(new ConnectGene(n1, n2, e1, NEAT.innovationNumber));		//add connection gene
			NEAT.innovationNumber++;
		}else{																			//connect n1 ->  n -> n2
			n.setID(NEAT.nodeNumber);
			nodeGeneList.add(new NodeGene(n, NodeGene.NodeType.HIDDEN));	//Add the node gene
			NEAT.nodeNumber++;
			updateEnableConnectionGene(n1, n2, false);									//disable the connection between n1 and n2
			connectNodes(n1, n);														//Connect the nodes together
			connectGeneList.add(new ConnectGene(n1, n, e1, NEAT.innovationNumber));		//add connection gene
			NEAT.innovationNumber++;
			connectNodes(n, n2);
			connectGeneList.add(new ConnectGene(n, n2, e2, NEAT.innovationNumber));	
			NEAT.innovationNumber++;
		}
		
		highestInnovationNumber = NEAT.innovationNumber++; //NOTE: may need to refactor for threadsafe access
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