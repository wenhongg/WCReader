/* 
 * GraphDB class reads the file connections.csv and produces the entire graph. 
 * GraphDB class is used for SET 2
 * GraphDB is called upon by other classes such as Pathfinder, ShortestPaths and YenShortestPaths 
 */

import java.util.List;
import java.util.Map;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
	
// In the full graph, 109841 objects are expected.
public class GraphDB {
	Scanner scan1,scan2;
	Map<Integer,String> objmap,rsmap;
	Node[] nodes;
	Scanner scanner;
	String objectid, relationid, connection;
	public GraphDB(int data) throws IOException {
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<Integer,String>();
		System.out.println("Producing graph.");
		
		
		
		if(data == 1) {
			connection = "datasets/connections1.csv";
			relationid = "datasets/relationids1.csv";
			objectid = "datasets/objectids1.csv";
			
		} else if(data == 2) {
			connection = "datasets/connections2.csv";
			relationid = "datasets/relationids2.csv";
			objectid = "datasets/objectids2.csv";
		} else {
			System.out.println("Dataset chosen must be either 1 or 2.");
			System.exit(0);
		}
		getidmaps();
		
		
	}
	
	public void getidmaps() throws IOException {
		scan1 = new Scanner(new File(objectid));
		scan1.useDelimiter("\\r?\\\n");
		while(scan1.hasNext()) {
			String str = scan1.next();
			String[] arr = str.split(",");
			objmap.put(Integer.parseInt(arr[1]), arr[0]);
		}
		System.out.println(objmap.size() + " objects.");
		
		scan2 = new Scanner(new File(relationid));
		scan2.useDelimiter("\\r?\\\n");
		while(scan2.hasNext()) {
			String str = scan2.next();
			String[] arr = str.split(",");
			rsmap.put(Integer.parseInt(arr[1]), arr[0]);
		}
		System.out.println(rsmap.size() + " unique relations.");

		nodes = new Node[objmap.size()+1];
		for(int i=0; i<objmap.size()+1; i+=1) {
			nodes[i] = new Node(i);
		}
	}
	
	public void getconnections() throws IOException {
		scanner = new Scanner(new File(connection));
		scanner.useDelimiter("\\r?\\\n");
		int count =0;
		int selfcount = 0;
		while(scanner.hasNext()) {
			String str = scanner.next();
			String[] arr = str.split(",");
			
			if(arr[0].equals(arr[1])) {
				selfcount +=1;
				continue;
			}
			
			int a = Integer.parseInt(arr[0]);
				

			int b = Integer.parseInt(arr[1]);
			if(a>109841 || b>109841) {
				System.out.println("Error caught.");
			}
			String[] first = {arr[1],arr[2],Double.toString((1/Double.parseDouble(arr[3])))};
			String reverse = "-" + arr[2];
			String[] second = {arr[0],reverse,Double.toString((1/Double.parseDouble(arr[3])))};

			nodes[a].relations.add(first);
			nodes[b].relations.add(second);
			count +=1;
			System.out.println(count + "relationship parsed.");
		
		}
	}
	
	public void graphstats() {
		// Created this method just to get a feel of what the graph is like
		// Returns average connections in all the graph's nodes and the maximum&minimum number of connections to any node.
		double count = 0;
		long max = 0;
		long min = nodes.length;
		for(Node a : nodes) {
			if(a.relations.size() ==0) {
				continue;
			}
			count += a.relations.size();
			if(a.relations.size() > max ) {
				max = a.relations.size();
			} else if(a.relations.size() < min) {
				min  = a.relations.size();
			}
			
		}
		System.out.println("Average connections: " + count/nodes.length);
		System.out.println("Max connections: " + max);
		System.out.println("Min connections: " + min);
	}
	
	
	
	
	
}
