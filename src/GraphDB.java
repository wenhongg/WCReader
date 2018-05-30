/**
 *  Produced April-May of 2018. 
 *  
 *  This set of graph & traversals API was created for traversing the WebChild knowledge-base, but can be applied to various graph-structure databases.
 *  
 *  
 *  This is code produced by a self taught programmer who has yet to matriculate in university.
 *  Therefore if there were things I could have done better or techniques I could have used, please let me know, thank you.
 *  
 *  In each class exists a main method, which gives an example of how the class can be used.
 *  @author LAM WEN HONG 
 */

import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;

/**
 * This class reads 3 documents: the object map, the relationship map and the edge list.
 * Do look through documentation.txt for details on the format.
 * GraphDB generates the entire graph in the RAM, and is called upon by other classes for Graph traversal.
 * 
 * 
 * 
 * @author LAM WEN HONG!
 *
 */

public class GraphDB implements Comparator<Node> {
	List<Integer> answer;
	Scanner scan1,scan2;
	Map<Integer,String> objmap,rsmap;
	Node[] nodes;
	Scanner scanner;
	String objectid, relationid, connection;
	int dataset,selfcount;
	
	
	/**
	 * GraphDB constructor does not load the edgelist: it only reads and produces the object map and the graph map.
	 * @param the graph dataset to be loaded.
	 */
	public GraphDB(int data) {
		selfcount = 0;
		dataset =data;
		answer = new ArrayList<Integer>();
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<Integer,String>();
		System.out.println("Producing graph.");
		
		connection = "datasets/connections"+ data +".csv";
		relationid = "datasets/relationids"+ data +".csv";
		objectid = "datasets/objectids" + data + ".csv";
		
		try {
			getidmaps();
		} catch(IOException e) {
			System.out.println("Error reading from object map OR relationship map.");
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * Reads the object map and the relationship map.
	 * @throws IOException if the maps do not exist.
	 */
	private void getidmaps() throws IOException {
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

		nodes = new Node[objmap.size()+1000]; // 1000 is arbitrary spare capacity to store additional nodes. 
		for(int i=0; i<nodes.length; i+=1) {
			nodes[i] = new Node(i);
		}
	}
	
	/**
	 * 
	 * This method removes the artificial links which were generated earlier to accomodate ambiguity.
	 * Removes the links of ID 0. 
	 * 
	 */
	public void reset() {
		
		List<Integer> delete = new ArrayList<Integer>();
		List<String[]> toremove = new ArrayList<String[]>();
		for(String[] x : nodes[answer.get(0)].relations) {
			if(x[1].equals("0")) {
				delete.add(Integer.parseInt(x[0]));
				toremove.add(x);
			}
		}
		nodes[answer.get(0)].relations.removeAll(toremove);
		toremove.clear();
		for(int x: delete) {
			String[] temp = null;
			for(String[] str : nodes[x].relations) {
				if(str[1].equals("0")) {
					temp = str;
					break;
				}
			}
			nodes[x].relations.remove(temp);
		}
		delete.clear();
		// Repeat for second node
		for(String[] x : nodes[answer.get(1)].relations) {
			if(x[1].equals("0")) {
				delete.add(Integer.parseInt(x[0]));
				toremove.add(x);
			}
		}
		nodes[answer.get(1)].relations.removeAll(toremove);
		toremove.clear();
		for(int x: delete) {
			String[] temp = null;
			for(String[] str : nodes[x].relations) {
				if(str[1].equals("0")) {
					temp = str;
					break;
				}
			}
			nodes[x].relations.remove(temp);
		}
		delete.clear();
		answer.clear();
	}
	
	/**
	 * This method produces additional links to possible subsets of each query, if query provided is ambiguous.
	 * @param queries the set of words to be queried.
	 * @return list of IDs of the queries.
	 */
	// Note to self: inclusion of contextual knowledge can be an issue. fix asap 
	// TODO if query string is 2 words, check using .contains . OR other method. idk
	public List<Integer> process(Set<String> queries) {
		
		rsmap.put(0, "can be");
		for(String query : queries) {
			List<Integer> related = new ArrayList<Integer>();
			boolean mainqueryexists = false;
			int quer = -1;
			for(Map.Entry<Integer,String> entry: objmap.entrySet()) {
				if(query.equals(entry.getValue()) && !mainqueryexists) {
					// Check if node exists.
					answer.add(entry.getKey());
					mainqueryexists = true;
					quer = entry.getKey();
					continue;
				} 
				boolean exist = false;
				// This is the method of selection of related words: can change this as required.
				String[] tokens = entry.getValue().split(" ");
				for(String x : tokens) {
					if(x.equals(query)) {
						exist = true;
					} 
				}
				if(exist) {
					related.add(entry.getKey());
				}
			}
			if(!mainqueryexists && related.size()!=0) {
				// Create node
				objmap.put(objmap.size()+1 , query);
				quer = objmap.size();
				answer.add(quer);
			}
			for(int x : related) {
				// Create the branching links.
				String[] strx = { Integer.toString(x) , "0", "0"};
				nodes[quer].relations.add(strx);
				String[] stry = { Integer.toString(quer) , "0" , "0" };
				nodes[x].relations.add(stry);
			}
		}
		return answer;
	}
	
	/**
	 * This method reads the edgelist and produces all the links between the nodes. 
	 * @throws IOException if edgelist is not found or problem reading edgelist.
	 */
	public void getconnections() throws IOException {
		scanner = new Scanner(new File(connection));
		scanner.useDelimiter("\\r?\\\n");
		int count =0;
		while(scanner.hasNext()) {
			String str = scanner.next();
			String[] arr = str.split(",");
			
			if(arr[0].equals(arr[1])) {
				selfcount +=1;
				continue;
			}
			
			int a = Integer.parseInt(arr[0]);
				

			int b = Integer.parseInt(arr[1]);
			/*if(a>109841 || b>109841) {
				System.out.println("Error caught.");
			}*/
			String[] first = {arr[1],arr[2],Double.toString((1/Double.parseDouble(arr[3])))};
			String reverse = "-" + arr[2];
			String[] second = {arr[0],reverse,Double.toString((1/Double.parseDouble(arr[3])))};

			nodes[a].relations.add(first);
			nodes[b].relations.add(second);
			count +=1;
			System.out.println(count + " relationships parsed.");
			
		
		}
		
		
	}
	
	public int compare(Node x, Node y) {
		return (int) Math.round(y.relations.size()-x.relations.size());
	}
	/**
	 * This method gives some insight into the graph-data being produced. Run only after .getconnections 
	 * 
	 */
	public void graphstats() {
		// Created this method just to get a feel of what the graph is like
		// Returns average connections in all the graph's nodes and the maximum&minimum number of connections to any node.
		PriorityQueue<Node> pq = new PriorityQueue<Node>(this);
		double count = 0;
		long max = 0;
		long min = nodes.length;
		for(Node a : nodes) {
			pq.add(a);
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
		count = 0;
		System.out.println("10 most connected objects:");
		while(count<10) {
			count +=1;
			Node x = pq.poll();
			System.out.println(objmap.get(x.id) + " has this number of connections: " + x.relations.size());
			
		}
	}
	
	public static void main(String[] args) {
		try {
			// Run this main method to get a sense of the graph data.
			// Two lines first load the 3 data files required and .graphstats produce in console useful information of the graph.
			GraphDB graph = new GraphDB(1);
			graph.getconnections();
			graph.graphstats();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
