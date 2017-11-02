package Main;
import Evolution.NEAT;
import NeuralNetwork.InputNode;
import NeuralNetwork.OutputNode;

public class Main {
	public static void main(String[] args){
		InputNode[] inputNodes = new InputNode[2];
		OutputNode[] outputNodes = new OutputNode[1];
		
		inputNodes[0] = new InputNode(0);
		inputNodes[1] = new InputNode(1);
		outputNodes[0] = new OutputNode(2);
		
		NEAT neat = new NEAT(inputNodes, outputNodes);
		neat.runGeneration();
	}
}
