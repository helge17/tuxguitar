package app.tuxguitar.android.browser.saf;

import android.net.Uri;

import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.android.browser.model.TGBrowserFactory;
import app.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import app.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import app.tuxguitar.android.browser.model.TGBrowserSettings;
import app.tuxguitar.util.TGContext;

import androidx.documentfile.provider.DocumentFile;

public class TGSafBrowserFactory implements TGBrowserFactory{

	public static final String BROWSER_TYPE = "saf";
	public static final String BROWSER_NAME = "Storage Folder";

	private TGContext context;

	public TGSafBrowserFactory(TGContext context) {
		this.context = context;
	}

	public String getType(){
		return BROWSER_TYPE;
	}

	public String getName(){
		return BROWSER_NAME;
	}

	public void createBrowser(final TGBrowserFactoryHandler handler, final TGBrowserSettings settings) {
		handler.onCreateBrowser(new TGSafBrowser(this.context, TGSafBrowserSettings.createInstance(settings)));
	}

	public void createSettings(final TGBrowserFactorySettingsHandler handler) {
		TGSafBrowserUriRequest request = new TGSafBrowserUriRequest(this.context, new TGSafBrowserUriHandler() {
			public void onUriAccessGranted(Uri uri) {
				if( uri != null ) {
					handler.onCreateSettings(new TGSafBrowserSettings(createSettingsName(uri), uri).toBrowserSettings());
				}
			}
		});
		request.process();
	}

	public String createSettingsName(Uri uri) {
		TGActivity activity = TGActivityController.getInstance(this.context).getActivity();
		DocumentFile documentFile = DocumentFile.fromTreeUri(activity, uri);
		return activity.getString(R.string.browser_settings_title_format, documentFile.getName());
	}
}
