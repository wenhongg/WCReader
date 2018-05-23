import java.util.List;
import java.io.FileWriter;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import com.google.gson.*;

public class MixFinder {
	GraphDB graph;
	Set<String> queries;
	List<Integer> idlist;
	List<Map<Integer,Integer>> distancelist;
	int radius,common;
	boolean discovered;
	public MixFinder(List<String> list, GraphDB graph1) throws IOException {
		// Need to make copy of list to save later
		queries = new HashSet<String>();
		for(String x: list) {
			queries.add(x);
		}
		discovered = false;
		radius = 0;
		graph = graph1;
		idlist = graph.processquery(list);
		System.out.println(idlist.size() +" IDs obtained.");
		graph.getconnections();
		distancelist = new ArrayList<Map<Integer,Integer>>();
		for(int x :idlist) {
			Map<Integer,Integer> map = new HashMap<Integer,Integer>();
			map.put(x, 0);
			distancelist.add(map);
			graph.nodes[x].visited = true;
			graph.nodes[x].bestparent = x;
		}
		while(!discovered) {
			expand();
			check();
		}
		linkseek();
	}
	
	public void expand() {
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
			boolean comm = true;
			boolean[] mapcheck = new boolean[idlist.size()];
			for(boolean a : mapcheck) {
				a = false;
			}
			for(int i=0; i<distancelist.size(); i+=1) {
				if(distancelist.get(i).containsKey(x)) {
					mapcheck[i] = true;
				}
			}
			for(boolean a : mapcheck) {
				if(a = false) {
					comm = false;
					break;
				}
			}
			if(comm) {
				common = x;
				System.out.println("Common node is found.");
				System.out.println("Common node is " + graph.objmap.get(common));
				int net = 0;
				for(Map<Integer,Integer> map : distancelist) {
					net += map.get(common);
				}
				System.out.println("Net distance is " + net);
				discovered = true;
				break;
			}
		}
	}
	
	public void linkseek() throws IOException {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		ShortestPaths findpaths = new ShortestPaths(graph);
		for(int x : idlist) {
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
		
		String dest = "";
		for(String a : queries) {
			dest += a + "_";
		}
		dest = dest.substring(0, dest.length() - 1);
		dest += ".json";
		
		FileWriter file = new FileWriter(dest);
	    BufferedWriter bw = new BufferedWriter(file);
	    bw.write(x);
	    bw.flush();
	    bw.close();
	}
	public static void main(String[] args) {
		try {
			GraphDB graph = new GraphDB(2);
			List<String> list = new ArrayList<String>();
			list.add("cell");
			list.add("egg");
			list.add("man");
			MixFinder mix = new MixFinder(list, graph);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
