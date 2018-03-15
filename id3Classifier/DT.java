
package id3Classifier;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class DT {
	private String targetVal1;
	private String targetVal2;
	private ArrayList<String[]>testData;
	HashMap<Integer, ArrayList<String>> discVals;
	Node rootNode;
	double accuracy;
	
	public DT(ArrayList<String[]> trainData, ArrayList<String[]> testData, 
			HashMap<Integer, ArrayList<String>> discVals, int numTargetVal1, int numTargetVal2, 
			String targetVal1, String targetVal2, 
			ArrayList<Integer> untraversedAttrs) throws FileNotFoundException, UnsupportedEncodingException{
		
		this.targetVal1 = targetVal1;
		this.targetVal2 = targetVal2;
		this.testData = testData;
		this.discVals = discVals;
		
		double p1 = numTargetVal1/(numTargetVal1+numTargetVal2);
		double p2 = numTargetVal2/(numTargetVal1+numTargetVal2);
		rootNode = new Node();
		rootNode.trainData = trainData;
		rootNode.numTarget1 = numTargetVal1;
		rootNode.numTarget2 = numTargetVal2;
		rootNode.entropy = -p1*calcLogProb(p1) - p2*calcLogProb(p2);
		rootNode.untraversedAttrs = untraversedAttrs;
		generateTree(rootNode);
		calcAccuracy();
	}
	
	private void generateTree(Node rootNode){
		if(rootNode==null)	{
			return;
		}
		if(rootNode.untraversedAttrs.size()==1)	{
			if (rootNode.numTarget1>=rootNode.numTarget2) {
				rootNode.target = 1;
			} else {
				rootNode.target=2;
			}
			rootNode.leafNode = true;
		} else if(rootNode.trainData.size()==0 || rootNode.numTarget1==0 || rootNode.numTarget2==0)	{
			if (rootNode.numTarget1==0) {
				rootNode.target = 2;
			} else {
				rootNode.target=1;
			}			
			rootNode.leafNode = true;
		} else	{
			rootNode.children = addChild(rootNode);
			ArrayList<String> discreteVals = discVals.get(rootNode.targetAttr);
			for(int i=0; i<discreteVals.size(); i++){
				rootNode.children[i].trainData = new ArrayList<>();
				rootNode.children[i].untraversedAttrs = new ArrayList<>();
				for(int unt : rootNode.untraversedAttrs){
					if(unt!=rootNode.targetAttr)	rootNode.children[i].untraversedAttrs.add(unt);
				}
				String current = discreteVals.get(i);
				for(String[] row : rootNode.trainData){
					if(row[rootNode.targetAttr].equals(current)){
						rootNode.children[i].trainData.add(row);
					}
				}
				generateTree(rootNode.children[i]);
			}
		}
	}
	
	public Node[] addChild(Node root){
		//split on attribute that yields the highest gain.
		Node[] node = null;
		for(int i : root.untraversedAttrs){
			ArrayList<String> discreteVals = discVals.get(i);
			Node[] child = new Node[discreteVals.size()];
			for(int j=0; j < discreteVals.size(); j++){
				String current = discreteVals.get(j);
				child[j] = new Node();
				for(String[] row : root.trainData){
					if(row[i].equals(current)){
						if (row[row.length-1].equals(targetVal1))	{
							child[j].numTarget1++;
						} else	{
							child[j].numTarget2++;
						}
					}
				}
			}
			int nodeSize = root.trainData.size();
			double infoGain = root.entropy;
			for(int j = 0; j < discreteVals.size(); j++){
				int t1 = child[j].numTarget1;
				int t2 = child[j].numTarget2;
				if(t1==0 && t2==0)	{
					continue;
				}
				double prob1 = t1/(t1+t2+0.0);
				double prob2 = t2/(t1+t2+0.0);
				child[j].entropy = -prob1*calcLogProb(prob1) + -prob2*calcLogProb(prob2);
				infoGain -= child[j].entropy*((t1+t2)/(nodeSize+0.0));
			}
			double maxInfoGain = -1.0;
			if(infoGain > maxInfoGain){
				root.targetAttr = i;
				maxInfoGain = infoGain;
				node = child;
			}
		}
		return node;
	}
	
	public void calcAccuracy() throws FileNotFoundException, UnsupportedEncodingException{
		int truePred=0;
		int falsePred=0;
		PrintWriter writer = new PrintWriter("C:/Users/ap/documents/"
				+ "github/henmja-files#fork-destination-box/assignment-2/data/data.csv", "UTF-8");
		writer.println("ID, Target");
		int i = 1;
		for (String[] row : testData){
			int predicted = Node.predShot(discVals, rootNode, row);
			int actual = 0;
			if (row[row.length-1].equals(targetVal1)) {
				actual = 1;
			} else {
				actual = 2;
			}
			if (predicted  == actual)	{
				truePred++;
			}
			else	{
				falsePred++;
			}	
			String output = "";
			if (predicted==1) {
				output = targetVal1;
			} else {
				output = targetVal2;
			}
			writer.println(i + ", " + output);
			i++;
		}
		writer.close();
		accuracy = (truePred)/(falsePred+0.0+truePred);
	}
	
	private static double calcLogProb(double prob){
		double LogP = 0;
		if (prob==0) {
			LogP = 0;
		} else {
			LogP = prob*Math.log(prob);
		}
		return LogP;
	}
	public void printAccuracy(){
		System.out.println("Accuracy="+accuracy);
	}
}
