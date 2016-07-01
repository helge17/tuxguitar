package org.herac.tuxguitar.ui.resource;

public class UIFontModel {
	
	public static final String DEFAULT_NAME = "Default";
	
	private String name;
	private float height;
	private boolean bold;
	private boolean italic;
	
	public UIFontModel(){
		this(null,0,false,false);
	}
	
	public UIFontModel(String name, float height, boolean bold, boolean italic){
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
	
	public float getHeight() {
		return this.height;
	}
	
	public void setHeight(float height) {
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
