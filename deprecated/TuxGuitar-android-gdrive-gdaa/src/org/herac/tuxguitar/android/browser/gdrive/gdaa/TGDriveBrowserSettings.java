package org.herac.tuxguitar.android.browser.gdrive.gdaa;

import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;

public class TGDriveBrowserSettings  {
	
	public static final String DEFAULT_TITLE = TGDriveBrowserFactory.BROWSER_NAME;
	public static final String DEFAULT_ACCOUNT = "default";
	
	private String title;
	private String account;
	
	public TGDriveBrowserSettings(String title, String account){
		this.title = title;
		this.account = account;
	}
	
	public TGDriveBrowserSettings(){
		this(DEFAULT_TITLE, DEFAULT_ACCOUNT);
	}
	
	public String getTitle() {
		return title;
	}

	public String getAccount() {
		return account;
	}
	
	public boolean isDefaultAccount() {
		return (DEFAULT_ACCOUNT.equals(this.getAccount()));
	}
	
	public boolean equals(Object o) {
		return (this.hashCode() == o.hashCode());
	}
	
	public int hashCode() {
		return (TGDriveBrowserSettings.class.getName() + "-" + this.getAccount()).hashCode();
	}
	
	public TGBrowserSettings toBrowserSettings() {
		TGBrowserSettings settings = new TGBrowserSettings();
		settings.setTitle(this.getTitle());
		settings.setData(this.getAccount());
		return settings;
	}
	
	public static TGDriveBrowserSettings createInstance(TGBrowserSettings settings) {
		return new TGDriveBrowserSettings(settings.getTitle(), settings.getData());
	}
}
