package Evolution;

import java.util.ArrayList;

import NeuralNetwork.Edge;
import NeuralNetwork.HiddenNode;
import NeuralNetwork.InputNode;
import NeuralNetwork.Layer;
import NeuralNetwork.OutputNode;

public class NEAT {
	private static final double MUTATEWEIGHT = 0.8;				//probability of changing a connection weight
	private static final double MUTATEWEIGHTTYPE = 0.1;			//probability of mutating uniformly or assigning a random value
	private static final double MUTATEADDNODE = 0.03;			//probability of adding a new node
	private static final double MUTATEADDCONNECTION = 0.01;		//probability of adding a new connection between existing nodes
	
	private static final double POPULATIONFROMCROSSOVER = 0.25;	//percentage of the next generations population forming from crossover
	private static final double MAINTAINDISBALEGENE = 0.75;		//probability that an inherited gene is disabled if it was disabled in either parent
	private static final double INTERSPECIESMATINGRATE = 0.001;	//probability that two different species mate
	
	
	private static final int MAXSTAGNENTGENERATIONS = 30;
	
	private static final int POPULATIONSIZE = 500;
	private static final int MINIMUMSPECIESSIZE = 20;
	
	private static final double POPULATIONELIMINATION = 0.5;
	
	protected static int innovationNumber = 1;
	protected static int nodeNumber = 1;
	ArrayList<Species> population = new ArrayList<Species>();
	int speciesCount = 0;
	int generationCount = 0;
	
	public NEAT(int numInputNodes, int numOutputNodes){
		//innovationNumber += numInputNodes * numOutputNodes;	//initialized to take into account the structure of the initial network
		
		population.add(new Species());
		for(int i=0; i< POPULATIONSIZE; i++){							//initialize the Population
			InputNode[] inputNodes = new InputNode[numInputNodes];		//create new instances for each NN of populaton
			OutputNode[] outputNodes = new OutputNode[numOutputNodes];
			for(int x=0; x<numInputNodes; x++){
				inputNodes[x] = new InputNode(nodeNumber);
				nodeNumber++;
			}
			for(int x=0; x<numOutputNodes; x++){
				outputNodes[x] = new OutputNode(nodeNumber);
				nodeNumber++;
			}
			
			NEATNetwork NN = new NEATNetwork(inputNodes, outputNodes);
			population.get(0).add(NN);								//initialize the speciesList
			population.get(0).setFirstNN(NN);
		}
		speciesCount++;
	}
	
