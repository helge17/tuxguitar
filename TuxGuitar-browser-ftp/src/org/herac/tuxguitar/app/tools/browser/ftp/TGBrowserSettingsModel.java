package org.herac.tuxguitar.app.tools.browser.ftp;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserSettings;
import org.herac.tuxguitar.app.tools.browser.ftp.utils.Base64Decoder;
import org.herac.tuxguitar.app.tools.browser.ftp.utils.Base64Encoder;

public class TGBrowserSettingsModel {
	
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
	
	public TGBrowserSettingsModel(String name, String host, String path, String username, String password, String proxyUser, String proxyPwd, String proxyHost, int proxyPort) {
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
	
	public TGBrowserSettings toBrowserSettings() {		
		StringBuffer sb = new StringBuffer();
		sb.append(getHost());
		sb.append(STRING_SEPARATOR);
		if( getPath() != null ) {
			sb.append(getPath());
		}
		sb.append(STRING_SEPARATOR);
		if( getUsername() != null ) {
			sb.append(new String(Base64Encoder.encode(getUsername().getBytes())));
		}
		sb.append(STRING_SEPARATOR);
		if( getPassword() != null ) {
			sb.append(new String(Base64Encoder.encode(getPassword().getBytes())));
		}
		sb.append(STRING_SEPARATOR);
		if( getProxyUser() != null ) {
			sb.append(new String(Base64Encoder.encode(getProxyUser().getBytes())));
		}
		sb.append(STRING_SEPARATOR);
		if( getProxyPwd() != null ) {
			sb.append(new String(Base64Encoder.encode(getProxyPwd().getBytes())));
		}
		sb.append(STRING_SEPARATOR);
		if( getProxyHost() != null ) {
			sb.append(getProxyHost());
		}
		sb.append(STRING_SEPARATOR);
		sb.append(getProxyPort());
		
		TGBrowserSettings settings = new TGBrowserSettings();
		settings.setTitle(this.getName());
		settings.setData(sb.toString());
		
		return settings;
	}
	
	public static TGBrowserSettingsModel createInstance(TGBrowserSettings settings) {
		String[] data = settings.getData().split(STRING_SEPARATOR);
		if( data.length == 8){
			String username = new String(Base64Decoder.decode( data[2].getBytes()));
			String password = new String(Base64Decoder.decode( data[3].getBytes()));
			String proxyUser = new String(Base64Decoder.decode( data[4].getBytes()));
			String proxyPwd = new String(Base64Decoder.decode( data[5].getBytes()));
			return new TGBrowserSettingsModel(settings.getTitle(), data[0], data[1], username, password,  proxyUser, proxyPwd, data[6], Integer.parseInt(data[7]));
		}
		
		return null;
	}
}
