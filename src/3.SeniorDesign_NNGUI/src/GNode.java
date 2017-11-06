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
	
	private int xcoor, ycoor;

	public GNode(int x, int y){
		xcoor = x;
		ycoor = y;
	}

	public int getXCoor(){
		return xcoor;
	}
	
	public int getYCoor(){
		return ycoor;
	}
	

}
