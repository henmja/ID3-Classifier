
package id3Classifier;

import java.util.HashMap;
import java.util.ArrayList;


public class Node {
	public int numTarget1, numTarget2;//target1=missed and target2=made
	public double entropy;
	//training data that passed the conditions for the current node and all of it's parents:
	public ArrayList<String[]> trainData;
	public Node[] children;
	public boolean leafNode = false;
	public int targetAttr;
	public int target;//1 missed 2 made
	ArrayList<Integer> untraversedAttrs;
	
	public Node(int LSidx, int RSidx, double entropy, ArrayList<String[]> trainData, int targetAttr, 
			ArrayList<Integer> untraversedAttrs){
		this.numTarget1 = LSidx;
		this.numTarget2 = RSidx;
		this.entropy = entropy;
		this.trainData = trainData;
		this.targetAttr = targetAttr;
		this.untraversedAttrs = untraversedAttrs;
	}
	
	public Node(int target){
		leafNode = true;
		this.target = target;
	}
	public Node() {
		
	}
	public static void nodesFromRoot(Node rootNode){
		if(rootNode==null)	{
			return;
		}
		System.out.println(rootNode);
		if(rootNode.leafNode)	{
			return;
		}
		for(Node node : rootNode.children)	{
			nodesFromRoot(node);
		}
	}
	public static int predShot(HashMap<Integer, ArrayList<String>> discVals, Node 
			rootNode, String[] testData){
		
		if(rootNode==null)	{
			return 1;
		} else if (rootNode.leafNode)	{
			return rootNode.target;
		}
		
		ArrayList<String> discreteVals = discVals.get(rootNode.targetAttr);
		String actual = testData[rootNode.targetAttr];
		
		for(int i = 0; i < discreteVals.size(); i++){
			if(actual.equals(discreteVals.get(i))){
				return predShot(discVals, rootNode.children[i], testData);
			}
		}
		return 1;
	}
}
