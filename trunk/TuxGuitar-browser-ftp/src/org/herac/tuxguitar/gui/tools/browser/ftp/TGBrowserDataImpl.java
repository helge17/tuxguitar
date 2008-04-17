package org.herac.tuxguitar.gui.tools.browser.ftp;

import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.gui.tools.browser.ftp.utils.Base64Decoder;
import org.herac.tuxguitar.gui.tools.browser.ftp.utils.Base64Encoder;

public class TGBrowserDataImpl implements TGBrowserData{

	private static final String STRING_SEPARATOR = ";";
	
	private String host;
	private String path;
	private String username;
	private String password;
	
	public TGBrowserDataImpl(String host,String path,String username,String password){
		this.host = host;
		this.path = path;
		this.username = username;
		this.password = password;
	}

	public String getHost() {
		return this.host;
	}

	public String getPath() {
		return this.path;
	}
	
	public String getPassword() {
		return ((this.username != null && this.username.length() > 0)?this.password:"anonymous");
	}

	public String getUsername() {
		return ((this.username != null && this.username.length() > 0)?this.username:"anonymous");
	}

	public String getTitle(){
		return (getHost() + ":" + getPath());
	}
	
	public String toString(){
		String username = new String( Base64Encoder.encode( getUsername().getBytes() ) );
		String password = new String( Base64Encoder.encode( getPassword().getBytes() ) );
		return getHost() + STRING_SEPARATOR + getPath() + STRING_SEPARATOR + username + STRING_SEPARATOR + password;
	}

	public static TGBrowserData fromString(String string) {
		String[] data = string.split(STRING_SEPARATOR);
		if(data.length == 4){
			String username = new String( Base64Decoder.decode( data[2].getBytes() ) );
			String password = new String( Base64Decoder.decode( data[3].getBytes() ) );
			return new TGBrowserDataImpl(data[0],data[1],username,password);
		}
		return null;
	}
}
