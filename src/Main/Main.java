package Main;
import Evolution.NEAT;
import ImageCategorizer.ImageProcessor;
import ImageCategorizer.ImageTrainer;
import NeuralNetwork.Edge;
import NeuralNetwork.Layer;
import NeuralNetwork.Node;

public class Main {
	public static void main(String[] args) throws InterruptedException{
		/*ImageProcessor IP = new ImageProcessor();
		IP.loadAndProcessImage("C:/Users/Nyarlathotep/Desktop/directoryA/1495997661352.png", 90);
		IP.displayLoadedImage();*/
		
		while(true){
			IT.runGeneration();

		/*int numInputNodes = 3;
		int numOutputNodes = 1;
		NEAT neat = new NEAT(numInputNodes, numOutputNodes);
		while(1 != neat.getBestSpeciesFitness())
			neat.runGeneration();
		System.out.println("COMPLETED: " + neat.getBestSpeciesFitness());*/
		
				
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

	}
}
