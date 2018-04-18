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
	ArrayList<ArrayList<Integer>> RANK;
	
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
	
	public ChessSampleTrainer() throws IOException, InterruptedException{
		super(128, 1);	
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
	
	public void BuildRank() {
		// Assuming population size is 100 The code implemented here is desgined for flexible population.
		int PopulationSize = population.get(0).size();
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
						NewPlayers.add(Players.get(CurrentPlayersSize-i));
					}
				}
				else {
					Divider = 0 - Divider;
					j = PopulationSize - 1 - i;
					if ((PopulationSize % 2) == 1) {
						NewPlayers.add(Players.get(0));
					}
				}
				Winner = RunGame(population.get(0).getPopulation().get(Players.get(j)),population.get(0).getPopulation().get(Players.get(j+Divider)));
				if (Winner == 1) {
					NewPlayers.add(j);
					CurrentLevelRank.add(j+Divider);
				}
				else {
					NewPlayers.add(j+Divider);
					CurrentLevelRank.add(j);
				}
			}
			CurrentPlayersSize = Players.size();
			NewRank.add(CurrentLevelRank);
		}
		
		RANK = NewRank;
		return;
	}
	
	
	
	
	
	
	@Override
	public double fitness(NEATNetwork NN){
		BuildRank();
//		Play tournemnt
		double maxFitness = 0;
		for(Species s : population) {
			if(s.getMaxFitness() > maxFitness) {
				maxFitness = s.getMaxFitness();
			}
		}
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

}