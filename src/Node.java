/*
 * Class to be called by GraphDB. A node represents an object with many links.
 */
import java.util.LinkedList;
import java.util.List;

class Node {
		List<String[]> relations;
		double dist;
		boolean visited;
		int id,bestparent,bestlink,root;
		double bestweight;
		Node(int i){
			id = i;
			relations = new LinkedList<String[]>();
			dist = Double.POSITIVE_INFINITY;
			visited = false;
		}
	}