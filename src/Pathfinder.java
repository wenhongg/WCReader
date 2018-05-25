/*
 * Pathfinder is uses a simple recursive depth-first search to traverse the graph and find all possible links (avoiding loops)
 * In line 146, the maximum permitted number of links can be restricted to a certain number
 * 
 * In my opinion, given that most links in the webchild graph are 1 and a tiny portion is slightly above 1,
 * I think this could be a more efficient function for getting multiple, useful links than other algorithms.
 * Pathfinder ignores the weights (takes them ALL to be 1).
 * 
 * WC files must have been processed by ReadCSV and Grapher.
 * 
 */

import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Iterator;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

// Connections: Object1, Object2, Link, Score
// 

// Depth-first search implemented here

// VM arguments to use
public class Pathfinder {
	LinkedList<Integer> visited = new LinkedList<Integer>();
	LinkedList<Integer> visitrs = new LinkedList<Integer>();
	LinkedList<Double> visitweight = new LinkedList<Double>();
	
	LinkedList<String> pathlist = new LinkedList<String>();
	Set<String> queries = new HashSet<String>();
	List<Integer> idslist;
	Scanner scan1,scan2;
	List<String> answers;
	Map<Integer,String> objmap,rsmap;
	
	String q1,q2;
	int q1id,q2id, link;
	GraphDB graph;
	
	public Pathfinder(GraphDB graph1, int maxlinks) throws IOException {
		// Below lines are standard format for checking the query and visualizing the graph.
		link = maxlinks;
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<Integer,String>();
				
		graph = graph1;
		getidmaps();
		//
		answers = new LinkedList<String>();
		
		visited.add(q1id);
		
		
		
	}
	
	public void reset() {
		graph.reset();
	}
	
	public void getidmaps() throws IOException {
		objmap = graph.objmap;
		rsmap = graph.rsmap;
	} 
	
	
	public void processquery(String query1, String query2) throws IOException {
		queries.add(query1);
		queries.add(query2);
		q1 = query1;
		q2 = query2;
		
		idslist = graph.process(queries);
		if(idslist.size()!= 2) {
			System.out.println("Something went wrong. Size is " + idslist.size());
			System.out.println("One or more query was not found. Try something else");
		}
		q1id = idslist.get(0);
		q2id = idslist.get(1);
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
	
	public List<String[]> search(int query){
		System.out.println("Searching: ID " + query + " - presently " + (pathlist.size()/3) + " links found. " + visited.size() + " deep in the rabbit trail");
		List<String[]> mylist = new LinkedList<String[]>();
		
		if(graph.nodes[query].relations.size() ==0) {
			return mylist;
		}
		for(String[] a : graph.nodes[query].relations) {
			mylist.add(a);
		}
		return mylist;
	}
	
	public void findpaths() {
		searcher();
		makejson();
		decode();
		reset();
	}
	
	public void searcher(){
		
		
		int counter = 0;
		List<String[]> neighbours = search(visited.getLast());
		for (String[] a : neighbours) {
			
            if (visited.contains(Integer.parseInt(a[0]))) {
            	// If a loop is formed, continue on to find path from another node.
                continue;
            } else if (Integer.parseInt(a[0]) == q2id) {
            	// Destination is reached. Add node to save the path, then remove it later to find other paths.
                
            	visited.add(Integer.parseInt(a[0]));
            	visitrs.add(Integer.parseInt(a[1]));
                visitweight.add(Double.parseDouble(a[2]));
                
            	Iterator<Integer> ite = visited.iterator();
                String str = "";
                while(ite.hasNext()) {
                	str += ite.next();
                	if(ite.hasNext()) {
                		str += ",";
                	}
                }
                Iterator<Integer> iter = visitrs.iterator();
                String str1 = "";
                while(iter.hasNext()) {
                	str1 += iter.next();
                	if(iter.hasNext()) {
                		str1 += ",";
                	}
                }
                Iterator<Double> iter1 = visitweight.iterator();
                String str2 = "";
                while(iter1.hasNext()) {
                	str2 += iter1.next();
                	if(iter1.hasNext()) {
                		str2 += ",";
                	}
                }
                
                
                pathlist.add(str);
                pathlist.add(str1);
                pathlist.add(str2);
                
                
                counter += 1;
                System.out.println(counter + " link(s) found.");
                for(Integer b : visited) {
            		System.out.print(b + " ");
            		
            	}
            	System.out.println();
                visited.removeLast();
                visitrs.removeLast();
                visitweight.removeLast();
                break;
            } else {
            	// Proceed down the trail to find the destination.
            	if(visited.size()>link-1) {
            		continue;
            	}
            	 visited.addLast(Integer.parseInt(a[0]));
            	 visitrs.addLast(Integer.parseInt(a[1]));
            	 visitweight.addLast(Double.parseDouble(a[2]));
            	 
                 searcher();
                 visited.removeLast();
                 visitrs.removeLast();
                 visitweight.removeLast();
            }
        }
		
		
	}
	
	public Map<String, Object> makejson() {
		// Make JSON FILE: TO DO ON 15 MARCH 
		List<Map<String,Object>> listofpaths = new LinkedList<Map<String,Object>>();
		
		List<String> data = pathlist;
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
		
		
		
		String dest = "results/" +q1+ "_" +q2 +".json";
		System.out.println("Writing to json.");
		
		try {
		Gson gson = new GsonBuilder().create();
		FileWriter file = new FileWriter(dest);
	    BufferedWriter bw = new BufferedWriter(file);
	    bw.write(gson.toJson(toplvl));
		bw.flush();
		bw.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			return toplvl;
		}
	}
	
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
			answers.add(sentence);
			
		}
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		try {
			GraphDB graph = new GraphDB(2);
			Pathfinder finder = new Pathfinder(graph, 2);
			finder.processquery("fat", "guy");
			graph.getconnections();
			finder.findpaths();
			// Should processquery be together with findpaths? just dump findpaths under processquery.
		} catch(Exception e) {
			e.printStackTrace();
		}
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println(elapsedTime + "miliseconds passed.");
	}
}
// https://stackoverflow.com/questions/58306/graph-algorithm-to-find-all-connections-between-two-arbitrary-vertices