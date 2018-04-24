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

public class BFSPathfinder implements Comparator<Node>{
	Scanner scan1,scan2;
	Map<Integer,String> objmap,rsmap;
	Scanner scanner1;
	LinkedList<String> pathlist = new LinkedList<String>();
	PriorityQueue<Node> pq;

	String q1,q2;
	int q1id,q2id;
	GraphDB graph;
	public BFSPathfinder(String query1, String query2) throws IOException {
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<Integer,String>();
		q1 = query1;
		q2 = query2;
		
		getidmaps();
		processquery(query1,query2);
		graph = new GraphDB(109842);
		
		obtainpaths(1);

		decode();
		System.out.println("ShortestPaths ended.");
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
		scan1 = new Scanner(new File("objectids.csv"));
		scan1.useDelimiter("\\r?\\\n");
		while(scan1.hasNext()) {
			String str = scan1.next();
			String[] arr = str.split(",");
			objmap.put(Integer.parseInt(arr[1]), arr[0]);
		}
		System.out.println(objmap.size() + " objects.");
		
		scan2 = new Scanner(new File("relationids.csv"));
		scan2.useDelimiter("\\r?\\\n");
		while(scan2.hasNext()) {
			String str = scan2.next();
			String[] arr = str.split(",");
			rsmap.put(Integer.parseInt(arr[1]), arr[0]);
		}
		System.out.println(rsmap.size() + " unique relations.");
	} 
	
	public void obtainpaths(int count) {
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
			System.out.println("Passed Djikstra's shortest.");
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
	}
	
	public int compare(Node x, Node y) {
		return (int) Math.round(x.dist-y.dist);
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		try {
			new BFSPathfinder("president","deer");
		} catch(Exception e) {
			e.printStackTrace();
		}
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println(elapsedTime + " miliseconds elapsed.");
	}
	
}
