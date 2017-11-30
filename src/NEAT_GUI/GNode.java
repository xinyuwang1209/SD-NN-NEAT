package NEAT_GUI;
/*
 * Swing classes
 * =========================
 * JComponent: Swing component to be visualized (GNode)
 */
import javax.swing.JComponent;

/*
 * Class representing visualization of a node gene
 * 
 * Node(int x, int y): Constructor for GNode object
 * getXCoor(): Returns X coordinate of GNode
 * getYCoor(): Returns Y coordinate of GNode
 */
public class GNode extends JComponent{
	
	private double xcoor, ycoor;

	public GNode(double x, double y){
		xcoor = x;
		ycoor = y;
	}

	public double getXCoor(){
		return xcoor;
	}
	
	public double getYCoor(){
		return ycoor;
	}
	

}
