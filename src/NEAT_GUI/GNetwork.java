package NEAT_GUI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

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
	private int nodeSize;
	private int numColumns;
	/*
	 * Constructor
	 * 
	 * Note: Calculations dependent on totalWidth and totalHeight, so if ever decide to allow them
	 * to be changed, calculations for everything need to be done all over again
	 * Easier to assume fixed size
	 * */
	public GNetwork(NeuralNetwork n, int num) {
		network = n;
		numColumns = num;
		totalWidth = 1250;
		totalHeight = 750;
		nodeSize = 30;
		
		
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

		//int numRegions = hidLayers.size();
		//double regionSize = (totalWidth-5) / numRegions;
		double startRegionX = nodeSize;
		double startRegionY = nodeSize;

		/*
		 * Create and store inLayer GNodes
		 * ===============================
		 * Position: Upper Left corner, in grid fashion (not scattered)
		 * Color: Red
		 * Opacity: Based on the input node value 
		 */
		int xCounter = 0;// for determining if max number of nodes per row reached

		for(InputNode input : inLayer) {

			/*Creating the initial input GNode*/
			inputLayer.put(input.getID(), 
					new GNode(startRegionX, startRegionY, Color.RED));

			/*Incrementing and checking xCounter for grid placement*/
			xCounter++;
			if(xCounter == numColumns) {
				startRegionX = nodeSize;
				startRegionY += nodeSize;
				xCounter = 0;
			}
			else {
				startRegionX += nodeSize;
			}

			/*Determining the activeness of the node*/
			boolean isActive = false;
			ArrayList<Edge> outEdges = input.getOutgoingEdges();
			for(int i=0; (!isActive)&&i<outEdges.size(); i++){
				isActive = outEdges.get(i).isActive();
			}
			//System.out.println("The node is active: " + isActive);
			//Note that isActive value not important for input nodes; added for sake of completion
			
			/*Determining the alpha value of the node*/
			double inputValue = input.getInput();
			float alpha = 0;
			if(inputValue > 0) {
				alpha = 1f;
			}
			else if(inputValue < 0) {
				alpha = 0.7f;
			}
			//Note: if inputVal == 0, default alpha  value of 0
			
			inputLayer.get(input.getID()).setIsActive(isActive);
			inputLayer.get(input.getID()).setAlpha(alpha);
		}

		/* Preparing for next set of nodes*/
		startRegionY = nodeSize;
		startRegionX = (nodeSize * 2) + (nodeSize * numColumns);

		/*
		 * Create and store hidLayer GNodes
		 * ===============================
		 * Position: Middle of the panel, scattered
		 * Color: Blue
		 * Opacity: True if there is at least one incoming edge that is active
		 */
		
		/*x axis spacing between hidden layers*/
		int middleSectionWidth = totalWidth - ((nodeSize * 4) + (nodeSize * numColumns));
		
		for(Layer<HiddenNode> hLayer : hidLayers){	
			int equiDistanceX = middleSectionWidth/(hidLayers.size()*2);
			int equiDistanceY = 0;
			ArrayList<HiddenNode> hidNodes = hLayer.getNodeList();
			
			for(HiddenNode hidden : hidNodes) {
				/*Creating the initial hidden GNode*/
				hiddenLayer.put(hidden.getID(), 
						new GNode((startRegionX + equiDistanceX), (startRegionY + equiDistanceY), Color.BLUE));
				startRegionY += equiDistanceY;
				
				/*Determining the activeness of the node
				 * AND determining the activeness of the incoming edges of the node*/
				boolean isActive = false;
				ArrayList<Edge> inEdges = hidden.getIncomingEdges();
				for(Edge e : inEdges) {
					isActive = (isActive == true ? true : e.isActive());				

					int n1ID = e.getNode1().getID();
					if(inputLayer.containsKey(n1ID)) { 
						float alpha = (e.isActive() ? 1f : 0.7f);
						networkEdges.put(edgeIndex++, 
								new GEdge(inputLayer.get(n1ID),	hiddenLayer.get(hidden.getID()), (float) e.getWeight(),	alpha));
						
					}else if(hiddenLayer.containsKey(n1ID)) {
						float alpha = (e.isActive() ? 1f : 0.7f);
						networkEdges.put(edgeIndex++, 
								new GEdge(hiddenLayer.get(n1ID), hiddenLayer.get(hidden.getID()), (float) e.getWeight(), alpha));
					}else
						System.out.println("Error: Previous node not stored for hidden node");
					//System.out.println("The edge is active: " + e.isActive()); //Debug
				}
				//System.out.println("The node is active: " + isActive); //Debug
				hiddenLayer.get(hidden.getID()).setIsActive(isActive);
				
				/*Determining the alpha value of the node*/
				float alpha = 0;
				if(isActive) 
					alpha = 1f;
				else
					alpha = 0.7f;
				hiddenLayer.get(hidden.getID()).setAlpha(alpha);
			}
			/* Preparing for next set of nodes*/
			startRegionY = nodeSize;
		}

		startRegionY = nodeSize;
		startRegionX = totalWidth - (nodeSize * 2);
		/*
		 * Create and store outLayer GNodes
		 * ===============================
		 * Position: Upper Right of the panel, in grid fashion (not scattered)
		 * Color: Black
		 * Opacity: True if there is at least one incoming edge that is active
		 */

		for(OutputNode output : outLayer) {
			/*Creating initial output GNode*/
			double newRegionY = nodeSize + startRegionY;
			outputLayer.put(output.getID(), 
					new GNode(startRegionX, newRegionY,	Color.GREEN));
			//System.out.println("startregionx = " + startRegionX);
			startRegionY = newRegionY;
			
			/*Determining the activeness of the node
			 * AND determining the activeness of the incoming edges of the node*/
			boolean isActive = false;
			ArrayList<Edge> inEdges = output.getIncomingEdges();
			System.out.println("Number of incoming edges to output node: " + inEdges.size());
			for(Edge e : inEdges) {
				isActive = (isActive == true ? true : e.isActive());

				int n1ID = e.getNode1().getID();
				if(inputLayer.containsKey(n1ID)) {
					float alpha = (e.isActive() ? 1f : 0.7f);
					networkEdges.put(edgeIndex++, 
							new GEdge(inputLayer.get(n1ID), outputLayer.get(output.getID()), (float) e.getWeight(), alpha));
				}else if(hiddenLayer.containsKey(n1ID)) {
					float alpha = (e.isActive() ? 1f : 0.7f);
					networkEdges.put(edgeIndex++, 
							new GEdge(hiddenLayer.get(n1ID), outputLayer.get(output.getID()), (float) e.getWeight(), alpha));
				}else
					System.out.println("Error: Previous node not stored for output node");
				//System.out.println("The edge " + e.getNode1().getID() + "-" + e.getNode2().getID() + " is active: " + e.isActive()); //Debug
			}
			//System.out.println("The node is active: " + isActive); //Debug
			outputLayer.get(output.getID()).setIsActive(isActive);
			
			/*Determining the alpha value of the node*/
			System.out.println("The output node is fired: " + output.checkFired());
			float alpha = (output.checkFired() ? 1f : 0.2f);
			outputLayer.get(output.getID()).setAlpha(alpha);
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
	public int getNodeSize() {
		return nodeSize;
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
