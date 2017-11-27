package Evolution;

import java.util.ArrayList;
import java.util.Random;

import NeuralNetwork.Edge;
import NeuralNetwork.HiddenNode;
import NeuralNetwork.InputNode;
import NeuralNetwork.Layer;
import NeuralNetwork.Node;
import NeuralNetwork.OutputNode;

public class NEAT {
	protected static final double MUTATEWEIGHT = 0.8;				//0.8probability of changing a connection weight
	protected static final double MUTATEWEIGHTTYPE = 0.1;			//probability of mutating uniformly or assigning a random value
	protected static final double MUTATEADDNODE = 0.001;			//0.03probability of adding a new node
	protected static final double MUTATEADDCONNECTION = 0.003;		//0.01probability of adding a new connection between existing nodes
	
	protected static final double POPULATIONFROMCROSSOVER = 0.25;	//0.25percentage of the next generations population forming from crossover
	protected static final double MAINTAINDISBALEGENE = 0.75;		//probability that an inherited gene is disabled if it was disabled in either parent
	protected static final double INTERSPECIESMATINGRATE = 0.001;	//probability that two different species mate	
	
	protected static final int MAXSTAGNENTGENERATIONS = 50;
	
	protected static final int POPULATIONSIZE = 500;
	protected static final int MINIMUMSPECIESSIZE = 5;
	protected static final int MAXNUMBEROFSPECIES = 5;
	
	protected static final double POPULATIONELIMINATION = 0.75;
	
