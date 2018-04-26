package Evolution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Store {
	//save + load one network
	public void saveNet(NEATNetwork net, File savefile) {
		try {
			if (savefile == null)
					savefile = new File("./src/res/save.txt");
			//ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savefile, true));
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savefile));
			out.writeObject(net);
			out.flush();
			out.close();
			System.out.println("NEATNetwork saved.");
		} catch (Exception e) {
			System.out.println("NEATNetwork save failed.");
			System.out.println(e);
		}
	}

	public NEATNetwork loadNet(File savefile) {
		try {
			if (savefile == null)
				savefile = new File("./src/res/save.txt"); //check if empty
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(savefile));
			NEATNetwork net = (NEATNetwork) in.readObject();
			in.close();
			System.out.println("NEATNetwork loaded.");
			return net;
		} catch (Exception e) {
			System.out.println("NEATNetwork load failed.");
			System.out.println(e);
		}
		return null;
	}

	//save+load entire neat object
	public void saveNEAT(NEAT neat, File savefile){
		try {
			if (savefile == null)
				savefile = new File("./src/res/save.txt");
			//ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savefile, true));
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savefile));
			out.writeObject(neat);
			out.flush();
			out.close();
			System.out.println("NEAT saved.");
		} catch (Exception e) {
			System.out.println("NEAT save failed.");
			System.out.println(e);
		}
	}

	public NEAT loadNEAT(File savefile) {
		try {
			if (savefile == null)
				savefile = new File("./src/res/save.txt");
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(savefile));
			NEAT neat = (NEAT) in.readObject();
			in.close();
			System.out.println("NEAT loaded.");
			return neat;
		} catch (Exception e) {
			System.out.println("NEAT load failed.");
			System.out.println(e);
		}
		return null;
	}

	//save+load pop list, testing, probably not useful
	public void savePop(ArrayList<Species> pop, File savefile){
		try {
			if (savefile == null)
				savefile = new File("./src/res/save.txt");
			//ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savefile, true));
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savefile));
			out.writeObject(pop);
			out.flush();
			out.close();
			System.out.println("Population saved.");
		} catch (Exception e) {
			System.out.println("Population save failed.");
			System.out.println(e);
		}
	}

	public ArrayList<Species> loadPop(File savefile) {
		try {
			if (savefile == null)
				savefile = new File("./src/res/save.txt");
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(savefile));
			ArrayList<Species> pop = (ArrayList<Species>) in.readObject();
			in.close();
			System.out.println("Population loaded.");
			return pop;
		} catch (Exception e) {
			System.out.println("Population load failed.");
			System.out.println(e);
		}
		return null;
	}
}
