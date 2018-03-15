package id3Classifier;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Pruning {
	private double startAccuracy;
	private double maximumAccuracy;
	public DT DecisionTree;
	
	public Pruning(DT tree) throws FileNotFoundException, UnsupportedEncodingException{
		startAccuracy = tree.accuracy;
		maximumAccuracy = startAccuracy;this.DecisionTree = tree;
		pruneDT(tree.rootNode);
	}
	
	private void pruneDT(Node rootNode) throws FileNotFoundException, UnsupportedEncodingException{ 
		
		if(rootNode==null)	{
			return;
		}
		
		rootNode.leafNode = true;
		
		if (rootNode.numTarget1>=rootNode.numTarget2)	{
			rootNode.target = 1;
		} else	{
			rootNode.target = 2;
		}
		DecisionTree.calcAccuracy();
		
		if(DecisionTree.accuracy > maximumAccuracy){
			maximumAccuracy = DecisionTree.accuracy;
			return;
		}
		rootNode.leafNode = false;
		for(Node node : rootNode.children){
			if(node.leafNode)	{
				continue;
			}
			pruneDT(node);
		}
		
	}
}
