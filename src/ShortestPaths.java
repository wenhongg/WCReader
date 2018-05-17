/*
 * Djikstra's algorithm is implemented here to find the shortest possible path.
 * A priority queue is used to make the runtime the best possible.
 * 
 * WC files must have been processed by ReadCSV and Grapher.
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

public class ShortestPaths implements Comparator<Node>{
	Scanner scan1,scan2;
	Map<Integer,String> objmap,rsmap;
	Scanner scanner1;
	LinkedList<String> pathlist = new LinkedList<String>();
	PriorityQueue<Node> pq;

	String q1,q2;
	int q1id,q2id;
	GraphDB graph;
	public ShortestPaths(String query1, String query2, GraphDB graph1) throws IOException {
		// Below lines are standard format for checking the query and visualizing the graph.
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<Integer,String>();
		q1 = query1;
		q2 = query2;
		
		graph = graph1;
		getidmaps();
		processquery(query1,query2);
		//
		

		decode();
		System.out.println("ShortestPaths ended.");
	}
	
	public ShortestPaths(GraphDB graph1) throws IOException {
		// Below lines are standard format for checking the query and visualizing the graph.
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<Integer,String>();
		
		graph = graph1;
		getidmaps();
		System.out.println("Shortest path finder produced.");
		//obtainpaths();
		//
		//decode();
		//System.out.println("ShortestPaths ended.");
	}
	
	
	
	public void processquery(String query1, String query2) throws IOException {
		q1 = query1;
		q2 = query2;
		
		for(Map.Entry<Integer,String> entry: objmap.entrySet()) {
			if(query1.equals(entry.getValue())) {
				q1id = entry.getKey();
			} 
			if(query2.equals(entry.getValue())) {
				q2id = entry.getKey();
			} 
		}
			
		if(q1id == -1 || q2id == -1) {
			System.out.println("Query not found. Program terminating.");
			System.exit(0);
		}
	}
	
	public void getidmaps() throws IOException {
		objmap = graph.objmap;
		rsmap = graph.rsmap;
	} 
	
	public void obtainpaths(int one, int two) {
		q1id = one;
		q2id = two;
		
		obtainpaths();
	}
	
	public void obtainpaths() {
		// djikstra's implementation (with PQ)

		pq = new PriorityQueue<Node>(this);
		
		graph.nodes[q1id].visited = true;
		graph.nodes[q1id].dist = 0;
		
		pq.add(graph.nodes[q1id]);
		
		while(pq.size()!=0) {
			move();
		}
		if(graph.nodes[q2id].dist==Double.POSITIVE_INFINITY) {
			System.out.println("No paths. System exiting.");
			System.exit(0);
		}
		
		Node end  = graph.nodes[q2id]; 
		Stack<String> p = new Stack<String>();
		while(end.id!=q1id) {

			p.push(Double.toString(end.bestweight));
			p.push(Integer.toString(end.bestlink));
			p.push(Integer.toString(end.id));
			end = graph.nodes[end.bestparent];
		}
		String nodez = "";
		String link = "";
		String weight = "";
		nodez += q1id + ",";
		while(p.size()!=0) {
			nodez += p.pop() + ",";
			link += p.pop() + ",";
			weight += p.pop() + ",";
		}
		pathlist.add(nodez);
		pathlist.add(link);
		pathlist.add(weight);
		System.out.println("Shortest path is: " + graph.nodes[q2id].dist);
		
		System.out.println(q1id+","+q2id);
	}
	
	public void move() {
		
		Node x = pq.poll();
		if(x.equals(graph.nodes[q2id])) {
			System.out.println("End of search, shortest path found.");
			pq.clear();
			return;
		}
		System.out.println("Present distance is " + x.dist);
		for(String[] arr : x.relations ) {
			double newdist = Double.parseDouble(arr[2]) + x.dist;
			Node y = graph.nodes[Integer.parseInt(arr[0])];
			if(y.dist > newdist) {
				y.dist = newdist;
				y.bestparent = x.id;
				y.bestlink = Integer.parseInt(arr[1]);
				y.bestweight = Double.parseDouble(arr[2]);
				if(y.visited == false) {
					pq.add(y);
				}
			}
			
			
			
		}
		x.visited = true;
	
	}
	
	public Map<String, Object> decodem() {
		// Make JSON FILE: TO DO ON 15 MARCH 
		
		List<String> data = pathlist;
		Iterator<String> iter = data.iterator();
			String str = iter.next();
			String str1 = iter.next();
			String str2 = iter.next();
			String[] nodes = str.split(",");
			String[] nodes1 = str1.split(",");
			String[] nodes2 = str2.split(",");
			
			double score=0;
			for(String x : nodes2) {
				score +=Double.parseDouble(x);
			}
			
			String[][] path = new String[nodes1.length][5]; 
			
			for(int i=0; i<nodes1.length;i+=1) {
				String first = objmap.get(Integer.parseInt(nodes[i]));
				String second = objmap.get(Integer.parseInt(nodes[i+1]));
				
				if(Integer.parseInt(nodes1[i]) < 0) {
					String rs = rsmap.get(-Integer.parseInt(nodes1[i]));
					String[] link = {nodes[i+1] , second , rs, nodes[i] , first};
					path[i] = link;
				} else {
					String rs = rsmap.get(Integer.parseInt(nodes1[i]));
					String[] link = {nodes[i] , first , rs, nodes[i+1] , second};
					path[i] = link;
				}
			}
			String[] query = {Integer.toString(q1id), graph.objmap.get(q1id), Integer.toString(q2id), graph.objmap.get(q2id)}; 
			Map<String, Object> onepath = new HashMap<String,Object>();
			onepath.put("path", path);
			onepath.put("score", score);
			onepath.put("query", query);
			
			
		
		reset();
		return onepath;
	}
	
	public void decode() throws IOException {
		List<String> data = pathlist;
		Iterator<String> iter = data.iterator();
		while(iter.hasNext()) {
			String str = iter.next();
			String str1 = iter.next();
			String str2 = iter.next();
			String[] nodes = str.split(",");
			String[] nodes1 = str1.split(",");
			String[] nodes2 = str2.split(",");
			
			String sentence = "";
			for(int i=0; i<nodes1.length;i+=1) {
				String first = objmap.get(Integer.parseInt(nodes[i]));
				String second = objmap.get(Integer.parseInt(nodes[i+1]));
				String temp;
				
				if(Integer.parseInt(nodes1[i]) < 0) {
					temp = second +" "+ rsmap.get(-Integer.parseInt(nodes1[i])) +" "+first+", "; 
				} else {
					temp = first +" "+ rsmap.get(Integer.parseInt(nodes1[i])) +" "+second+", "; 
				}
				sentence += temp;
			}
			System.out.println(sentence);
			
		}
		pathlist.clear();
		reset();
	}
	
	public void reset() {
		for(Node x : graph.nodes) {
			x.dist = Double.POSITIVE_INFINITY;
			x.bestlink = -1;
			x.bestparent = -1;
			x.bestweight = -1;
			x.visited = false;
		}
	}
	
	public int compare(Node x, Node y) {
		return (int) Math.round(x.dist-y.dist);
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		try {
			GraphDB graph = new GraphDB(2);
			ShortestPaths findpath = new ShortestPaths("milk","cell", graph);
			graph.getconnections();
			findpath.obtainpaths();
		} catch(Exception e) {
			e.printStackTrace();
		}
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println(elapsedTime + " miliseconds elapsed.");
	}
	
}
