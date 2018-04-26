package NEAT_GUI;
/*
 * NeuralNetwork package
 * =========================
 * Classes for accessing the layers, nodes, and edges of the neural network
 */

/*
 * Utilities classes 
 * =========================
 * ArrayList: Object that holds nodes in each layer
 */
import java.util.HashMap;
import java.util.Iterator;

/*
 * Swing classes
 * =========================
 * JPanel: Container for custom drawing (GNetwork)
 * BorderFactory: Defines border of JPanel
 */
import javax.swing.JPanel;
import javax.swing.BorderFactory;
/*
 * AWT classes
 * =========================
 * Graphics: Abstract object used for rendering 2D graphics
 * Graphics2D: Subclass of Graphics, used to render more sophisticated graphics
 * geom.Line2D: Abstraction of a line; used to visualize edges
 * geom.Rectangle2D: Abstraction of a rectangle; used to visualize nodes
 * BasicStroke: Property of Graphics; defines type and thickness of a stroke
 * Color: Property of Graphics; defines color of component
 */
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;

/*
 * Custom drawing surface where nodes and connections of neural network actually drawn on
 * 
 * GNetwork(): Constructor for GNetwork JPanel
 * paintEdge(Graphics2D g2d, GEdge c): paints the connection and the nodes that define it
 * paintNode(Graphics2D g2d, GNode n): paints the node; for nodes w/o any connections
 * paintComponent(Graphics g): Overridden method; renders objects
 */

public class GUINetworkPanel extends JPanel{

	private GNetwork gNetwork;
	private double nodeSize;
	/*
	 * Constructor 
	 */
	
	public GUINetworkPanel(){
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.GRAY);
		repaint();
	}
	
	/*
	 * Setter for member variable 
	 */
	public void setNetwork(GNetwork gn){
		gNetwork = gn;
		nodeSize = gn.getNodeSize();
		setBorder(BorderFactory.createLineBorder(Color.BLACK)); //redundant?
		repaint();
	}
	
	/*
	 * Paint method: paints given GNode
	 */
	private void paintNode(Graphics2D g2d, GNode n){
		g2d.setStroke(new BasicStroke(1f));
		
		/*Paint the node*/
		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, n.getAlpha());
		g2d.setComposite(alcom);
		g2d.setPaint(n.getColor());
		g2d.fill(new Rectangle2D.Double(n.getXCoor(), n.getYCoor(),nodeSize,nodeSize));	
		
		/*Draw the grid*/
		alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g2d.setComposite(alcom);
		g2d.setPaint(Color.BLACK);
		g2d.draw(new Rectangle2D.Double(n.getXCoor(), n.getYCoor(),nodeSize,nodeSize));
	}
	

	/*
	 * Paint method: paints given GEdge
	 */
	private void paintEdge(Graphics2D g2d, GEdge c){
		GNode n1 = c.getStartNode();
		GNode n2 = c.getEndNode();
		
		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, c.getAlpha());
		//System.out.println("The alpha value is " + c.getAlpha());
		g2d.setComposite(alcom);
		
		if(c.getWeight() > 0) g2d.setColor(Color.WHITE);
		else g2d.setColor(Color.BLACK);
		
		if(Math.abs(c.getWeight()) < 0.5f) g2d.setStroke(new BasicStroke(0.5f));
		else if(Math.abs(c.getWeight()) < 1f) g2d.setStroke(new BasicStroke(1f));
		else g2d.setStroke(new BasicStroke(2f));
		
		g2d.draw(new Line2D.Double(n1.getXCoor()+(nodeSize/2), n1.getYCoor()+(nodeSize/2), 
									n2.getXCoor()+(nodeSize/2), n2.getYCoor()+(nodeSize/2))
				);
	}
	
	/*
	 * Responsible for painting the network
	 */
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		
		HashMap<Integer, GNode> inLayer;
		HashMap<Integer, GNode> hidLayers;
		HashMap<Integer, GNode> outLayer;
		HashMap<Integer, GEdge> netEdges;

		Iterator<Integer> inKeyIter;
		Iterator<Integer> hidKeyIter;
		Iterator<Integer> outKeyIter;
		Iterator<Integer> edgeKeyIter;
		
		try{
			inLayer = gNetwork.getInputLayer(); 
			hidLayers = gNetwork.getHiddenLayer();
			outLayer = gNetwork.getOutputLayer();
			netEdges = gNetwork.getNetworkEdges();
			
			inKeyIter = inLayer.keySet().iterator();
			hidKeyIter = hidLayers.keySet().iterator();
			outKeyIter = outLayer.keySet().iterator();
			edgeKeyIter = netEdges.keySet().iterator();
			
			//Paint input nodes
			while(inKeyIter.hasNext()) {
				paintNode(g2d, inLayer.get(inKeyIter.next()));
			}
			

			//Paint hidden nodes per hidden layer
			nodeSize = gNetwork.getHidNodeSize();
			while(hidKeyIter.hasNext()) {
				paintNode(g2d, hidLayers.get(hidKeyIter.next()));
			}

			//Paint output nodes
			nodeSize = gNetwork.getNodeSize();
			while(outKeyIter.hasNext()) {
				paintNode(g2d, outLayer.get(outKeyIter.next()));
			}		
			
			//Paint edges
			while(edgeKeyIter.hasNext()) {
				paintEdge(g2d, netEdges.get(edgeKeyIter.next()));
			}
				
			//Code for debugging
			/*System.out.println("Number of inputNodes: " + inLayer.size());
			System.out.println("Number of hiddenNodes: " + hidLayers.size());
			System.out.println("Number of outputNodes: " + outLayer.size());
			System.out.println("Number of edges: " + netEdges.size());*/
			 
		}catch(Exception e){
			//System.out.println("Help I'm not working still: " + e);
		}
	}
}