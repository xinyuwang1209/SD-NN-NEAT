package DKAI;

import java.io.IOException;
import java.util.ArrayList;

import Evolution.NEAT;
import Evolution.NEATNetwork;

public class DKTrainer extends NEAT{
	static LuaInterface LI = new LuaInterface();
	public DKTrainer() throws IOException {
		super(LI.getSmallInputs().size()+2, 6);
	}
	
	@Override
	public double fitness(NEATNetwork NN){
		LI.startNewGame();
		double fitness = 0;
		LI.updateInputs();										//load inputs
		ArrayList<Integer> inputs = LI.getSmallInputs();;
		
		while(LI.deathFlag == 0){									//keep running until death
			LI.updateInputs();	
			inputs = LI.getSmallInputs();
			for(int i=0; i<inputs.size(); i++){						//set the value for each input node
				NN.getInputNodes().get(i).setInput(inputs.get(i));
			}
			NN.getInputNodes().get(NN.getInputNodes().size()-2).setInput(LI.position[6]);	//pass YPos
			NN.getInputNodes().get(NN.getInputNodes().size()-1).setInput(LI.position[7]);	//Pass XPos
			
			NN.execute();											//execute on input
			
			for(int i=0; i<NN.getOutputNodes().size(); i++){		//set outputs based on fired nodes in output layer
				if(NN.getOutputNodes().get(i).checkFired())
					LI.outputs[i] = 1;
				else
					LI.outputs[i] = 0;
			}
			LI.writeOutputs();										//write outputs to LUA
		}

		fitness = ((int)((207-LI.position[6])/32.0)*10) +	//fitness is how high mario gets
				LI.i_point + 					//plus how many points are earned
				(5000-LI.timer)/100.0;  		//+ how long mario survies/100
		
		return fitness;
	}

}
