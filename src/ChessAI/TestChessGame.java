package ChessAI;

import java.util.Scanner;

public class TestChessGame {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameSimulator NewGame = new GameSimulator();
		NewGame.Initialization();
		NewGame.DisplayMatrixInConsole(NewGame.GetBoard());

		//8x8 matrix stores game board in current round
		int[][] CurrentBoard;
		// 8x8 matrix stores game board in previous round
		int[][] CurrentPrevBoard;
		//1x4 array stores Other player's move in previous round
		//Original X, Original Y, Destination X, Destination Y
		int[] PrevMove;
		//2xn matrix stores coordinates of available chesses left on the game board
		int[][] AvailableChess;
		int ChessOrgX;
		int ChessOrgY;
		int ChessDesX;
		int ChessDesY;
		
		int GameRoundCounter = 0;
		
		while (!NewGame.DeathCheck()) {
			System.out.println("Game Round: " + Integer.toString(GameRoundCounter));
			System.out.println("Current Game Board:");
			NewGame.DisplayMatrixInConsole(NewGame.GetBoard());
			Scanner reader = new Scanner(System.in);
			System.out.println("Who's Turn: " + Integer.toString(NewGame.GetTurn()));
			
			
			
			
			ChessOrgX = reader.nextInt();
			ChessOrgY = reader.nextInt();
			System.out.println("Display The Move Matrix:");
			NewGame.DisplayMatrixInConsole(NewGame.GetMoveList(NewGame.GetBoard()[ChessOrgX][ChessOrgY], ChessOrgX, ChessOrgY));

			ChessDesX = reader.nextInt();
			ChessDesY = reader.nextInt();
			System.out.println("Input Validality:" + NewGame.CheckMoveValidality(ChessOrgX, ChessOrgY, ChessDesX, ChessDesY));
			System.out.println();
			
			if (NewGame.GetTurn() == 1) {
				System.out.println(NewGame.CheckMoveValidality(ChessOrgX, ChessOrgY, ChessDesX, ChessDesY));
				//Player 1 Turn
				System.out.println("Player 1 is moving the chess...");
				if (NewGame.Move(1, ChessOrgX, ChessOrgY, ChessDesX, ChessDesY)) {
					GameRoundCounter += 1;
				}
			}
			else {
				//Player 2 Turn
				System.out.println("Player 2 is moving the chess...");
				if (NewGame.Move(2, ChessOrgX, ChessOrgY, ChessDesX, ChessDesY)) {
					GameRoundCounter += 1;
				}
			}
			System.out.println("\n\n\n");
		}

	}

}
