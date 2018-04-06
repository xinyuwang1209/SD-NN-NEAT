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
	
	private String luaPath;
	private String javaPath;
	private File luaFile;
	private File javaFile;

	private int viewSize = 7;
	
	
	public LuaInterface(String lua, String java){
		luaPath = lua;
		javaPath = java;
		luaFile = new File(luaPath);
		javaFile = new File(javaPath);
	}
	
	public int getViewSize(){
		return viewSize;
	}
	
	public ArrayList<Double> getSmallInputs(){
		int[][] inputs = new int[viewSize][viewSize];
		
		int currentYPos = (int)((position[4]+position[0])/2.0)-(8*(viewSize/2));	//center top right to start searching for blocks
		int currentXPos = (int)((position[5]+position[1])/2.0)-(8*(viewSize/2));	//Mario takes up 2x2 8p sprites. Set position to midpoint.
		
		//System.out.println("B4: " + currentYPos);
		while(currentYPos % 8 != 0){		//shift current position to line with closest 8x8 grid
			if(currentYPos % 8 >= 4)
				currentYPos++;
			else
				currentYPos--;
		}
		//System.out.println("A4: " + currentYPos);
		while(currentXPos % 8 != 0){
			if(currentXPos % 8 >= 4)
				currentXPos++;
			else
				currentXPos--;
		}				
		
		for(int x=0; x<viewSize; x++){
			for(int y=0; y<viewSize; y++){
				for(int i=0; i<ladders.length; i+=2){
					if(Math.abs(currentYPos+(y*8)-ladders[i]) < 8 && Math.abs(currentXPos+(x*8)-ladders[i+1]) < 8){
						inputs[x][y] = 1;
					}
				}
				for(int i=0; i<platforms.length; i+=2){
					if(Math.abs(currentYPos+(y*8)-platforms[i]) < 8 && Math.abs(currentXPos+(x*8)-platforms[i+1]) < 8){
						inputs[x][y] = 1;
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
		
		ArrayList<Double> output = new ArrayList<Double>();
		for(int y=0; y<viewSize; y++)
			for(int x=0; x<viewSize; x++)
				output.add((double)inputs[x][y]);
		
		//output.add(1.0);
		//output.add((((position[4]+position[0])/2.0)-23)/(199-23));	//YPos 199 to 23
		//output.add((((position[5]+position[1])/2.0)-20)/(228-20));	//XPos 20 to 228
		double yPos = (((position[4]+position[0])/2.0)-23)/(199-23);

		if(yPos <= 1.0 && yPos > 0.841)
			output.add(1.0);
		else
			output.add(0.0);
		
		if(yPos <= 0.841 && yPos > 0.654)
			output.add(1.0);
		else
			output.add(0.0);

		if(yPos <= 0.654 && yPos > 0.483)
			output.add(1.0);
		else
			output.add(0.0);
		
		if(yPos <= 0.483 && yPos > 0.319)
			output.add(1.0);
		else
			output.add(0.0);
		
		if(yPos <= 0.319 && yPos > 0.1591)
			output.add(1.0);
		else
			output.add(0.0);
		
		if(yPos <= 0.1591 && yPos > 0.0)
			output.add(1.0);
		else
			output.add(0.0);
		
		//Output smalloutputs view to console
		for(int y=0; y<viewSize; y++){
			for(int x=0; x<viewSize; x++){
				if(inputs[x][y] == 1)
					System.out.print(1);
				if(inputs[x][y] == 0)
					System.out.print(0);
				if(inputs[x][y] == -1)
					System.out.print("x");
			}
			System.out.println();
		}
		System.out.println(output.get(output.size()-6));
		System.out.println(output.get(output.size()-5));
		System.out.println(output.get(output.size()-4));
		System.out.println(output.get(output.size()-3));
		System.out.println(output.get(output.size()-2));
		System.out.println(output.get(output.size()-1));
		System.out.println("___________________________________");
		
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
		boolean written = false;
		while(!written){
			try{
				Scanner sc = new Scanner(luaFile);
				if(sc.nextLine().equals("Lua=1"))
					continue;
				BufferedWriter writer = new BufferedWriter(new FileWriter(luaFile));
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
				written = true;
			}catch(Exception e){
			}
			
		}
	}
	
	public void startNewGame(){
		boolean written = false;
		while(!written){
			try{
				BufferedWriter writer = new BufferedWriter(new FileWriter(luaFile));
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
				Thread.sleep(30);
				
				writer = new BufferedWriter(new FileWriter(luaFile));
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
				//System.out.println("Written Start New Game");
				written = true;
			}catch(Exception e){
				//System.out.println("Start New Game Failed: " + e);
			}
		}
	}
	
	public void updateInputs(){													//load inputs and update member variables
		try{
			
			boolean wait = true;
			while(wait){
				Thread.sleep(100);
				Scanner sc = new Scanner(luaFile);
				if(sc.nextLine().equals("Lua=0"))	//finish waiting once lua has updated the output
					wait = false;
				sc.close();
			}			
			
			Scanner sc = new Scanner(luaFile);
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
			/*System.out.println("Exception updating inputs: " + e);
			updateInputs();
			System.out.println("Resolved.");*/
		}
	}
}
