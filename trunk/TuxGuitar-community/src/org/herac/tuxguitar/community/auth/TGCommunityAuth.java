package org.herac.tuxguitar.community.auth;

import java.security.MessageDigest;

import org.herac.tuxguitar.community.auth.utils.Base64Decoder;
import org.herac.tuxguitar.community.auth.utils.Base64Encoder;
import org.herac.tuxguitar.gui.system.config.TGConfigManager;

public class TGCommunityAuth {
	
	private static final String STRING_SEPARATOR = ";";
	
	private String username;
	private String password;
	private String authCode;
	
	public TGCommunityAuth(){
		this.username = new String();
		this.password = new String();
		this.authCode = new String();
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAuthCode() {
		return this.authCode;
	}
	
	public boolean isEmpty(){
		return ( this.username == null || this.password == null || this.username.length() == 0 || this.password.length() == 0 );
	}
	
	public void update(){
		try {
			String passwordMD5 = new String();
			if( this.password != null && this.password.length() > 0 ){
				MessageDigest md = MessageDigest.getInstance("md5");
				md.update(this.password.getBytes());
				byte[] digest = md.digest();
				for (int i = 0; i < digest.length; i++){
					passwordMD5 += Integer.toHexString((digest[i] >> 4) & 0xf);
					passwordMD5 += Integer.toHexString((digest[i] & 0xf));
				}
			}
			this.authCode = new String(Base64Encoder.encode( new String(this.username + ";" + passwordMD5 ).getBytes() ) );
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void save( TGConfigManager config ){
		String data = new String(this.getUsername() + STRING_SEPARATOR + this.getPassword() );
		String encodedData = new String(Base64Encoder.encode( data.getBytes() ) ) ;
		config.setProperty("community.account", encodedData );
	}
	
	public void load( TGConfigManager config ) {
		String encodedData = config.getStringConfigValue("community.account");
		if( encodedData != null && encodedData.length() > 0 ){
			String data = new String(Base64Decoder.decode( encodedData.getBytes() ));
			String[] values = data.split(STRING_SEPARATOR);
			if(values.length == 2){
				this.setUsername( values[0] );
				this.setPassword( values[1] );
			}
		}
	}
}
