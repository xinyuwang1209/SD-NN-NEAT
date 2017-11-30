package NEAT_GUI;
/*
 * NeuralNetwork package
 * =========================
 * Classes for accessing the layers, nodes, and edges of the neural network
 */
import NeuralNetwork.*;
/*
 * Swing Classes
 * =========================
 * JFrame: Application Frame (GNeuralNetwork)
 * JPanel: Container for custom drawing (GNetwork)
 * SwingUtilities: Assures GUI creation code in EDT
 * */
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * Application frame for Neural Network 
 * 
 * main(): Runs GUI
 * GNeuralNetwork(): Constructor for GNeuralNetwork JFrame
 */
public class GNeuralNetwork extends JFrame{

	private JPanel gNetwork;
	private NeuralNetwork network;
	
//	public static void main(String[] args){
//			SwingUtilities.invokeLater(new Runnable() {
//			public void run(){
//				try{
//					GNeuralNetwork frame = new GNeuralNetwork();
//					frame.setVisible(true);
//				}
//				catch(Exception e){
//					e.printStackTrace();
//				}
//			}		
//		});	
//	}
	
	/* Suggestions:
	 * Change title according to what genome and generation is currently running 
	 * 
	 * To do:
	 * JPanel for game running is underneath JPanel that displays NN
	 * JPanel determines bounds of frame; setBounds() method temporary
	 * Button that toggles whether or not NN visualization is shown
	 */
	public GNeuralNetwork(NeuralNetwork n){
		network = n;
		
		setTitle("NEAT Donkey Kong Neural Network");
		setBounds(0, 0, 1000, 1000); 
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		gNetwork = new GNetwork(network);
		add(gNetwork);
	}
}
