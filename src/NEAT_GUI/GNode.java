package NEAT_GUI;
import java.awt.Color;

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
	private Color color;
	private boolean isActive; 
	private float alpha;
	
	/*
	 * Constructor 
	 */
	public GNode(double x, double y, Color c){
		xcoor = x;
		ycoor = y;
		color = c;
		isActive = true;
		alpha = 1;
	}

	/*
	 * Getters for member variables 
	 */
	public double getXCoor(){
		return xcoor;
	}
	public double getYCoor(){
		return ycoor;
	}
	public Color getColor() {
		return color;
	}
	public boolean getIsActive() {
		return isActive;
	}
	public float getAlpha() {
		return alpha;
	}
	
	/*
	 * Setters for member variables 
	 */
	public void setIsActive(boolean b) {
		isActive = b;
	}
	public void setAlpha(float a) {
		alpha = a;
	}
}