	public void runGeneration(){
		ArrayList<ArrayList<NEATNetwork>> species = new ArrayList<ArrayList<NEATNetwork>>();
		
		for(Species s : population){																	//run each NN and update their fitness
			for(NEATNetwork NN : s.getPopulation()){
				NN.setCurrentFitness(fitness(NN));
			}
		}
		
		System.out.println("EXCUTE COMPLETED");
		
		//ELIMINATE_____________________________________________________________________________________________________________________________
		int eliminate = (int) ((POPULATIONSIZE*POPULATIONELIMINATION)/(population.size()));				//number to be eliminated from each species
		int eliminated = 0; 																			//actual number eliminated

		for(int i=0; i<population.size(); i++){															//iterate with index i to avoid concurrentModification error
			Species s = population.get(i);																//set s to the current species
			
			Boolean stagnantFlag = false;																//marks if maxfitness was updated
			for(NEATNetwork NN : s.getPopulation()){													//update stagnant markers
				if(NN.getCurrentFitness() > s.getMaxFitness()){
					s.resetGenerationsWithoutImprovement();
					s.updateMaxFitness();																//update the species max fitness
					stagnantFlag = true;
				}
			}
			
			for(int x=0; x<eliminate; x++){
				if(s.size() <= MINIMUMSPECIESSIZE){														//if the species population is below minimum don't remove any
					continue;
				}else{
					s.removeLowest();																	//remove the lowest performer of the species
					eliminated++;
				}
			}
			
			if(population.size() == 1)																	//if their is only one species we shouldn't kill off the whole population so skip it
				break;
			
			if(!stagnantFlag){
				s.incGenerationsWithoutImprovement();
				if(s.getGenerationsWithoutImprovement() >= MAXSTAGNENTGENERATIONS){						//eliminate stagnant species 
					Boolean isBest = true;																//if s is the best performing species don't kill it
					for(Species x : population)
						if(s.getMaxFitness() < x.getMaxFitness())										//if s's maxFitness is less than any other species then it can't be the best so we can kill it
							isBest = false;
						
					if(!isBest){
						eliminated += s.size();
						population.remove(s);
						i--;																				//back up one to account for the removed species
						speciesCount--;
					}
				}
			}
			
			
		}
		System.out.println("ELIMINATE COMPLETED");
		
		int AmountFromCrossOver = (int)(eliminated*POPULATIONFROMCROSSOVER);
		int AmountFromMutate = eliminated - AmountFromCrossOver;
		
		//MUTATE_____________________________________________________________________________________________________________________________
		ArrayList<NEATNetwork> replacements = new ArrayList<NEATNetwork>();
		int speciesIndex = 0;
		int speciesDepth = 0;
		
		while(AmountFromMutate > 0){
			/*System.out.println("b4 copy");
			population.get(speciesIndex).getPopulation().get(speciesDepth).debugPrintNetworkFromNodeGenes();*/
			NEATNetwork NN = population.get(speciesIndex).getPopulation().get(speciesDepth).createCopyFromGenes();	//Set NN to a copy of the referenced NN				
			
			/*System.out.println("Before Mutate");
			NN.debugPrintNetworkFromNodeGenes();*/
			
			mutate(NN);
			
			/*System.out.println("After Mutate");
			NN.debugPrintNetworkFromNodeGenes();*/
			
			replacements.add(NN);
			AmountFromMutate--;
			
			if(speciesIndex >= population.size()-1){
				speciesIndex = 0;
				speciesDepth++;
			}else{
				speciesIndex++;
			}
			if(speciesDepth >= population.get(speciesIndex).getPopulation().size()-1 && speciesDepth != 0){					//skip to next species if we've reached max depth
				speciesDepth = 0;																							//reset species depth
				continue; 
			}
		}
		System.out.println("MUTATE COMPLETED");
		
		//CROSSOVER_____________________________________________________________________________________________________________________________
		speciesIndex = 0;
		speciesDepth = 0;
		while(AmountFromCrossOver > 0){
			int speciesSize = population.get(speciesIndex).getPopulation().size();
			NEATNetwork NN1 = population.get(speciesIndex).getPopulation().get(speciesDepth).createCopyFromGenes();						//Set NN to a copy of the referenced NN
			NEATNetwork NN2 = population.get(speciesIndex).getPopulation().get((int) (Math.random()*speciesSize)).createCopyFromGenes();	//Set NN2 to a copy of the a random NN in the species
			
			replacements.add(crossover(NN1, NN2));
			AmountFromCrossOver--;
			
			if(speciesIndex >= population.size()-1){
				speciesIndex = 0;
				speciesDepth++;
			}else{
				speciesIndex++;
			}
			if(speciesDepth >= population.get(speciesIndex).getPopulation().size()-1 && speciesDepth != 0){					//skip to next species if we've reached max depth
				speciesDepth = 0;																							//reset species depth
				continue; 
			}
		}
		System.out.println("CROSSOVER COMPLETED");
		
		//SPECIATE_____________________________________________________________________________________________________________________________
		for(NEATNetwork NN : replacements){
			Boolean newSpecies = true;
			for(Species s : population){
				if(!speciate(NN, s.getFirstNN())){	//same species
					s.add(NN);
					newSpecies = false;
					break;
				}
			}
			if(newSpecies){							//if no matching species create a new one for it
				population.add(new Species(NN));
				speciesCount++;
			}
		}
		
		System.out.println("Number of Species: " + population.size());
		
		for(Species s : population)					//sort each species High to Low
			s.sortPopulation();
		
		System.out.println("SPECIATE COMPLETED");
		generationCount++;
		
		for(Species s : population)
			System.out.println("Max Species Fitness: " + s.getMaxFitness() + " Number of NN in Species: " + s.getPopulation().size());
		System.out.println("FINISHED GENERATION: " + generationCount + "_____________________________________________________");
	}
	
