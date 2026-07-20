package app.tuxguitar.android.browser.assets;

import app.tuxguitar.android.R;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.tools.browser.base.TGBrowserSettings;
import app.tuxguitar.util.TGContext;

public class TGAssetBrowserSettings  {

	private static final String DEFAULT_ID = "browser-assets";
	private static final String DEFAULT_PATH = "demo-songs";

	private String title;

	public TGAssetBrowserSettings(TGContext context){
		super();
		this.title = TGActivityController.getInstance(context).getActivity().getString(R.string.storage_saf_assets_provider_title);
	}

	public String getId(){
		return DEFAULT_ID;
	}

	public String getTitle(){
		return this.title;
	}

	public String getPath(){
		return DEFAULT_PATH;
	}

	public boolean equals(Object o) {
		return (this.hashCode() == o.hashCode());
	}

	public int hashCode() {
		return (TGAssetBrowserSettings.class.getName() + "-" + this.getId()).hashCode();
	}

	public TGBrowserSettings toBrowserSettings() {
		TGBrowserSettings settings = new TGBrowserSettings();
		settings.setTitle(this.getTitle());
		settings.setData(this.getId());
		return settings;
	}
}
