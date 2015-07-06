package de.l3s.myowncode;

/***
 *
 * Extracts the text content from JSON files and output them to separate txt files.
 *
 * @author  Renato Stoffalette Joao
 * @version 1.0
 * @since   2015-04
 *
 ***/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TextExtractor {

	private String inputFilesPath = null;

	public TextExtractor(String inputFilesPath) throws IOException,	ParseException {
		File[] faFiles = new File(inputFilesPath).listFiles();
		for (File file : faFiles) {

			InputStream fileStream = new FileInputStream(file);
			Reader decoder = new InputStreamReader(fileStream);
			BufferedReader buffered = new BufferedReader(decoder);
			String line = null;
			JSONParser jparser = new JSONParser();
			int i = 0;
			while ((line = buffered.readLine()) != null) {
				JSONObject jobj = new JSONObject();
				jobj = (JSONObject) jparser.parse(line);
				String text = (String) jobj.get("text");
				String fileOut = FilenameUtils.removeExtension(file.getAbsolutePath());
				PrintWriter out = new PrintWriter(fileOut + "_" + i+".txt");
				out.write(text);
				out.close();
				i++;
			}
			buffered.close();

		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			printUsage();
			System.exit(1);
		} else {
			TextExtractor hE = new TextExtractor(args[0]);
		}
	}

	private static void printUsage() {
		System.out.println("Utility class to parse JSON files and write text files.");
		System.out.println("Please specify the input directory where the JSON files are located.");
	}

	/**
	 * @return the inputFilesPath
	 */
	public String getInputFilesPath() {
		return inputFilesPath;
	}

	/**
	 * @param inputFilesPath
	 *            the inputFilesPath to set
	 */
	public void setInputFilesPath(String inputFilesPath) {
		this.inputFilesPath = inputFilesPath;
	}
}