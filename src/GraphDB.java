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
	Scanner scan1,scan2;
	Map<Integer,String> objmap,rsmap;
	Node[] nodes;
	Scanner scanner;
	String objectid, relationid, connection;
	int dataset;
	public GraphDB(int data) throws IOException {
		dataset =data;
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

		nodes = new Node[objmap.size()+1000]; // 1000 is arbitrary spare capacity to store additional nodes. 
		for(int i=0; i<nodes.length; i+=1) {
			nodes[i] = new Node(i);
		}
	}
	
	public List<Integer> processquery(List<String> list) {
		// This processquery is for the 3 way search. It will just return only 1 ID per search.
		List<String> queries = list;
		List<Integer> answers = new ArrayList<Integer>();
		if(dataset == 2) {
			for(Map.Entry<Integer,String> entry: objmap.entrySet()) {
				if(queries.contains(entry.getValue())) {
					queries.remove(entry.getValue());
					answers.add(entry.getKey());
				} 
			}
		} else {
			for(Map.Entry<Integer,String> entry: objmap.entrySet()) {
				String x = entry.getValue();
				String actual = x.split("#")[0];
				if(queries.contains(actual)) {
					queries.remove(actual);
					answers.add(entry.getKey());
				}
		}
		if(queries.size()!=0) {
			System.out.println("Query not found.");
			System.exit(0);
			}
		
		}
		return answers;
	}
	
	public List<Integer> process(List<String> queries) {
		List<Integer> answer = new ArrayList<Integer>();
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
	
	
	
	public Map<Integer,List<Integer>> processquery(String query1, String query2) {
		List<Integer> q1ids = new LinkedList<Integer>();
		List<Integer> q2ids = new LinkedList<Integer>();
		
		if(dataset == 2) {
			for(Map.Entry<Integer,String> entry: objmap.entrySet()) {
				if(query1.equals(entry.getValue())) {
					q1ids.add(entry.getKey());
				} 
				if(query2.equals(entry.getValue())) {
					q2ids.add(entry.getKey());
				} 
			}
			
		} else {
			for(Map.Entry<Integer,String> entry: objmap.entrySet()) {
				String x = entry.getValue();
				String actual = x.split("#")[0];
				if(query1.equals(actual)) {
					q1ids.add(entry.getKey());
				} 
				if(query2.equals(actual)) {
					q2ids.add(entry.getKey());
				} 
			}
		}
		
		if(q1ids.size() == 0 || q2ids.size()==0) {
			System.out.println("Query not found.");
			System.exit(0);
		}
		Map<Integer,List<Integer>> answer = new HashMap<Integer,List<Integer>>();
		answer.put(1, q1ids);
		answer.put(2, q2ids);
		return answer;	
		
		
		// Program will not run unless queries are checked to be valid.
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
			GraphDB graph = new GraphDB(1);
			if(graph.objmap.containsValue("")) {
				System.out.println("Yes");
			} else {
				System.out.println("No");
			}
			//graph.getconnections();
			//graph.graphstats();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
