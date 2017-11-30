package NEAT_GUI;
/*
 * NeuralNetwork package
 * =========================
 * Classes for accessing the layers, nodes, and edges of the neural network
 */
import NeuralNetwork.*;
/*
 * Utilities classes 
 * =========================
 * ArrayList: Object that holds nodes in each layer
 */
import java.util.ArrayList;
import java.util.HashMap;

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
public class GNetwork extends JPanel{
	private ArrayList<InputNode> inputLayer;			//ArrayList of input nodes
	private ArrayList<OutputNode> outputLayer;			//ArrayList of output nodes
	private ArrayList<Layer<HiddenNode>> hiddenLayers;	//ArrayList of the layers of hidden nodes
	
	private HashMap<Integer, GNode> GNodeMap;
		
	public GNetwork(NeuralNetwork network){
		inputLayer = network.getInputNodes();
		outputLayer = network.getOutputNodes();
		hiddenLayers = network.getHiddenLayers();
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	
		GNodeMap = new HashMap<Integer, GNode>();
	}
	
	private void paintNode(Graphics2D g2d, GNode n){
		g2d.setPaint(Color.BLACK);
		g2d.fill(new Rectangle2D.Double(n.getXCoor(),
										n.getYCoor(),
										10,
										10));
	}
	
	private void paintNode(Graphics2D g2d, GNode n, Color c){
		g2d.setPaint(c);
		g2d.fill(new Rectangle2D.Double(n.getXCoor(),
										n.getYCoor(),
										10,
										10));
	}
	
	private void paintEdge(Graphics2D g2d, GEdge c){
		GNode n1 = c.getStartNode();
		GNode n2 = c.getEndNode();
		
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(c.getWeight()));
		g2d.draw(new Line2D.Double(n1.getXCoor(),
					n1.getYCoor(),
					n2.getXCoor(),
					n2.getYCoor()));
		
		//paintNode(g2d, n1);
		//paintNode(g2d, n2);
		/*g2d.setPaint(Color.BLACK);
		g2d.fill(new Rectangle2D.Double(n1.getXCoor(),
										n1.getYCoor(),
										10,
										10));
		g2d.fill(new Rectangle2D.Double(n2.getXCoor(),
										n2.getYCoor(),
										10,
										10));*/
	}
	
	/*Responsible for painting the network:
	 * -for each layer i:
	 * 		[x]put down the nodes
	 * 		[]if not the first layer, paint edges incoming from nodes in layer i*/
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		
		int numRegions = 2 + hiddenLayers.size();
		double regionSize = 1000/numRegions; 		//1000 is the current width of JFrame; change later to be more robust
		double startRegionX = 0;
		double startRegionY = 0;

		/*Paint input nodes*/
		for(InputNode input : inputLayer){
			GNode gn = new GNode(Math.random()*regionSize + startRegionX,
					Math.random()*250 + startRegionY);
			GNodeMap.put(input.getID(), gn);
			paintNode(g2d, gn, Color.RED);
			startRegionY += gn.getYCoor();
		}
		startRegionY = 0;
		startRegionX += regionSize;

		/*Paint hidden nodes per hidden layer*/
		for(Layer<HiddenNode> hiddenLayer : hiddenLayers){
			ArrayList<HiddenNode> hiddenNodes = hiddenLayer.getNodeList();
			for(HiddenNode hidden : hiddenNodes){
				GNode gn = new GNode(Math.random()*regionSize + startRegionX,
						Math.random()*250 + startRegionY);
				GNodeMap.put(hidden.getID(), gn);
				
				/*Paint edges going to hidden nodes*/
				ArrayList<Edge> inEdges = hidden.getIncomingEdges();
				for (Edge e : inEdges){
					int n1ID = e.getNode1().getID();
					GNode n1 = GNodeMap.get(n1ID);
					GEdge ge = new GEdge(n1, gn, 1f);
					paintEdge(g2d, ge);
				}
				
				paintNode(g2d, gn, Color.BLUE);
				startRegionY += gn.getYCoor();
				
				
			}
			startRegionY = 0;
			startRegionX += regionSize;
		}

		/*Paint output nodes*/
		for(OutputNode output : outputLayer){
			GNode gn = new GNode(Math.random()*regionSize + startRegionX,
					Math.random()*250 + startRegionY);
			GNodeMap.put(output.getID(), gn);
			
			/*Paint edges going to output nodes*/
			ArrayList<Edge> inEdges = output.getIncomingEdges();
			for (Edge e : inEdges){
				int n1ID = e.getNode1().getID();
				GNode n1 = GNodeMap.get(n1ID);
				GEdge ge = new GEdge(n1, gn, 1f);
				paintEdge(g2d, ge);
			}
			
			paintNode(g2d, gn);
			startRegionY += gn.getYCoor();
		}
		
		/* Code for debugging
		System.out.println("Number of regions: " + numRegions + "\n");
		System.out.println("Number of input nodes: " + inputLayer.size() + "\n");
		int ttlHidden = 0;
		for(Layer<HiddenNode> hiddenLayer : hiddenLayers){
			ArrayList<HiddenNode> hiddenNodes = hiddenLayer.getNodeList();
			ttlHidden += hiddenNodes.size();
		}
		System.out.println("Number of hidden nodes: " + ttlHidden + "\n");
		System.out.println("Number of output nodes: " + outputLayer.size() + "\n");*/
		
	}
}
