package org.herac.tuxguitar.io.tef.base;

public class TEInfo {
	
	private String title;
	
	private String subtitle;
	
	private String comments;
	
	private String notes;
	
	public TEInfo(String title, String subtitle, String comments) {
		this.title = title;
		this.subtitle = subtitle;
		this.comments = comments;
	}
	
	public String getComments() {
		return this.comments;
	}
	
	public String getSubtitle() {
		return this.subtitle;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getNotes() {
		return this.notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String toString(){
		String string = new String("[INFO]");
		string += "\n     Title:       " + getTitle();
		string += "\n     Subtitle:    " + getSubtitle();
		string += "\n     Comments:    " + getComments();
		if(this.getNotes() != null){
			string += "\n     Notes:       " + getNotes();
		}
		return string;
	}
}
