package DKAI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import Evolution.NEAT;
import Evolution.NEATNetwork;
import Evolution.Species;

public class DKSampleTrainer extends NEAT{
	static LuaInterface LI = new LuaInterface("./src/res/LUA.txt", "./src/res/Java.txt");
	ArrayList<ArrayList<Double>> view = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>();

	private boolean resetFlag = false;
	private boolean pauseFlag = false;
	
	public DKSampleTrainer() throws IOException{
		super(LI.getSmallInputs().size(), 6);
		System.out.println(LI.getSmallInputs().size());
		parallelExecution = true;	//initially we can run parallelExecution on sampleFitness		
		
		JFrame f = new JFrame();
		f.addKeyListener(new inputHandler());
		f.setVisible(true);
		
		
		
		LI.startNewGame();
		LI.updateInputs();											//load inputs
		ArrayList<Double> inputs = LI.getSmallInputs();
		try{
			Thread.sleep(500);										//wait 500ms
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		view.add(inputs);
		
		ArrayList<Integer> outputs = new ArrayList<Integer>();
		for(Integer x : LI.outputs)
			outputs.add(x);
		
		output.add(outputs);

		int frameCounter = 0;
		while(LI.deathFlag == 0){
			if(resetFlag){				//reset game if flag is set
				LI.startNewGame();
				resetFlag = false;
			}
			
			LI.writeOutputs();										//write outputs to LUA
			LI.updateInputs();

			inputs = LI.getSmallInputs();/*
			System.out.println("View: " + view.size());
			System.out.println("Out: " + output.size());*/
			Boolean changed = false;
			for(int i=0; i<inputs.size(); i++){
				if(!inputs.get(i).equals(view.get(view.size()-1).get(i))){	//if a single input is differet then the frame has changed
					frameCounter++;
					view.add(inputs);
					
					outputs = new ArrayList<Integer>();
					for(Integer j : LI.outputs)
						outputs.add(j);
					
					output.add(outputs);
					changed = true;
				}
			}
			//this is for when you enter a frame holding a key but you want to leave the frame by holding a different set of keys
			if(!changed){								//if the frame hasn't changed
				for(int j=0; j<outputs.size(); j++){	//compare each of the outputs to each of the outputs in the last element of output
					if(output.get(output.size()-1).get(j) != outputs.get(j)){	//if a single output has changed
						output.remove(output.size()-1);							//remove the last entry in output
																				//note don't removove and re-add view as it hasn't changed
						outputs = new ArrayList<Integer>();						//re-add the updated outputs
						for(Integer k : LI.outputs)
							outputs.add(k);
						
						output.add(outputs);
					}
				}
			}
			
		}

		System.out.println("View Size: " + view.size());
		System.out.println("Finsihed");
		//f.dispose();
	}
	
	Boolean finSample = false;
	double sampleFitnessThreshold = 0.90;
	@Override
	public double fitness(NEATNetwork NN){
		double maxFitness = 0;
		for(Species s : population)
			if(s.getMaxFitness() > maxFitness)
				maxFitness = s.getMaxFitness();
		
		
		if(maxFitness >= sampleFitnessThreshold && finSample == false){
			finSample = true;
			removeNonMaxSpecies();
		}
		
		if(maxFitness < sampleFitnessThreshold){
			double sf = sampleFitness(NN);
			if(sf >= sampleFitnessThreshold){		//we've finished with the sample so next execution we'll run the game fitness
				parallelExecution = false;			//running on the emulator so only one execution at a time
				
			}
			return sampleFitness(NN);
		}else{
			return gameFitness(NN);
		}
	}
	
	public void removeNonMaxSpecies(){
		double maxFitness = 0;
		for(Species s : population)
			if(s.getMaxFitness() > maxFitness)
				maxFitness = s.getMaxFitness();
		
		for(int i=0; i<population.size(); i++){
			if(population.get(i).getMaxFitness() != maxFitness){
				population.remove(i);
				i--;
			}
		}
				
	}
	
	
	public double sampleFitness(NEATNetwork NN){
		while(pauseFlag){	//busy waiting while paused
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		double fitness = 0;
		for(int i=0; i<view.size(); i++){	//for each saved frame in view
			
			for(int j=0; j<NN.getInputNodes().size(); j++){	//for each input node set it equal to the corresponding int from the frame
				NN.getInputNodes().get(j).setInput(view.get(i).get(j));
			}
			
			NN.execute();
			
			Boolean match = true;
			for(int o=0; o<NN.getOutputNodes().size(); o++){
				if(NN.getOutputNodes().get(o).checkFired() && output.get(i).get(o) != 1)
					match = false;
				if(!NN.getOutputNodes().get(o).checkFired() && output.get(i).get(o) == 1)
					match = false;
			}
			
			if(match)
				fitness++;
		}

		return fitness/view.size();
	}
	
	
	public double gameFitness(NEATNetwork NN){
		LI.startNewGame();
		double fitness = 0;
		LI.updateInputs();										//load inputs
		ArrayList<Double> inputs = LI.getSmallInputs();
		
		int maxHeight = 0;
		int timeElapasedSinceLastMove = LI.timer;
		int lastX = LI.position[5];
		
		while(LI.deathFlag == 0){									//keep running until death
			LI.updateInputs();
			inputs = LI.getSmallInputs();
			for(int i=0; i<inputs.size(); i++){						//set the value for each input node
				NN.getInputNodes().get(i).setInput(inputs.get(i));
			}
			
			NN.execute();											//execute on input
			
			for(int i=0; i<NN.getOutputNodes().size(); i++){		//set outputs based on fired nodes in output layer
				if(NN.getOutputNodes().get(i).checkFired())
					LI.outputs[i] = 1;
				else
					LI.outputs[i] = 0;
			}
			LI.writeOutputs();										//write outputs to LUA
			
			if(LI.position[6] > maxHeight)		//fitness based on max height mario reaches b4 dieing + how quickly he manages to get there
				maxHeight = 207-LI.position[6];
			
			/*if(lastX != LI.position[5]){
				timeElapasedSinceLastMove = LI.timer;
				lastX = LI.position[5];
			}
			
			if(timeElapasedSinceLastMove-LI.timer == 300)		//if we've stood still for 3 ticks reset
				break;*/
			
			//System.out.println(timeElapasedSinceLastMove-LI.timer);
		}
		//System.out.println(yPos);
		fitness += maxHeight;
		fitness += LI.i_point/100; 					//plus how many points are earned
		//fitness += (5000-LI.timer)/100.0;  			//+ how long mario survies/100
		
		return fitness;
	}
	
	private class inputHandler implements KeyListener{
		@Override
		public void keyTyped(KeyEvent e){
			//System.out.println("HERE");
		}

		@Override
		public void keyPressed(KeyEvent e){	//up,down,left,right,a,b
			int key = e.getKeyCode();
			
			if(key == KeyEvent.VK_UP)
				LI.outputs[0] = 1;
			if(key == KeyEvent.VK_DOWN)
				LI.outputs[1] = 1;
			if(key == KeyEvent.VK_LEFT)
				LI.outputs[2] = 1;
			if(key == KeyEvent.VK_RIGHT)
				LI.outputs[3] = 1;
			if(key == KeyEvent.VK_A)
				LI.outputs[4] = 1;
			if(key == KeyEvent.VK_B)
				LI.outputs[5] = 1;
			
			if(key == KeyEvent.VK_Q)
				resetFlag = true;
			
			if(key == KeyEvent.VK_O)
				pauseFlag = true;
			
			if(key == KeyEvent.VK_P)
				pauseFlag = false;
		}

		@Override
		public void keyReleased(KeyEvent e){
			int key = e.getKeyCode();
			
			if(key == KeyEvent.VK_UP)
				LI.outputs[0] = 0;
			if(key == KeyEvent.VK_DOWN)
				LI.outputs[1] = 0;
			if(key == KeyEvent.VK_LEFT)
				LI.outputs[2] = 0;
			if(key == KeyEvent.VK_RIGHT)
				LI.outputs[3] = 0;
			if(key == KeyEvent.VK_A)
				LI.outputs[4] = 0;
			if(key == KeyEvent.VK_B)
				LI.outputs[5] = 0;
		}
		
	}

}
