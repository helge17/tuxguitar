package org.herac.tuxguitar.io.tef.base;

public class TEText {
	
	private String value;
	
	public TEText(String value){
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String toString(){
		String string = new String("[TEXT]");
		string += "\n     Value:       " + getValue();
		return string;
	}
}
