package app.tuxguitar.android.storage;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.storage.browser.TGBrowserProvider;
import app.tuxguitar.android.storage.saf.TGSafProvider;
import app.tuxguitar.util.TGAbstractContext;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGStorageManager {

	private TGContext context;
	private TGStorageProvider provider;

	private TGStorageManager(TGContext context) {
		this.context = context;
		this.createListeners();
	}

	public void createListeners() {
		TGActionManager.getInstance(this.context).addPostExecutionListener(new TGStorageEventListener(this.context));
	}

	public void openDocument() {
		TGStorageProvider provider = this.getProvider();
		if( provider != null ) {
			provider.openDocument();
		}
	}

	public void saveDocument() {
		TGStorageProvider provider = this.getProvider();
		if( provider != null ) {
			provider.saveDocument();
		}
	}

	public void saveDocumentAs() {
		TGStorageProvider provider = this.getProvider();
		if( provider != null ) {
			provider.saveDocumentAs();
		}
	}

	public void updateSession(TGAbstractContext source) {
		TGStorageProvider provider = this.getProvider();
		if( provider != null ) {
			provider.updateSession(source);
		}
	}

	public TGStorageProvider getProvider() {
		if( this.provider == null ) {
			this.loadSettings();
		}
		return provider;
	}

	public void loadSettings() {
		boolean useSafProvider = false;
		if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT ) {
			useSafProvider = new TGStorageProperties(this.context).isUseSafProvider();
		}
		if( useSafProvider && (this.provider == null || !this.provider.getClass().equals(TGSafProvider.class))) {
			this.provider = new TGSafProvider(this.context);
		}
		else if(!useSafProvider && (this.provider == null || !this.provider.getClass().equals(TGBrowserProvider.class))) {
			this.provider = new TGBrowserProvider(this.context);
		}
	}

	public static TGStorageManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGStorageManager.class.getName(), new TGSingletonFactory<TGStorageManager>() {
			public TGStorageManager createInstance(TGContext context) {
				return new TGStorageManager(context);
			}
		});
	}
}
