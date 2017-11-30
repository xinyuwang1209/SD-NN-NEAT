package ImageCategorizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Evolution.ConnectGene;
import Evolution.NEAT;
import Evolution.NEATNetwork;
import Evolution.Species;
import NeuralNetwork.Layer;

import java.awt.image.BufferedImage;


public class ImageTrainer extends NEAT{
	private static final int size = 90;
	private static final int numInputNodes = size*size;
	private static final int numOutputNodes = 1;
	private static String directoryA;
	private static String directoryB;
	private static File dirA;
	private static File dirB;
	private static ImageProcessor IP = new ImageProcessor();
	
	private static ArrayList<BufferedImage> directoryABufferedImages = new ArrayList<BufferedImage>();
	private static ArrayList<BufferedImage> directoryBBufferedImages = new ArrayList<BufferedImage>();
	
	
	public ImageTrainer(String dA, String dB) throws InterruptedException {
		super(numInputNodes+1, numOutputNodes);	//+1 to input node is for bias
		
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		directoryA = dA;
		directoryB = dB;
		dirA = new File(directoryA);
		dirB = new File(directoryB);
		
		for(String s : dirA.list())
			threadList.add(addImage(directoryA+s, directoryABufferedImages));
		
		for(String s : dirB.list())
			threadList.add(addImage(directoryB+s, directoryBBufferedImages));
		
		System.out.println("Loading images");
		for(Thread t : threadList)
			t.start();
		for(Thread t : threadList)
			t.join();
		System.out.println("Finshed Loading Images");
	}
	
	private Thread addImage(String filePath, ArrayList<BufferedImage> directoryBufferedImages){
		Thread thread = new Thread(){
            @Override
            public void run(){
				IP.loadAndProcessImage(filePath, size);
            	directoryBufferedImages.add(IP.getImage());
            }
		};
		return thread;
	}
	
	@Override
	public void execute(){										//overrides execute to update fitness for all NN concurrently
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for(Species s : population){
			for(NEATNetwork NN : s.getPopulation()){
				threads.add(updateFitness(NN));
			}
		}
		for(Thread t : threads)
			t.start();
		for(Thread t : threads){
			try{
				t.join();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	
	private Thread updateFitness(NEATNetwork NN){
		return new Thread(){
			@Override
            public void run(){
				NN.setCurrentFitness(fitness(NN));
            }
		};
	}
	
	@Override
	public double fitness(NEATNetwork NN){ //defined per task
		double fitness = 0;
		
		for(int c=0; c<400; c++){	//process 100 images
			BufferedImage img;
			File dir;
			if(c<200){	//choose an image from either dirA or dirB
				img = directoryABufferedImages.get(c);
				dir = dirA;
			}else{
				img = directoryBBufferedImages.get(c-200);
				dir = dirB;
			}
			IP.setImage(img);
			
			int sqrtSize = (int) Math.sqrt(numInputNodes);
            
			for(int x=0; x<sqrtSize; x++){
				for(int y=0; y<sqrtSize; y++){
					NN.getInputNodes().get((x+(y*sqrtSize))).setInput(IP.getPixelValue(x,y));
				}
			}
			NN.getInputNodes().get(numInputNodes).setInput(1);	//bias = 1
			
			
			NN.execute();
			
			if(dir.equals(dirA) && NN.getOutputNodes().get(0).checkFired())		//reward correct guess
				fitness++;
			if(dir.equals(dirB) && !NN.getOutputNodes().get(0).checkFired())
				fitness++;
		}
		
		return fitness;	//perfect fitness is 100
	}
	
	
}
