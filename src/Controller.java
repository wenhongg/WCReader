import javax.swing.*;  
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

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
	public Controller() throws IOException{
		super("Webchild searcher");
		setSize(500, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		loading.add(load);
		add(load);
		setVisible(true);
		//try { Thread.sleep(5000); }
		//catch (InterruptedException e){}
		try {
		graph = new GraphDB(2);
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		graph.getconnections();
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
			List<String> answers = path.findpaths(x, y);
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
		new Controller();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
