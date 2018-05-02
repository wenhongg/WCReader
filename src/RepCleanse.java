import java.util.Scanner;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;

public class RepCleanse {
	Scanner scanner;
	private FileWriter file;
	private BufferedWriter bw;
	public ArrayList<List<String[]>> list;
	public RepCleanse(String x) throws IOException {
		list = new ArrayList<List<String[]>>();
		for(int i=0;i<109842;i+=1) {
			list.add(new LinkedList<String[]>());
		}
		
		scanner = new Scanner(new File(x));
		scanner.useDelimiter("\\r?\\\n");
		int countx =0;
		int selfcount = 0;
		
		file = new FileWriter("selfconnections2.csv");
    	bw = new BufferedWriter(file);
    	int failcount = 0;
		while(scanner.hasNext()) {
			countx +=1;
			System.out.println("Parsing object " + countx);
			String str = scanner.next();
			String[] arr = str.split(",");
			
			if(arr[0].equals(arr[1])) {
				selfcount +=1;
				String stri = arr[0]+","+arr[1]+","+arr[2]+","+arr[3]+"\n";
				bw.write(stri);
				continue;
			}
			
			
			String[] first = {arr[1],arr[2],Double.toString((1/Double.parseDouble(arr[3])))};
			String reverse = "-" + arr[2];
			String[] second = {arr[0],reverse,Double.toString((1/Double.parseDouble(arr[3])))};
			
			boolean firstrepeat = false;
			boolean secondrepeat = false;
			for(String[] a : list.get(Integer.parseInt(arr[0]))) {
				if(Arrays.equals(a, first)) {
					firstrepeat = true;
				}
			}
			for(String[] a : list.get(Integer.parseInt(arr[1]))) {
				if(Arrays.equals(a, second)) {
					secondrepeat = true;
				}
			}
			if(secondrepeat == false && firstrepeat == false) {
				list.get(Integer.parseInt(arr[1])).add(second);
				list.get(Integer.parseInt(arr[0])).add(first);
			} else {
				failcount +=1;
				continue;
			}
		}
		bw.flush();
		bw.close();
		file.close();
		
		file = new FileWriter("cleanconnections2.csv");
    	bw = new BufferedWriter(file);
    	
    	for(int i=0;i<list.size();i+=1) {
    		for(String[] arr : list.get(i)) {
    			bw.write(i+","+arr[0]+","+arr[1]+","+arr[2]+"\n");
    		}
    	}
		bw.flush();
		bw.close();
		file.close();
		System.out.println("Cleansed. Fail count was " + failcount);
	}
	public static void main(String[] args) {
		try {
			new RepCleanse("may/connections2.csv");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}