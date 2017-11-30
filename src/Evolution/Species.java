package Evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Species{
	int generationsWithoutImprovement = 0;
	double maxFitness = 0;
	NEATNetwork firstNN;
	int speciesID;
	
	ArrayList<NEATNetwork> population = new ArrayList<NEATNetwork>();
	
	public Species(int sID){
		this(null, sID);
	}
	
	public Species(NEATNetwork nn, int sID){
		if(nn != null){
			population.add(nn);
			firstNN = nn;
			speciesID = sID;
		}
	}
	
	public int getSpeciesID(){
		return speciesID;
	}
	
	public NEATNetwork getFirstNN(){
		return firstNN;
	}
	
	public void setFirstNN(NEATNetwork nn){
		firstNN = nn;
	}
	
	public void add(NEATNetwork nn){
		population.add(nn);
	}
	
	public void removeLowest(){
		ArrayList<NEATNetwork> lowestPerformer = new ArrayList<NEATNetwork>();		//place lowestPerformer in arrayList, if duplicates w/ same performance select randomly
		double lowestFitness = maxFitness;
		
		for(int i=0; i<population.size(); i++){
			if(population.get(i).getCurrentFitness() == lowestFitness && !population.get(i).equals(firstNN))
				lowestPerformer.add(population.get(i));
			
			if(population.get(i).getCurrentFitness() < lowestFitness && !population.get(i).equals(firstNN)){	//remove lowestFitness but don't remove the firstNN
				lowestPerformer = new ArrayList<NEATNetwork>();
				lowestPerformer.add(population.get(i));
				lowestFitness = population.get(i).getCurrentFitness();
			}
		}
		
		population.remove(lowestPerformer.get((int)Math.floor(Math.random()*lowestPerformer.size())));	//remove the lowest performer
	}
	
	public NEATNetwork getBestNetwork(){
		for(NEATNetwork NN : population)
			if(NN.getCurrentFitness() == maxFitness)
				return NN;
		return null;
	}
	
	public int size(){
		return population.size();
	}
	
	public int getGenerationsWithoutImprovement(){
		return generationsWithoutImprovement;
	}
	
	public void incGenerationsWithoutImprovement(){
		generationsWithoutImprovement++;
	}
	
	public void resetGenerationsWithoutImprovement(){
		generationsWithoutImprovement = 0;
	}
	
	public double getMaxFitness(){
		return maxFitness;
	}
	
	public void updateMaxFitness(){
		for(NEATNetwork NN : population)
			if(maxFitness < NN.getCurrentFitness())
				maxFitness = NN.getCurrentFitness();
	}
	
	public ArrayList<NEATNetwork> getPopulation(){
		return population;
	}
	
	public void sortPopulation(){	//sorts population from Highest to Lowest
		Collections.sort(population, new Comparator<NEATNetwork>() {
			@Override
			public int compare(NEATNetwork nn1, NEATNetwork nn2){
				return Double.compare(nn2.getCurrentFitness(), nn1.getCurrentFitness());
			}
		});
	}
}

