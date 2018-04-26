package DKAI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import Evolution.NEAT;
import Evolution.NEATNetwork;
import Evolution.Species;
import Evolution.Store;
import NEAT_GUI.GUINetworkFrame;

public class DKSampleTrainer extends NEAT{
	private static LuaInterface LI = new LuaInterface("./src/res/LUA.txt");
	private GUINetworkFrame GNF;
	
	
	ArrayList<ArrayList<Double>> view = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>(); //[i][up,down,left,right,a,b]

	private boolean resetFlag = false;
	private boolean pauseFlag = false;
	private boolean runSampleGenerationFlag = true;
	
	private boolean killFlag = false;
	
	double sampleFitnessThreshold = 1.0;
	
	public DKSampleTrainer() throws IOException{
		super(LI.getSmallInputs().size(), 5);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				try{
					GNF = new GUINetworkFrame();
					GNF.addKeyListener(new inputHandler());
					GNF.setVisible(true);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}		
		});
		
		System.out.println(LI.getSmallInputs().size());
		parallelExecution = true;	//initially we can run parallelExecution on sampleFitness		
		
		/*JFrame f = new JFrame();
		f.addKeyListener(new inputHandler());
		f.setVisible(true);*/

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
			//System.out.println(207-LI.position[6]);
			if(killFlag)				//end the run if killFlag is set
				break;
			if(resetFlag){				//reset game if flag is set
				LI.startNewGame();
				resetFlag = false;
			}
			
			LI.updateInputs();
			LI.writeOutputs();										//write outputs to LUA
			
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
					//System.out.println("FRAME CHANGED!\n");
					break;	//break as conditions loop was checking for have been met
				}
			}
			//this is for when you enter a frame holding a key but you want to leave the frame by holding a different set of keys
			if(!changed){								//if the frame hasn't changed
				for(int j=0; j<outputs.size(); j++){	//compare each of the outputs to each of the outputs in the last element of output
					if(output.get(output.size()-1).get(j) != LI.outputs[j]){	//if a single output has changed
						output.remove(output.size()-1);							//remove the last entry in output
																				//note don't remove and re-add view as it hasn't changed
						ArrayList<Integer> newOutputs = new ArrayList<Integer>();						//re-add the updated outputs
						for(Integer k : LI.outputs)
							newOutputs.add(k);
						
						output.add(newOutputs);
						//System.out.println("UPDATED NEW OUTPUTS");
						break;
					}
				}
			}
		}

		System.out.println("View Size: " + view.size());
		int i=0;
		int j=0;
		for(ArrayList<Double> ad : view){
			for(Double d : ad){
				System.out.print(d.intValue());
				i++;
				if(i%LI.getViewSize() == 0)
					System.out.println();
			}
			System.out.println("\n\nudlrab");
			for(Integer o : output.get(j)){
				System.out.print(o);
			}
			j++;
			System.out.println("\n_______________________");
			i=0;
		}
		System.out.println("Finsihed");
		//f.dispose();
	}
	
	Boolean finSample = false;
	@Override
	public double fitness(NEATNetwork NN){
		//System.out.println("NUMBER OF HIDDEN LAYERS: " + NN.getHiddenLayers().size());
		double maxFitness = getMaxFitness();
		
		if(maxFitness < sampleFitnessThreshold && runSampleGenerationFlag && parallelExecuteRunningFlag)
			return sampleFitness(NN);
		else
			return gameFitness(NN);
	}
	
	@Override
	public void updateParallelExecution(){
		double maxFitness = getMaxFitness();
		
		if((maxFitness >= sampleFitnessThreshold && !finSample) || (!runSampleGenerationFlag && !finSample)){
			System.out.println("PING PING PING");
			//we've finished with the sample so next execution we'll run the game fitness
			parallelExecution = false;			//running on the emulator so only one execution at a time
			finSample = true;
			removeNonMaxSpecies();
		}
	}
	
	private double getMaxFitness(){
		double maxFitness = 0;
		for(Species s : population)
			if(s.getMaxFitness() > maxFitness)
				maxFitness = s.getMaxFitness();
		return maxFitness;
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
			
			for(int j=0; j<view.get(i).size(); j++){	//for each input node set it equal to the corresponding int from the frame
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
			/*else
				break;		//Only reward sequential success.
*/		}

		return fitness/view.size();
	}
	
	
	public double gameFitness(NEATNetwork NN){
		System.out.println("NUMBER OF HIDDEN LAYERS: " + NN.getHiddenLayers().size());
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
			GNF.updateNetwork(NN, LI.getViewSize());
			
			for(int i=0; i<NN.getOutputNodes().size(); i++){		//set outputs based on fired nodes in output layer
				
				if(NN.getOutputNodes().get(i).checkFired())
					LI.outputs[i] = 1;
				else
					LI.outputs[i] = 0;
				//System.out.print(LI.outputs[i]);
			}
			//System.out.println("\n");
			LI.writeOutputs();										//write outputs to LUA
			
			if(LI.position[6] > maxHeight)		//fitness based on max height mario reaches b4 dieing + how quickly he manages to get there
				maxHeight = 207-LI.position[6];	//176 is max fitness
			
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
		public synchronized void keyPressed(KeyEvent e){	//up,down,left,right,a,b
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
			
			if(key == KeyEvent.VK_Q)				//press "Q" to reset the game while providing user training input
				resetFlag = true;
			
			if(key == KeyEvent.VK_O)				//press "O" to pause training
				pauseFlag = true;
			
			if(key == KeyEvent.VK_P)				//press "P" to resume training
				pauseFlag = false;
			
			if(key == KeyEvent.VK_W){				//press "W" to end training generations and proceed to game training
				runSampleGenerationFlag = false;
				System.out.println("ENDING TRAINING GENERATION");
			}
			
			if(key == KeyEvent.VK_E){				//press "E" to kill the run
				killFlag = true;
				System.out.println("killFlag set to 1");
			}
			
			if(key == KeyEvent.VK_S){				//press "E" to kill the run
				Store store = new Store();
				store.saveNet(getBestNetwork(), new File("./src/res/bestNetwork.network"));
			}
		}

		@Override
		public synchronized void keyReleased(KeyEvent e){
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
