package org.herac.tuxguitar.android.browser.filesystem;

import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;

public class TGFsBrowserSettings implements TGBrowserSettings{
	
	private static final String STRING_SEPARATOR = ";";
	
	private String title;
	private String path;
	
	public TGFsBrowserSettings(String title,String path){
		this.title = title;
		this.path = path;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getPath(){
		return this.path;
	}
	
	public String toString(){
		return (getTitle() + STRING_SEPARATOR + getPath());
	}
	
	public static TGBrowserSettings fromString(String string) {
		String[] data = string.split(STRING_SEPARATOR);
		if(data.length == 2){
			return new TGFsBrowserSettings(data[0],data[1]);
		}
		return null;
	}
}
