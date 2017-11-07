package Main;
import Evolution.NEAT;

public class Main {
	public static void main(String[] args){

		int numInputNodes = 2;
		int numOutputNodes = 1;
		
		NEAT neat = new NEAT(numInputNodes, numOutputNodes);
		
		while(0.9 >= neat.getBestSpeciesFitness())
			neat.runGeneration();
		
		/*for(int i=0; i<100; i++)
			neat.runGeneration();*/
	}
}
