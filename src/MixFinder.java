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
import java.util.List;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import com.google.gson.*; 
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * This class does a multidirectional breadth first search to link more than 3 nodes.
 * Idea here is to find some common concept which links the entities queried.
 */
public class MixFinder{
	
	GraphDB graph;
	Set<String> queries;
	List<Integer> idlist;
	Map<Integer, Integer> totallist;
	List<Map<Integer,Integer>> distancelist;
	int radius,common, want;
	boolean discovered;

	/**
	 * Constructor for the multi-directional breadth first searcher
	 * @param graph1 the graph to traverse
	 */
	public MixFinder(GraphDB graph1) {
		totallist = new HashMap<Integer,Integer>();
		graph = graph1;
	}
	
	/**
	 * Traverse the graph
	 * @param list the list of queries
	 * @param count the number of common concepts to be returned
	 */
	public void traverse(Set<String> list, int count){
		want = count;
		totallist = new HashMap<Integer,Integer>();
		queries = new HashSet<String>();
		for(String x: list) {
			queries.add(x);
		}
		discovered = false;
		radius = 0;
		idlist = graph.process(list);
		System.out.println(idlist.size() +" IDs obtained.");
		if(idlist.size()!=list.size()) {
			System.out.println(list.size()-idlist.size() + " query don't exist.");
			System.exit(0);
		}
		distancelist = new ArrayList<Map<Integer,Integer>>();
		for(int x :idlist) {
			Map<Integer,Integer> map = new HashMap<Integer,Integer>();
			map.put(x, 0);
			distancelist.add(map);
			graph.nodes[x].visited = true;
			graph.nodes[x].bestparent = x;
		}
		int check = 0;
		while(!discovered && check<graph.objmap.size()) {
			expand();
			check();
		}
		
		
		linkseek();
		graph.reset();
		
	}
	
	private void expand() {
		System.out.println("Expanding");
		for(Map<Integer,Integer> map : distancelist) {
			List<Integer> list = new ArrayList<Integer>();
			for(Map.Entry<Integer,Integer> entry : map.entrySet()) {
				if(entry.getValue() == radius) {
					for(String[] x: graph.nodes[entry.getKey()].relations) {
						list.add(Integer.parseInt(x[0]));
						if(!graph.nodes[Integer.parseInt(x[0])].visited) {
							graph.nodes[Integer.parseInt(x[0])].visited = true;
							graph.nodes[Integer.parseInt(x[0])].bestparent = entry.getKey();
						}
					}
				}
			}
			for(int x: list) {
				if(!map.containsKey(x)) {
					map.put(x, radius+1);
				}
			}
			
		}
		radius+=1;
	}
	
	
	private void check() {
		Map<Integer,Integer> map1 = distancelist.get(0);
		List<Integer> list = new ArrayList<Integer>();
		for(Map.Entry<Integer,Integer> entry : map1.entrySet()) {
			list.add(entry.getKey());
		}
		
		for(int x : list) {
			Set<Boolean> mapcheck = new HashSet<Boolean>();
			
			for(int i=0; i<distancelist.size(); i+=1) {
				if(distancelist.get(i).containsKey(x)) {
					mapcheck.add(true);
				} else {
					mapcheck.add(false);
				}
			}
			if(!mapcheck.contains(false)) {
				if(!discovered) {
					common = x;
				}
				System.out.println("Common node is found.");
				System.out.println("Common node is " + graph.objmap.get(common));
				int net = 0;
				for(Map<Integer,Integer> map : distancelist) {
					System.out.println("Map distance" + map.get(common));
					net += map.get(common);
				}
				
				totallist.put(x, net);
				System.out.println("Net distance is " + net);
				discovered = true;
			}
		}
	}
	
	private void linkseek() {
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		ShortestPaths findpaths = new ShortestPaths(graph);
		for(int x : idlist) {
			findpaths.traverse(x, common);
			Map<String,Object> map = findpaths.decodem();
			list.add(map);
		}
		Map<String,Object> mapp = new HashMap<String,Object>();
		mapp.put("paths", list);
		mapp.put("common", graph.objmap.get(common));
		mapp.put("query", queries);
		Gson gson = new GsonBuilder().create();
		String x = gson.toJson(mapp);
		System.out.println(x);
		
		
		String dest = "results/";
		for(String a : queries) {
			dest += a + "_";
		}
		dest = dest.substring(0, dest.length() - 1);
		dest += ".json";
		
		try {
		if(!Files.isDirectory(Paths.get("results"))){
			System.out.println("Creating RESULTS folder.");
			Files.createDirectories(Paths.get("results"));
		}
		System.out.println("Saved to " + dest);
		FileWriter file = new FileWriter(dest);
	    BufferedWriter bw = new BufferedWriter(file);
	    bw.write(x);
	    bw.flush();
	    bw.close();
		} catch(Exception e) {
			System.out.println("Unable to write json file.");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			GraphDB graph = new GraphDB(2);
			Set<String> list = new HashSet<String>();
			list.add("cat");
			list.add("tree");
			list.add("ground");
			MixFinder mix = new MixFinder(graph);
			mix.traverse(list, 5);
			graph.getconnections();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
