package org.herac.tuxguitar.android.browser.saf;

import android.net.Uri;

import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;

public class TGSafBrowserSettings {

	private String title;
	private Uri uri;
	
	public TGSafBrowserSettings(String title, Uri uri){
		this.title = title;
		this.uri = uri;
	}

	public String getTitle(){
		return this.title;
	}

	public Uri getUri(){
		return this.uri;
	}

	public TGBrowserSettings toBrowserSettings() {
		TGBrowserSettings settings = new TGBrowserSettings();
		settings.setTitle(this.getTitle());
		settings.setData(this.getUri().toString());
		return settings;
	}
	
	public static TGSafBrowserSettings createInstance(TGBrowserSettings settings) {
		return new TGSafBrowserSettings(settings.getTitle(), Uri.parse(settings.getData()));
	}
}
