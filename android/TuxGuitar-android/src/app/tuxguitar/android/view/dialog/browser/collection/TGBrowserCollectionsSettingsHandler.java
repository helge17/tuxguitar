package app.tuxguitar.android.view.dialog.browser.collection;

import app.tuxguitar.android.browser.TGBrowserCollection;
import app.tuxguitar.android.browser.TGBrowserManager;
import app.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import app.tuxguitar.android.browser.model.TGBrowserSettings;
import app.tuxguitar.util.error.TGErrorManager;

public class TGBrowserCollectionsSettingsHandler implements TGBrowserFactorySettingsHandler {

	private TGBrowserCollectionsDialog dialog;
	private String type;

	public TGBrowserCollectionsSettingsHandler(TGBrowserCollectionsDialog dialog, String type) {
		this.dialog = dialog;
		this.type = type;
	}

	public void onCreateSettings(final TGBrowserSettings settings) {
		this.dialog.postWhenReady(new Runnable() {
			public void run() {
				postOnCreateSettings(settings);
			}
		});
	}

	@Override
	public void handleError(final Throwable throwable) {
		this.dialog.postWhenReady(new Runnable() {
			public void run() {
				postHandleError(throwable);
			}
		});
	}

	public void postOnCreateSettings(TGBrowserSettings settings) {
		TGBrowserManager browserManager = TGBrowserManager.getInstance(this.dialog.findContext());
		TGBrowserCollection collection = browserManager.createCollection(this.type, settings);

		this.dialog.addCollection(collection);
	}

	public void postHandleError(Throwable throwable) {
		TGErrorManager.getInstance(this.dialog.findContext()).handleError(throwable);
	}
}
