package ChessAI;

public class GameSimulator {
	//private int[] counter = new int[338];
	private int[][] Board = new int[8][8];
	private int[][] PrevBoard = new int[8][8];
	public boolean DeathFlag;
	private int[][] P1AvailChess = new int[16][2];
	private int[][] P2AvailChess = new int[16][2];
	private int P1KingX;
	private int P1KingY;
	private int P2KingX;
	private int P2KingY;
	private int Turn;
	private int Winner = 0;
	private int[] PrevMove = {0,0,0,0};
	//Original X, Original Y, Destination X, Destination Y

	public int[] GetPrevMove() {
		return(PrevMove);
	}
	
	public GameSimulator(){
	}
	
	public int[][] GetAvailChess(int Player) {
		if (Player == 1) {
			return(P1AvailChess);
		}
		else {
			return(P2AvailChess);
		}
	}

	
	public int GetWinner () {
		return(Winner);
	}
	
	public int[][] GetBoard() {
		return(Board);
	}
	
	public int[][] GetPrevBoard() {
		return(PrevBoard);
	}
	
	public int GetTurn() {
		return(Turn);
	}

	
	public int[][] CloneBoard(int[][] DesBoard) {
		int[][] TargetBoard = new int[8][8];
		for (int i=0; i<DesBoard.length; i++) {
			TargetBoard[i] = DesBoard[i].clone();
		}
		return(TargetBoard);
	}
	public boolean CheckMoveValidality(int ChessOrgX, int ChessOrgY, int ChessDesX, int ChessDesY) {
		int[][] ChessMoveList = GetMoveList(Board[ChessOrgX][ChessOrgY],ChessOrgX,ChessOrgY);
		if (ChessMoveList[ChessDesX][ChessDesY] == 1) {
			return(true);
		}
		else {
			return(false);
		}
	}
	
	public void DisplayMatrixInConsole(int[][] Matrix) {
		for (int i = 0; i < Matrix.length; i++) {
		    for (int j = 0; j < Matrix[i].length; j++) {
		        System.out.print(Matrix[i][j] + " ");
		    }
		    System.out.println();
		}
	}
	public void Initialization() {
		//abs(king=1 queen=2 rook = 3 bishop = 4 knight = 5 pawn = 6)
		Board[0][4]=-1;
		P1AvailChess[0][0] = 0;
		P1AvailChess[0][1] = 4;		
		Board[0][3]=-2;
		P1AvailChess[1][0] = 0;
		P1AvailChess[1][1] = 3;		
		Board[0][2]=-3;
		P1AvailChess[2][0] = 0;
		P1AvailChess[2][1] = 2;		
		Board[0][5]=-3;
		P1AvailChess[3][0] = 0;
		P1AvailChess[3][1] = 5;		
		Board[0][1]=-4;
		P1AvailChess[4][0] = 0;
		P1AvailChess[4][1] = 1;		
		Board[0][6]=-4;
		P1AvailChess[5][0] = 0;
		P1AvailChess[5][1] = 6;		
		Board[0][0]=-5;
		P1AvailChess[6][0] = 0;
		P1AvailChess[6][1] = 0;		
		Board[0][7]=-5;
		P1AvailChess[7][0] = 0;
		P1AvailChess[7][1] = 7;		
		
		Board[7][4]=1;
		P2AvailChess[0][0] = 7;
		P2AvailChess[0][1] = 4;		
		Board[7][3]=2;
		P2AvailChess[1][0] = 7;
		P2AvailChess[1][1] = 3;		
		Board[7][2]=3;
		P2AvailChess[2][0] = 7;
		P2AvailChess[2][1] = 2;		
		Board[7][5]=3;
		P2AvailChess[3][0] = 7;
		P2AvailChess[3][1] = 5;		
		Board[7][1]=4;
		P2AvailChess[4][0] = 7;
		P2AvailChess[4][1] = 1;		
		Board[7][6]=4;
		P2AvailChess[5][0] = 7;
		P2AvailChess[5][1] = 6;		
		Board[7][0]=5;
		P2AvailChess[6][0] = 7;
		P2AvailChess[6][1] = 0;		
		Board[7][7]=5;
		P2AvailChess[7][0] = 7;
		P2AvailChess[7][1] = 7;		
		
		for (int i=0; i<8; i++) {
			Board[1][i]=-6;
			P1AvailChess[8+i][0] = 1;
			P1AvailChess[8+i][1] = i;					
			Board[6][i]=6;
			P2AvailChess[8+i][0] = 6;
			P2AvailChess[8+i][1] = i;					
		}
		PrevBoard = CloneBoard(Board);
		 DeathFlag = false;
		 P1KingX = 0;
		 P1KingY = 4;
		 P2KingX = 7;
		 P2KingY = 4;
		 Turn = 2;
	}

