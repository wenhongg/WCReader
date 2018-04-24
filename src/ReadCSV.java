/*
 * This class deals with the raw data from the webchild dataset: combined result is saved as output.txt
 * 
 * Further improvements:
 * Modify code such that only the directory containing the 6 necessary files need be supplied as an argument to main.
 * 
 * readknowly was originally the method to read knowlywood but it doesn't quite fit with the rest of the data and was left out.
 */
import java.util.Scanner;
import java.io.*;

public class ReadCSV {
	public String filedest;
	public int size;
	private FileWriter file;
	private BufferedWriter bw;
	public int netcount;
	// In the future, change tensor to take in shape. 
	public ReadCSV(String destination) throws IOException {
		file = new FileWriter(destination);
    	bw = new BufferedWriter(file);
		netcount = 0;

		readData("wc/comparative-cw0912.txt",1,3,6, true);
		readData("wc/property.txt",0,1,2, false);
		readDataWithScore("wc/webchild_partof_memberof.txt",1,2,4,true,7);
		readDataWithScore("wc/webchild_partof_substanceof.txt",1,2,4,true,7);
		readDataWithScore("wc/webchild_partof_physical.txt",1,2,4,true,7);
		readspatial("wc/spatial.txt", 0,2,1,true);
		//readknowly("wc/knowlywood.txt", 0, 1 , false);
		
		System.out.println(netcount + " entries in total.");
	}
	public void readknowly(String place, int first, int last, boolean header) throws IOException {
		//rs will all be dash
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
			counter+=1;
			String line = scanner.next();
			//System.out.println(line);
			String[] values = line.split("\t");
			
			// Write each CSV line:
			String output = values[first] + "," + " - " + "," + values[last] + "," + 1 + "\n";
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
	
	public void readDataWithScore(String place, int first, int rs, int last, boolean header, int score) throws IOException {
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
			counter+=1;
			String line = scanner.next();
			//System.out.println(line);
			String[] values = line.split("\t");
			
			// Write each CSV line:
			String output = values[first] + "," + values[rs] + "," + values[last] + "," + values[score] +"\n";
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
		new ReadCSV("output.csv");
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/*
	public void normalize() {
		for(int i=0; i<featurecount-1; i+=1) {
			for(int j=0; j<size; j+=1) {
				dataset[j][i]= dataset[j][i]/100;
			}
		}
		*/
	}
	
	
		
	
	
	

