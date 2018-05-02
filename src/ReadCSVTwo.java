/*
 * This class deals with the raw data from the webchild dataset:
 *  - hasMember
 *  - physical
 *  - hasSubstance
 *  
 * Further improvements:
 * Modify code such that only the directory containing the 6 necessary files need be supplied as an argument to main.
 * This ReadCSV converts the file directly to graph notation (3 files - object IDs, rs IDs, connections)
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ReadCSVTwo {
	public String filedest;
	public int size;
	
	Map<Integer, String> objmap;
	Map<String, Integer> rsmap;
	
	private FileWriter file;
	private BufferedWriter bw;
	public int netcount;
	// In the future, change tensor to take in shape. 
	public ReadCSVTwo(String destination) throws IOException {
		file = new FileWriter(destination);
    	bw = new BufferedWriter(file);
		netcount = 0;
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<String,Integer>();
		
		readDataWithScore("wc/webchild_partof_memberof.txt",0,1,2,3,4,7,true);
		readDataWithScore("wc/webchild_partof_substanceof.txt",0,1,2,3,4,7,true);
		readDataWithScore("wc/webchild_partof_physical.txt",0,1,2,3,4,7,true);
		
		writemaps();
		
		System.out.println(netcount + " entries in total.");
	}
	public void readDataWithScore(String place, int id1, int word1, int rs, int id2, int word2, int score, boolean header) throws IOException {
		int counter=0;
		
		Scanner scanner = new Scanner(new File(place));
		scanner.useDelimiter("\\r?\\n");

		if(header) {
		String headers = scanner.next();
		
		String[] headerarray = headers.split("\t");
		
		
			for(int i=0; i<headerarray.length;i+=1) {
				System.out.println(headerarray[i] + " - feature ID " + i);
			}
		}
		while(scanner.hasNext()) {
			counter +=1;
			String line = scanner.next();
			//System.out.println(line);
			String[] values = line.split("\t");
			
			// Write each CSV line:
			objmap.put(Integer.parseInt(values[id1]), values[word1]);
			if(!rsmap.containsKey(values[rs])) {
				rsmap.put(values[rs], rsmap.size()+1);
			}
			
			String output = values[id1] + "," + values[rs] + "," + values[id2] + "," + values[score] +"\n";
			bw.write(output);

			System.out.println("Parsed entry " + counter + ".");
		}
		bw.flush();
		size = counter;
		System.out.println("Size: " + size);
		netcount += size;	
		System.out.println("Data successfully parsed from " + place);
		scanner.close();
		
		
	}
	
	public void writemaps() throws IOException {
		// Write object IDs file
				file = new FileWriter("may/objectids2.csv");
				bw = new BufferedWriter(file);
				    	
				for(Map.Entry<Integer,String> a : objmap.entrySet()) {
					bw.write(a.getKey() + "," + a.getValue() + "\n");
				}
				bw.flush();
				bw.close();
				file.close();
						
				// Write RS IDs file
				file = new FileWriter("may/relationids2.csv");
				bw = new BufferedWriter(file);
				   	
				for(Map.Entry<String,Integer> a : rsmap.entrySet()) {
					bw.write(a.getKey() + "," + a.getValue() + "\n");
				}
				bw.flush();
				bw.close();
				    	
				System.out.println(objmap.size() + " objects found. " + netcount + " relationships saved.");
				System.out.println(rsmap.size() + " unique relations.");
				System.out.println("End of check.");
	}
	
	public static void main(String[] args) {
		try {
		new ReadCSVTwo("may/connections2.csv");
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
