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
	
	private final float TRANS = 0.3f;

	private NeuralNetwork network;
	private HashMap<Integer, GNode> inputLayer; //Keys are node IDs
	private HashMap<Integer, GNode> hiddenLayer;
	private HashMap<Integer, GNode> outputLayer;
	private HashMap<Integer, GEdge> networkEdges; //Keys are incrementing index
	private ArrayList<String> networkInfo;

	private int totalWidth;
	private int totalHeight;
	private double nodeSize, hidNodeSize;
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
			
			/*Deciding the color of the GNode*/
			Color c;
			if(Math.abs(input.getInput()) == 1){
				c = Color.RED;
			}else if(Math.abs(input.getInput()) == 2){
				c = Color.ORANGE;
			}else if(Math.abs(input.getInput()) == 3){
				c = Color.YELLOW;
			}else if(Math.abs(input.getInput()) == 4){
				c = Color.GREEN;
			}else if(Math.abs(input.getInput()) == 5){
				c = Color.BLUE;
			}else if(Math.abs(input.getInput()) == 6){
				c = new Color(255, 0, 255); //Purple
			}else{
				c = Color.BLACK;
			}

			/*Creating the initial input GNode*/			
			inputLayer.put(input.getID(), 
					new GNode(startRegionX, startRegionY, c));

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

			/*Determining the alpha value and activeness of the node*/
			double inputValue = input.getInput();
			boolean isActive = true;
			if(inputValue == 0.0) {
				isActive = false;
			}
			//System.out.println("The node is active: " + isActive);
			
			float alpha = 0;
			if(inputValue > 0) {
				alpha = 1f;
			}
			else if(inputValue < 0) {
				alpha = TRANS;
			}
			//Note: if inputVal == 0, default alpha value of 0
			
			inputLayer.get(input.getID()).setIsActive(isActive);
			inputLayer.get(input.getID()).setAlpha(alpha);
		}

		
		/*
		 * Create and store hidLayer GNodes
		 * ===============================
		 * Position: Middle of the panel, centered
		 * Color: Blue
		 * Opacity: True if there is at least one incoming edge that is active
		 */
		
		hidNodeSize = nodeSize;
		double equiDistance = 2 * nodeSize;
		double middle = totalWidth - ((nodeSize * 2) + (nodeSize * numColumns) + (nodeSize * 2));	//hiddenlayer region = totalwidth - inputlayer region - outputlayer region
		/*For X-axis, determine number of layers by subtracting the total from the number of empty layers*/
		int emptyLayerCount = 0;
		for(Layer<HiddenNode> l : hidLayers){
			if(l.getNodeList().size() == 0)
				emptyLayerCount++;	
		}
		int numNonEmptyLayers = hidLayers.size()-emptyLayerCount;
		/*For the Y-axis, nodes centered with respect to deepest depth*/
		int maxHeight = 1;
		for(Layer<HiddenNode> hLayer : hidLayers) {
			int layerSize = hLayer.getNodeList().size();
			maxHeight = (layerSize > maxHeight ? layerSize : maxHeight);
		}
		
		/*
		 * Determine whether the hidden nodes will fit into the middle region
		 */		
		
		/*Checking X-axis*/
		if((numNonEmptyLayers * equiDistance) > middle) {
			equiDistance = middle/numNonEmptyLayers;
			hidNodeSize = equiDistance/2;
		} 
		/*Checking Y-axis*/
		if((maxHeight * equiDistance) > totalHeight) {
			double newEquiDistance = totalHeight/maxHeight;
			equiDistance = (newEquiDistance < equiDistance ? newEquiDistance : equiDistance);
			hidNodeSize = equiDistance / 2;
		}
		
		/*
		 * Figuring out the starting positions of the hidden layers:
		 *
		 * totalwidth = inputlayer region + outputlayer region + hiddenlayer region
		 * hiddenlayer region = 2*nodesize*number of nonempty hidden layers
		 * 
		 * starting x-position = space + inputlayer region + hiddenlayer region shifted to the left
		 *
		 */
		startRegionX = (nodeSize) + (nodeSize * numColumns) + (middle - (2 * hidNodeSize * numNonEmptyLayers))/ 2;
		int x_multiplier = 1;
				
		for(Layer<HiddenNode> hLayer : hidLayers){	
			ArrayList<HiddenNode> hidNodes = hLayer.getNodeList();	
			
			/*
			 * Figuring out the starting positions of the hidden nodes:
			 * 
			 * totalheight = 2y + 2*nodesize*number of nodes in the layer
			 * where y is the distance between the frame and the first (and last) node
			 * Note that starting region dependent on number of nodes per layer
			 * 
			 * y = (totalheight - 2*nodesize*number of nodes in the layer) 2
			 * 
			 * */
			
			startRegionY = ((maxHeight * 2 * hidNodeSize) - (2*hidNodeSize*hidNodes.size()))/2;
			int y_multiplier = 1;
			
			for(HiddenNode hidden : hidNodes) {
				/*Creating the initial hidden GNode*/
				hiddenLayer.put(hidden.getID(), 
						new GNode((startRegionX + equiDistance * x_multiplier), (startRegionY + equiDistance * y_multiplier), Color.BLUE));
				//System.out.println("Placing node at: (" + (startRegionX + equiDistance) + "," + (startRegionY + equiDistance) + ")");
				y_multiplier++;
				
				/*Determining the activeness of the node AND determining the activeness of the incoming edges of the node*/
				boolean isActive = false;
				ArrayList<Edge> inEdges = hidden.getIncomingEdges();
				for(Edge e : inEdges) {
					isActive = (isActive == true ? true : e.isActive());				
					int n1ID = e.getNode1().getID();
					if(inputLayer.containsKey(n1ID)) { 
						float alpha;
						/*Only show the edge if the input node is active*/
						if(inputLayer.get(n1ID).getIsActive()) {
							alpha = (e.isActive() ? 1f : 0.7f);
						} else {
							alpha = 0;
						}
						networkEdges.put(edgeIndex++,  
								new GEdge(inputLayer.get(n1ID),	hiddenLayer.get(hidden.getID()), (float) e.getWeight(),	alpha));
					}else if(hiddenLayer.containsKey(n1ID)) {
						float alpha = (e.isActive() ? 1f : 0.7f);
						networkEdges.put(edgeIndex++, 
								new GEdge(hiddenLayer.get(n1ID), hiddenLayer.get(hidden.getID()), (float) e.getWeight(), alpha));
					}
					//System.out.println("Error: Previous node not stored for hidden node");
					//System.out.println("The edge is active: " + e.isActive()); //Debug
				}
				//System.out.println("The node is active: " + isActive); //Debug
				hiddenLayer.get(hidden.getID()).setIsActive(isActive);
				
				/*Determining the alpha value of the node*/
				float alpha = 0;
				if(isActive) 
					alpha = 1f;
				else
					alpha = TRANS;
				hiddenLayer.get(hidden.getID()).setAlpha(alpha);
			}
			if(hidNodes.size() > 0)
				x_multiplier++;
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
			//System.out.println("Number of incoming edges to output node: " + inEdges.size());
			for(Edge e : inEdges) {
				isActive = (isActive == true ? true : e.isActive());				
				int n1ID = e.getNode1().getID();
				if(inputLayer.containsKey(n1ID)) { 
					float alpha;
					/*Only show the edge if the input node is active*/
					if(inputLayer.get(n1ID).getIsActive()) {
						alpha = (e.isActive() ? 1f : 0.7f);
					} else {
						alpha = 0f;
					}
					networkEdges.put(edgeIndex++, 
							new GEdge(inputLayer.get(n1ID), outputLayer.get(output.getID()), (float) e.getWeight(), alpha));
				}else if(hiddenLayer.containsKey(n1ID)) {
					float alpha = (e.isActive() ? 1f : 0.7f);
					networkEdges.put(edgeIndex++, 
							new GEdge(inputLayer.get(n1ID), outputLayer.get(output.getID()), (float) e.getWeight(), alpha));
				}
				
					//System.out.println("Error: Previous node not stored for output node");
				//System.out.println("The edge " + e.getNode1().getID() + "-" + e.getNode2().getID() + " is active: " + e.isActive()); //Debug
			}
			//System.out.println("The node is active: " + isActive); //Debug
			outputLayer.get(output.getID()).setIsActive(isActive);
			
			/*Determining the alpha value of the node*/
			//System.out.println("The output node is fired: " + output.checkFired());
			float alpha = (output.checkFired() ? 1f : TRANS);
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
	public double getNodeSize() {
		return nodeSize;
	}
	public double getHidNodeSize() {
		return hidNodeSize;
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