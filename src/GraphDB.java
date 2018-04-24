/* 
 * GraphDB class reads the file connections.csv and produces the entire graph. 
 * GraphDB is called upon by other classes such as Pathfinder, ShortestPaths and YenShortestPaths 
 */

import java.util.List;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

// In the full graph, 109841 objects are expected.
public class GraphDB {
	Node[] nodes;
	Scanner scanner;
	public GraphDB(int nodes1) throws FileNotFoundException {
		System.out.println("Producing graph.");
		nodes = new Node[nodes1];
		
		for(int i=0; i<nodes1; i+=1) {
			nodes[i] = new Node(i);
		}
		
		scanner = new Scanner(new File("connectionstrunc.csv"));
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
		System.out.println("Graph successfully created.");
		scanner.close();
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
