package Evolution;

import NeuralNetwork.InputNode;
import NeuralNetwork.OutputNode;

public class NEAT {
	protected static int innovationNumber = 1;
	protected static int nodeNumber = 1;
	NEATNetwork[] population = new NEATNetwork[100];
	
	public NEAT(InputNode[] inputNodes, OutputNode[] outputNodes){
		innovationNumber += inputNodes.length * outputNodes.length;	//initialized to take into account the structure of the initial network
		nodeNumber += inputNodes.length + outputNodes.length;
		
		for(NEATNetwork NN : population){	//initialize the Population
			NN = new NEATNetwork(inputNodes, outputNodes);
		}
	}
	
	public void runGeneration(){
		
	}
	
	public void mutate(){
		
	}
	
	public void crossover(){
		
	}
	
	public double speciate(NEATNetwork n1, NEATNetwork n2){
		int n1Size = n1.getConnectGeneList().size();
		int n2Size = n2.getConnectGeneList().size();
		
		double c1 = 1;
		double c2 = 1;
		double c3 = 1;
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
		
		return (c1*E/N) + (c2*D/N) + (c3*w);
	}
	
	public int numDisjointGenes(NEATNetwork n1, NEATNetwork n2){
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
	
	public int numMatchingGenes(NEATNetwork n1, NEATNetwork n2){
		int matches = 0;
		
		for(ConnectGene cg1 : n1.getConnectGeneList())
			for(ConnectGene cg2 : n2.getConnectGeneList())
				if(cg1.getInnovationNumber() == cg2.getInnovationNumber())
					matches++;
		
		return matches;
	}
	
	public int numExcessGenes(NEATNetwork n1, NEATNetwork n2){
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
	
	public void eliminate(){
		
	}
	
	public void fitness(){ //defined per task
		
	}
}
