/*
 * This class deals with the raw data from the webchild dataset:
 *  - property
 *  - spatial
 *  - comparative
 *  
 * Further improvements:
 * Modify code such that only the directory containing the 6 necessary files need be supplied as an argument to main.
 */
import java.util.Scanner;
import java.io.*;

public class ReadCSVOne {
	public String filedest;
	public int size;
	private FileWriter file;
	private BufferedWriter bw;
	public int netcount;
	// In the future, change tensor to take in shape. 
	public ReadCSVOne(String destination) throws IOException {
		file = new FileWriter(destination);
    	bw = new BufferedWriter(file);
		netcount = 0;

		readData("wc/comparative-cw0912.txt",1,3,6, true);
		readData("wc/property.txt",0,1,2, false);
		readspatial("wc/spatial.txt", 0,2,1,true);
		
		System.out.println(netcount + " entries in total.");
	}
	
	public void readspatial(String place, int first, int rs, int last, boolean header) throws IOException {
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
			String line = scanner.next();
			//System.out.println(line);
			String[] values = line.split("\t");
			
			// Write each CSV line:
			String[] deets = values[rs].split(",");
			for(String str: deets) {
				String[] str1 = str.split(" :");
				bw.write(values[first] + "," + str1[0] + "," + values[last] + "," + 1 + "\n");
				counter +=1;
			}

			System.out.println("Parsed entry " + counter + ".");
		}
		bw.flush();
		size = counter;
		System.out.println("Size: " + size);
		netcount += size;	
		System.out.println("Data successfully parsed from " + place);
		scanner.close();
	}
	public void readData(String place, int first, int rs, int last, boolean header) throws IOException {
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
		
		if(place.contains("comparative-cw0912.txt")) {
			scanner.next();
		}
		
		while(scanner.hasNext()) {
			counter+=1;
			String line = scanner.next();
			//System.out.println(line);
			String[] values = line.split("\t");
			
			// Write each CSV line:
			String output = values[first] + "," + values[rs] + "," + values[last] + "," + 1 +  "\n";
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
	
	

	public static void main(String[] args) {
		try {
		new ReadCSVOne("may/dataset1.csv");
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}