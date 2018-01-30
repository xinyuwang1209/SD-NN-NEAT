package Main;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import DKAI.DKSampleTrainer;
import DKAI.DKTrainer;
import DKAI.LuaInterface;
import DKAI.WatchDir;
import Evolution.NEAT;
import ImageCategorizer.ImageProcessor;
import ImageCategorizer.ImageTrainer;
import NeuralNetwork.Edge;
import NeuralNetwork.Layer;
import NeuralNetwork.Node;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException{
        /*LuaInterface LI = new LuaInterface();
        LI.startNewGame();
        LI.outputs[3] = 1; 
        LI.writeOutputs();
        Thread.sleep(5000);
        LI.outputs[3] = 0; 
        LI.outputs[2] = 1; 
        LI.outputs[4] = 1; 
        LI.writeOutputs();
        Thread.sleep(2000);
        LI.updateInputs();*/
		
		
		DKSampleTrainer test = new DKSampleTrainer();
		while(true)
			test.runGeneration();
		//DKTrainer DK = new DKTrainer();
		//while(true)
		//	DK.runGeneration();
		
		
		
		
		//___________________________________________________________________________________________________________________
		//Runs the Image Trainer. No relevance to the project; however, it is a good example for how to extend NEAT to other applications
		/*ImageProcessor IP = new ImageProcessor();
		IP.loadAndProcessImage("C:/Users/Nyarlathotep/Desktop/directoryA/1495997661352.png", 90);
		IP.displayLoadedImage();*/
		
		/*ImageTrainer IT = new ImageTrainer("C:/Users/Nyarlathotep/Desktop/directoryA/", "C:/Users/Nyarlathotep/Desktop/directoryB/");
		while(true){
			IT.runGeneration();
		}*/
		
		//___________________________________________________________________________________________________________________
		
		//Runs a single XOR tesqt and returns the final fitness
		/*int numInputNodes = 3;
		int numOutputNodes = 1;
		NEAT neat = new NEAT(numInputNodes, numOutputNodes);
		while(1 != neat.getBestSpeciesFitness())
			neat.runGeneration();
		System.out.println("COMPLETED: " + neat.getBestSpeciesFitness());*/
		
		//___________________________________________________________________________________________________________________
		
		//Runs 100 XOR tests and prints the average, min, and max solve times as well as the average number of nodes used
		/*int numInputNodes = 3;
		int numOutputNodes = 1;
		double total = 0.0;
		double totalActiveNodes = 0.0;
		int min = 0;
		int max = 0;
		for(int i=0; i<100; i++){
			int count = 0;
			NEAT neat = new NEAT(numInputNodes, numOutputNodes);
			while(0.9 >= neat.getBestSpeciesFitness()){
				neat.runGeneration();
				count++;
			}
			
			double activeNodes = 0;
			for(Layer l : neat.getBestNetwork().getHiddenLayers()){
				for(Object hn : l.getNodeList()){
					Boolean enabled = false;
					for(Edge e : ((Node) hn).getIncomingEdges()){
						if(e.isEnabled())
							enabled = true;
					}
					if(enabled)
						activeNodes++;
				}
			}
			totalActiveNodes += activeNodes;
			
			total += count;
			if(min == 0 && max == 0){
				min = count;
				max = count;
			}
			if(min > count)
				min = count;
			if(max < count)
				max = count;
		}
		System.out.println("Average Solve Time: " + total/100 + " Generations");
		System.out.println("Max Solve Time: " + max + " Generations");
		System.out.println("Min Solve Time: " + min + " Generations");

		System.out.println("Average Active Nodes: " + totalActiveNodes/100);*/
	}
}
