package NeuralNetwork;

import java.util.ArrayList;

public class Layer <E> {
	ArrayList<E> nodeList = new ArrayList<E>();
	
	public E get(int i){
		return nodeList.get(i);
	}
	
	public void add(E n){
		nodeList.add(n);
	}
	
	public ArrayList<E> getNodeList(){
		return nodeList;
	}
}
