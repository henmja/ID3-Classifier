package id3Classifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Convert {
	static HashMap<Integer, ArrayList<String>> discMap = new HashMap<>();
	static double[] splitPoint;
	int t1;
	int t2;
	ArrayList<Integer> contFeats;
	String left, right;
	
	public Convert(ArrayList<Integer> contFeats, String left, String right){
		this.contFeats = contFeats;
		this.left = left;
		this.right = right;
	}
	
	public static void calcDisc(ArrayList<String[]> data){
		String[] firstRow = data.get(0);
		HashSet<String> traversedVals = new HashSet<>();
		for(int i = 0; i < firstRow.length-1; i++){
			discMap.put(i, new ArrayList<String>());
		}
		for(String[] row : data){
			for(int i = 0; i < row.length-1; i++){
				String val = row[i]; //e.g. row[12] is the target values
				if(val.equals("?"))	continue;
				if(!traversedVals.contains(val)){
					discMap.get(i).add(val);
					traversedVals.add(val);
				}
			}
		}
	}
	
	public ArrayList<String[]> convert(URL trainAndData) throws IOException{
		BufferedReader myReader = new BufferedReader(new InputStreamReader(trainAndData.openStream()));
		String line = myReader.readLine();
		myReader.close();
		StringTokenizer myTokenizer = new StringTokenizer(line, ",");
		ArrayList<String[]> data = new ArrayList<>();
		int numAttrs = myTokenizer.countTokens();
		splitPoint = new double[numAttrs];
		myReader = new BufferedReader(new InputStreamReader(trainAndData.openStream()));
		while((line = myReader.readLine())!=null){
			String[] rowData = new String[numAttrs]; 
			myTokenizer = new StringTokenizer(line, ",");
			for(int i = 0; i < numAttrs; i++)	{
				rowData[i] = myTokenizer.nextToken();
			}
			if(rowData[numAttrs-1].equals(left))	{
				t1++;
			}
			else	{
				t2++;
			}
			data.add(rowData);
		}
		for(int i : contFeats){
			Collections.sort(data, new CompareStrings(i));
			String previous = data.get(0)[i];
			double lsLeft = 0, lsRight=t1, rsLeft = 0, rsRight=t2;
			double sum = t1+t2;
			double minEnt = 1;
			double splittingPoint=1;
			for(String[] dataset: data){
				String current = dataset[i];
				if(dataset[numAttrs-1].equals(left)){
					lsLeft++;
					lsRight--;
				}else{
					rsLeft++;
					rsRight--;
				}
				if(current.equals(previous))	{
					continue;
				}
				double plr = (rsLeft)/(lsLeft+rsLeft);
				double pll = (lsLeft)/(lsLeft+rsLeft);
				double prl = (lsRight)/(lsRight+rsRight);
				double prr = (rsRight)/(lsRight+rsRight);	
				double entropy = ((lsLeft+rsLeft)/sum)*(-pll*Math.log(pll) -plr*Math.log(plr)) + 
						((lsRight+rsRight)/sum)*(-prl*Math.log(prl) -prr*Math.log(prr));
				if(entropy < minEnt){
					splittingPoint = Double.parseDouble(current)/2+Double.parseDouble(previous);
					splitPoint[i] = splittingPoint;
					minEnt = entropy;
				}
				previous = current;
			}
			//convert continous features to discrete variables by splitting them at splitpoint to get 
			//minimum entropy
			String newLS = "<="+splittingPoint;
			String newRS = ">"+splittingPoint;
			for(String[] dataset : data){   
				double dat = Double.parseDouble(dataset[i]);
				if(dat <= splittingPoint) {
					dataset[i] = newLS;
				}else	{
					dataset[i] = newRS;
				}
			}
		}
		myReader.close();
		return data;
	}
}

class CompareStrings implements Comparator<String[]>{
	private int idx;
	public CompareStrings(int idx)	{
		this.idx = idx;
		}
	public int compare(String[] a, String[] b){
		return a[idx].compareTo(b[idx]);
	}
}