	public double fitness(NEATNetwork NN){ //defined per task
		double fitness = 0;
		
		//0 + 0 -> 0
		NN.getInputNodes().get(0).setInput(0);
		NN.getInputNodes().get(1).setInput(0);
		NN.execute();
		if(!NN.getOutputNodes().get(0).checkFired()){
			fitness+=0.25;
			System.out.println("!FIRE: 00");
		}
		
		//0 + 1 -> 1
		NN.getInputNodes().get(0).setInput(1);
		NN.getInputNodes().get(1).setInput(0);
		NN.execute();
		if(NN.getOutputNodes().get(0).checkFired()){
			fitness+=0.25;
			System.out.println("FIRE: 10");
		}
		
		//1 + 0 -> 1
		NN.getInputNodes().get(0).setInput(0);
		NN.getInputNodes().get(1).setInput(1);
		NN.execute();
		if(NN.getOutputNodes().get(0).checkFired()){
			fitness+=0.25;
			System.out.println("FIRE: 01");
		}
			
		//1 + 1 -> 0
		NN.getInputNodes().get(0).setInput(1);
		NN.getInputNodes().get(1).setInput(1);
		NN.execute();
		if(!NN.getOutputNodes().get(0).checkFired()){
			fitness+=0.25;
			System.out.println("!FIRE: 11");
		}
		
		int count = 0;
		for(Layer l : NN.getHiddenLayers())		//penalize network size
			for(Object hn : l.getNodeList())
				count++;
		fitness-=(count/2)*0.001;
		
		if(count == 0)
			fitness = 0;
		
		System.out.println("CurrentFitness: " + fitness);
		
		return fitness;
	}
	
	public NEATNetwork crossover(NEATNetwork NN1, NEATNetwork NN2){
		NEATNetwork NN = new NEATNetwork();
		
		for(NodeGene ng : NN1.nodeGeneList)									//initialize with copy of the input/output nodeGenes
			if(ng.getNode() instanceof InputNode || ng.getNode() instanceof OutputNode)
				NN.nodeGeneList.add(ng);
		
		for(ConnectGene cg1 : NN1.connectGeneList){							//select genes to crossover
			for(ConnectGene cg2 : NN2.connectGeneList){
				Boolean repeatFlag = false;									//flag for checking if we've already added the gene to the NN
				for(ConnectGene cg : NN.getConnectGeneList()){				//iterate through all the connectionGenes added to NN
					if(cg.getInnovationNumber() == cg1.getInnovationNumber())
						repeatFlag = true;
					if(cg.getInnovationNumber() == cg2.getInnovationNumber())
						repeatFlag = true;
				}
				if(repeatFlag)												//if the a gene with the same innovationNumber has already been added to the NN don't add it again
					continue;
				
				Boolean cg1n1Repeat = false;
				Boolean cg1n2Repeat = false;
				for(NodeGene ng : NN.getNodeGeneList()){					//check if the nodeGene has been added for the nodes referenced in cg1
					if(ng.getNode().getID() == cg1.getInNode().getID())
						cg1n1Repeat = true;
					
					if(ng.getNode().getID() == cg1.getOutNode().getID())
						cg1n2Repeat = true;
				}
				
				Boolean cg2n1Repeat = false;
				Boolean cg2n2Repeat = false;
				for(NodeGene ng : NN.getNodeGeneList()){					//check if the nodeGene has been added for the nodes referenced in cg1
					if(ng.getNode().getID() == cg2.getInNode().getID())
						cg2n1Repeat = true;
					
					if(ng.getNode().getID() == cg2.getOutNode().getID())
						cg2n2Repeat = true;
				}
				
				if(cg1.getInnovationNumber() == cg2.getInnovationNumber()){	//Matching Genes are inhereted randomly
					if(Math.random() > 0.5){
						NN.addConnectGene(cg1);
						crossOverAddNodeGenes(NN, NN1, cg1, cg1n1Repeat, cg1n2Repeat);
					}else{
						NN.addConnectGene(cg2);
						crossOverAddNodeGenes(NN, NN2, cg2, cg2n1Repeat, cg2n2Repeat);
					}
				}else{														//Disjoint & Excess Genes are always inhereted from the more fit parent
					if(NN1.getCurrentFitness() > NN2.getCurrentFitness()){
						NN.addConnectGene(cg1);
						crossOverAddNodeGenes(NN, NN1, cg1, cg1n1Repeat, cg1n2Repeat);
					}else{
						NN.addConnectGene(cg2);
						crossOverAddNodeGenes(NN, NN2, cg2, cg2n1Repeat, cg2n2Repeat);
					}
				}
			}
		}
		
		NN = NN.createCopyFromGenes();										//Set NN to a copy of itself to build and create new instances of the genes/nodes/connections
		for(ConnectGene cg : NN.connectGeneList)							//re-enable genes with probability MAINTAINDISBALEGENE
			if(!cg.isEnabled())
				if(Math.random() > MAINTAINDISBALEGENE)
					cg.toogleEnable();
		
		return NN;
	}
	
