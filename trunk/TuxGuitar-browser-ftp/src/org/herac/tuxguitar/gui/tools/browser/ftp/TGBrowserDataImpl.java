package org.herac.tuxguitar.gui.tools.browser.ftp;

import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.gui.tools.browser.ftp.utils.Base64Decoder;
import org.herac.tuxguitar.gui.tools.browser.ftp.utils.Base64Encoder;

public class TGBrowserDataImpl implements TGBrowserData{
	
	private static final String STRING_SEPARATOR = ";";
	
	private String name;
	private String host;
	private String path;
	private String username;
	private String password;
	private String proxyUser;
	private String proxyPwd;
	private String proxyHost;
	private int proxyPort;
	
	public TGBrowserDataImpl(String name, String host, String path, String username, String password, String proxyUser, String proxyPwd, String proxyHost, int proxyPort) {
		this.name = name;
		this.host = host;
		this.path = path;
		this.username = username;
		this.password = password;
		this.proxyUser = proxyUser;
		this.proxyPwd = proxyPwd;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String getPassword() {
		return ((this.username != null && this.username.length() > 0)?this.password:TGBrowserFTPClient.DEFAULT_USER_PASSWORD);
	}
	
	public String getUsername() {
		return ((this.username != null && this.username.length() > 0)?this.username:TGBrowserFTPClient.DEFAULT_USER_NAME);
	}
	
	public String getTitle(){
		return getName();
	}
	
	public String getProxyHost() {
		return this.proxyHost;
	}

	public int getProxyPort() {
		return this.proxyPort;
	}

	public String getProxyUser() {
		return this.proxyUser;
	}

	public String getProxyPwd() {
		return this.proxyPwd;
	}

	public String toString(){
		String username = new String( Base64Encoder.encode( getUsername().getBytes() ) );
		String password = new String( Base64Encoder.encode( getPassword().getBytes() ) );
		String	proxyUser = new String( Base64Encoder.encode( getProxyUser().getBytes() ));
		String	proxyPwd = new String( Base64Encoder.encode( getProxyPwd().getBytes() ));
		
		return getName() + STRING_SEPARATOR + getHost() + STRING_SEPARATOR
				+ getPath() + STRING_SEPARATOR + username + STRING_SEPARATOR
				+ password + STRING_SEPARATOR + proxyUser + STRING_SEPARATOR
				+ proxyPwd + STRING_SEPARATOR + getProxyHost()
				+ STRING_SEPARATOR + getProxyPort();
	}
	
	public static TGBrowserData fromString(String string) {
		String[] data = string.split(STRING_SEPARATOR);
		if(data.length == 9){
			String username = new String( Base64Decoder.decode( data[3].getBytes() ) );
			String password = new String( Base64Decoder.decode( data[4].getBytes() ) );
			String proxyUser = new String( Base64Decoder.decode( data[5].getBytes() ));
			String proxyPwd = new String( Base64Decoder.decode( data[6].getBytes() ) );
			return new TGBrowserDataImpl(data[0], data[1], data[2], username, password,  proxyUser, proxyPwd, data[7], Integer.parseInt(data[8]));
		}
		return null;
	}
}
