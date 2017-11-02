package Evolution;

import java.util.ArrayList;

import NeuralNetwork.Edge;
import NeuralNetwork.HiddenNode;
import NeuralNetwork.InputNode;
import NeuralNetwork.OutputNode;

public class NEAT {
	private static final double MUTATEWEIGHT = 0.8;				//probability of changing a connection weight
	private static final double MUTATEWEIGHTTYPE = 0.1;			//probability of mutating uniformly or assigning a random value
	private static final double MUTATEADDNODE = 0.03;			//probability of adding a new node
	private static final double MUTATEADDCONNECTION = 0.01;		//probability of adding a new connection between existing nodes
	
	private static final double POPULATIONFROMCROSSOVER = 0.25;	//percentage of the next generations population forming from crossover
	private static final double MAINTAINDISBALEGENE = 0.75;		//probability that an inherited gene is disabled if it was disabled in either parent
	private static final double INTERSPECIESMATINGRATE = 0.001;	//probability that two different species mate
	
	private static final int MINIMUMSPECIESSIZE = 5;
	private static final int MAXSTAGNENTGENERATIONS = 30;
	

	private static final int POPULATIONSIZE = 100;
	private static final double POPULATIONELIMINATION = 0.5;
	
	protected static int innovationNumber = 1;
	protected static int nodeNumber = 1;
	ArrayList<Species> population = new ArrayList<Species>();
	int speciesCount = 0;
	int generationCount = 0;
	
	public NEAT(InputNode[] inputNodes, OutputNode[] outputNodes){
		innovationNumber += inputNodes.length * outputNodes.length;	//initialized to take into account the structure of the initial network
		nodeNumber += inputNodes.length + outputNodes.length;
		
		population.add(new Species());
		for(int i=0; i< POPULATIONSIZE; i++){							//initialize the Population
			NEATNetwork NN = new NEATNetwork(inputNodes, outputNodes);
			population.get(0).add(NN);								//initialize the speciesList
			population.get(0).setFirstNN(NN);
		}
		speciesCount++;
	}
	
	public void runGeneration(){
		ArrayList<ArrayList<NEATNetwork>> species = new ArrayList<ArrayList<NEATNetwork>>();
		//EXECUTE
		for(Species s : population){																	//run each NN and update their fitness
			for(NEATNetwork NN : s.getPopulation()){
				NN.setCurrentFitness(fitness(NN));
				System.out.println("Fitness: " + NN.getCurrentFitness());
			}
		}
		
		//ELIMINATE
		int eliminate = (int) ((POPULATIONSIZE/POPULATIONELIMINATION)/(population.size()));				//number to be eliminated from each species
		int eliminated = 0; 																			//actual number eliminated
		for(Species s : population){
			Boolean stagnantFlag = false;																//marks if maxfitness was updated
			for(NEATNetwork NN : s.getPopulation()){													//update stagnant markers
				if(NN.getCurrentFitness() > s.getMaxFitness()){
					s.resetGenerationsWithoutImprovement();
					s.updateMaxFitness(NN.getCurrentFitness());
					stagnantFlag = true;
				}
			}
			if(!stagnantFlag){
				s.incGenerationsWithoutImprovement();
				if(s.getGenerationsWithoutImprovement() >= MAXSTAGNENTGENERATIONS){						//eliminate stagnant species 
					eliminated += s.size();
					population.remove(s);
					speciesCount--;
				}
			}
			
			for(int i=0; i<eliminate; i++){
				if(s.size() <= MINIMUMSPECIESSIZE){														//if the species population is below minimum don't remove any
					continue;
				}else{
					s.removeLowest();																	//remove the lowest performer of the species
					eliminated++;
				}
			}
		}
		
		int AmountFromCrossOver = (int)(eliminated*POPULATIONFROMCROSSOVER);
		int AmountFromMutate = eliminated - AmountFromCrossOver;
		
		//MUTATE
		ArrayList<NEATNetwork> replacements = new ArrayList<NEATNetwork>();
		int speciesIndex = 0;
		int speciesDepth = 0;
		
		while(AmountFromMutate > 0){
			if(speciesIndex >= population.size()-1){
				speciesIndex = 0;
				speciesDepth++;
			}else{
				speciesIndex++;
			}
			if(speciesDepth >= population.get(speciesIndex).getPopulation().size()-1)										//skip to next species if we've reached max depth
				continue; 
			
			NEATNetwork NN = population.get(speciesIndex).getPopulation().get(speciesDepth).createCopyFromConnectGenes();	//Set NN to a copy of the referenced NN
			mutate(NN);
			replacements.add(NN);
			AmountFromMutate--;
		}
		
		//CROSSOVER
		speciesIndex = 0;
		speciesDepth = 0;
		while(AmountFromCrossOver > 0){
			if(speciesIndex >= population.size()){
				speciesIndex = 0;
				speciesDepth++;
			}else{
				speciesIndex++;
			}
			if(speciesDepth >= population.get(speciesIndex).getPopulation().size())											//skip to next species if we've reached max depth
				continue; 
			
			int speciesSize = population.get(speciesIndex).getPopulation().size();
			NEATNetwork NN1 = population.get(speciesIndex).getPopulation().get(speciesDepth).createCopyFromConnectGenes();						//Set NN to a copy of the referenced NN
			NEATNetwork NN2 = population.get(speciesIndex).getPopulation().get((int) (Math.random()*speciesSize)).createCopyFromConnectGenes();	//Set NN2 to a copy of the a random NN in the species
			
			replacements.add(crossover(NN1, NN2));
			AmountFromCrossOver--;
		}
		
		//SPECIATE
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
		
		for(Species s : population)					//sort each species High to Low
			s.sortPopulation();
	}
	
