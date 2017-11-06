/*
 * Swing Classes
 * =========================
 * JFrame: Application Frame (NeuralNetwork_GUI)
 * JPanel: Container for custom drawing (network)
 * SwingUtilities: Assures GUI creation code in EDT
 * */
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * Application frame for Neural Network 
 * 
 * main(): Runs GUI
 * NeuralNetwork_GUI(): Constructor for NeuralNetwork_GUI JFrame
 */
public class NeuralNetwork_GUI extends JFrame{

	private JPanel network;
	
	public static void main(String[] args){
			SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				try{
					NeuralNetwork_GUI frame = new NeuralNetwork_GUI();
					frame.setVisible(true);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}		
		});	
	}
	
	/* Suggestions:
	 * Change title according to what genome and generation is currently running 
	 * 
	 * To do:
	 * JPanel for game running is underneath JPanel that displays NN
	 * JPanel determines bounds of frame; setBounds() method temporary
	 * Button that toggles whether or not NN visualization is shown
	 */
	public NeuralNetwork_GUI(){
		setTitle("NEAT Donkey Kong Neural Network");
		setBounds(0, 0, 1000, 1000); 
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		network = new Network();
		add(network);
	}
}
