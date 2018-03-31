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
	
	/*
	 * Constructor 
	 */
	
	public GUINetworkPanel(){
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		repaint();
	}
	
	public GUINetworkPanel(GNetwork gn){
		gNetwork = gn;
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		repaint();
	}
	
	/*
	 * Setter for member variable 
	 */
	public void setNetwork(GNetwork gn){
		gNetwork = gn;
		setBorder(BorderFactory.createLineBorder(Color.BLACK)); //redundant?
		repaint();
	}
	
	/*
	 * Paint method: paints given GNode
	 */
	private void paintNode(Graphics2D g2d, GNode n){
		g2d.setPaint(n.getColor());
		
		float alpha = 1;
		if(n.getIsActive()) 
			alpha = 10 * 0.1f;
		else
			alpha = 2 * 0.1f;;
		
		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2d.setComposite(alcom);
		g2d.fill(new Rectangle2D.Double(n.getXCoor(),
										n.getYCoor(),
										10,
										10));
		
	}
	
	/*
	 * Paint method: paints given GEdge
	 */
	private void paintEdge(Graphics2D g2d, GEdge c){
		GNode n1 = c.getStartNode();
		GNode n2 = c.getEndNode();
		
		float alpha = 1;
		if(c.getIsActive()) 
			alpha = 10 * 0.1f;
		else
			alpha = 2 * 0.1f;;
		
		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2d.setComposite(alcom);
		
		if(c.getWeight() > 0)
			g2d.setColor(Color.WHITE);
		else
			g2d.setColor(Color.BLACK);
		
		if(Math.abs(c.getWeight()) < 0.5f)
			g2d.setStroke(new BasicStroke(0.5f));
		else if(Math.abs(c.getWeight()) < 1f)
			g2d.setStroke(new BasicStroke(1f));
		else
			g2d.setStroke(new BasicStroke(2f));
		
		g2d.draw(new Line2D.Double(n1.getXCoor()+10,
					n1.getYCoor()+5,
					n2.getXCoor(),
				n2.getYCoor()+5));
		
	}
	
	/*
	 * Responsible for painting the network
	 */
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		
		HashMap<Integer, GNode> inLayer = gNetwork.getInputLayer(); 
		HashMap<Integer, GNode> hidLayers = gNetwork.getHiddenLayer();
		HashMap<Integer, GNode> outLayer = gNetwork.getOutputLayer();
		HashMap<Integer, GEdge> netEdges = gNetwork.getNetworkEdges();

		Iterator<Integer> inKeyIter = inLayer.keySet().iterator();
		Iterator<Integer> hidKeyIter = hidLayers.keySet().iterator();
		Iterator<Integer> outKeyIter = outLayer.keySet().iterator();
		Iterator<Integer> edgeKeyIter = netEdges.keySet().iterator();
		
		//Paint edges
		while(edgeKeyIter.hasNext()) {
			paintEdge(g2d, netEdges.get(edgeKeyIter.next()));
		}
		
		//Paint input nodes
		while(inKeyIter.hasNext()) {
			paintNode(g2d, inLayer.get(inKeyIter.next()));
		}
		

		//Paint hidden nodes per hidden layer
		while(hidKeyIter.hasNext()) {
			paintNode(g2d, hidLayers.get(hidKeyIter.next()));
		}

		//Paint output nodes
		while(outKeyIter.hasNext()) {
			paintNode(g2d, outLayer.get(outKeyIter.next()));
		}		
			
		/* Code for debugging
		System.out.println("Number of inputNodes: " + inLayer.size());
		System.out.println("Number of hiddenNodes: " + hidLayers.size());
		System.out.println("Number of outputNodes: " + outLayer.size());
		System.out.println("Number of edges: " + netEdges.size());
		 */
		
	}
}
