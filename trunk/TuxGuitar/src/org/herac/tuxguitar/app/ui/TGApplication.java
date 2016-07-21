package org.herac.tuxguitar.app.ui;

import java.util.Iterator;

import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGServiceReader;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGApplication {
	
	public static final String NAME = "TuxGuitar";
	
	private TGContext context;
	private UIApplication application;
	private UIFactory factory;
	
	public TGApplication(TGContext context) {
		this.context = context;
		this.application = this.lookupApplication();
		this.application.setApplicationName(NAME);
		this.factory = this.application.getFactory();
	}
	
	private UIApplication lookupApplication() {
		Iterator<UIApplication> it = TGServiceReader.lookupProviders(UIApplication.class, TGResourceManager.getInstance(this.context));
		if( it.hasNext() ){
			return it.next();
		}
		throw new TGException("No implementation class found for: " + UIApplication.class.getName());
	}
	
	public UIApplication getApplication() {
		return application;
	}

	public UIFactory getFactory() {
		return factory;
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
