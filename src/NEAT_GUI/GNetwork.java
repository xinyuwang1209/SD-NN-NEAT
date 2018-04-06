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
	private int numColumns;
	/*
	 * Constructor
	 * 
	 * Note: Calculations dependent on totalWidth and totalHeight, so if ever decide to allow them
	 * to be changed, calculations for everything need to be done all over again
	 * Easier to assume fixed size
	 * */
	public GNetwork(NeuralNetwork n, int num) {
		numColumns = num;
		network = n;
		totalWidth = 500;
		totalHeight = 1000;
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
		double startRegionX = 10;
		double startRegionY = 10;

		/*
		 * Create and store inLayer GNodes
		 * ===============================
		 * Position: Upper Left corner, in grid fashion (not scattered)
		 * Color: White
		 * Opacity: Based on the input node value 
		 */
		int xCounter = 0;// for determining if max number of nodes per row reached

		for(InputNode input : inLayer) {

			/*Creating the initial input GNode*/
			inputLayer.put(input.getID(), new GNode(startRegionX,
					startRegionY,
					Color.WHITE));

			/*Incrementing and checking xCounter for grid placement*/
			xCounter++;
			if(xCounter == numColumns) {
				startRegionX = 10;
				startRegionY += 10;
				xCounter = 0;
			}
			else {
				startRegionX += 10;
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
				alpha = 10 * 0.1f;
			}
			else if(inputValue < 0) {
				alpha = 7 * 0.1f;
			}
			//Note: if inputVal == 0, default alpha  value of 0
			
			inputLayer.get(input.getID()).setIsActive(isActive);
			inputLayer.get(input.getID()).setAlpha(alpha);
		}

		/* Preparing for next set of nodes*/
		startRegionY = 10;
		startRegionX = 10 + regionSize;

		/*
		 * Create and store hidLayer GNodes
		 * ===============================
		 * Position: Middle of the panel, scattered
		 * Color: Gray
		 * Opacity: True if there is at least one incoming edge that is active
		 */

		for(Layer<HiddenNode> hLayer : hidLayers){
			int equiDistance = 0;
			ArrayList<HiddenNode> hidNodes = hLayer.getNodeList();
			if(hidNodes.size() > 0) {
				equiDistance = totalHeight / hidNodes.size();
			}
			for(HiddenNode hidden : hidNodes) {
				/*Creating the initial hidden GNode*/
				hiddenLayer.put(hidden.getID(), new GNode(startRegionX,
						(startRegionY + equiDistance),
						Color.GRAY));
				startRegionY += equiDistance;
				
				/*Determining the activeness of the node
				 * AND determining the activeness of the incoming edges of the node*/
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
				
				/*Determining the alpha value of the node*/
				float alpha = 0;
				if(isActive) 
					alpha = 10 * 0.1f;
				else
					alpha = 7 * 0.1f;
				hiddenLayer.get(hidden.getID()).setAlpha(alpha);
			}
			/* Preparing for next set of nodes*/
			startRegionY = 10;
		}

		startRegionY = 10;
		startRegionX = totalWidth - 20;
		/*
		 * Create and store outLayer GNodes
		 * ===============================
		 * Position: Upper Right of the panel, in grid fashion (not scattered)
		 * Color: Black
		 * Opacity: True if there is at least one incoming edge that is active
		 */

		for(OutputNode output : outLayer) {
			/*Creating initial output GNode*/
			double newRegionY = 10 + startRegionY;
			outputLayer.put(output.getID(), new GNode(startRegionX,
					newRegionY,
					Color.BLACK));

			System.out.println("startregionx = " + startRegionX);
			startRegionY = newRegionY;
			
			/*Determining the activeness of the node
			 * AND determining the activeness of the incoming edges of the node*/
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
			
			/*Determining the alpha value of the node*/
			System.out.println("The output node is fired: " + output.checkFired());
			float alpha = (output.checkFired() ? (10 * 0.1f) : (2 * 0.1f));
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
