package de.l3s.myowncode;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveBMP {

	public static void main(String[] args) throws UnsupportedEncodingException {

		String inputString = "� Motörhead [ˈmoʊtərhɛd] ist eine 1975 gegrün­dete britische Musik­gruppe. "
				+ "Am 16. Juni 2015 feiert sie das 40-jährige Band­jubiläum. Die Musik von Motör­head vereint "
				+ "Einflüsse aus Punk, Hard Rock, Rock ’n’ Roll und Blues Rock. Ihr Ein­fluss auf andere Musiker ist "
				+ "im Ver­gleich zum eigenen kommerziellen Erfolg groß.";
		//String sanitizedString = inputString.replaceAll("[^\u0000-\uFFFF]", "");
		String utf8tweet = "";
		try {
			byte[] utf8Bytes = "#Hello twitter  How are you��W��f?".getBytes("UTF-8");
			utf8tweet = new String(utf8Bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]",Pattern.UNICODE_CASE | Pattern.CANON_EQ| Pattern.CASE_INSENSITIVE);
		Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8tweet);
		
		String cleanText = inputString.toString();
		cleanText = cleanText.replaceAll("127", ""); //removing funny characters
		
		String strangeString = "#@HelloA tBwCiDtEtFer123 RSTUV How ArCe you��W��!f?";
		String nonStrange = strangeString.replaceAll("\\p{Cntrl}", ""); 
		//System.out.println(new String(inputString.getBytes("ISO-8859-1")));
		
		/*
		 * 
		 */
		System.out.println(strangeString.replaceAll("[ [\uFFFD] | [\u0000-\u001F] | [\u007F-\u009F] | [\u0041-\u005A] | [\u202A-\u202F] | [\uF0B3-\uF0BF] ]", ""));
//		\u200E, \u200F, 
		//[\u0000-\u001F] | [ \u007F-\u009F] | [\u0030-\u0032] | |
			
	}

}
