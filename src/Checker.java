/*
 * This class just helped to check that all the formatting is correct.
 */

import java.util.Scanner;
import java.io.*;
import java.io.File;
import java.util.Set;
import java.util.HashSet;

// Expected 13515915 
public class Checker {
	Scanner scanner;
	Set<String> relationships;
	public Checker(String address) throws FileNotFoundException {
		scanner = new Scanner(new File(address));
		scanner.useDelimiter("\\r?\\\n");
		relationships = new HashSet<String>();
		int counter = 0;
		long ones = 0;
		while(scanner.hasNext()) {
			String str = scanner.next();
			String[] arr = str.split(",");
			
			if(arr.length!=4) {
				System.out.println("Incorrect length obtained. System terminating");
				System.out.println(arr.length);
				System.exit(0);
				
			}
			if(Double.parseDouble(arr[3]) == 1) {
				ones+=1;
			}
			relationships.add(arr[1]);
			
			counter+=1;
			System.out.println("Entry " + counter + " found. " + "Score of " + arr[3]);
		}
		
		for(String str: relationships) {
		System.out.println(str);
		}
		System.out.println(counter + " entries found. End of check.");
		System.out.println(ones + " of these entries scored 1.");
		System.out.println(relationships.size() + " types of relationships");
		
	}
	public static void main(String[] args) {
		try {
		new Checker("output.csv");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
