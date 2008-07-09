package org.herac.tuxguitar.io.gtp;



public class GTPSettings {
	
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	private String charset;
	
	public GTPSettings(){
		//windows-31j
		//x-IBM943
		//x-IBM943C
		//x-PCK
		//
		//ISO-8859-15
		//
		//UTF-8
		//this.charset = "x-IBM943C";
		this.charset = DEFAULT_CHARSET;
	}
	
	public String getCharset() {
		return this.charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
}
