package DKAI;

import java.io.IOException;
import java.util.ArrayList;

import Evolution.NEAT;
import Evolution.NEATNetwork;

public class DKTrainer extends NEAT{
	static LuaInterface LI = new LuaInterface("./src/res/LUA.txt");
	
	public DKTrainer() throws IOException {
		super(LI.getSmallInputs().size(), 6);
	}
	
	public double fitness(NEATNetwork NN){
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
			
			if(LI.outputs[3] == 1 && LI.timer == 5000)				//nudge networks in the "right" direction
				fitness = 1;
			
			if(LI.position[6] > maxHeight)		//fitness based on max height mario reaches b4 dieing + how quickly he manages to get there
				maxHeight = 207-LI.position[6];
			
			if(lastX != LI.position[5]){
				timeElapasedSinceLastMove = LI.timer;
				lastX = LI.position[5];
			}
			
			if(timeElapasedSinceLastMove-LI.timer == 300)		//if we've stood still for 3 ticks reset
				break;
			
			//System.out.println(timeElapasedSinceLastMove-LI.timer);
		}
		//System.out.println(yPos);
		fitness += maxHeight;
		fitness += LI.i_point/100; 					//plus how many points are earned
		//fitness += (5000-LI.timer)/100.0;  			//+ how long mario survies/100
		
		return fitness;
	}

}
