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
import javax.swing.*;  
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
/**
 * This is a GUI for the k-shortest paths algorithm (Yen's algorithm). 
 * The graph is first loaded and query can be entered.
 * Query is saved both as .json file and also displayed as plain text in Swing window.
 * 
 */
public class Controller extends JFrame implements ActionListener{
	JPanel loading = new JPanel();
	JLabel load = new JLabel("Please wait, webchild graph is loading.", SwingConstants.CENTER);
	JPanel screen = new JPanel();
	JLabel search = new JLabel("Search WEBCHILD dataset");
	JTextField query1 = new JTextField(40);
	JTextField query2 = new JTextField(40);
	JTextArea answerkey = new JTextArea(5,40);
	JButton enter = new JButton("Search");
	GraphDB graph;
	YenShortestPaths path;
	
	/**
	 * Produces the GUI. Exception thrown if problems reading from dataset or dataset does not exist.
	 * @param dataset
	 */
	public Controller(int dataset){
		super("Webchild searcher");
		setSize(500, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		loading.add(load);
		add(load);
		setVisible(true);
		//try { Thread.sleep(5000); }
		//catch (InterruptedException e){}
		try {
		graph = new GraphDB(dataset);
		graph.getconnections();
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		path = new YenShortestPaths(graph , 5);
		getContentPane().removeAll();
		search.setSize(500, 10);
		answerkey.setEditable(false);
		answerkey.setLineWrap(true);
		screen.add(search);
		screen.add(query1);
		screen.add(query2);
		screen.add(enter);
		screen.add(answerkey);
		

		enter.addActionListener(this);
		
		add(screen);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event){
		if(event.getSource() == enter){
			// Get query from text fields
			enter.setEnabled(false);
			answerkey.setText("Searching...");
			String x = query1.getText();
			String y = query2.getText();
			List<String> answers = path.traverse(x, y);
			System.out.println(answers.size() + " items");
			answerkey.setText("");
			for(String aa : answers) {
				answerkey.append(aa + "\n");
			}
			path.answers.clear();
			enter.setEnabled(true);
		}
	}
	
	
	public static void main(String[] args) {
		try {
		new Controller(1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
