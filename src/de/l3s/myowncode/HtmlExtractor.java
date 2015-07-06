package de.l3s.myowncode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlExtractor {
	private String inputFilesPath = null;

	public HtmlExtractor(String inputFilesPath) throws IOException,
			ParseException {

		File[] faFiles = new File(inputFilesPath).listFiles();
		for (File file : faFiles) {

			InputStream fileStream = new FileInputStream(file);
			InputStream gzipStream = new GZIPInputStream(fileStream);
			Reader decoder = new InputStreamReader(gzipStream);
			BufferedReader buffered = new BufferedReader(decoder);
			String line = null;
			JSONParser jparser = new JSONParser();
			int i = 0;
			while ((line = buffered.readLine()) != null) {
				JSONObject jobj = new JSONObject();
				jobj = (JSONObject) jparser.parse(line);
				String html = (String) jobj.get("c");
				Document htmlDOC = Jsoup.parse(html);
				String fileOut = FilenameUtils.removeExtension(file	.getAbsolutePath());
				String fileOutII = FilenameUtils.removeExtension(fileOut);
				PrintWriter out = new PrintWriter(fileOutII + "_" + i + ".html");
				out.write(htmlDOC.toString());
				out.close();
				i++;

			}

		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			printUsage();
			System.exit(1);
		} else {
			HtmlExtractor hE = new HtmlExtractor(args[0]);

		}

	}

	private static void printUsage() {
		System.out.println("Utility class to parse JSON files and write HTML files.");
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
