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
		q1 = query1;
		q2 = query2;
		

		graph = new GraphDB(2);
		getidmaps();
		processquery(query1,query2);
		graph.getconnections();
		
		handler(10);
		decode();
		
		System.out.println("YenShortestPaths ended.");
	}
	
	public void getidmaps() {
		rsmap = graph.rsmap;
		objmap = graph.objmap;
	}
	
	public void handler(int k) throws IOException {
		int[] not = new int[0];
		if(container.size()==0) {
			obtainpaths(q1id,q2id, not);
			reset();
			if(pathlist.size()==0) {
				System.out.println("No paths at all.");
				System.exit(0);
			}
			write();

			
		}
		for(int i=0; i<k-1; i+=1) {
			System.out.println(container.get(container.size()-3));
			//System.out.println(pathlist.size()/3 + " routes in pathlist and " + container.size()/3 + " routes in container.");
			String[] arr = container.get(container.size()-3).split(",");
			String[] rsid = container.get(container.size()-2).split(",");
			String[] weight = container.get(container.size()-1).split(",");
			
			for(int j=0; j<arr.length-1; j+=1) {
				not = new int[j+1];
				for(int m=0; m<=j; m+=1) {
					graph.nodes[Integer.parseInt(arr[m])].visited = true;
					not[m] = graph.nodes[Integer.parseInt(arr[m])].id;
					if(m!=0) {
						System.out.println("Returning node " + Integer.parseInt(arr[m]));
						graph.nodes[Integer.parseInt(arr[m])].bestparent = Integer.parseInt(arr[m-1]);
						System.out.println(m + "< order - " + graph.nodes[Integer.parseInt(arr[m])].bestparent);
						graph.nodes[Integer.parseInt(arr[m])].bestlink = Integer.parseInt(rsid[m-1]);
						graph.nodes[Integer.parseInt(arr[m])].bestweight = Double.parseDouble(weight[m-1]);
					} 
				}
				// Temporarily remove a certain link
				for(int a=0; a< graph.nodes[Integer.parseInt(arr[j])].relations.size(); a+=1) {
					if(graph.nodes[Integer.parseInt(arr[j])].relations.get(a)[0].equals(arr[j+1].toString())) {
						temp1 = graph.nodes[Integer.parseInt(arr[j])].relations.remove(a);
						//System.out.println("Link removed");
						break;
					}
				}
				
				for(int a=0; a< graph.nodes[Integer.parseInt(arr[j+1])].relations.size(); a+=1) {
					if(graph.nodes[Integer.parseInt(arr[j+1])].relations.get(a)[0].equals(arr[j].toString())) {
						temp2 = graph.nodes[Integer.parseInt(arr[j+1])].relations.remove(a);
						//System.out.println("Link removed");
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
				System.out.println(Integer.parseInt(arr[j]) + " node to be spur.");
				obtainpaths(Integer.parseInt(arr[j]),q2id, not);
				
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
		System.out.println("Containing next path.");
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
	
	
	
	public void obtainpaths(int id1,int id2, int[] not) {
		// djikstra's implementation (with PQ)

		pq = new PriorityQueue<Node>(this);
		
		graph.nodes[id1].visited = true;
		graph.nodes[id1].dist = 0;
		
		pq.add(graph.nodes[id1]);
		
		while(pq.size()!=0) {
			move(not);
		}
		if(graph.nodes[id2].dist==Double.POSITIVE_INFINITY) {
			System.out.println("No paths. Exiting Djikstra.");
			return;
		}
		
		Node end  = graph.nodes[id2]; 
		Stack<String> p = new Stack<String>();
		while(end.id!=q1id) {
			System.out.println(end.id + " node being processed. Parent is " + end.dist);
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
		if(pathlist.contains(nodez) || container.contains(nodez)) {
			System.out.println("Repeated path, not indexing.");
			return;
		}
		pathlist.add(nodez);
		pathlist.add(link);
		pathlist.add(weight);
		//System.out.println("Shortest path is: " + graph.nodes[id2].dist);
		
		System.out.println(id1+","+id2 +" is path identified.");
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
	
	public void move(int[] not) {
		
		Node x = pq.poll();
		if(x.equals(graph.nodes[q2id])) {
			System.out.println("End of search, shortest path found.");
			pq.clear();
			return;
		}
		//System.out.println("Present distance is " + x.dist);
		for(String[] arr : x.relations ) {
			double newdist = Double.parseDouble(arr[2]) + x.dist;
			Node y = graph.nodes[Integer.parseInt(arr[0])];
			boolean touch = true;
			for(int id : not) {
				if(y.id == id) {
					touch = false;
				}
			}
			if(y.dist > newdist && touch) {
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
			YenShortestPaths x = new YenShortestPaths("fat","guy");
		} catch(Exception e) {
			e.printStackTrace();
		} 
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println(elapsedTime + " miliseconds elapsed.");
	}
	
}