	private void crossOverAddNodeGenes(NEATNetwork NN, NEATNetwork nn, ConnectGene cg, Boolean n1Repeat, Boolean n2Repeat){
		for(NodeGene ng : nn.getNodeGeneList()){			//add nodeGenes
			if(ng.getNode().getID() == cg.getInNode().getID() && !n1Repeat){
				if(ng.getNode() instanceof HiddenNode){
					NN.addHiddenNode((HiddenNode)ng.getNode(), nn.getHiddenNodeLayerDepth((HiddenNode)ng.getNode()));
					NN.addNodeGene(ng);
				}
			}
			
			if(ng.getNode().getID() == cg.getOutNode().getID() && !n2Repeat){
				if(ng.getNode() instanceof HiddenNode){
					NN.addHiddenNode((HiddenNode)ng.getNode(), nn.getHiddenNodeLayerDepth((HiddenNode)ng.getNode()));
					NN.addNodeGene(ng);
				}
			}
		}
	}
	
	//Need to add in tracking of innovations per generation to to prevent duplicate innovations
	public void mutate(NEATNetwork NN){
		ArrayList<ConnectGene> hcg = new ArrayList<ConnectGene>();								//holders of genes for iterating over to prevent conccurentModificationException
		ArrayList<NodeGene> hng = new ArrayList<NodeGene>();
		for(ConnectGene cg : NN.connectGeneList)
			hcg.add(cg);
		for(NodeGene ng : NN.nodeGeneList)
			hng.add(ng);
		
		for(ConnectGene cg : hcg){
			if(!cg.isEnabled())																	//if the node is disabled don't try and change its weight or add nodes between it
				continue;
			
			if(MUTATEWEIGHT >= Math.random()){													//mutate weights
				if(MUTATEWEIGHTTYPE >= Math.random()){											//replace weight with random value between -1 and 1
					NN.updateEdgeWeight(cg.getEdge(), -1+(2*Math.random()));
				}else{																			//mutate the weight uniformly by multiplying by a number between 0.5 and 1.5
					NN.updateEdgeWeight(cg.getEdge(), 0.5+Math.random());
				}
			}
			
			if(MUTATEADDNODE >= Math.random()){													//mutate add new node
				HiddenNode n = new HiddenNode(nodeNumber);
				nodeNumber++;
				NN.addNodeBetween(n, cg.getEdge().getNode1(), cg.getEdge().getNode2());
			}
		}
		
		
		
		for(NodeGene ng1 : hng){																//add new connections between nodes
			for(NodeGene ng2 : hng){															//check if every node already connects to every other node
				if(ng1.equals(ng2))																//reccurent connections not allowed so skip
					continue;
				if(ng1.getNode() instanceof OutputNode)											//OutputNodes don't have outGoingEdges so skip
					continue;
				if(ng1.getNode() instanceof InputNode && ng2.getNode() instanceof InputNode)	//no connection between input nodes
					continue;
				if(ng1.getNode() instanceof HiddenNode && ng2.getNode() instanceof InputNode)	//hidden nodes can't connect back to input nodes (hidden -> input) is bad (input -> hidden) is ok
					continue;
				
				if(ng1.getNode() instanceof HiddenNode && ng2.getNode() instanceof HiddenNode)	//nodes can't connect to nodes in the same or lower layer
					if(NN.getHiddenNodeLayerDepth((HiddenNode)ng1.getNode()) >= NN.getHiddenNodeLayerDepth((HiddenNode)ng2.getNode()))
						continue;

				Boolean preExistingConnection = false;											//if n1 -> n2 don't try to add another connection
				for(Edge e : ng1.getNode().getOutgoingEdges()){
					if(e.getNode2().equals(ng2.getNode())){
						preExistingConnection = true;
						break;
					}
				}
				
				if(MUTATEADDCONNECTION >= Math.random() && !preExistingConnection){				//else connect n1 and n2 on probability MUTATEADDCONNECTION		
					NN.addConnection(ng1.getNode(), ng2.getNode());
				}
			}
		}
	}
	
