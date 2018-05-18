/*
 * Yen's algorithm is implemented here. This algorithm returns the k-shortest paths. 

 * Djikstra's is repeatedly used.
 * 
 * WC files must have been processed by ReadCSV and Grapher.
 * 
 * There's still a bug somewhere ...
 * 
 */

import java.io.BufferedWriter;


import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.*;

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
	int q1id,q2id, countx;
	
	List<Integer> idslist;
	GraphDB graph;
	
	public YenShortestPaths(GraphDB graph1, int count) {
		
		countx = count;
		answers = new LinkedList<String>();
		container = new LinkedList<String>();
		
		
		graph = graph1;
		getidmaps();
		
		
		
		//handler(count);
		
		
		System.out.println("YenShortestPaths ended.");
	}
	
	public List<String> findpaths(String query1, String query2){
		List<String> querylist = new ArrayList<String>();
		querylist.add(query1);
		querylist.add(query2);
		idslist = graph.process(querylist);
		if(idslist.size()!= 2) {
			System.out.println("Something went wrong. Size is " + idslist.size());
			List<String> failresponse = new ArrayList<String>();
			failresponse.add("One or more query was not found.");
			return failresponse;
		}
		q1id = idslist.get(0);
		q2id = idslist.get(1);
		handler(countx);
		List<String> answer = decode();
		return answer;
		/*
		for(int x : oneids) {
			for(int y : twoids) {
				q1id = x;
				q2id = y;
				handler(countx);
				System.out.println("Beginning cycle");
				permutations.add(makejson());
				System.out.println("Set complete.");
				pathlist.clear();
				container.clear();
			}
		}
		
		Map<String, Object> finalanswer = new HashMap<String,Object>();
		finalanswer.put("permutations", permutations);
		String[] query = {q1,q2};
		finalanswer.put("query", query);
		String dest = q1+ "_" +q2 +".json";
		System.out.println("Writing to json.");
		Gson gson = new GsonBuilder().create();
		FileWriter file = new FileWriter(dest);
	    BufferedWriter bw = new BufferedWriter(file);
	    bw.write(gson.toJson(finalanswer));
		bw.flush();
		bw.close();
		*/
	}
	
	public void masterreset() {
		pathlist.clear();
		container.clear();
		reset();
		System.out.println("Master reset.");
		int q1id = -1;
		int q2id = -1;
	}
	
	public void getidmaps() {
		rsmap = graph.rsmap;
		objmap = graph.objmap;
	}
	
	public void handler(int k) {
		int[] not = new int[0];
		if(container.size()==0) {
			
			obtainpaths(q1id,q2id, not);
			reset();
			if(pathlist.size()==0) {
				System.out.println("No paths at all.");
				return;
			}
			write();
		}
		for(int i=0; i<k-1; i+=1) {
			
			//System.out.println(container.get(container.size()-3));
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
						//System.out.println("Returning node " + Integer.parseInt(arr[m]));
						graph.nodes[Integer.parseInt(arr[m])].bestparent = Integer.parseInt(arr[m-1]);
						System.out.println(arr[m-1] + " made best parent of " + arr[m]);
						graph.nodes[Integer.parseInt(arr[m])].bestlink = Integer.parseInt(rsid[m-1]);
						graph.nodes[Integer.parseInt(arr[m])].bestweight = Double.parseDouble(weight[m-1]);
					} 
				}
				
				
				// Checking for additional links to cut: ALKDJFHALFDHALKDSJFLAK
				
				String spur = arr[j];
				for(int b=0;b<container.size();b+=3) {
					String[] arrx = container.get(b).split(",");
					
					for(int c=0;c<arrx.length;c+=1) {
						if(arrx[c].equals(spur)) {
							for(int a=0; a< graph.nodes[Integer.parseInt(arrx[c])].relations.size(); a+=1) {
								System.out.println("Scanning for link to remove" + a);
								if(graph.nodes[Integer.parseInt(arrx[c])].relations.get(a)[0].equals(arrx[c+1].toString())) {
									graph.nodes[Integer.parseInt(arr[c])].removed.add(graph.nodes[Integer.parseInt(arrx[c])].relations.remove(a));
									System.out.println("Link removed");
									break;
								}
							}
							
							for(int a=0; a< graph.nodes[Integer.parseInt(arrx[c+1])].relations.size(); a+=1) {
								System.out.println("Scanning for link to remove" + a);
								if(graph.nodes[Integer.parseInt(arrx[c+1])].relations.get(a)[0].equals(arrx[c].toString())) {
									graph.nodes[Integer.parseInt(arrx[c+1])].removed.add(graph.nodes[Integer.parseInt(arrx[c+1])].relations.remove(a));
									//System.out.println("Link removed");
									break;
								}
							}
						}
					}
				}
				
				/*
				for(int a=0; a< graph.nodes[Integer.parseInt(arr[j])].relations.size(); a+=1) {
					if(graph.nodes[Integer.parseInt(arr[j])].relations.get(a)[0].equals(arr[j+1].toString())) {
						graph.nodes[Integer.parseInt(arr[j])].removed.add(graph.nodes[Integer.parseInt(arr[j])].relations.remove(a));
						//System.out.println("Link removed");
						break;
					}
				}
				
				for(int a=0; a< graph.nodes[Integer.parseInt(arr[j+1])].relations.size(); a+=1) {
					if(graph.nodes[Integer.parseInt(arr[j+1])].relations.get(a)[0].equals(arr[j].toString())) {
						graph.nodes[Integer.parseInt(arr[j+1])].removed.add(graph.nodes[Integer.parseInt(arr[j+1])].relations.remove(a));
						//System.out.println("Link removed");
						break;
					}
				}
				*/
				// Do djikstras on graph with missing link
				System.out.println(Integer.parseInt(arr[j]) + " node to be spur.");
				obtainpaths(Integer.parseInt(arr[j]),q2id, not);
				
				reset();
				
				// Add links back
				returnlinks();
			}
			if(pathlist.size()==0) {
				System.out.println("Possible paths less than requested number of paths.");
				return;
			}
			// Write best path out from pathlist to container.
			write();
		}
	}
	
	public void returnlinks() {
		for(Node x : graph.nodes) {
			while(x.removed.size()!=0) {
				x.relations.add(x.removed.remove(0));
			}
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
		System.out.println("Containing next path: " + pathlist.get(index));
		container.add(pathlist.remove(index));
		container.add(pathlist.remove(index));
		container.add(pathlist.remove(index));
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
			//System.out.println(end.id + " node being processed. Parent is " + end.dist);
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
		/*
		if(pathlist.contains(nodez) || container.contains(nodez)) {
			
			System.out.println("Repeated path, not indexing.");
			System.out.println(nodez);
			System.exit(0);
			return;
		}
		*/
		System.out.println("Identified path: " + nodez);
		pathlist.add(nodez);
		pathlist.add(link);
		pathlist.add(weight);
		//System.out.println("Shortest path is: " + graph.nodes[id2].dist);
		
		//System.out.println(id1+","+id2 +" is path identified.");
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
			//System.out.println("End of search, shortest path found.");
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
	
	public Map<String, Object> makejson() {
		// Make JSON FILE: TO DO ON 15 MARCH 
		List<Map<String,Object>> listofpaths = new LinkedList<Map<String,Object>>();
		
		List<String> data = container;
		Iterator<String> iter = data.iterator();
		while(iter.hasNext()) {
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
			Map<String, Object> onepath = new HashMap<String,Object>();
			onepath.put("path", path);
			onepath.put("score", score);
			listofpaths.add(onepath);
			
			
		}
		Map<String, Object> toplvl = new HashMap<String,Object>();
		String[] query = {Integer.toString(q1id), q1, Integer.toString(q2id), q2}; 
		toplvl.put("query", query);
		toplvl.put("paths", listofpaths);
		
		return toplvl;
	}
	
	public List<String> decode() {
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
			//System.out.println(sentence);
			answers.add(sentence);
		}
		return answers;
	}
	
	public int compare(Node x, Node y) {
		return (int) Math.round(x.dist-y.dist);
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		try {
			GraphDB graph = new GraphDB(1);
			YenShortestPaths pathfinding = new YenShortestPaths(graph, 5);
			
			graph.getconnections();
			pathfinding.findpaths("man","cell");
			System.out.println("Program ended.");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println(elapsedTime + " miliseconds elapsed.");
		}
	}
	
}
