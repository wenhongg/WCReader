/* 
 * GraphDB class reads the file connections.csv and produces the entire graph. 
 * GraphDB class is used for SET 2
 * GraphDB is called upon by other classes such as Pathfinder, ShortestPaths and YenShortestPaths 
 */
import java.util.PriorityQueue;
import java.util.Comparator;

import java.util.List;


import java.util.Map;
import java.io.*;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
	
// In the full graph, 109841 objects are expected.
public class GraphDB implements Comparator<Node> {
	List<Integer> answer;
	Scanner scan1,scan2;
	Map<Integer,String> objmap,rsmap;
	Node[] nodes;
	Scanner scanner;
	String objectid, relationid, connection;
	int dataset,selfcount;
	public GraphDB(int data) throws IOException {
		selfcount = 0;
		dataset =data;
		answer = new ArrayList<Integer>();
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<Integer,String>();
		System.out.println("Producing graph.");
		
		
		
		if(data == 1) {
			connection = "datasets/connections1.csv";
			relationid = "datasets/relationids1.csv";
			objectid = "datasets/objectids1.csv";
			
		} else if(data == 2) {
			connection = "datasets/modconnections2.csv";
			relationid = "datasets/relationids2.csv";
			objectid = "datasets/modobjectids2.csv";
		} else {
			System.out.println("Dataset chosen must be either 1 or 2.");
			System.exit(0);
		}
		getidmaps();
		
		
	}
	
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
	
	public void reset() {
		// remove 0 weighted nodes
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
		while(count<500) {
			count +=1;
			Node x = pq.poll();
			System.out.println(objmap.get(x.id) + " - " + x.relations.size());
			
		}
	}
	
	public static void main(String[] args) {
		try {
			// Run this as main to get a feel
			GraphDB graph = new GraphDB(1);
			graph.getconnections();
			graph.graphstats();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