	protected static int innovationNumber = 1;
	protected static int nodeNumber = 1;
	protected ArrayList<Species> population = new ArrayList<Species>();
	protected int speciesCount = 0;			//count number of active species
	protected int speciesIDCount = 0;		//unqiue ID for species 
	protected int generationCount = 0;		//number of generations that have passed
	
	
	public NEAT(int numInputNodes, int numOutputNodes){	
		population.add(new Species(speciesIDCount++));
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
	
	public void execute(){
		for(Species s : population){																	//run each NN and update their fitness
			for(NEATNetwork NN : s.getPopulation()){
				NN.setCurrentFitness(fitness(NN));
			}
		}
	}
	
	public void runGeneration() throws InterruptedException{	
		if(generationCount == 0){		//initial execution at generation 0
			execute();
			for(Species s : population)
				s.updateMaxFitness();	//set initial species fitness
		}
		
		//ELIMINATE_____________________________________________________________________________________________________________________________
		int eliminate = (int) (POPULATIONSIZE*POPULATIONELIMINATION);									//number of NN to elimate
		int eliminated = 0;
		int weakestSpeciesIndex = 0;
		
		//Eliminate stagnant species
		for(int i=0; i<population.size(); i++){
			Species s = population.get(i);	
			Boolean stagnant = false;				
			
			
			if(s.getGenerationsWithoutImprovement() >= MAXSTAGNENTGENERATIONS)
				stagnant = true;
			
			if(population.size() == 1)																	//if their is only one species we shouldn't kill off the whole population so skip it
				break;
			
			if(stagnant){
				Boolean isBest = true;																//if s is the best performing species don't kill it
				for(Species x : population)
					if(s.getMaxFitness() < x.getMaxFitness())										//if s's maxFitness is less or equal than any other species then it can't be the best so we can kill it
						isBest = false;
					
				if(!isBest){
					eliminated += s.size();
					population.remove(s);
					i--;																			//back up one to account for the removed species
					speciesCount--;
				}
			}
		}
		
		//Eliminate excess species
		while(population.size() >= MAXNUMBEROFSPECIES){													//if we have too many species eliminate the lowest performers
			for(int x=0; x<population.size(); x++)
				if(population.get(x).getMaxFitness() < population.get(weakestSpeciesIndex).getMaxFitness())
					weakestSpeciesIndex = x;
			
			eliminated += population.get(weakestSpeciesIndex).getPopulation().size();
			population.remove(weakestSpeciesIndex);
			weakestSpeciesIndex = 0;																	//reset index
		}
		
		//eliminate from species the remaining required
		double percentFromEachPopulationToEliminate = (POPULATIONELIMINATION)*((eliminate-eliminated)/((double)eliminate));
		if(eliminated < eliminate){																				//check if we've already eliminated enough
			for(int i=0; i<population.size(); i++){																//iterate with index i to avoid concurrentModification error
				Species s = population.get(i);																	//set s to the current species
				int eliminateInSpecies = (int)(s.getPopulation().size() * percentFromEachPopulationToEliminate);//remove species populationSize * percent remaining to be eliminated
				
				for(int x=0; x<eliminateInSpecies; x++){
					if(s.size() <= MINIMUMSPECIESSIZE){															//if the species population is below minimum don't remove any
						continue;
					}else{
						s.removeLowest();																		//remove the lowest performer of the species
						eliminated++;
					}
				}
			}
		}
		
		System.out.println("Eliminated: " + eliminated);
		System.out.println("ELIMINATE COMPLETED");
		
		int AmountFromCrossOver = (int)(eliminated*POPULATIONFROMCROSSOVER);
		int AmountFromMutate = eliminated - AmountFromCrossOver;
		
		//MUTATE_____________________________________________________________________________________________________________________________
		ArrayList<NEATNetwork> replacementsMutate = new ArrayList<NEATNetwork>();		//cycles through species list and for each pick a random NN to mutate into next generation
		int speciesIndex = 0;
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		while(AmountFromMutate > 0){
			int speciesSize = population.get(speciesIndex).getPopulation().size();
			NEATNetwork NN = population.get(speciesIndex).getPopulation().get((int)(speciesSize*Math.random())).createCopyFromGenes();	//Set NN to a copy of randomly referenced NN in current species
			threadList.add(mutate(NN));	//create mutate thread
			replacementsMutate.add(NN);	//list of all NN to be mutated as replacements
			AmountFromMutate--;
			
			if(speciesIndex >= population.size()-1)	//loop back if every species has had a random NN mutated
				speciesIndex = 0;
			else
				speciesIndex++;						//jump to the next species
		}
		
		for(Thread t : threadList)			//start all of the threads
			t.start();
		for(Thread t : threadList)			//wait for all of the threads to finish
			t.join();
		
		/*threadList = new ArrayList<Thread>();										//checks for duplicate innovation in the replacements
		for(NEATNetwork NN : replacementsMutate)
			threadList.add(mergeDuplicateInnovation(NN, replacementsMutate));
		
		for(NEATNetwork NN : replacementsMutate)									//checks for duplicate innovation in the population
			for(Species s : population)
				threadList.add(mergeDuplicateInnovation(NN, s.getPopulation()));
		
		for(Thread t : threadList)			//start all of the threads
			t.start();
		for(Thread t : threadList)			//wait for all of the threads to finish
			t.join();*/
		
		
		System.out.println("MUTATE COMPLETED");
		
		//CROSSOVER_____________________________________________________________________________________________________________________________
		ArrayList<NEATNetwork> replacementsCrossover = new ArrayList<NEATNetwork>();	//cycles through species list and for each pick two random NN from a species to mutate
		threadList = new ArrayList<Thread>();
		speciesIndex = 0;
		while(AmountFromCrossOver > 0){
			int speciesSize = population.get(speciesIndex).getPopulation().size();
			NEATNetwork NN1;
			NEATNetwork NN2;
			
			if(INTERSPECIESMATINGRATE >= Math.random()){	//probability that a NN will mate randomly outside its species
				NN1 = population.get(speciesIndex).getPopulation().get((int) (Math.random()*speciesSize)).createCopyFromGenes();		//Set NN1 to a copy of a random NN in the species
				int randomSpeciesIndex = (int)((population.size()-1) * Math.random());													//choose a random species
				speciesSize = population.get(randomSpeciesIndex).getPopulation().size();												//update species size to the size of the randomly chosen species
				NN2 = population.get(randomSpeciesIndex).getPopulation().get((int) (Math.random()*speciesSize)).createCopyFromGenes();	//Set NN2 to a copy of a random NN in a random species
			}else{
				NN1 = population.get(speciesIndex).getPopulation().get((int) (Math.random()*speciesSize)).createCopyFromGenes();	//Set NN1 to a copy of a random NN in the species
				NN2 = population.get(speciesIndex).getPopulation().get((int) (Math.random()*speciesSize)).createCopyFromGenes();	//Set NN2 to a copy of a random NN in the species
			}
			threadList.add(crossover(NN1, NN2));																							//create crossover thread
			AmountFromCrossOver--;
			
			if(speciesIndex >= population.size()-1)	//loop back if every species has had a random NN crossover
				speciesIndex = 0;
			else
				speciesIndex++;						//jump to the next species
		}
		System.out.println("CREATED THREADS");
		
		for(Thread t : threadList)			//start all of the threads
			t.start();
		System.out.println("STARTED THREADS");
		
		for(Thread t : threadList)			//wait for all of the threads to finish
			t.join();
		
		System.out.println("JOINED THREADS");
		for(Thread t : threadList)
			replacementsCrossover.add(((Crossover)t).getNN());																						//add NN that will be the resulting crossover
		
		System.out.println("CROSSOVER COMPLETED");
		
		//SPECIATE_____________________________________________________________________________________________________________________________
		ArrayList<NEATNetwork> replacements = new ArrayList<NEATNetwork>();	//combine replacements from mutate and crossover into one list
		for(NEATNetwork NN : replacementsMutate)
			replacements.add(NN);
		for(NEATNetwork NN : replacementsCrossover)
			replacements.add(NN);
		
		for(NEATNetwork NN : replacements){			//for each replacement
			Boolean newSpecies = true;
			for(Species s : population){
				if(!speciate(NN, s.getFirstNN())){	//same species
					s.add(NN);
					newSpecies = false;
					break;
				}
			}
			if(newSpecies){							//if no matching species create a new one for it
				population.add(new Species(NN, speciesIDCount++));
				speciesCount++;
			}
		}
		
		System.out.println("Number of Species: " + population.size());
		
		for(Species s : population)					//sort each species High to Low
			s.sortPopulation();
		
		System.out.println("SPECIATE COMPLETED");
		
		//EXECUTE_____________________________________________________________________________________________________________________________
		execute();																						//excute moved to seperate function in case child class wishes to override to be multithreaded
		System.out.println("EXCUTE COMPLETED");
		
		for(Species s : population){																	//update stagnant count for each species
			Boolean reset = false;
			for(NEATNetwork NN : s.getPopulation()){
				if(NN.getCurrentFitness() > s.getMaxFitness()){
					s.resetGenerationsWithoutImprovement();
					s.updateMaxFitness();																//update the species max fitness
					reset = true;
				}
			}
			if(!reset)																					//if the generations w/o improvement counter wasn't reset increment it
				s.incGenerationsWithoutImprovement();
		}
		
		
		generationCount++;
		printGenerationStatistics();
		System.out.println("FINISHED GENERATION: " + generationCount + "_____________________________________________________");
	}
	
	private void printGenerationStatistics(){
		double maxFitness = 0;
		for(Species s : population){
			//s.updateMaxFitness();
			if(s.getMaxFitness() > maxFitness)
				maxFitness = s.getMaxFitness();
			System.out.println("Max Species Fitness: " + s.getMaxFitness() + " Number of NN in Species: " + s.getPopulation().size() + " Species ID: " + s.getSpeciesID());
			//System.out.println("    CURRENT MAXFITNESS: " + maxFitness);
		}
		System.out.println("Overall Max Fitness: [" + maxFitness + "]");
	}
	
	public double fitness(NEATNetwork NN){ //defined per task, default is XOR
		double fitness = 0;
		NN.getInputNodes().get(2).setInput(1);	//bias node set to 1
		
		//0 + 0 -> 0
		NN.getInputNodes().get(0).setInput(0);
		NN.getInputNodes().get(1).setInput(0);
		NN.execute();
		if(!NN.getOutputNodes().get(0).checkFired())
			fitness+=0.25;
		
		//0 + 1 -> 1
		NN.getInputNodes().get(0).setInput(1);
		NN.getInputNodes().get(1).setInput(0);
		NN.execute();
		if(NN.getOutputNodes().get(0).checkFired())
			fitness+=0.25;
		
		//1 + 0 -> 1
		NN.getInputNodes().get(0).setInput(0);
		NN.getInputNodes().get(1).setInput(1);
		NN.execute();
		if(NN.getOutputNodes().get(0).checkFired())
			fitness+=0.25;
			
		//1 + 1 -> 0
		NN.getInputNodes().get(0).setInput(1);
		NN.getInputNodes().get(1).setInput(1);
		NN.execute();
		if(!NN.getOutputNodes().get(0).checkFired())
			fitness+=0.25;
		
		/*double count = 0;
		for(Layer l : NN.getHiddenLayers()){		//penalize network size
			for(Object hn : l.getNodeList()){
				Boolean enabled = false;
				for(Edge e : ((Node) hn).getIncomingEdges()){
					if(e.isEnabled())
						enabled = true;
				}
				if(enabled)
					count++;
			}
		}
					
		fitness-=count*0.001;
		
		if(count == 0)
			fitness = 0;*/
		
		return fitness;
	}
	
	public Thread crossover(NEATNetwork NN1, NEATNetwork NN2){
		return new Crossover(NN1, NN2);
	}
	
	private class Crossover extends Thread{
		NEATNetwork NN = new NEATNetwork();
		NEATNetwork NN1;
		NEATNetwork NN2;
		
		public Crossover(NEATNetwork nn1, NEATNetwork nn2){
			NN1 = nn1;
			NN2 = nn2;
		}
		
		public NEATNetwork getNN(){
			return NN;
		}
		
		@Override
	    public void run(){
			for(NodeGene ng : NN1.nodeGeneList)									//initialize with copy of the input/output nodeGenes
				if(ng.getNode() instanceof InputNode || ng.getNode() instanceof OutputNode)
					NN.nodeGeneList.add(ng);
			
			ArrayList<ConnectGene> cgList = new ArrayList<ConnectGene>();		//list of crossed-over connect genes
			ArrayList<Integer> nodeIDList = new ArrayList<Integer>();			//list of added nodes
			NN1.sortConnectGeneList();											//sort connectgenes low to high by innovation number
			NN2.sortConnectGeneList();
			int i=0;
			NEATNetwork largerNN;
			NEATNetwork smallerNN;
			NEATNetwork moreFitNN;
			
			if(NN1.getCurrentFitness() > NN2.getCurrentFitness())
				moreFitNN = NN1;
			else
				moreFitNN = NN2;
			
			if(NN1.connectGeneList.size() > NN2.connectGeneList.size()){			//determine which is the smaller network
				largerNN = NN1;
				smallerNN = NN2;
			}else{
				largerNN = NN2;
				smallerNN = NN1;
			}
			
			if(smallerNN.getConnectGeneList().size() == 0 && largerNN.getConnectGeneList().size() == 0)							//if either network is zero just set NN to either parent
				NN = NN1;
			if(smallerNN.getConnectGeneList().size() == 0 && largerNN.getConnectGeneList().size() != 0)
				NN = largerNN;
			if(smallerNN.getConnectGeneList().size() != 0 && largerNN.getConnectGeneList().size() == 0)
				NN = smallerNN;
			
			if(smallerNN.getConnectGeneList().size() != 0 && largerNN.getConnectGeneList().size() != 0){						//if either NN have no connects we can't crossover
				while(NN1.connectGeneList.get(i) == NN2.connectGeneList.get(i) && i<smallerNN.getConnectGeneList().size()-1){	//while we have matcing genes inherit randomly from parent
					Boolean cgn1Repeat = false;
					Boolean cgn2Repeat = false; 
					
					if(Math.random() > 0.5){
						if(nodeIDList.contains(NN1.connectGeneList.get(i).getInNode().getID()))				//check if in/out nodes have already been added
							cgn1Repeat = true;
						else
							nodeIDList.add(NN1.connectGeneList.get(i).getInNode().getID());
						
						if(nodeIDList.contains(NN1.connectGeneList.get(i).getOutNode().getID()))
							cgn2Repeat = true;
						else
							nodeIDList.add(NN1.connectGeneList.get(i).getOutNode().getID());
						
						NN.addConnectGene(NN1.connectGeneList.get(i));										//add the cg to the NN
						crossOverAddNodeGenes(NN, NN1, NN1.connectGeneList.get(i), cgn1Repeat, cgn2Repeat);	//add HiddenNodes
					}else{
						if(nodeIDList.contains(NN1.connectGeneList.get(i).getInNode().getID()))
							cgn1Repeat = true;
						else
							nodeIDList.add(NN1.connectGeneList.get(i).getInNode().getID());
						
						if(nodeIDList.contains(NN1.connectGeneList.get(i).getOutNode().getID()))
							cgn2Repeat = true;
						else
							nodeIDList.add(NN1.connectGeneList.get(i).getOutNode().getID());
						
						NN.addConnectGene(NN2.connectGeneList.get(i));
						crossOverAddNodeGenes(NN, NN2, NN2.connectGeneList.get(i), cgn1Repeat, cgn2Repeat);
					}
					i++;
				}
				
				while(i<moreFitNN.getConnectGeneList().size()-1){											//disjoint and excess genes always inhereted from the more fit parent
					Boolean cgn1Repeat = false;
					Boolean cgn2Repeat = false; 
					
					if(nodeIDList.contains(moreFitNN.connectGeneList.get(i).getInNode().getID()))
						cgn1Repeat = true;
					else
						nodeIDList.add(moreFitNN.connectGeneList.get(i).getInNode().getID());
					
					if(nodeIDList.contains(moreFitNN.connectGeneList.get(i).getOutNode().getID()))
						cgn2Repeat = true;
					else
						nodeIDList.add(moreFitNN.connectGeneList.get(i).getOutNode().getID());
					
					crossOverAddNodeGenes(NN, moreFitNN, moreFitNN.connectGeneList.get(i), cgn1Repeat, cgn2Repeat);
					NN.addConnectGene(moreFitNN.connectGeneList.get(i));
					i++;
				}
			}
			
			NN = NN.createCopyFromGenes();										//Set NN to a copy of itself to build and create new instances of the genes/nodes/connections
			for(ConnectGene cg : NN.connectGeneList)							//re-enable genes with probability MAINTAINDISBALEGENE
				if(!cg.isEnabled())
					if(Math.random() > MAINTAINDISBALEGENE)
						cg.toogleEnable();
	    }
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
	
	//slow, look into refactoring later
	public Thread mergeDuplicateInnovation(NEATNetwork NN, ArrayList<NEATNetwork> NN2List){
		Thread thread = new Thread(){
            @Override
            public void run(){
            	merge(NN2List);
            }
            
            private void merge(ArrayList<NEATNetwork> NN2List){	
            	for(ConnectGene cg : NN.connectGeneList){											//compare every connect gene in NN to every other connect gene		
            		for(NEATNetwork NN2 : NN2List){
						for(ConnectGene cg2 : NN2.connectGeneList){
							if(cg.getInNode().getID() == cg2.getInNode().getID() &&				//if the two nodes share the same in/out node but have different innovationNumbers
									cg.getOutNode().getID() == cg2.getOutNode().getID() &&
									cg.getInnovationNumber() != cg2.getInnovationNumber()){
								
								if(cg.getInnovationNumber() > cg2.getInnovationNumber()){		//set the higher iv number to the lower one
									cg.setInnovationNumber(cg2.getInnovationNumber());
								}else{
									cg2.setInnovationNumber(cg.getInnovationNumber());
								}
							}
						}
            		}
            	}
            }
            
		};
		return thread;
	}
	
	public Thread mutate(NEATNetwork NN){
		Thread thread = new Thread(){
            @Override
            public void run(){
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
							NN.updateEdgeWeight(cg.getEdge(), cg.getEdge().getWeight()+(-0.1+(0.2*Math.random())));
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
						
						if(ng1.getNode() instanceof HiddenNode && ng2.getNode() instanceof HiddenNode)	//nodes can't connect to nodes in a lower layer
							if(NN.getHiddenNodeLayerDepth((HiddenNode)ng1.getNode()) >= NN.getHiddenNodeLayerDepth((HiddenNode)ng2.getNode()))
								continue;
		
						Boolean preExistingConnection = false;											//if n1 -> n2 don't try to add another connection
						for(Edge e : ng1.getNode().getOutgoingEdges()){
							if(e.getNode2().equals(ng2.getNode())){
								preExistingConnection = true;
								break;
							}
						}
						
						if(!preExistingConnection && MUTATEADDCONNECTION >= Math.random())				//else connect n1 and n2 w/ probability MUTATEADDCONNECTION
							NN.addConnection(ng1.getNode(), ng2.getNode());
					}
				}
            }
		};
		return thread;
	}
	
