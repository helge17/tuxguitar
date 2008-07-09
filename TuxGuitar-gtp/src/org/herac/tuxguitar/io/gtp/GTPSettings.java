package org.herac.tuxguitar.io.gtp;

public class GTPSettings {
	
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	private String charset;
	
	public GTPSettings(){
		this.charset = DEFAULT_CHARSET;
	}
	
	public String getCharset() {
		return this.charset;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
}
