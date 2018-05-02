/*
 *  GrapherOne class deals with output produced by the ReadCSVOne class. 

 *  This class gives each object an ID, and each type of relationship a unique ID
 * 	The map of objects to their ID is saved as objectsid1.csv and relationships to their ID as relationshipsid1.csv
 * 
 *  The links are saved in connections1.csv
 */

import java.util.Scanner;

import java.io.*;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;

// Save link and save node data.
// collection of nodes, each node has a unique ID
// collection of edges, each edge has two node IDs (or however many nodes an edge connects to)
public class GrapherOne {
	Scanner scanner;
	//Set<String> relationships;
	Map<String, Integer> objmap;
	Map<String, Integer> rsmap;
	private FileWriter file;
	private BufferedWriter bw;
	
	public GrapherOne(String address) throws IOException {
		// Scan dataset1.csv from readcsv. 
		scanner = new Scanner(new File(address));
		scanner.useDelimiter("\\r?\\\n");
		
		//Write connections to new csv
		file = new FileWriter("may/connections1.csv");
    	bw = new BufferedWriter(file);
		
		objmap = new HashMap<String,Integer>();
		rsmap = new HashMap<String,Integer>();
		
		int counter = 0;
		while(scanner.hasNext()) {
			String str = scanner.next();
			String[] arr = str.split(",");
			
			// Obtain/generate object IDs
			if(!objmap.containsKey(arr[0])) {
				objmap.put(arr[0], objmap.size()+1);
			}
			if(!objmap.containsKey(arr[2])) {
				objmap.put(arr[2], objmap.size()+1);
			}
			if(!rsmap.containsKey(arr[1])) {
				rsmap.put(arr[1], rsmap.size()+1);
			}
			
			
			String output = objmap.get(arr[0]) + "," + objmap.get(arr[2]) + "," + rsmap.get(arr[1]) + "," + arr[3] + "\n";
			
			bw.write(output);
			
			counter+=1;
			System.out.println("Entry " + counter + " found. " + "Score of " + arr[3]);
		}
		bw.flush();
		bw.close();
		file.close();
		
		// Write object IDs file
		file = new FileWriter("may/objectids1.csv");
    	bw = new BufferedWriter(file);
    	
    	for(Map.Entry<String,Integer> a : objmap.entrySet()) {
			bw.write(a.getKey() + "," + a.getValue() + "\n");
		}
		bw.flush();
		bw.close();
		file.close();
		
		// Write RS IDs file
		file = new FileWriter("may/relationids1.csv");
    	bw = new BufferedWriter(file);
    	
    	for(Map.Entry<String,Integer> a : rsmap.entrySet()) {
			bw.write(a.getKey() + "," + a.getValue() + "\n");
		}
		bw.flush();
		bw.close();
    	
		System.out.println(objmap.size() + " objects found. " + counter + " relationships saved.");
		System.out.println(rsmap.size() + " unique relations.");
		System.out.println("End of check.");
	
	}
	
	public static void main(String[] args) {
		try {
		new GrapherOne("may/dataset1.csv");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
