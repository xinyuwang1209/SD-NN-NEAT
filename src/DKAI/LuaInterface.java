package DKAI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class LuaInterface {
	public int[] platforms = new int[338];
	public int[] ladders = new int[76];
	public int[] enemies2 = new int[44];
	public int[] enemies = new int[48];
	public int[] position = new int[8];
	
	
	public int deathFlag = 0;
	public int timer = 0;
	public int i_point = 0;
	
	public int outputs[] = {0,0,0,0,0,0}; //up,down,left,right,a,b
	
	private String luaPath = "./src/res/LUA.txt";
	private String javaPath = "./src/res/Java.txt";
	private File luaFile = new File(luaPath);
	private File javaFile = new File(javaPath);
	
	
	public ArrayList<Integer> getSmallInputs(){
		int viewSize = 12;
		int[][] inputs = new int[viewSize][viewSize];
		int currentYPos = position[6]-(8*(viewSize/2));	//center top right to start searching for blocks
		int currentXPos = position[7]-(8*(viewSize/2));
		
		for(int x=0; x<viewSize; x++){
			for(int y=0; y<viewSize; y++){
				for(int i=0; i<platforms.length; i+=2){
					if(Math.abs(currentYPos+(y*8)-platforms[i]) < 8 && Math.abs(currentXPos+(x*8)-platforms[i+1]) < 8){
						inputs[x][y] = 1;
					}
				}
				for(int i=0; i<ladders.length; i+=2){
					if(Math.abs(currentYPos+(y*8)-ladders[i]) < 8 && Math.abs(currentXPos+(x*8)-ladders[i+1]) < 8){
						inputs[x][y] = 2;
					}
				}
				for(int i=0; i<enemies.length; i+=2){
					if(Math.abs(currentYPos+(y*8)-enemies[i]) < 8 && Math.abs(currentXPos+(x*8)-enemies[i+1]) < 8){
						inputs[x][y] = -1;
					}
				}
				for(int i=0; i<enemies2.length; i+=2){
					if(Math.abs(currentYPos+(y*8)-enemies2[i]) < 8 && Math.abs(currentXPos+(x*8)-enemies2[i+1]) < 8){
						inputs[x][y] = -1;
					}
				}
			}
		}
		/*for(int x=0; x<8; x++)
			for(int y=0; y<8; y++)
				System.out.print(inputs[x][y]);
		System.out.println("\n");*/
		
		ArrayList<Integer> output = new ArrayList<Integer>();
		for(int x=0; x<8; x++)
			for(int y=0; y<8; y++)
				output.add(inputs[x][y]);
		return output;
	}
	
	
	public ArrayList<Integer> getInputs(){
		ArrayList<Integer> inputs = new ArrayList<Integer>();
		for(Integer i : platforms)
			inputs.add(i);
		for(Integer i : ladders)
			inputs.add(i);
		for(Integer i : enemies2)
			inputs.add(i);
		for(Integer i : enemies)
			inputs.add(i);
		for(Integer i : position)
			inputs.add(i);
		
		return inputs;
	}
	
	public void writeOutputs(){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(javaFile));
			String input = "Lua=1\n"
					+ "up=" + outputs[0] + "\n"
					+ "down=" + outputs[1] + "\n"
					+ "left=" + outputs[2] + "\n"
					+ "right=" + outputs[3] + "\n"
					+ "a=" + outputs[4] + "\n"
					+ "b=" + outputs[5] + "\n"
					+ "reset=0";
			writer.write(input);
			writer.close();
		}catch(Exception e){
		}
	}
	
	public void startNewGame(){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(javaFile));
			String input = "Lua=1\n"
					+ "up=0\n"
					+ "down=0\n"
					+ "left=0\n"
					+ "right=0\n"
					+ "a=0\n"
					+ "b=0\n"
					+ "reset=1";
			writer.write(input);
			writer.close();
			Thread.sleep(25);
			
			writer = new BufferedWriter(new FileWriter(javaFile));
			input = "Lua=1\n"
					+ "up=0\n"
					+ "down=0\n"
					+ "left=0\n"
					+ "right=0\n"
					+ "a=0\n"
					+ "b=0\n"
					+ "reset=0";
			writer.write(input);
			writer.close();
			Thread.sleep(500);
		}catch(Exception e){
		}
		
	}
	
	public void updateInputs(){													//load inputs and update member variables
		try{
			Scanner sc = new Scanner(luaFile);
			sc.nextLine();//removes Lua=0
			int i=0;
			while(sc.hasNext()){
				String s = sc.nextLine();
				//System.out.println("ln: " + s);
				if(s.equals("platforms:")){
					i=0;
					while(i<platforms.length){												//load platforms
						s = sc.nextLine().replaceAll("[\\D]", "");
						if(!s.equals("")){
							platforms[i] = Integer.parseInt(s);
							i++;
						}
					}
				}

				if(s.equals("ladders:")){
					i=0;
					while(i<ladders.length){												//load ladders
						s = sc.nextLine().replaceAll("[\\D]", "");
						if(!s.equals("")){
							ladders[i] = Integer.parseInt(s);
							i++;
						}
					}
				}
				
				if(s.equals("enemies2:")){
					i=0;
					while(i<enemies2.length){												//load enemies2
						s = sc.nextLine().replaceAll("[\\D]", "");
						if(!s.equals("")){
							enemies2[i] = Integer.parseInt(s);
							i++;
						}
					}
				}
				
				if(s.equals("deathFlag:")){										//load deathFlag
					deathFlag = Integer.parseInt(sc.nextLine().replaceAll("[\\D]", ""));
				}
				
				if(s.equals("enemies:")){
					i=0;
					while(i<enemies.length){												//load enemies
						s = sc.nextLine().replaceAll("[\\D]", "");
						if(!s.equals("")){
							enemies[i] = Integer.parseInt(s);
							i++;
						}
					}
				}
				if(s.equals("position:")){
					i=0;
					while(i<position.length){													//load enemies
						s = sc.nextLine().replaceAll("[\\D]", "");
						if(!s.equals("")){
							position[i] = Integer.parseInt(s);
							//System.out.println("position[i]: " + position[i]);
							i++;
						}
					}
				}
				
				if(s.equals("states:")){										//load stateinfo
					timer = Integer.parseInt(sc.nextLine().replaceAll("[\\D]", ""));	//timer
					sc.nextLine();	//life
					sc.nextLine();	//t_pointr
					i_point = Integer.parseInt(sc.nextLine().replaceAll("[\\D]", ""));
				}
			}
			sc.close();
		}catch(Exception e){
		}
	}
}