	public boolean speciate(NEATNetwork n1, NEATNetwork n2){
		int n1Size = n1.getConnectGeneList().size();
		int n2Size = n2.getConnectGeneList().size();
		
		//constant weighting for speciation formula
		double c1 = 1;
		double c2 = 1;
		double c3 = 0.4;
		double E = numExcessGenes(n1, n2);
		double D = numDisjointGenes(n1, n2);
		double N;			//number of genes in the larger network
		double w = 0;		//Average weight difference
		
		if(n1Size < 20 && n2Size < 20){	//if both networks have fewer than 20 connectGenes set N to 1
			N = 1;
		}else if(n1Size > n2Size){
			N = n1Size;
		}else{
			N = n2Size;
		}
		
		for(ConnectGene cg1 : n1.getConnectGeneList())
			for(ConnectGene cg2 : n2.getConnectGeneList())
				if(cg1.getInnovationNumber() == cg2.getInnovationNumber())
					w += Math.abs(cg1.getWeight() - cg2.getWeight());
		w = w/numMatchingGenes(n1, n2);
		
		/*System.out.println("E: " + E);
		System.out.println("D: " + D);
		System.out.println("w: " + w);
		System.out.println("N: " + N);
		System.out.println("E/N: " + E/N);
		System.out.println("SPECIATION: " + ((c1*E/N) + (c2*D/N) + (c3*w)));*/
		
		return 3 <= ((c1*E/N) + (c2*D/N) + (c3*w));	//return true is S >= 3
	}
	
	private int numDisjointGenes(NEATNetwork n1, NEATNetwork n2){
		int n1Max = n1.getHighestInnovationNumber();
		int n2Max = n2.getHighestInnovationNumber();
		
		int n1Size = n1.getConnectGeneList().size();
		int n2Size = n2.getConnectGeneList().size();
		
		int matching = numMatchingGenes(n1, n2);
		int excess = numExcessGenes(n1, n2);
		
		if(n1Max > n2Max){
			return (n1Size - matching - excess) + (n2Size - matching);	//n1 has excess genes
		}else if(n1Max < n2Max){
			return (n2Size - matching - excess) + (n1Size - matching);	//n2 has excess genes
		}else{
			return (n2Size - matching) + (n1Size - matching);			//no excess genes
		}
	}
	
	private int numMatchingGenes(NEATNetwork n1, NEATNetwork n2){
		int matches = 1;
		
		for(ConnectGene cg1 : n1.getConnectGeneList())
			for(ConnectGene cg2 : n2.getConnectGeneList())
				if(cg1.getInnovationNumber() == cg2.getInnovationNumber())
					matches++;
		
		return matches;
	}
	
	private int numExcessGenes(NEATNetwork n1, NEATNetwork n2){
		int n1Max = n1.getHighestInnovationNumber();
		int n2Max = n2.getHighestInnovationNumber();
		
		int excess = 0;
		
		if(n1Max < n2Max)	//count the number of genes in n2 with innovation number greater than n1Max
			for(ConnectGene cg : n2.getConnectGeneList())
				if(cg.getInnovationNumber() > n1Max)
					excess++;
		
		if(n1Max > n2Max)
			for(ConnectGene cg : n1.getConnectGeneList())
				if(cg.getInnovationNumber() > n2Max)
					excess++;
		
		return excess;
	}
	
	public double getBestSpeciesFitness(){
		double bestFitness = -1;
		for(Species s : population)
			if(s.getMaxFitness() > bestFitness)
				bestFitness = s.getMaxFitness();
		return bestFitness;
	}
}
