package org.herac.tuxguitar.graphics;

public class TGFontModel {
	
	private String name;
	private int height;
	private boolean bold;
	private boolean italic;
	
	public TGFontModel(){
		this(null,0,false,false);
	}
	
	public TGFontModel(String name, int height, boolean bold, boolean italic){
		this.name = name;
		this.height = height;
		this.bold = bold;
		this.italic = italic;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public boolean isBold() {
		return this.bold;
	}
	
	public void setBold(boolean bold) {
		this.bold = bold;
	}
	
	public boolean isItalic() {
		return this.italic;
	}
	
	public void setItalic(boolean italic) {
		this.italic = italic;
	}
}
