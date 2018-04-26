package NEAT_GUI;
/*
 * NeuralNetwork package
 * =========================
 * Classes for accessing the layers, nodes, and edges of the neural network
 */
import NeuralNetwork.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
/*
 * Swing Classes
 * =========================
 * JFrame: Application Frame (GNeuralNetwork)
 * JPanel: Container for custom drawing (GNetwork)
 * SwingUtilities: Assures GUI creation code in EDT
 * */
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Evolution.NEATNetwork;

/*
 * Version 2: GUI with continuously updating networks
 * 
 *  Network
 *  	Updating
 *  Navigation
 *  	By generation
 *  Information
 *  	Yes plz
 *  
 */

/*
 * Application frame for Neural Network 
 * 
 * GNeuralNetwork(): Constructor for GNeuralNetwork JFrame	
 */
public class GUINetworkFrame extends JFrame{

	private JPanel contentPane = (JPanel) getContentPane();
	
	private GUINetworkPanel guiNetworkPanel;
	private ArrayList<GNetwork> gNetworks;
	
	/* Suggestions:
	 * Change title according to what genome and generation is currently running 
	 * 
	 * Tasks:
	 * Include button that allows user to cycle through all neural networks
	 * 		in future, would like user to control which nn by population
	 * 		for now, defualt to nn 1, 2, 3, etc.
	 * Allow user to click on a node, and only that node and its connections are
	 * highlighted
	 */
	public GUINetworkFrame(){		
		gNetworks = new ArrayList<GNetwork>();
		guiNetworkPanel = new GUINetworkPanel();
		//GUIKeyPanel guiKeyPanel = new GUIKeyPanel();
		/*
		 * Setting up the JFrame
		 * */
		setTitle("NEAT Neural Network GUI");
		setBounds(0, 0, 1250, 750); 
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		

		setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));		
		contentPane.add(guiNetworkPanel);	
		//contentPane.add(guiKeyPanel);
		
	}
	
	public void updateNetwork(NeuralNetwork n, int num){
		GNetwork gNetwork = new GNetwork(n, num);
		gNetworks.add(gNetwork);
		
		guiNetworkPanel.setNetwork(gNetwork);
	}
	
	public void setFrameTitle(String title) {
		setTitle(title);
	}
}