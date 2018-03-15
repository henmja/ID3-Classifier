package id3Classifier;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class MAIN {
	
	public static ArrayList<Integer> getContAtts(int[]id) {
		ArrayList<Integer> contAtts = new ArrayList<Integer>();
		for (int i = 0; i<id.length; i++) {
			contAtts.add(id[i]);
		}
		return contAtts;
	}

	public static void main(String[] args) throws IOException {		
		//int[] ids = {2,3,5,6,7,8,9,11};
		int[] ids = {0,2,4,10,11,12};
		ArrayList<Integer> contAtts = getContAtts(ids);
		
		//String target1 = "missed"; 
		//target2 = "made";
		String target1 = "<=50K";
		String target2 = ">50K";
		URL trainFile = Thread.currentThread().getContextClassLoader()
				  .getResource("id3Classifier/adult.train.csv");
		Convert conv = new Convert(contAtts, target1, target2);
		ArrayList<String[]> dataset = conv.convert(trainFile);
		Collections.shuffle(dataset);
		ArrayList<String[]>testingData = new ArrayList<String[]>(dataset.subList(dataset.size()/2, dataset.size()));
		ArrayList<String[]>trainingData = new ArrayList<String[]>(dataset.subList(0, dataset.size()/2));
		int numTarget1 = conv.t1, numTarget2 = conv.t2;		
		Convert.calcDisc(trainingData);
		
		ArrayList<Integer> untAttr = new ArrayList<>();
		for(int i = 0; i < trainingData.get(0).length-1; i++)	untAttr.add(i);
		System.out.println("Generating ID3 Decision Tree without pruning...");
		DT decisionTree = new DT(trainingData, testingData, Convert.discMap, numTarget1, numTarget2, target1, target2, untAttr);
		decisionTree.printAccuracy();
		System.out.println("\nPruning the generated ID3 Decision Tree.....");
		Pruning prune = new Pruning(decisionTree);
		prune.DecisionTree.printAccuracy();
		/*int noOftrees = 10;
		double fractionOfAttributesToTake = 0.5, fractionOfTrainingInstancesToTake = 0.33;
		System.out.println("\nInitializing Random Forest with "+noOftrees+" trees, "+fractionOfAttributesToTake
				+" fraction of attributes and "+fractionOfTrainingInstancesToTake+" fraction of training instances in each tree");
		RandomForest rf = new RandomForest(noOftrees, fractionOfAttributesToTake, fractionOfTrainingInstancesToTake, 
				trainingData, testingData, numTarget1, numTarget2, target1, target2, Convert.discMap);
		rf.printAnalysis();
		
		System.out.println("\nEnd...");*/
	}

}
