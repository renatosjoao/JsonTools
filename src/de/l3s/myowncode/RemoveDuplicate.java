/**
 *	
 * Utility code do remove duplicate lines
 *
 * @author  Renato Stoffalette Joao
 * @version 1.0
 * @since   2015-05 
 */
package de.l3s.myowncode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/***
 * 
 * @author renato
 * 
 * Utility class to remove duplicate lines.
 */
public class RemoveDuplicate {
	
	public static void main(String args[]) throws IOException {
		String filename = "URLsFromVI.csv";
		String outputFilename = "URLsFromVI_uniq.csv";
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		Set<String> lines = new HashSet<String>(); 
		String line;
		
		while ((line = reader.readLine()) != null) {
		//I am only splitting by comma because of the input files
		// i.e.  http://www.ftd.de/rss2/,1323122460000
			String[] col = line.split(",");
			lines.add(col[0]);
		}
		reader.close();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));
		for (String unique : lines) {
			writer.write(unique);
			writer.newLine();
		}
		writer.close();	
	}

}