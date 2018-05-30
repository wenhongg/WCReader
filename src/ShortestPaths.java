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

import java.io.File;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class implements Djikstra's algorithm with the Priority queue to find the shortest possible path.
 * It is not intended to be used on its own as typically much noise exists in a large knowledge-base and it would be useful to get a number of varied paths instead.
 * As such, there is significant method overloading for various purposes (for use by other classes such as MixFinder).
 * However, it is still possible to use this class alone. Do note however that the single path produced by this traverser cannot be stored in json format. 
 * For that, YenShortestPaths is recommended.
 * 
 */
public class ShortestPaths implements Comparator<Node>{
	Scanner scan1,scan2;
	Map<Integer,String> objmap,rsmap;
	Scanner scanner1;
	LinkedList<String> pathlist = new LinkedList<String>();
	PriorityQueue<Node> pq;
	List<Integer> idslist;
	String q1,q2;
	int q1id,q2id;
	GraphDB graph;
	
	/**
	 * Constructs the traverser. Note this traverser produces a single path only.
	 * @param graph1 the graph to traverse
	 */
	public ShortestPaths(GraphDB graph1) {
		// Below lines are standard format for checking the query and visualizing the graph.
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<Integer,String>();
		graph = graph1;
		getidmaps();
		System.out.println("Shortest path finder produced.");
	}
	
	private void getidmaps() {
		objmap = graph.objmap;
		rsmap = graph.rsmap;
	} 
	
	void traverse(int one, int two) {
		System.out.println("Searching ID " + one + "," + two);
		q1id = one;
		q2id = two;
		
		traverse();
	}
	
	public void traverse(String query1, String query2) {
		q1 = query1;
		q2 = query2;
		Set<String> querylist = new HashSet<String>();
		querylist.add(query1);
		querylist.add(query2);
		idslist = graph.process(querylist);
		if(idslist.size()!= 2) {
			System.out.println("Something went wrong. Size is " + idslist.size());
			List<String> failresponse = new ArrayList<String>();
			failresponse.add("One or more query was not found.");
		}
		q1id = idslist.get(0);
		q2id = idslist.get(1);
		
		traverse();
		decode();
	}
	
	private void traverse() {
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
	
	private void move() {
		
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
	/**
	 * Saves the path details in a map. This is meant for use by the MixFinder class. All variables are then reset.
	 * @return map of path details
	 */
	Map<String, Object> decodem() {
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
			
			
		
		graph.reset();
		reset();
		pathlist.clear();
		return onepath;
	}
	/**
	 * Prints result for display in console. All variables are then reset.
	 */
	public void decode() {
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
	
	private void reset() {
		graph.reset();
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
	/**
	 * Supply the query to main method as argument in the format Dataset-Query1-Query2 (e.g. 1-blue deer-green cat).
	 * Else program finds relationship between cat and dog.
	 * @param args provide dataset + 2 queries separated by a single dash (-) with no spaces.
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		try {
			if(args.length==0) {
				// This runs if no argument is provided.
				GraphDB graph = new GraphDB(1);
				ShortestPaths pathfinding = new ShortestPaths(graph);
				graph.getconnections();
				pathfinding.traverse("cat","dog");
				System.out.println("Traversals ended.");
			} else {
				// If an argument is provided in the correct format.
				String[] x = args.toString().split("-");
				if(x.length==3) {
					GraphDB graph = new GraphDB(Integer.parseInt(args[0]));
					ShortestPaths pathfinding = new ShortestPaths(graph);
					graph.getconnections();
					pathfinding.traverse(args[1], args[2]);
					System.out.println("Traversals ended");
				} else {
					// Argument is provided, but in the wrong format.
					System.out.println("Provide dataset + 2 queries as argument to main method, separate them with a - (without spaces)");
					System.out.println("For instance: 1-food additive-candy floss");
				}
			} 
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println(elapsedTime + " miliseconds elapsed.");
		}
	}
	
}
