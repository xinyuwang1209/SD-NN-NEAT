/*
 * Swing classes
 * =========================
 * JPanel: Container for custom drawing (Network)
 * BorderFactory: Defines border of JPanel
 */
import javax.swing.JPanel;
import javax.swing.BorderFactory;
/*
 * AWT classes
 * =========================
 * Graphics: Abstract object used for rendering 2D graphics
 * Graphics2D: Subclass of Graphics, used to render more sophisticated graphics
 * geom.Rectangle2D: Abstraction of a rectangle; used to visualize Nodes
 * BasicStroke: Property of Graphics; defines type and thickness of a stroke
 */
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;
import java.awt.Color;

/*
 * Custom drawing surface where nodes and connections of neural network actually drawn on
 * 
 * Network(): Constructor for Network JPanel
 * paintEdge(Graphics2D g2d, GEdge c): paints the connection and the nodes that define it
 * paintNode(Graphics2D g2d, GNode n): paints the node; for nodes w/o any connections
 * paintComponent(Graphics g): Overridden method; renders objects
 */
public class Network extends JPanel{

	/*GNode node1 = new GNode(0, 0);
	GNode node2 = new GNode(100, 100);
	GEdge con = new GEdge(node1, node2, 2.0f);
	
	GNode node3 = new GNode(250, 100);
	GNode node4 = new GNode(100, 100);
	GEdge con2 = new GEdge(node3, node4, 0.5f);*/
	
	public Network(){
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	private void paintEdge(Graphics2D g2d, GEdge c){
		GNode n1 = c.getStartNode();
		GNode n2 = c.getEndNode();
		
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke(c.getWeight()));
		g2d.drawLine(n1.getXCoor(),
					n1.getYCoor(),
					n2.getXCoor(),
					n2.getYCoor());
		
		g2d.setPaint(Color.BLACK);
		g2d.fill(new Rectangle2D.Double(n1.getXCoor(),
										n1.getYCoor(),
										10,
										10));
		g2d.fill(new Rectangle2D.Double(n2.getXCoor(),
										n2.getYCoor(),
										10,
										10));
	}
	
	private void paintNode(Graphics2D g2d, GNode n){
		g2d.setPaint(Color.BLACK);
		g2d.fill(new Rectangle2D.Double(n.getXCoor(),
										n.getYCoor(),
										10,
										10));
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		
		//paintEdge(g2d, con);
		//paintNode(g2d, node3);
	}
}
