package org.herac.tuxguitar.io.abc.base;

import java.util.ArrayList;
import java.util.List;

public class ABCInfo {
	
	private String title;
	private String subtitle;
	private String comments;
	private String componist;
	private String artist;
	private String area;
	private String book;
	private String discography;
	private String filename;
	private String group;
	private List<String> history;
	private List<String> note;
	private String information;
	private String origin;
	private String source;
	private String transcriptor;
	
	public ABCInfo(String title, String subtitle, String comments) {
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
		if(this.note==null) return null;
		String s="";
		for(int i=0;i<note.size();i++) s+=(String)note.get(i)+"\n";
		return s;
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

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @param subtitle the subtitle to set
	 */
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public void setArtist(String string) {
		this.artist=string;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	public void setComponist(String string) {
		this.componist=string;
	}

	public void setArea(String string) {
		this.area=string;
	}

	/**
	 * @return the area
	 */
	public String getArea() {
		return area;
	}
	/**
	 * @return the componist
	 */
	public String getComponist() {
		return componist;
	}

	public void setBook(String string) {
		this.book=string;
	}
	/**
	 * @return the book 
	 */
	public String getBook() {
		return book;
	}

	public void setDiscography(String string) {
		this.discography=string;
	}
	/**
	 * @return the discography 
	 */
	public String getDiscography() {
		return discography;
	}

	public void setFilename(String string) {
		this.filename=string;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	public void setGroup(String string) {
		this.group = string;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	public void addHistory(String string) {
		if(history==null) history=new ArrayList<String>();
		history.add(string);
	}

	public void setInformation(String string) {
		this.information=string;
	}

	/**
	 * @return the history
	 */
	public List<String> getHistory() {
		return history;
	}

	/**
	 * @return the information
	 */
	public String getInformation() {
		return information;
	}

	public void addNote(String string) {
		if(note==null) note=new ArrayList<String>();
		note.add(string);
	}

	public void setOrigin(String string) {
		this.origin=string;
	}

	public void setSource(String string) {
		this.source=string;
	}

	public void setTranscriptor(String string) {
		this.transcriptor = string;
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the transcriptor
	 */
	public String getTranscriptor() {
		return transcriptor;
	}
}
