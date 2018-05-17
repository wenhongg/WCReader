/*
 * Class to be called by GraphDB. A node represents an object with many links.
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class Node {
		List<String[]> relations,removed;
		double dist;
		boolean visited;
		int id,bestparent,bestlink,root;
		double bestweight;
		List<Integer> temp;
		Node(int i){
			id = i;
			relations = new ArrayList<String[]>();
			removed = new ArrayList<String[]>();
			dist = Double.POSITIVE_INFINITY;
			visited = false;
			
		}
	}