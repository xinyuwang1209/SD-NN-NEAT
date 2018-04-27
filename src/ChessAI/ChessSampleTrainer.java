package ChessAI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import Evolution.NEAT;
import Evolution.NEATNetwork;
import Evolution.Species;
import Evolution.Store;
import NEAT_GUI.GUINetworkFrame;

public class ChessSampleTrainer extends NEAT{
	


	ArrayList<ArrayList<Double>> view = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>(); //[i][up,down,left,right,a,b]

	ArrayList<Integer> rank = new ArrayList<Integer>();
	double sampleFitnessThreshold = 1.0;
	static GameSimulator game = new GameSimulator();
	Boolean finSample = false;
	ArrayList<ArrayList<Integer>> RANK;
	
	private GUINetworkFrame GNF;
	private static final int ViewSize = 128;
	Boolean UpdateGUI = false;
	
	public ChessSampleTrainer() throws IOException, InterruptedException{
		super(ViewSize, 1);
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
		Thread.sleep(1000);
	}
	
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
	
	
	public int RunGame(NEATNetwork NN1, NEATNetwork NN2) {
		// TODO Auto-generated method stub
		GameSimulator NewGame = new GameSimulator();
		NewGame.Initialization();
		//NewGame.DisplayMatrixInConsole(NewGame.GetBoard());

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
			//System.out.println("Game Round: " + Integer.toString(GameRoundCounter));
			//System.out.println("Current Game Board:");
			//NewGame.DisplayMatrixInConsole(NewGame.GetBoard());
			/*try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
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
			//System.out.println("NewGame.GetTurn(): " + NewGame.GetTurn());
			if (CurrentPlayer == 1) {
				NN = NN1;
				//System.out.println("P1 turn");
			}
			else {
				NN = NN2;
				//System.out.println("P2 turn");
			}
//						MoveListSimulation = game.GenerateInput(CurrentPlayer);
			
			AvailChess = NewGame.GetAvailChess(CurrentPlayer);
			Boolean moveTaken = false;
			
			/*int randomI = (int)(Math.random()*AvailChess.length);	//used to randomly start I index
			for (int i=0; i<AvailChess.length;i++) {
				randomI++;											//inc
				if(!(randomI < AvailChess.length))					//wrap around
					randomI = 0;
				if(moveTaken)
					break;
				if (CurrentBoard[AvailChess[i][0]][AvailChess[i][1]] != 0) {
					CurrentMoveMatrix = NewGame.GetMoveList(CurrentBoard[AvailChess[i][0]][AvailChess[i][1]], AvailChess[i][0], AvailChess[i][1]);
					
					
					int randomJ = (int)(Math.random()*8);	//used to randomly start J index
					int randomK = (int)(Math.random()*8);	//used to randomly start K index
					for (int j=0;j<8;j++) {
						randomJ++;							//inc
						if(!(randomJ < 8))					//wrap around
							randomJ = 0;
						if(moveTaken)
							break;
						for (int k=0;k<8;k++) {
							randomK++;						//inc
							if(!(randomK < 8))				//wrap around
								randomK = 0;
							if(moveTaken)
								break;
							if (CurrentMoveMatrix[randomJ][randomK] != 0) {
								SimulateBoard = NewGame.SimulateMove(CurrentPlayer, AvailChess[i][0], AvailChess[i][1], randomJ, randomK);
								inputs = GenerateArrayList(CurrentBoard, SimulateBoard, CurrentPlayer);
								
								for (int m=0;m<inputs.size()-1;m++) {
									if(moveTaken)
										break;
									if(NN == NN1)													//flip nodes values such that the current players nodes are always positive
										NN.getInputNodes().get(m).setInput(inputs.get(m));
									if(NN == NN2)
										NN.getInputNodes().get(m).setInput(-1*inputs.get(m));

									NN.execute();
									if(UpdateGUI)
										GNF.updateNetwork(NN, 8);
									//System.out.println("Who's Turn: " + Integer.toString(NewGame.GetTurn()));
									//System.out.println(NewGame.CheckMoveValidality(AvailChess[i][0], AvailChess[i][1], j, k));
									//System.out.println("Player is moving the chess...");

									if (NN.getOutputNodes().get(0).checkFired()) {
										if (NewGame.Move(CurrentPlayer, AvailChess[i][0], AvailChess[i][1], randomJ, randomK)) {
											GameRoundCounter += 1;
											moveTaken = true;
											System.out.println("Current Game Board:");
											NewGame.DisplayMatrixInConsole(NewGame.GetBoard());
										}
									}
								}
							}
						}
					}
				}
			}*/
			/*if(!moveTaken){
				while(true){
					int i = (int)(Math.random() * AvailChess.length);
					int j = (int)(Math.random()*8);
					int k = (int)(Math.random()*8);
					//System.out.println("i=" + i + " j=" + j +" k=" + k);
					
					CurrentMoveMatrix = NewGame.GetMoveList(CurrentBoard[AvailChess[i][0]][AvailChess[i][1]], AvailChess[i][0], AvailChess[i][1]);
					if (CurrentMoveMatrix[j][k] != 0){
						NewGame.Move(CurrentPlayer, AvailChess[i][0], AvailChess[i][1], j, k);
						GameRoundCounter += 1;
						System.out.println("Current Game Board:");
						NewGame.DisplayMatrixInConsole(NewGame.GetBoard());
						break;
					}
				}
			}*/
			if(!moveTaken){
				while(true){
					int i = (int)(Math.random() * AvailChess.length);
					int j = (int)(Math.random()*8);
					int k = (int)(Math.random()*8);
					//System.out.println("i=" + i + " j=" + j +" k=" + k);
					
					CurrentMoveMatrix = NewGame.GetMoveList(CurrentBoard[AvailChess[i][0]][AvailChess[i][1]], AvailChess[i][0], AvailChess[i][1]);
					if (CurrentMoveMatrix[j][k] != 0){
						NewGame.Move(CurrentPlayer, AvailChess[i][0], AvailChess[i][1], j, k);
						GameRoundCounter += 1;
						/*System.out.println("Current Game Board:");
						NewGame.DisplayMatrixInConsole(NewGame.GetBoard());*/
						//____________________________________________________________________________
						SimulateBoard = NewGame.SimulateMove(CurrentPlayer, AvailChess[i][0], AvailChess[i][1], j, i);
						inputs = GenerateArrayList(CurrentBoard, SimulateBoard, CurrentPlayer);
						for (int m=0;m<inputs.size()-1;m++){
							if(NN == NN1)													//flip nodes values such that the current players nodes are always positive
								NN.getInputNodes().get(m).setInput(inputs.get(m));
							if(NN == NN2)
								NN.getInputNodes().get(m).setInput(-1*inputs.get(m));
						}
						NN.execute();
						if(UpdateGUI)
							GNF.updateNetwork(NN, 8);
						//____________________________________________________________________________
						
						break;
					}
				}
			}
			if(Math.random() > 0.95)
				Winner = 1;
			else
				Winner = 2;
			
			//System.out.println("The Winner is Player " + Winner);
			return(Winner);
			//System.out.println("\n\n\n");
		}
		Winner = NewGame.GetWinner();
		//System.out.println("The Winner is Player " + Winner);
		return(Winner);
	}
	
	public void BuildRank() {
		ArrayList<NEATNetwork> tournamentPopulation = new ArrayList<NEATNetwork>();
		for(Species s : population)
			for(NEATNetwork nn : s.getPopulation())
				tournamentPopulation.add(nn);
		
		// Assuming population size is 100 The code implemented here is desgined for flexible population.
		int PopulationSize = tournamentPopulation.size();
		int Level = 0;
		int Divider = (int) Math.floor(PopulationSize/2);
		ArrayList<ArrayList<Integer>> NewRank = new ArrayList<ArrayList<Integer>>();
		int CurrentPlayersSize;
		int j;
		int Winner;
		ArrayList<Integer> NewPlayers = new ArrayList<Integer>();
		ArrayList<Integer> CurrentLevelRank;
		ArrayList<Integer> Players = new ArrayList<Integer>();
		for (int i=0;i<PopulationSize;i++) {
			Players.add(i);
		}
		CurrentPlayersSize = Players.size();
		while (CurrentPlayersSize > 1) {
			CurrentLevelRank = new ArrayList<Integer>();
			NewPlayers = new ArrayList<Integer>();
			Divider = (int) Math.floor(CurrentPlayersSize/2);
			for (int i=0;i<Divider;i++) {
				if ((Level % 2) == 0) {
					j = i;	
					if ((PopulationSize % 2) == 1) {
//						System.out.println("Testing: " + CurrentPlayersSize);
//						System.out.println(Players.size());
						NewPlayers.add(Players.get(CurrentPlayersSize-1));
					}
				}
				else {
					Divider = 0 - Divider;
					j = PopulationSize - 1 - i;
					if ((PopulationSize % 2) == 1) {
						NewPlayers.add(Players.get(0));
					}
				}
				
				Winner = RunGame(tournamentPopulation.get(Players.get(j)),tournamentPopulation.get(Players.get(j+Divider)));
				if (Winner == 1) {
					NewPlayers.add(j);
					CurrentLevelRank.add(j+Divider);
				}
				else {
					NewPlayers.add(j+Divider);
					CurrentLevelRank.add(j);
				}
			}
			Players = NewPlayers;
			CurrentPlayersSize = Players.size();
			NewRank.add(CurrentLevelRank);
		}
		
		CurrentLevelRank = new ArrayList<Integer>();
		CurrentLevelRank.add(Players.get(0));
		
		NewRank.add(CurrentLevelRank);
		
		RANK = NewRank;
	}
	
	
	
	@Override
	public void execute() throws InterruptedException{
		System.out.println("parallelExecution: "  + parallelExecution);
		BuildRank();
		if(!parallelExecution){
			for(Species s : population){																	//run each NN and update their fitness
				for(NEATNetwork NN : s.getPopulation()){
					NN.incGenerationsAlive();
					double fitness = fitness(NN);
					NN.setCurrentFitness(fitness);
					//System.out.println("NN " + NN + " Set to: " + fitness);
				}
			}
		}else{
			ArrayList<Thread> threadList = new ArrayList<Thread>();
			for(Species s : population){																	//run each NN and update their fitness
				for(NEATNetwork NN : s.getPopulation()){
					threadList.add(new Thread(){
							@Override
				            public void run(){
				            	NN.setCurrentFitness(fitness(NN));
				            }
						}
					);
				}
			}
			for(Thread t : threadList)			//start all of the threads
				t.start();
			for(Thread t : threadList)			//wait for all of the threads to finish
				t.join();	
		}
	}

	
	
	@Override
	public double fitness(NEATNetwork NN){
		ArrayList<NEATNetwork> tournamentPopulation = new ArrayList<NEATNetwork>();
		for(Species s : population)
			for(NEATNetwork nn : s.getPopulation())
				tournamentPopulation.add(nn);
		
		double fitness = 0;
		int LevelSize;
		ArrayList<Integer> CurrentLevelRank;
		int NNIndex;
		for (int i=0; i<RANK.size();i++) {
			CurrentLevelRank = RANK.get(i);	//ArrayList containing the indices to tournamentPopulation for the corresponding NN
			for (int j=0; j<CurrentLevelRank.size(); j++) {
				NNIndex = CurrentLevelRank.get(j);
				if (NN == tournamentPopulation.get(NNIndex)) {
					//System.out.println("NN: " + NN);
					fitness = i+1;
					/*System.out.println("i: " + i);
					System.out.println("Rank.size(): " + RANK.size());
					System.out.println("NNIndex(): " + NNIndex);
					System.out.println("WE SUCCESSFULLY DETERMINED A FITNESS: " + fitness);
					System.out.println();*/
				}
			}
		}
		//System.out.println("NN ID: " + NN + " Returned Fitness: " + (NN.getCurrentFitness() + fitness)/NN.incGenerationsAlive());
		//return (NN.getCurrentFitness() + fitness)/NN.getGenerationsAlive();	//fitness is a running average of all ranks
		return NN.getConnectGeneList().size() + NN.getNodeGeneList().size();
	}

	private class inputHandler implements KeyListener{
		@Override
		public void keyTyped(KeyEvent e){
			//System.out.println("HERE");
		}

		@Override
		public synchronized void keyPressed(KeyEvent e){	//up,down,left,right,a,b
			int key = e.getKeyCode();
			
			if(key == KeyEvent.VK_Q)
				UpdateGUI = true;
			if(key == KeyEvent.VK_W)
				UpdateGUI = false;
		}

		@Override
		public synchronized void keyReleased(KeyEvent e){
			int key = e.getKeyCode();
			
			/*if(key == KeyEvent.VK_Q)
				LI.outputs[0] = 0;*/
		}
		
	}
}