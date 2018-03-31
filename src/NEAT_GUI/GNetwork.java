package NEAT_GUI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import NeuralNetwork.*;

/*
 * Class that creates a GNetworkEntry instance to store in the GNetwork HashMap
 * 
 */
public class GNetwork{

	private NeuralNetwork network;
	private HashMap<Integer, GNode> inputLayer; //Keys are node IDs
	private HashMap<Integer, GNode> hiddenLayer;
	private HashMap<Integer, GNode> outputLayer;
	private HashMap<Integer, GEdge> networkEdges; //Keys are incrementing index
	private ArrayList<String> networkInfo;
	
	private int totalWidth;
	private int totalHeight;
	
	/*
	 * Constructor
	 * 
	 * Note: Calculations dependent on totalWidth and totalHeight, so if ever decide to allow them
	 * to be changed, calculations for everything need to be done all over again
	 * Easier to assume fixed size
	 * */
	public GNetwork(NeuralNetwork n) {
		network = n;
		totalWidth = 1000;
		totalHeight = 400;
		inputLayer = new HashMap<Integer, GNode>();
		hiddenLayer = new HashMap<Integer, GNode>();
		outputLayer = new HashMap<Integer, GNode>();
		networkEdges = new HashMap<Integer, GEdge>();
		networkInfo = new ArrayList<String>();
		
		int edgeIndex = 0;
		
		/*
		 * ArrayList variables for the nodes of the network, 
		 * for the sake of convenience and ease of coding
		 */
		ArrayList<InputNode> inLayer = network.getInputNodes();
		ArrayList<Layer<HiddenNode>> hidLayers = network.getHiddenLayers();
		ArrayList<OutputNode> outLayer = network.getOutputNodes();
		
		/*
		 * Placement of the nodes of each layer is dependent on which "region"
		 * the layer is assigned to.
		 * For sake of simplicity, assumed that JPanel/ JFrame is of fixed, known size
		 */
		
		int numRegions = 2 + hidLayers.size();
		double regionSize = (totalWidth-5) / numRegions;
		double startRegionX = 0;
		double startRegionY = 0;
		
		/*
		 * Create and store inLayer GNodes
		 * ===============================
		 * Position: Upper Left corner, in grid fashion (not scattered)
		 * Color: Red
		 * Opacity: True if there is at least one outgoing edge that is active
		 */
		for(InputNode input : inLayer) {
			double newRegionY = 10 + startRegionY;
			inputLayer.put(input.getID(), new GNode((10+startRegionX),
													newRegionY,
													Color.RED));
			 startRegionY += newRegionY;
			 
			 boolean isActive = false;
			 ArrayList<Edge> outEdges = input.getOutgoingEdges();
			 for(int i=0; (!isActive)&&i<outEdges.size(); i++){
				 isActive = outEdges.get(i).isActive();
			 }
			 System.out.println("The node is active: " + isActive);
			 inputLayer.get(input.getID()).setIsActive(isActive);
		}
		
		/* Preparing for next set of nodes*/
		startRegionY = 0;
		startRegionX += regionSize;
		
		/*
		 * Create and store hidLayer GNodes
		 * ===============================
		 * Position: Middle of the panel, scattered
		 * Color: Blue
		 * Opacity: True if there is at least one incoming edge that is active
		 */
		
		for(Layer<HiddenNode> hLayer : hidLayers){
			ArrayList<HiddenNode> hidNodes = hLayer.getNodeList();
			for(HiddenNode hidden : hidNodes) {
				hiddenLayer.put(hidden.getID(), new GNode((Math.random()*regionSize + startRegionX),
														  Math.random()*totalHeight,
														  Color.BLUE));
				boolean isActive = false;
				ArrayList<Edge> inEdges = hidden.getIncomingEdges();
				for(Edge e : inEdges) {
					isActive = (isActive == true ? true : e.isActive());				
					
					int n1ID = e.getNode1().getID();
					if(inputLayer.containsKey(n1ID)) 
						networkEdges.put(edgeIndex++, new GEdge(inputLayer.get(n1ID),
																	hiddenLayer.get(hidden.getID()),
																	(float) e.getWeight(),
																	e.isActive()));
					else if(hiddenLayer.containsKey(n1ID))
						networkEdges.put(edgeIndex++, new GEdge(hiddenLayer.get(n1ID),
																	hiddenLayer.get(hidden.getID()),
																	(float) e.getWeight(),
																	e.isActive()));
					else
						System.out.println("Error: Previous node not stored for hidden node");
					
					//System.out.println("The edge is active: " + e.isActive()); //Debug
				}
				//System.out.println("The node is active: " + isActive); //Debug
				hiddenLayer.get(hidden.getID()).setIsActive(isActive);
			}
			/* Preparing for next set of nodes*/
			startRegionY = 0;
			startRegionX += regionSize;
		}
		
		/*
		 * Create and store outLayer GNodes
		 * ===============================
		 * Position: Upper Right of the panel, in grid fashion (not scattered)
		 * Color: Black
		 * Opacity: True if there is at least one incoming edge that is active
		 */
		
		for(OutputNode output : outLayer) {
			double newRegionY = 10 + startRegionY;
			outputLayer.put(output.getID(), new GNode((10 + startRegionX),
													   newRegionY,
													   Color.BLACK));
			
			startRegionY += newRegionY;
			
			boolean isActive = false;
			ArrayList<Edge> inEdges = output.getIncomingEdges();
			System.out.println("Number of incoming edges to output node: " + inEdges.size());
			for(Edge e : inEdges) {
				isActive = (isActive == true ? true : e.isActive());
				
				int n1ID = e.getNode1().getID();
				if(inputLayer.containsKey(n1ID)) 
					networkEdges.put(edgeIndex++, new GEdge(inputLayer.get(n1ID),
																outputLayer.get(output.getID()),
																(float) e.getWeight(),
																e.isActive()));
				else if(hiddenLayer.containsKey(n1ID)) 
					networkEdges.put(edgeIndex++, new GEdge(hiddenLayer.get(n1ID),
																outputLayer.get(output.getID()),
																(float) e.getWeight(),
																e.isActive()));
				else
					System.out.println("Error: Previous node not stored for output node");
				
				//System.out.println("The edge " + e.getNode1().getID() + "-" + e.getNode2().getID() + " is active: " + e.isActive()); //Debug
			}
			//System.out.println("The node is active: " + isActive); //Debug
			outputLayer.get(output.getID()).setIsActive(isActive);
		}
		
	}
	
	
	/*
	 * Getters for member variables.
	 */
	public NeuralNetwork getNetwork() {
		return network;
	}
	public HashMap<Integer, GNode> getInputLayer(){
		return inputLayer;
	}
	public HashMap<Integer, GNode> getHiddenLayer(){
		return hiddenLayer;
	}
	public HashMap<Integer, GNode> getOutputLayer(){
		return outputLayer;
	}
	public HashMap<Integer, GEdge> getNetworkEdges(){
		return networkEdges;
	}
	public ArrayList<String> getNetworkInfo(){
		return networkInfo;
	}
	public int getTotalWidth() {
		return totalWidth;
	}
	public int getTotalHeight() {
		return totalHeight;
	}

	/*
	 * Setters for member variables 
	 */
	public void setTotalWidth(int w) {
		totalWidth = w;
	}
	public void setTotalHeight(int h) {
		totalHeight = h;
	}
	
	

}
