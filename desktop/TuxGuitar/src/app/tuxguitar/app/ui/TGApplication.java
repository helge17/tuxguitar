package app.tuxguitar.app.ui;

import java.util.Iterator;

import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.ui.UIApplication;
import app.tuxguitar.ui.UIApplicationFactory;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.appearance.UIAppearance;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGServiceReader;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGApplication {

	public static final String NAME = "TuxGuitar";

	private TGContext context;
	private UIApplication application;
	private UIAppearance appearance;
	private UIFactory factory;

	public TGApplication(TGContext context) {
		this.context = context;
		this.application = this.lookupApplication().createApplication(NAME);
		this.appearance = this.application.getAppearance();
		this.factory = this.application.getFactory();
	}

	private UIApplicationFactory lookupApplication() {
		Iterator<UIApplicationFactory> it = TGServiceReader.lookupProviders(UIApplicationFactory.class, TGResourceManager.getInstance(this.context));
		if( it.hasNext() ){
			return it.next();
		}
		throw new TGException("No implementation class found for: " + UIApplicationFactory.class.getName());
	}

	public UIApplication getApplication() {
		return this.application;
	}


	public UIAppearance getAppearance() {
		return this.appearance;
	}

	public UIFactory getFactory() {
		return this.factory;
	}

	public boolean isDisposed() {
		return this.application.isDisposed();
	}

	public void dispose() {
		this.application.dispose();
	}

	public static TGApplication getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGApplication.class.getName(), new TGSingletonFactory<TGApplication>() {
			public TGApplication createInstance(TGContext context) {
				return new TGApplication(context);
			}
		});
	}
}
