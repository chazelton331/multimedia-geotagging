package gr.iti.mklab.method;
import gr.iti.mklab.util.EasyBufferedReader;
import gr.iti.mklab.util.EasyBufferedWriter;
import gr.iti.mklab.util.MyHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;


/**
 * Entropy class update the file that contains the tag-cell probabilities.
 * Calculate the spacial tag entropy for all of the tags and sort-rank them based on their values.
 * @author gkordo
 *
 */
public class Entropy {
	
	static Logger logger = Logger.getLogger("gr.iti.mklab.method.Entropy");
	
	// Calculate the spatial tag entropy
	public static void createEntropyFile(String fileTagCell){
		
		EasyBufferedReader reader = new EasyBufferedReader(fileTagCell);		

		Map<String,Double> entropyMap = new HashMap<String,Double>();
		Map<String,String[]> output = new HashMap<String,String[]>();

		String input;
		String[] inputLine;

		logger.info("update file " + fileTagCell);
		logger.info("add tags' entropy values");
		
		long sTime = System.currentTimeMillis();
		
		while ((input=reader.readLine())!=null){

			inputLine = input.split(" ");
			int cellNum = inputLine.length-2;

			double[] p = new double[cellNum];

			for(int i=0; i<cellNum ;i++){
				p[i] = Double.parseDouble(inputLine[i+2].split(">")[1]);

			}

			double entropy;
			entropy = computeEntropyNaive(p);
			entropyMap.put(inputLine[0],entropy);
			output.put(inputLine[0], inputLine);
		}
		
		Map<String, Double> cellsProbsSorted = MyHashMap.sortByValues(entropyMap);
		
		writeUpdatedFile(cellsProbsSorted, output, fileTagCell);
		
		logger.info("file updated with tags' entropy values");
		logger.info("total time needed " + (System.currentTimeMillis()-sTime)/1000.0 + "s");
		
		reader.close();		
	}

	// Save the updated file
	public static void writeUpdatedFile(Map<String, Double> cellsProbsSorted, Map<String,String[]> output, String fileTagCell){
		
		EasyBufferedWriter writer = new EasyBufferedWriter(fileTagCell.replace(".txt","")+"_entropy.txt");
		int i=0;
		
		for(Entry<String, Double> entryCell: cellsProbsSorted.entrySet()){
			i++;
			String tag = entryCell.getKey();
			writer.write(tag+" "+i+"_"+entryCell.getValue()+" ");
			int cellNum = output.get(tag).length;
			
			for(int j=2; j<cellNum ;j++){
				writer.write(output.get(tag)[j]+" ");	
			}
			writer.write("\n");
		}
		writer.close();
	}
	
	// Shannon entropy formula
	private static double computeEntropyNaive(final double[] probabilities) {
		double entropy = 0.0;
		for (int i = 0; i < probabilities.length; i++) {
			final double p = probabilities[i];
			if(p!=0.0){
				entropy -= p * Math.log(p);
			}
		}
		return entropy;
	}
}