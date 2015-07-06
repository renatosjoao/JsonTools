package de.l3s.myowncode;
/**
 *	
 * * *  Utility  code do read from file with JSON format and remove <html>tags.
 * 
 * Input  {"s":"STATUS CODE","ts":"TIMESTAMP","c":"RAW HTML","type":"MIMETYPE","url":"URL"};
 *
 * Output  {"ts":"TIMESTAMP","url":"URL","text":"ARTICLE TEXT","s":"STATUS","type":"MIMETYPE"}"
 * 
 * 
 *
 * @author  Renato Stoffalette Joao
 * @version 1.0
 * @since   2015-04 
 */

//**********  NOTE : THIS CODE WAS USED TO CLEAN FINACIAL TIMES DEUTSCHLAND FILES HTML TAGS

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class TagCleaner {

	private static BufferedReader bf;
	private static String NEW_LINE_SEPARATOR = "\n";
	private String COMMA_DELIMITER = ",";
	private static Document doc = null;
	static boolean compressed = false;
	private static int nFiles = 0;
	private static int nJSONs = 0;
	private boolean isDir = false;
	private String inputFilesPath;
	private String outputFilesPath; 
	private int t1,t2,t3,t4,t5,t6,t7,empty,nonempty=0;
	
	/**
	 * 
	 * @param htmlContent
	 * @return
	 */
	public Document getHtmlDoc(String htmlContent){
		Document htmlDOC = Jsoup.parse(htmlContent);
		return htmlDOC;
	}

	public TagCleaner(String iFilesPath, String oFilesPath ){
		super();
		inputFilesPath = iFilesPath; 
		outputFilesPath = oFilesPath; 
	}
	
	public TagCleaner(){
		
	}
	/***
	 * 
	 * @param iFilesPath a path where the input files will be read from
	 * @param oFilesPath a path where the output files will be written to
	 * @throws Exception 
	 */
	public void cleanTags() throws Exception{
		int i=0;
		Charset charset = Charset.forName("UTF-8");
		// I am expecting to parse all files from the input iFilesPath
		// and write everything to output oFilesPath
		// iFilesPath is expected to contain ***.gz files 

		//String inputFilesPath = iFilesPath;
		//String ouputFilesPath = oFilesPath;

		final long start = System.currentTimeMillis();
		Map<String, String> mObj = new LinkedHashMap<String,String>();
		LinkedList<String> lista = new LinkedList<String>();
		//Are we dealing with a single file or a directoy ???
		File fDir = new File(inputFilesPath);
		if(fDir.isDirectory()){
			isDir = true;
		}else{
			isDir = false;
			//Sorry, but so far I am only dealing with the files inside a directory
			throw new Exception("ERROR : "+inputFilesPath+" is not a proper directoy.");
		}
		
		StringBuilder finalFile = new StringBuilder();
		File[] faFiles = new File(inputFilesPath).listFiles();
		FileWriter fW = null;
		File dir = new File(outputFilesPath);
		//just to make sure the output dir will exist
		if(!dir.exists()){
			dir.mkdirs();
		}
		//iterating over all the files inside args[0] directory
		for (File file : faFiles) {
			System.out.printf("Reading file %s \n",file.toString());
		//File file = new File(args[0]);
			nFiles++;
			
			String[] path = file.getAbsolutePath().split("/");
			//String filename = file.toString().replaceFirst("[.][^.]+$", "");
			//System.out.println(path[path.length -1].replaceFirst("[.][^.]+$", ""));
			File ff = new File(outputFilesPath, path[path.length -1].replaceFirst("[.][^.]+$", ""));
			fW = new FileWriter(ff);	
			BufferedWriter bw = new BufferedWriter(fW);
			if (file.getName().matches("^(.*?)")) {
				//The files are Gzipped
				if (file.getName().endsWith(".gz")) {
					compressed = true;
				}else{
					compressed = false;
					fW.close();
					return;
				}
					InputStream fileStream = new FileInputStream(file);
					InputStream gzipStream = new GZIPInputStream(fileStream);
					Reader decoder = new InputStreamReader(gzipStream);
					BufferedReader buffered = new BufferedReader(decoder);
					String line = null;
					JSONParser jparser = new JSONParser();
					//Ok, now I am going to parse each line from the current input file
					while ((line = buffered.readLine()) != null) {
						nJSONs++;
						JSONObject jobj = new JSONObject();
						jobj = (JSONObject) jparser.parse(line);
						
						String url = (String) jobj.get("url");
						Long timestamp = (Long) jobj.get("ts");
						String content = (String) jobj.get("c");
						String type = (String) jobj.get("type");
						Long status =  (Long) jobj.get("s");
						
					
						if (type.equalsIgnoreCase("text/html") || 
							type.equalsIgnoreCase("text/xml")  ||
							type.equalsIgnoreCase("application/http; msgtype=response")	){ 
							t1++;
						//I am using JSoup here just to make sure tags are balanced
						Document doc = Jsoup.parse(content);
						doc = getHtmlDoc(content); 
						//Here I am using Boilerpipe to clean <html> tags
						String cleanText = getText(doc.toString());
						
						
						
				        cleanText = cleanText.replaceAll("[ [\uFFFD] | "
								+ "[\uFEFF] | [\u0000-\u001F] | "
								+ "[\u007F-\u009F] |  "
								+ "[\uF0B3-\uF0BF] | "
								+ "[\uF366] | [\u05FA] | [\uA68D] | "
								+ "[\u0604] | [\u00AD] | [\u2013]]", " "); //removing funny characters
				        
				       // cleanText = cleanText.replaceAll("[ \\p{Cntrl}&&[^\r\n\t] ]", " ");
				        //cleanText = cleanText.replaceAll("\\p{Cc}", " ");
						if((cleanText==" ")||(cleanText=="\n") || cleanText.trim().equalsIgnoreCase("")){
							empty++;
							continue;
						}else{
							nonempty++;
							mObj.put("ts", timestamp.toString());
							mObj.put("url", url);
							mObj.put("text", cleanText);
							mObj.put("s",status.toString());
							mObj.put("type",type);
						//The final file will have JSON objects (1 per line)
						//writeToTxTFile(cleanText);
							bw.write(JSONValue.toJSONString(mObj));
							bw.write(NEW_LINE_SEPARATOR);
							char array[] = cleanText.toCharArray();
							//for(char c:cleanText.toCharArray()){
							//	System.out.println(c);
							//}
							String ss = new String(array);
							lista.add(ss);
							lista.add("\n");
							System.out.println("controle");
						}
						}else if(type.equalsIgnoreCase("text/css")){
							t2++;
							continue;
						}else if(type.equalsIgnoreCase("text/plain")){
							t3++;
							continue;
						}else if(type.equalsIgnoreCase("text/javascript")){
							t4++;
							continue;
						}else{
							continue;
						}
					}
					buffered.close();
					//Trying to write toJson file , cleaned file though !!!
					
					//fW.flush();
					bw.close();
					fW.close();
					//compressGzipFile(file, gzipFile);
					System.out.printf("Finished processing %s\n",file.toString());
					//System.out.println("text/html = "+t1);
					//System.out.println("text/css = "+t2);
					//System.out.println("text/plain = "+t3);
					//System.out.println("text/javascript = "+t4);
					//System.out.println("application/http = "+t5);
				} 
			
			
		}
		
		final long stop = System.currentTimeMillis();
		System.out.printf("Finished processing %d file(s).\n",nFiles);
		System.out.printf("Total JSON file(s) = %d.\n",nJSONs);
		System.out.println("Total empty file(s) = "+empty);
		System.out.println("Total non empty file(s) = "+nonempty);
		System.out.printf("Total processing time is : %.2f seconds.",(double)(stop-start)/1000.0);
	}

	
	/**
	 * This is a tentative of writing to text files separately.
	 * Each text will be written to a separate text file. 
	 * @param cleanText
	 */

	private void writeToTxTFile(String cleanText) {
		FileWriter writer;
		try {
			writer = new FileWriter(outputFilesPath+"part_"+nJSONs+".txt");
			writer.append(cleanText);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}


	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if(args.length < 2){
			printUsage();
			System.exit(1);
		}else{
			TagCleaner tC = new TagCleaner(args[0],args[1]);
			tC.cleanTags();
		}

	}

	/**
	 * 
	 * @param mObj
	 * @param fileW
	 */
	public static void writeToJSONFile(Map<String, String> mObj, FileWriter fileW){
		try {
			fileW.write(JSONValue.toJSONString(mObj));
			fileW.write(NEW_LINE_SEPARATOR);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Usage function
	 */
	private static void printUsage() {
		System.out.println("Utility class to parse JSON files and remove <html> tags.\n ");
		System.out.println("Input files format = {\"s\":\"STATUS CODE\",\"ts\":\"TIMESTAMP\",\"c\":\"RAW HTML \",\"type\":\"MIMETYPE\",\"url\":\"URL\"}");
		System.out.println("Output files format = {\"ts\":\"TIMESTAMP\",\"url\":\"URL\",\"text\":\"ARTICLE TEXT\",\"s\":\"STATUS\",\"type\":\"MIMETYPE\"}\n" );
		System.out.println("Please specify the input directory where the JSON files are located and the output directory");
		System.out.println("i.e.  --- inputPath/  outPutPath/");
	}

	
	
/**
 * method to reader from html content and return headers
 * 
 * @param htmlContent
 * @return headers h1, h2 and h3
 */
	public static String[] getTitleFromHtml(String htmlContent) {		
		//Document htmlDOC = Jsoup.parse(htmlContent);
		String title = doc.title().trim();
		String h1 = doc.getElementsByTag("h1").text().trim().replace("\n", " ").replace("\r", " ").replace("\\", " ");
		String h2 = doc.getElementsByTag("h2").text().trim().replace("\n", " ").replace("\r", " ").replace("\\", " ");
		String h3 = doc.getElementsByTag("h3").text().trim().replace("\n", " ").replace("\r", " ").replace("\\", " ");
		String[] headers = {title,h1,h2,h3};
		return headers;
	}

	/**
	 * 
	 * @param html the whole html content
	 * @return htmlText Returns the text extracted from the news article
	 * @throws FileNotFoundException
	 * @throws BoilerpipeProcessingException
	 */
	public static String getText(String html) throws FileNotFoundException, BoilerpipeProcessingException{
		String htmlText = null;
		//FileReader fr = new FileReader(html); 
		StringReader fr = new StringReader(html);
        //htmlText = ArticleExtractor.INSTANCE.getText(fr).trim().replace("\n", " ").replace("\r", " ").replace("\\", " ");
        htmlText = ArticleExtractor.INSTANCE.getText(fr);
        
		return htmlText;
		
	}
	
	/**
	 * 
	 * @param html
	 * @return
	 */
	public static String getContentFromHtml(String html) {
		//String body = doc.body().children().text();
		//Elements innerBox = Jsoup.parse(html).getElementsByClass("innerbox");
		//Elements txtSnip = doc.getElementsByClass("article");
		Elements txtSnip = doc.getElementsByClass("sectionEmbeddedBox");
		//Elements txtSnip = doc.getElementsByClass("floatbox.info");
		//Map<String, String> mObj = null;
		StringBuffer texto = new StringBuffer();
		for (Element txt : txtSnip) {
			if ((txt.text() != null) || (txt.hasText())) {
				texto.append(txt.text().trim().replace("\n", " ").replace("\r", " ").replace("\\", " "));
			}
		}
		String cleanTxt = texto.toString();
		
		//if (innerBox == null || innerBox.isEmpty()) {
		//	return "";
		//}
		return cleanTxt;
	}

	/**
	 * 
	 * @param sFileName
	 * @param content
	 */
	private static void writeToCSVFile(String sFileName, String content) {

	}
	
	/**
	 * 
	 * @param sFileName
	 * @param content
	 */
	private static void writeToXMLFile(String sFileName, String content) {
		
	}
	
	  private static void compressGzipFile(String file, String gzipFile) {
	        try {
	            FileInputStream fis = new FileInputStream(file);
	            FileOutputStream fos = new FileOutputStream(gzipFile);
	            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
	            byte[] buffer = new byte[1024];
	            int len;
	            while((len=fis.read(buffer)) != -1){
	                gzipOS.write(buffer, 0, len);
	            }
	            //close resources
	            gzipOS.close();
	            fos.close();
	            fis.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	         
	    }
	
/*	private static void writeToJSONFile(){
		File ff = new File(onputFilesPath, path[path.length -1].replaceFirst("[.][^.]+$", ""));
		fW = new FileWriter(ff);					
	
		try {
			fW.write(JSONValue.toJSONString(mObj));
			fW.write(NEW_LINE_SEPARATOR);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}*/
}