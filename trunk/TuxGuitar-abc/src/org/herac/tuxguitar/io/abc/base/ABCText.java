package org.herac.tuxguitar.io.abc.base;

public class ABCText {
	
	private String value;
	
	public ABCText(String value){
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
