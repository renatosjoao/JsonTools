/**
 *
 * @author  Renato Stoffalette Joao
 * @version 1.0
 * @since   2015-04 
 */
package de.l3s.myowncode;

public class JSONLine {
	Long timestamp;
	String field;
	String url;
	String c;
	
	
	
	public JSONLine() {
		super();
	}

	public JSONLine(Long timestamp, String field, String url, String c) {
		super();
		this.timestamp = timestamp;
		this.field = field;
		this.url = url;
		this.c = c;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}

	
	
}