	public double fitness(NEATNetwork NN){ //defined per task
		double fitness = 0;
		
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
		
		return fitness;
	}
	
	public NEATNetwork crossover(NEATNetwork NN1, NEATNetwork NN2){
		NEATNetwork NN = new NEATNetwork();
		for(ConnectGene cg1 : NN1.connectGeneList){
			for(ConnectGene cg2 : NN2.connectGeneList){
				if(cg1.getInnovationNumber() == cg2.getInnovationNumber()){	//Matching Genes are inhereted randomly
					if(Math.random() > 0.5){
						NN.addConnectGene(cg1);
					}else{
						NN.addConnectGene(cg2);
					}
				}else{														//Disjoint & Excess Genes are always inhereted from the more fit parent
					if(NN1.getCurrentFitness() > NN2.getCurrentFitness()){
						NN.addConnectGene(cg1);
					}else{
						NN.addConnectGene(cg2);
					}
				}
			}
		}
		
		NN = NN.createCopyFromConnectGenes();
		for(ConnectGene cg : NN.connectGeneList)							//re-enable genes with probability MAINTAINDISBALEGENE
			if(!cg.isEnabled())
				if(Math.random() > MAINTAINDISBALEGENE)
					cg.toogleEnable();
		
		return NN;
	}
	
	public void mutate(NEATNetwork NN){
		//Need to add in tracking of innovations per generation to to prevent duplicate innovations
		
		for(ConnectGene cg : NN.connectGeneList){
			if(MUTATEWEIGHT <= Math.random()){								//mutate weights
				if(MUTATEWEIGHTTYPE <= Math.random()){						//replace weight with random value between -1 and 1
					NN.updateEdgeWeight(cg.getEdge(), -1+(2*Math.random()));
				}else{														//mutate the weight uniformly by multiplying by a number between 0.5 and 1.5
					NN.updateEdgeWeight(cg.getEdge(), 0.5+Math.random());
				}
			}
			
			if(MUTATEADDNODE <= Math.random()){								//mutate add new node
				HiddenNode n = new HiddenNode(nodeNumber);				
				nodeNumber++;
				NN.addNodeBetween(n, cg.getEdge().getNode1(), cg.getEdge().getNode2());
			}
		}
		
		for(NodeGene ng1 : NN.nodeGeneList){								//mutate add new connections
			for(NodeGene ng2 : NN.nodeGeneList){							//check if every node already connects to every other node
				for(Edge e : ng1.getNode().getOutgoingEdges()){
					if(e.getNode2().equals(ng2.getNode()))					//if n1 already connects to n2 don't try and add another connection
						continue;
					else if(MUTATEADDCONNECTION <= Math.random())			//else connect n1 and n2 on probability MUTATEADDCONNECTION
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
		int E = numExcessGenes(n1, n2);
		int D = numDisjointGenes(n1, n2);
		int N;			//number of genes in the larger network
		double w = 0;	//Average weight difference
		
		if(n1Size > n2Size){
			N = n1Size;
		}else{
			N = n2Size;
		}
		
		for(ConnectGene cg1 : n1.getConnectGeneList())
			for(ConnectGene cg2 : n2.getConnectGeneList())
				if(cg1.getInnovationNumber() == cg2.getInnovationNumber())
					w += Math.abs(cg1.getWeight() - cg2.getWeight());
		w = w/numMatchingGenes(n1, n2);
		
		return 3 >= (c1*E/N) + (c2*D/N) + (c3*w);
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
}
