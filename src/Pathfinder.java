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

import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Iterator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

/**
 * This class uses a simple recursive depth-first search to traverse the graph and find all possible links (avoiding loops).
 * Pathfinder ignores the weights and relies instead on hops. (takes them ALL to be 1).
 * The number of paths must be constrained (especially for large graphs). 
 * 
 */
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
	/**
	 * Pathfinder constructor.
	 * @param graph1 the graph to traverse
	 * @param maxlinks the maximum number of links to traverse through
	 */
	public Pathfinder(GraphDB graph1, int maxlinks) {
		// Below lines are standard format for checking the query and visualizing the graph.
		link = maxlinks;
		objmap = new HashMap<Integer,String>();
		rsmap = new HashMap<Integer,String>();
				
		graph = graph1;
		getidmaps();
		answers = new LinkedList<String>();
	}
	
	private void reset() {
		graph.reset();
		visited.clear();
		visitrs.clear();
		visitweight.clear();
		pathlist.clear();
		queries.clear();
		answers.clear();
		idslist.clear();
	}
	
	private void getidmaps(){
		objmap = graph.objmap;
		rsmap = graph.rsmap;
	} 
	
	private List<String[]> search(int query){
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
	/**
	 * Processes query and runs graph traversal. Prints error message and returns if query is not found.
	 * Graph traversal results are stored as .json file. 
	 * @param query1 first query word
	 * @param query2 second query word
	 */
	public void traverse(String query1, String query2) {
		q1 = query1;
		q2 = query2;
		Set<String> querylist = new HashSet<String>();
		querylist.add(query1);
		querylist.add(query2);
		idslist = graph.process(querylist);
		if(idslist.size()!= 2) {
			System.out.println("One or more query was not found.");
			return;
		}
		q1id = idslist.get(0);
		q2id = idslist.get(1);
		visited.add(q1id);
		searcher();
		makejson();
		decode();
		reset();
	}
	
	private void searcher(){
		
		
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
	
	private Map<String, Object> makejson() {
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
		
		
		
		String dest = "results/" +q1+ "_" +q2 + "_ALL" + link +".json";
		System.out.println("Writing to json.");
		
		try {
		if(!Files.isDirectory(Paths.get("results"))){
			System.out.println("Creating RESULTS folder.");
			Files.createDirectories(Paths.get("results"));
		}
		Gson gson = new GsonBuilder().create();
		FileWriter file = new FileWriter(dest);
	    BufferedWriter bw = new BufferedWriter(file);
	    bw.write(gson.toJson(toplvl));
		bw.flush();
		bw.close();
		} catch(Exception e) {
			e.printStackTrace();
		} 
		return toplvl;
		
	}
	
	private List<String> decode() {
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
		return answers;
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		try {
			GraphDB graph = new GraphDB(2);

			graph.getconnections();
			Pathfinder finder = new Pathfinder(graph, 3);
			finder.traverse("cat","dog");

		} catch(Exception e) {
			e.printStackTrace();
		}
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println(elapsedTime + "miliseconds passed.");
	}
}
// https://stackoverflow.com/questions/58306/graph-algorithm-to-find-all-connections-between-two-arbitrary-vertices