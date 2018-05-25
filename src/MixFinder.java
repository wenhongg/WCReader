import java.util.List;
import java.io.FileWriter;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import com.google.gson.*; 
import java.util.Comparator;
import java.util.PriorityQueue;

public class MixFinder implements Comparator<Map.Entry<Integer, Integer>>{
	PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<Map.Entry<Integer,Integer>>(this);
	GraphDB graph;
	Set<String> queries;
	List<Integer> idlist;
	Map<Integer, Integer> totallist;
	List<Map<Integer,Integer>> distancelist;
	int radius,common, want;
	boolean discovered;
	public MixFinder(Set<String> list, GraphDB graph1, int count) throws IOException {
		// Need to make copy of list to save later
		want = count;
		totallist = new HashMap<Integer,Integer>();
		queries = new HashSet<String>();
		for(String x: list) {
			queries.add(x);
		}
		discovered = false;
		radius = 0;
		graph = graph1;
		idlist = graph.process(list);
		System.out.println(idlist.size() +" IDs obtained.");
		if(idlist.size()!=list.size()) {
			System.out.println(list.size()-idlist.size() + " query don't exist.");
			System.exit(0);
		}
		graph.getconnections();
		distancelist = new ArrayList<Map<Integer,Integer>>();
		for(int x :idlist) {
			Map<Integer,Integer> map = new HashMap<Integer,Integer>();
			map.put(x, 0);
			distancelist.add(map);
			graph.nodes[x].visited = true;
			graph.nodes[x].bestparent = x;
		}
		while(totallist.size()<count) {
			expand();
			check();
		}
		for(Map.Entry<Integer, Integer> entry : totallist.entrySet()) {
			pq.add(entry);
		}
		int countxxx = 0;
		
		linkseek();
		graph.reset();
		for(Map.Entry<Integer, Integer> entry: pq) {
			countxxx +=1;
			System.out.println("Common concept can be: " + graph.objmap.get(entry.getKey()) + " of score " + entry.getValue());
			
			
		}
		
	}
	public int compare(Map.Entry<Integer, Integer> a ,Map.Entry<Integer, Integer> b) {
		return -a.getValue()+b.getValue(); 
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
	
	public void expand() {
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
	
	public void check() {
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
	
	public void linkseek() throws IOException {
		for(Map.Entry<Integer, Integer> entry: pq) {
			
		}
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		ShortestPaths findpaths = new ShortestPaths(graph);
		for(int x : idlist) {
			reset();
			findpaths.obtainpaths(x, common);
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

		System.out.println("Saved to " + dest);
		FileWriter file = new FileWriter(dest);
	    BufferedWriter bw = new BufferedWriter(file);
	    bw.write(x);
	    bw.flush();
	    bw.close();
	}
	public static void main(String[] args) {
		try {
			GraphDB graph = new GraphDB(2);
			Set<String> list = new HashSet<String>();
			list.add("wheel");
			list.add("fragment");
			list.add("ground");
			MixFinder mix = new MixFinder(list, graph, 5);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
