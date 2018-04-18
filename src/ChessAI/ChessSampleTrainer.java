package ChessAI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import Evolution.NEAT;
import Evolution.NEATNetwork;
import Evolution.Species;

public class ChessSampleTrainer extends NEAT{

	ArrayList<ArrayList<Double>> view = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>(); //[i][up,down,left,right,a,b]

	ArrayList<Integer> rank = new ArrayList<Integer>();
	double sampleFitnessThreshold = 1.0;
	static GameSimulator game = new GameSimulator();
	Boolean finSample = false;
	
	public ArrayList<Integer> GenerateArrayList(int[][] Board, int[][]SimulateBoard, int Player){
		ArrayList<Integer> input = new ArrayList<Integer>();
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				input.add(Board[i][j]);
			}
		}
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				input.add(SimulateBoard[i][j]);
			}
		}
		input.add(Player);
		return(input);
	}	
	
	public ChessSampleTrainer() throws IOException{
		super(128, 1);
		
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run(){
////				try{
////					GNF = new GUINetworkFrame();
////					GNF.addKeyListener(new inputHandler());
////					GNF.setVisible(true);
////				}
////				catch(Exception e){
////					e.printStackTrace();
////				}
//			}		
//		});
		
//		game.DisplayMatrixInConsole(game.GetBoard());
		
//		parallelExecution = true;	//initially we can run parallelExecution on sampleFitness		
		
		/*JFrame f = new JFrame();
		f.addKeyListener(new inputHandler());
		f.setVisible(true);*/

//		LI.startNewGame();
//		LI.updateInputs();											//load inputs
//		ArrayList<Double> inputs = LI.getSmallInputs();
		
		
//		try{
//			Thread.sleep(500);										//wait 500ms
//		}catch(InterruptedException e){
//			e.printStackTrace();
//		}
		
		this.runGeneration();
	}
	
	public int RunGame(NEATNetwork NN1, NEATNetwork NN2) {
		// TODO Auto-generated method stub
		GameSimulator NewGame = new GameSimulator();
		NewGame.Initialization();
		NewGame.DisplayMatrixInConsole(NewGame.GetBoard());

		//8x8 matrix stores game board in current round
		int[][] CurrentBoard;
		// 8x8 matrix stores game board in previous round
//		int[][] CurrentPrevBoard;
		//1x4 array stores Other player's move in previous round
		//Original X, Original Y, Destination X, Destination Y
//		int[] PrevMove;
		//2xn matrix stores coordinates of available chesses left on the game board
//		int[][] AvailableChess;
//		int ChessOrgX;
//		int ChessOrgY;
//		int ChessDesX;
//		int ChessDesY;
//		int[][][] MoveListSimulation = new int[1][8][8];
		NEATNetwork NN;
		int CurrentPlayer;
		int[][] AvailChess;
//		int[][] MoveList;
		int GameRoundCounter = 0;
		int[][] CurrentMoveMatrix;
		int[][] SimulateBoard;
		ArrayList<Integer> inputs = new ArrayList<Integer>();
//		ArrayList<Integer> outputs = new ArrayList<Integer>();
		int Winner;
		
		while (!NewGame.DeathCheck()) {
			System.out.println("Game Round: " + Integer.toString(GameRoundCounter));
			System.out.println("Current Game Board:");
			NewGame.DisplayMatrixInConsole(NewGame.GetBoard());
			CurrentBoard = NewGame.GetBoard();
			
			
//					Scanner reader = new Scanner(System.in);
						
//					ChessOrgX = reader.nextInt();
//					ChessOrgY = reader.nextInt();
//					System.out.println("Display The Move Matrix:");
//					NewGame.DisplayMatrixInConsole(NewGame.GetMoveList(NewGame.GetBoard()[ChessOrgX][ChessOrgY], ChessOrgX, ChessOrgY));
//
//					ChessDesX = reader.nextInt();
//					ChessDesY = reader.nextInt();
//					System.out.println("Input Validality:" + NewGame.CheckMoveValidality(ChessOrgX, ChessOrgY, ChessDesX, ChessDesY));
//					System.out.println();
//					
			CurrentPlayer = NewGame.GetTurn();
			if (CurrentPlayer == 1) {
				NN = NN1;
			}
			else {
				NN = NN2;
			}
//						MoveListSimulation = game.GenerateInput(CurrentPlayer);
			AvailChess = NewGame.GetAvailChess(CurrentPlayer);
			for (int i=0; i<AvailChess.length;i++) {
				if (CurrentBoard[AvailChess[i][0]][AvailChess[i][1]] != 0) {
					CurrentMoveMatrix = NewGame.GetMoveList(CurrentBoard[AvailChess[i][0]][AvailChess[i][1]], AvailChess[i][0], AvailChess[i][1]);
					for (int j=0;j<8;j++) {
						for (int k=0;k<8;k++) {
							if (CurrentMoveMatrix[j][k] != 0) {
								SimulateBoard = NewGame.SimulateMove(CurrentPlayer, AvailChess[i][0], AvailChess[i][1], j, k);
								inputs = GenerateArrayList(CurrentBoard, SimulateBoard, CurrentPlayer);
								for (int m=0;m<inputs.size();m++) {
									NN.getInputNodes().get(i).setInput(inputs.get(i));

									NN.execute();
									System.out.println("Who's Turn: " + Integer.toString(NewGame.GetTurn()));
									System.out.println(NewGame.CheckMoveValidality(AvailChess[i][0], AvailChess[i][1], j, k));
									System.out.println("Player is moving the chess...");

									if (NN1.getOutputNodes().get(0).checkFired()) {
										if (NewGame.Move(CurrentPlayer, AvailChess[i][0], AvailChess[i][1], j, k)) {
											GameRoundCounter += 1;
										}
									}
								}
							}
						}
					}
				}
			}
			System.out.println("\n\n\n");
		}
		Winner = NewGame.GetWinner();
		System.out.println("The Winner is Player " + Winner);
		return(Winner);
	}
	
	public void Build
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public double fitness(NEATNetwork NN){
		
//		Play tournemnt
		double maxFitness = 0;
		for(Species s : population)
			if(s.getMaxFitness() > maxFitness)
				maxFitness = s.getMaxFitness();
		
		
		if((maxFitness >= sampleFitnessThreshold && !finSample) || (!runSampleGenerationFlag && !finSample)){
			System.out.println("PING PING PING ");
			//we've finished with the sample so next execution we'll run the game fitness
			parallelExecution = false;			//running on the emulator so only one execution at a time
			finSample = true;
			removeNonMaxSpecies();
		}
		
		if(maxFitness < sampleFitnessThreshold && runSampleGenerationFlag)
			return sampleFitness(NN);
		else
			return gameFitness(NN);
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
			GNF.updateNetwork(NN, 7);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
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
			
			if(key == KeyEvent.VK_O)				//press "P" to pause training
				pauseFlag = true;
			
			if(key == KeyEvent.VK_P)				//press "P" to resume training
				pauseFlag = false;
			
			if(key == KeyEvent.VK_W){				//press "W" to end training generations and proceed to game training
				runSampleGenerationFlag = false;
				System.out.println("ENDING TRAINING GENERATION");
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