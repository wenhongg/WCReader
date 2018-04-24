/*
 * Yen's algorithm is implemented here. This algorithm returns the k-shortest paths. 
 * Djikstra's is repeatedly used.
 * 
 * WC files must have been processed by ReadCSV and Grapher.
 * 
 * There's still a bug somewhere ...
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

public class YenShortestPaths implements Comparator<Node>{
	Scanner scan1,scan2;
	Map<Integer,String> objmap,rsmap;
	Scanner scanner1;
	LinkedList<String> pathlist = new LinkedList<String>();
	PriorityQueue<Node> pq;

	String[] temp1,temp2;
	
	List<String> answers;
	List<String> container;
	String q1,q2;
	int q1id,q2id;
	GraphDB graph;
	public YenShortestPaths(String query1, String query2) throws IOException, InterruptedException {
		answers = new LinkedList<String>();
		container = new LinkedList<String>();
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<Integer,String>();
		q1 = query1;
		q2 = query2;
		
		getidmaps();
		processquery(query1,query2);
		graph = new GraphDB(109842);
		
		handler(10);
		decode();
		
		System.out.println("YenShortestPaths ended.");
	}
	
	public void handler(int k) throws IOException {
		if(container.size()==0) {
			obtainpaths();
			if(pathlist.size()==0) {
				System.out.println("No paths at all.");
				System.exit(0);
			}
			write();
		}
		for(int i=0; i<k-1; i+=1) {
			String[] arr = container.get(container.size()-3).split(",");

			for(int j=0; j<arr.length-1; j+=1) {
				// Temporarily remove a certain link
				for(int a=0; a< graph.nodes[Integer.parseInt(arr[j])].relations.size(); a+=1) {
					
					if(graph.nodes[Integer.parseInt(arr[j])].relations.get(a)[0].equals(arr[j+1].toString())) {
						temp1 = graph.nodes[Integer.parseInt(arr[j])].relations.remove(a);
						System.out.println("Link removed");
						break;
					}
				}
				
				for(int a=0; a< graph.nodes[Integer.parseInt(arr[j+1])].relations.size(); a+=1) {
					if(graph.nodes[Integer.parseInt(arr[j+1])].relations.get(a)[0].equals(arr[j].toString())) {
						temp2 = graph.nodes[Integer.parseInt(arr[j+1])].relations.remove(a);
						System.out.println("Link removed");
						break;
					}
				}
				System.out.println(temp1 + " saved");
				System.out.println(temp2 + " saved");
				if(temp1 == null || temp2==null) {
					System.out.println("Link not found. System exiting.");
					System.exit(0);
				}
				// Do djikstras on graph with missing link
				obtainpaths();
				reset();
				
				// Add the link back
				
				graph.nodes[Integer.parseInt(arr[j])].relations.add(temp1);
				graph.nodes[Integer.parseInt(arr[j+1])].relations.add(temp2);
				temp1 = null;
				temp2 = null;
			}
			if(pathlist.size()==0) {
				System.out.println("Possible paths less than requested number of paths.");
				return;
			}
			// Write best path out from pathlist to container.
			write();
		}
	}
	
	public void write() {
		// Writes the best route to container.
		double minscore= Double.POSITIVE_INFINITY;
		int index = -1;
		for(int a=2; a<pathlist.size(); a+=3) {
			String[] x = pathlist.get(a).split(",");
			double score = 0;
			for(String str : x) {
				score += Double.parseDouble(str);
			}
			if(minscore> score) {
				minscore = score;
				index = a-2;
			}
		}
		container.add(pathlist.remove(index));
		container.add(pathlist.remove(index));
		container.add(pathlist.remove(index));
	}
	
	public void processquery(String query1, String query2) throws IOException, InterruptedException {
		q1id=-1;
		q2id =-1;
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
		System.out.println("Query 1 ID: " + q1id);
		System.out.println("Query 2 ID: " + q2id);
		// Program will not run unless queries are checked to be valid.
	}
	
	public void getidmaps() throws IOException {
		scan1 = new Scanner(new File("objectidstrunc.csv"));
		scan1.useDelimiter("\\r?\\\n");
		while(scan1.hasNext()) {
			String str = scan1.next();
			String[] arr = str.split(",");
			objmap.put(Integer.parseInt(arr[1]), arr[0]);
		}
		System.out.println(objmap.size() + " objects.");
		
		scan2 = new Scanner(new File("relationidstrunc.csv"));
		scan2.useDelimiter("\\r?\\\n");
		while(scan2.hasNext()) {
			String str = scan2.next();
			String[] arr = str.split(",");
			rsmap.put(Integer.parseInt(arr[1]), arr[0]);
		}
		System.out.println(rsmap.size() + " unique relations.");
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
			System.out.println("No paths. Exiting Djikstra.");
			return;
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
	
	public void reset() {
		for(Node x : graph.nodes) {
			x.dist = Double.POSITIVE_INFINITY;
			x.bestlink = -1;
			x.bestparent = -1;
			x.bestweight = -1;
			x.visited = false;
		}
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
	
	public void decode() throws IOException {
		List<String> data = container;
		Iterator<String> iter = data.iterator();
		while(iter.hasNext()) {
			String str = iter.next();
			String str1 = iter.next();
			String str2 = iter.next();
			String[] nodes = str.split(",");
			String[] nodes1 = str1.split(",");
			String[] nodes2 = str2.split(",");
			
			double a=0;
			for(String x : nodes2) {
				a +=Double.parseDouble(x);
			}
			
			String sentence = Double.toString(a) + " - ";
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
			answers.add(sentence);
		}
	}
	
	public int compare(Node x, Node y) {
		return (int) Math.round(x.dist-y.dist);
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		try {
			new YenShortestPaths("Rattus","Spyeria");
		} catch(Exception e) {
			e.printStackTrace();
		}
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println(elapsedTime + " miliseconds elapsed.");
	}
	
}