	public boolean DeathCheck() {
		if (Board[P1KingX][P1KingY] != -1) {
			DeathFlag = true;
			System.out.println("P2 wins");
			Winner = 2;
		}
		if (Board[P2KingX][P2KingY] != 1) {
			DeathFlag = true;
			System.out.println("P1 wins");
			Winner = 1;
		} 
		else {
			return(false);
		}
		return(true);
	}
	public boolean IsInBoard(int ChessX, int ChessY) {
		if ((ChessX >= 0) && (ChessX <= 7) && (ChessY >= 0) && (ChessY <= 7)) {
			return(true);
		}
		else {
			return(false);
		}
	}
	public int[][] GetMoveList (int ChessId, int ChessX, int ChessY) {
		int[][] MoveList = new int[8][8];
		int ChessAbsId = Math.abs(ChessId);
		int ChessSign = (int) Math.signum(ChessId);
		int temp;
		if (ChessAbsId == 1) {
			for (int i=-1;i<2;i++) {
				for (int j=-1; j<2; j++) {
					if (!((i==j) & (i==0))) {
						if (IsInBoard(ChessX+i, ChessY+j)) {
							if (Board[ChessX+i][ChessY+j] != 0) {
								temp = (int) Math.signum(Board[ChessX+i][ChessY+j]);
								if (ChessSign != temp) {
									MoveList[ChessX+i][ChessY+j] = 1;
								}
							}
							else {
								MoveList[ChessX+i][ChessY+j] = 1;
							}
						}
					}
				}
			}
		}
		else if (ChessAbsId == 2) {
			boolean HitUp = false;
			boolean HitLeft = false;
			boolean HitRight = false;
			boolean HitBottom = false;
			boolean HitLeftTop = false;
			boolean HitRightTop = false;
			boolean HitLeftBottom = false;
			boolean HitRightBottom = false;
						
			for (int i=1; i<8; i++) {
				if (ChessX-i > 0) {
					if (!HitUp) {
						temp = (int) Math.signum(Board[ChessX-i][ChessY]);
						if (Board[ChessX-i][ChessY] == 0) {
							MoveList[ChessX-i][ChessY] = 1;
						}
						else {
							HitUp = true;
							if (temp != ChessSign) {
								MoveList[ChessX-i][ChessY] = 1;
							}
						}
					}
				}
				if (ChessX+i < 7) {
					if (!HitBottom) {
						temp = (int) Math.signum(Board[ChessX+i][ChessY]);
						if (Board[ChessX+i][ChessY] == 0) {
							MoveList[ChessX+i][ChessY] = 1;
						}
						else {
							HitBottom = true;
							if (temp != ChessSign) {
								MoveList[ChessX+i][ChessY] = 1;
							}
						}
					}
				}
				if (ChessY-i > 0) {
					if (!HitLeft) {
						temp = (int) Math.signum(Board[ChessX][ChessY-i]);
						if (Board[ChessX][ChessY-i] == 0) {
							MoveList[ChessX][ChessY-i] = 1;
						}
						else {
							HitLeft = true;
							if (temp != ChessSign) {
								MoveList[ChessX][ChessY-i] = 1;
							}
						}
					}
				}
				if (ChessY+i > 0) {
					if (!HitRight) {
						temp = (int) Math.signum(Board[ChessX][ChessY+i]);
						if (Board[ChessX][ChessY+i] == 0) {
							MoveList[ChessX][ChessY+i] = 1;
						}
						else {
							HitRight = true;
							if (temp != ChessSign) {
								MoveList[ChessX][ChessY+i] = 1;
							}
						}
					}
				}
				if ((ChessX-i > 0) & ChessY-i > 0) {
					if (!HitLeftTop) {
						temp = (int) Math.signum(Board[ChessX-i][ChessY-i]);
						if (Board[ChessX-i][ChessY-i] == 0) {
							MoveList[ChessX-i][ChessY-i] = 1;
						}
						else {
							HitLeftTop = true;
							if (temp != ChessSign) {
								MoveList[ChessX-i][ChessY-i] = 1;
							}
						}
					}
				}
				if ((ChessX-i > 0) & ChessY+i < 7) {
					if (!HitRightTop) {
						temp = (int) Math.signum(Board[ChessX-i][ChessY+i]);
						if (Board[ChessX-i][ChessY+i] == 0) {
							MoveList[ChessX-i][ChessY+i] = 1;
						}
						else {
							HitRightTop = true;
							if (temp != ChessSign) {
								MoveList[ChessX-i][ChessY+i] = 1;
							}
						}
					}
				}
				if ((ChessX+i < 7) & ChessY-i > 0) {
					if (!HitLeftBottom) {
						temp = (int) Math.signum(Board[ChessX+i][ChessY-i]);
						if (Board[ChessX+i][ChessY-i] == 0) {
							MoveList[ChessX+i][ChessY-i] = 1;
						}
						else {
							HitLeftBottom = true;
							if (temp != ChessSign) {
								MoveList[ChessX+i][ChessY-i] = 1;
							}
						}
					}
				}
				if ((ChessX+i < 7) & ChessY+i < 0) {
					if (!HitRightBottom) {
						temp = (int) Math.signum(Board[ChessX+i][ChessY+i]);
						if (Board[ChessX+i][ChessY+i] == 0) {
							MoveList[ChessX+i][ChessY+i] = 1;
						}
						else {
							HitRightBottom = true;
							if (temp != ChessSign) {
								MoveList[ChessX+i][ChessY+i] = 1;
							}
						}
					}
				}
			}

		}
		else if (ChessAbsId == 3) {
			boolean HitLeftTop = false;
			boolean HitRightTop = false;
			boolean HitLeftBottom = false;
			boolean HitRightBottom = false;
			
			for (int i=1; i<8; i++) {
				if ((ChessX-i > 0) & ChessY-i > 0) {
					if (!HitLeftTop) {
						temp = (int) Math.signum(Board[ChessX-i][ChessY-i]);
						if (Board[ChessX-i][ChessY-i] == 0) {
							MoveList[ChessX-i][ChessY-i] = 1;
						}
						else {
							HitLeftTop = true;
							if (temp != ChessSign) {
								MoveList[ChessX-i][ChessY-i] = 1;
							}
						}
					}
				}
				if ((ChessX-i > 0) & ChessY+i < 7) {
					if (!HitRightTop) {
						temp = (int) Math.signum(Board[ChessX-i][ChessY+i]);
						if (Board[ChessX-i][ChessY+i] == 0) {
							MoveList[ChessX-i][ChessY+i] = 1;
						}
						else {
							HitRightTop = true;
							if (temp != ChessSign) {
								MoveList[ChessX-i][ChessY+i] = 1;
							}
						}
					}
				}
				if ((ChessX+i < 7) & ChessY-i > 0) {
					if (!HitLeftBottom) {
						temp = (int) Math.signum(Board[ChessX+i][ChessY-i]);
						if (Board[ChessX+i][ChessY-i] == 0) {
							MoveList[ChessX+i][ChessY-i] = 1;
						}
						else {
							HitLeftBottom = true;
							if (temp != ChessSign) {
								MoveList[ChessX+i][ChessY-i] = 1;
							}
						}
					}
				}
				if ((ChessX+i < 7) & ChessY+i < 0) {
					if (!HitRightBottom) {
						temp = (int) Math.signum(Board[ChessX+i][ChessY+i]);
						if (Board[ChessX+i][ChessY+i] == 0) {
							MoveList[ChessX+i][ChessY+i] = 1;
						}
						else {
							HitRightBottom = true;
							if (temp != ChessSign) {
								MoveList[ChessX+i][ChessY+i] = 1;
							}
						}
					}
				}
			}
		}
		else if (ChessAbsId == 4) {
			if (IsInBoard(ChessX+1, ChessY+2)) {
				temp = (int) Math.signum(Board[ChessX+1][ChessY+2]);
				if (Board[ChessX+1][ChessY+2] == 0) {
					MoveList[ChessX+1][ChessY+2] = 1;
				}
				else {
					if (temp != ChessSign) {
						MoveList[ChessX+1][ChessY+2] = 1;
					}
				}
			}
			if (IsInBoard(ChessX+1, ChessY-2)) {
				temp = (int) Math.signum(Board[ChessX+1][ChessY-2]);
				if (Board[ChessX+1][ChessY-2] == 0) {
					MoveList[ChessX+1][ChessY-2] = 1;
				}
				else {
					if (temp != ChessSign) {
						MoveList[ChessX+1][ChessY-2] = 1;
					}
				}
			}
			if (IsInBoard(ChessX+2, ChessY+1)) {
				temp = (int) Math.signum(Board[ChessX+2][ChessY+1]);
				if (Board[ChessX+2][ChessY+1] == 0) {
					MoveList[ChessX+2][ChessY+1] = 1;
				}
				else {
					if (temp != ChessSign) {
						MoveList[ChessX+2][ChessY+1] = 1;
					}
				}
			}
			if (IsInBoard(ChessX+2, ChessY-1)) {
				temp = (int) Math.signum(Board[ChessX+2][ChessY-1]);
				if (Board[ChessX+2][ChessY-1] == 0) {
					MoveList[ChessX+2][ChessY-1] = 1;
				}
				else {
					if (temp != ChessSign) {
						MoveList[ChessX+2][ChessY-1] = 1;
					}
				}
			}
			if (IsInBoard(ChessX-1, ChessY-2)) {
				temp = (int) Math.signum(Board[ChessX-1][ChessY-2]);
				if (Board[ChessX-1][ChessY-2] == 0) {
					MoveList[ChessX-1][ChessY-2] = 1;
				}
				else {
					if (temp != ChessSign) {
						MoveList[ChessX-1][ChessY-2] = 1;
					}
				}
			}
			if (IsInBoard(ChessX-1, ChessY+2)) {
				temp = (int) Math.signum(Board[ChessX-1][ChessY+2]);
				if (Board[ChessX-1][ChessY+2] == 0) {
					MoveList[ChessX-1][ChessY+2] = 1;
				}
				else {
					if (temp != ChessSign) {
						MoveList[ChessX-1][ChessY+2] = 1;
					}
				}
			}
			if (IsInBoard(ChessX-2, ChessY-1)) {
				temp = (int) Math.signum(Board[ChessX-2][ChessY-1]);
				if (Board[ChessX-2][ChessY-1] == 0) {
					MoveList[ChessX-2][ChessY-1] = 1;
				}
				else {
					if (temp != ChessSign) {
						MoveList[ChessX-2][ChessY-1] = 1;
					}
				}
			}
			if (IsInBoard(ChessX-2, ChessY+1)) {
				temp = (int) Math.signum(Board[ChessX-2][ChessY+1]);
				if (Board[ChessX-2][ChessY+1] == 0) {
					MoveList[ChessX-2][ChessY+1] = 1;
				}
				else {
					if (temp != ChessSign) {
						MoveList[ChessX-2][ChessY+1] = 1;
					}
				}
			}
		}
		else if (ChessAbsId == 5) {
			boolean HitUp = false;
			boolean HitLeft = false;
			boolean HitRight = false;
			boolean HitBottom = false;
			
			for (int i=1; i<8; i++) {
				if (ChessX-i > 0) {
					if (!HitUp) {
						temp = (int) Math.signum(Board[ChessX-i][ChessY]);
						if (Board[ChessX-i][ChessY] == 0) {
							MoveList[ChessX-i][ChessY] = 1;
						}
						else {
							HitUp = true;
							if (temp != ChessSign) {
								MoveList[ChessX-i][ChessY] = 1;
							}
						}
					}
				}
				if (ChessX+i < 7) {
					if (!HitBottom) {
						temp = (int) Math.signum(Board[ChessX+i][ChessY]);
						if (Board[ChessX+i][ChessY] == 0) {
							MoveList[ChessX+i][ChessY] = 1;
						}
						else {
							HitBottom = true;
							if (temp != ChessSign) {
								MoveList[ChessX+i][ChessY] = 1;
							}
						}
					}
				}
				if (ChessY-i > 0) {
					if (!HitLeft) {
						temp = (int) Math.signum(Board[ChessX][ChessY-i]);
						if (Board[ChessX][ChessY-i] == 0) {
							MoveList[ChessX][ChessY-i] = 1;
						}
						else {
							HitLeft = true;
							if (temp != ChessSign) {
								MoveList[ChessX][ChessY-i] = 1;
							}
						}
					}
				}
				if (ChessY+i < 8) {
					if (!HitRight) {
						temp = (int) Math.signum(Board[ChessX][ChessY+i]);
						if (Board[ChessX][ChessY+i] == 0) {
							MoveList[ChessX][ChessY+i] = 1;
						}
						else {
							HitRight = true;
							if (temp != ChessSign) {
								MoveList[ChessX][ChessY+i] = 1;
							}
						}
					}
				}
			}
		}
		else if (ChessAbsId == 6) {
			if (Board[ChessX-ChessSign][ChessY] == 0) {
				MoveList[ChessX-ChessSign][ChessY] = 1;
//				System.out.println("For two moves");
//				System.out.println(((ChessX == 1) && (ChessSign == -1)) | ((ChessX == 6) && (ChessSign == 1)));
				if (((ChessX == 1) && (ChessSign == -1)) | ((ChessX == 6) && (ChessSign == 1))) {
					if (Board[ChessX-2*ChessSign][ChessY] == 0) {
						MoveList[ChessX-2*ChessSign][ChessY] = 1;
					}
				}
			}
			if (ChessY > 0) {
				temp = (int) Math.signum(Board[ChessX-ChessSign][ChessY-1]);
				if ((temp != ChessSign) & (Board[ChessX-ChessSign][ChessY-1] != 0)) {
					MoveList[ChessX-ChessSign][ChessY-1] = 1;
				}
//				if (ChessX + ChessSign/2 == 3.5) {
//					if ((Board[ChessX][ChessY-1] == -ChessId) && (Board[ChessX-2*ChessSign][ChessY-1] == 0)) {
//						if ((PrevBoard[ChessX-2*ChessSign][ChessY-1] == 0) && (PrevBoard[ChessX][ChessY-1] == -ChessId)) {
//							MoveList[ChessX-ChessSign][ChessY-1] = 1;
//						}					
//					}					
//				}
			}
			if (ChessY < 7) {
				temp = (int) Math.signum(Board[ChessX-ChessSign][ChessY+1]);
				if ((temp != ChessSign) & (Board[ChessX-ChessSign][ChessY+1] != 0)) {
					MoveList[ChessX-ChessSign][ChessY+1] = 1;
				}
//				if (ChessX + ChessSign/2 == 3.5) {
//					if ((Board[ChessX][ChessY+1] == -ChessId) && (Board[ChessX-2*ChessSign][ChessY+1] == 0)) {
//						if ((PrevBoard[ChessX-2*ChessSign][ChessY+1] == 0) && (PrevBoard[ChessX][ChessY+1] == -ChessId)) {
//							MoveList[ChessX-ChessSign][ChessY+1] = 1;
//						}
//					}
//				}
			}
		}
		return(MoveList);

	}
	