	public boolean speciate(NEATNetwork n1, NEATNetwork n2){
		int n1Size = n1.getConnectGeneList().size();
		int n2Size = n2.getConnectGeneList().size();
		
		//constant weighting for speciation formula
		double c1 = 1.0;	//1.0
		double c2 = 1.0;	//1.0
		double c3 = 0.4;	//0.4
		double E = numExcessGenes(n1, n2);
		double D = numDisjointGenes(n1, n2);
		double N;			//number of genes in the larger network
		double w = 0;		//Average weight difference
		
		if(n1Size < 20 || n2Size < 20){	//if either networks have fewer than 20 connectGenes set N to 1
			N = 1;
		}else if(n1Size > n2Size){		//else set N to the smaller value
			N = n2Size;
		}else{
			N = n1Size;
		}
		
		for(ConnectGene cg1 : n1.getConnectGeneList())
			for(ConnectGene cg2 : n2.getConnectGeneList())
				if(cg1.getInnovationNumber() == cg2.getInnovationNumber())
					w += Math.abs(cg1.getWeight() - cg2.getWeight());
		//System.out.println("W!!: " + w);
		int numberOfMatchingGenes = numMatchingGenes(n1, n2);
		if(numberOfMatchingGenes != 0)
			w = w/numberOfMatchingGenes;
		
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
		int matches = 0;
		
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
		if(getBestNetwork() == null)
			return -1;
		return getBestNetwork().getCurrentFitness();
	}
	
	public NEATNetwork getBestNetwork(){
		NEATNetwork NN = population.get(0).getPopulation().get(0);
		double bestFitness = -1;
		for(Species s : population){
			if(s.getMaxFitness() > bestFitness){
				bestFitness = s.getMaxFitness();
				NN = s.getBestNetwork();
			}
		}
		
		return NN;
	}
}