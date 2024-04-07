package org.herac.tuxguitar.android.browser.gdrive;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.activity.TGActivityPermissionRequest;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactory;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;
import org.herac.tuxguitar.util.TGContext;

public class TGDriveBrowserFactory implements TGBrowserFactory{
	
	public static final String BROWSER_TYPE = "google-drive";
	public static final String BROWSER_NAME = "Google Drive";

	public static final String[] PERMISSIONS = {
			"android.permission.MANAGE_ACCOUNTS",
			"android.permission.GET_ACCOUNTS",
			"android.permission.USE_CREDENTIALS"
	};

	private TGContext context;
	
	public TGDriveBrowserFactory(TGContext context) {
		this.context = context;
	}
	
	public String getType(){
		return BROWSER_TYPE;
	}
	
	public String getName(){
		return BROWSER_NAME;
	}
	
	public void createBrowser(final TGBrowserFactoryHandler handler, final TGBrowserSettings data) {
		this.runWithPermissions(new Runnable() {
			public void run() {
				handler.onCreateBrowser(new TGDriveBrowser(TGDriveBrowserFactory.this.context, TGDriveBrowserSettings.createInstance(data)));
			}
		});
	}

	public void createSettings(final TGBrowserFactorySettingsHandler handler) {
		this.runWithPermissions(new Runnable() {
			public void run() {
				new TGDriveBrowserSettingsFactory(TGDriveBrowserFactory.this.context, handler).createSettings();
			}
		});
	}

	public void runWithPermissions(final Runnable runnable) {
		TGActivity activity = TGActivityController.getInstance(this.context).getActivity();
		if( activity != null ) {
			new TGActivityPermissionRequest(activity, PERMISSIONS, null, new Runnable() {
				public void run() {
					runnable.run();
				}
			}, null).process();
		}
	}
}