	public int[][] SimulateMove(int Player, int ChessOrgX, int ChessOrgY, int ChessDesX, int ChessDesY) {
		int[][] OutputBoard = new int[8][8];
//		System.out.println("Trying to move Chess...");
		int[][] TempPrevBoard = CloneBoard(PrevBoard);
		OutputBoard = CloneBoard(Board);
		int temp = (int) Math.signum(OutputBoard[ChessOrgX][ChessOrgY]);
//		System.out.println("Sign of targeted Chess: " + Integer.toString(temp));
		if (((temp == 1) & (Player == 1)) | ((temp == -1) & (Player == 2))) {
//			System.out.println("You cannot move other player's chess!");
			return(OutputBoard);
		}
		else if (Turn != Player) {
//			System.out.println("It's not your turn to move chess!");
			return(OutputBoard);
		}
		else {
//			System.out.println("Player Identity verified");
			boolean IsMoveValid = CheckMoveValidality(ChessOrgX, ChessOrgY, ChessDesX, ChessDesY);
			if (!IsMoveValid) {
				return(OutputBoard);
			}
			else {
				int[][] TempBoard = CloneBoard(OutputBoard);
				//special rule will apply here
				//record King's coordinate
				if (Math.abs(OutputBoard[ChessOrgX][ChessOrgY]) == 1) {
					if (Player == 1) {
						P1KingX = ChessDesX;
						P1KingY = ChessDesY;
					}
					else {
						P2KingX = ChessDesX;
						P2KingY = ChessDesY;
					}
				}
				OutputBoard[ChessDesX][ChessDesY] = OutputBoard[ChessOrgX][ChessOrgY];
				OutputBoard[ChessOrgX][ChessOrgY] = 0;
				
//				System.out.println("Chess Moved from (" + Integer.toString(ChessOrgX)+ ", " + Integer.toString(ChessOrgY) + ") to (" + Integer.toString(ChessDesX) + ", " + Integer.toString(ChessDesY) + ")");
				TempPrevBoard = CloneBoard(OutputBoard);
				if (Turn == 1) {
					Turn = 2;
				}
				else {
					Turn = 1;
				}
				return(OutputBoard);
			}
		}
	}
				
