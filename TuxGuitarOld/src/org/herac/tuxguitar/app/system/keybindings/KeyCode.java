package org.herac.tuxguitar.app.system.keybindings;

public class KeyCode {
	
	private int code;
	private String key;
	
	public KeyCode(int code, String key){
		this.code = code;
		this.key = key;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public String getKey(){
		return this.key;
	}
}
