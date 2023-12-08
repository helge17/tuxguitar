package org.herac.tuxguitar.community.io;

public class TGShareFile {
	
	private byte[] file;
	private String title;
	private String description;
	private String tagkeys;
	
	public TGShareFile(){
		this.title = new String();
		this.description = new String();
		this.tagkeys = new String();
	}
	
	public byte[] getFile() {
		return this.file;
	}
	
	public void setFile(byte[] file) {
		this.file = file;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTagkeys() {
		return this.tagkeys;
	}
	
	public void setTagkeys(String tagkeys) {
		this.tagkeys = tagkeys;
	}
	
	public String getFilename(){
		return ( this.title + ".tg" );
	}
}