	public boolean Move(int Player, int ChessOrgX, int ChessOrgY, int ChessDesX, int ChessDesY) {
		System.out.println("Trying to move Chess...");
		int temp = (int) Math.signum(Board[ChessOrgX][ChessOrgY]);
		System.out.println("Sign of targeted Chess: " + Integer.toString(temp));
		if (((temp == 1) & (Player == 1)) | ((temp == -1) & (Player == 2))) {
			System.out.println("You cannot move other player's chess!");
			return(false);
		}
		else if (Turn != Player) {
			System.out.println("It's not your turn to move chess!");
			return(false);
		}
		else {
			System.out.println("Player Identity verified");
			boolean IsMoveValid = CheckMoveValidality(ChessOrgX, ChessOrgY, ChessDesX, ChessDesY);
			if (!IsMoveValid) {
				return(false);
			}
			else {
				int[][] TempBoard = CloneBoard(Board);
				//special rule will apply here
				//record King's coordinate
				if (Math.abs(Board[ChessOrgX][ChessOrgY]) == 1) {
					if (Player == 1) {
						P1KingX = ChessDesX;
						P1KingY = ChessDesY;
					}
					else {
						P2KingX = ChessDesX;
						P2KingY = ChessDesY;
					}
				}
				Board[ChessDesX][ChessDesY] = Board[ChessOrgX][ChessOrgY];
				Board[ChessOrgX][ChessOrgY] = 0;
				PrevMove[0] = ChessOrgX;
				PrevMove[1] = ChessOrgY;
				PrevMove[2] = ChessDesX;
				PrevMove[3] = ChessDesY;
				
				System.out.println("Chess Moved from (" + Integer.toString(ChessOrgX)+ ", " + Integer.toString(ChessOrgY) + ") to (" + Integer.toString(ChessDesX) + ", " + Integer.toString(ChessDesY) + ")");
				PrevBoard = CloneBoard(Board);
				if (Turn == 1) {
					Turn = 2;
				}
				else {
					Turn = 1;
				}
				return(true);
			}
		}
	}
	
	public int[][][] ReplaceMatrix(int[][][] Matrices, int[][] Matrix, int Index) {
		if (Index < Matrices.length) {
			if ((Matrices[0].length == 8) & (Matrices[0][0].length == 8)) {
				for (int i=0;i<8;i++) {
					for (int j=0;j<8;j++) {
						Matrices[Index][i][j] = Matrix[i][j];
					}
				}
				return(Matrices);
			}
		}
		return(new int[Matrices.length][8][8]);
				}
	public int[][][] GenerateInput(int Player) {
		int[][] CurrentMoveList = new int[8][8];
		int[][] AvailChess = new int[16][2];
		int[][] SimulateBoard = new int[8][8];
		int SizeInputMatrices = 0;
		if (Player == 1) {
			AvailChess = P1AvailChess;
		}
		else {
			AvailChess = P2AvailChess;
		}
		
		for (int i=0;i<AvailChess.length;i++) {
			if (Board[AvailChess[i][0]][AvailChess[i][1]] != 0) {
				CurrentMoveList = this.GetMoveList(Board[AvailChess[i][0]][AvailChess[i][1]], AvailChess[i][0], AvailChess[i][1]);
				for (int j=0; j<8; j++ ) {
					for (int k=0; k<8; k++) {
						if (CurrentMoveList[i][j] == 1) {
							SizeInputMatrices += 1;
						}
					}
				}
			}
		}
		int[][][] InputMatrices = new int [SizeInputMatrices][8][8];
		int Counter = 0;
		
		for (int i=0;i<AvailChess.length;i++) {
			if (Board[AvailChess[i][0]][AvailChess[i][1]] != 0) {
				CurrentMoveList = this.GetMoveList(Board[AvailChess[i][0]][AvailChess[i][1]], AvailChess[i][0], AvailChess[i][1]);
				for (int j=0; j<8; j++ ) {
					for (int k=0; k<8; k++) {
						if (CurrentMoveList[i][j] == 1) {
							SimulateBoard = this.SimulateMove(Player, AvailChess[i][0], AvailChess[i][1], j, k);
							InputMatrices = ReplaceMatrix(InputMatrices,SimulateBoard,Counter);
						}
					}
				}
			}
		}
		return(InputMatrices);
	}
}
