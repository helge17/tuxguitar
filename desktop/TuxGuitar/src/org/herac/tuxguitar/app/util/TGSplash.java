package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.widget.UISplashWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGSplash {
	
	private TGContext context;
	private UISplashWindow shell;
	
	private TGSplash(TGContext context){
		this.context = context;
	}
	
	public void init() {
		if( TGConfigManager.getInstance(this.context).getBooleanValue(TGConfigKeys.SHOW_SPLASH)){
			TGIconManager iconManager = TGIconManager.getInstance(this.context);
			UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
			
			this.shell = uiFactory.createSplashWindow();
			this.shell.setText(TGApplication.NAME);
			this.shell.setImage(iconManager.getAppIcon());
			this.shell.setSplashImage(iconManager.getAppSplash());
			this.shell.open();
		}
	}
	
	public void finish(){
		if( this.shell != null && !this.shell.isDisposed()){
			this.shell.dispose();
		}
		this.context.removeAttribute(TGSplash.class.getName());
	}
	
	public static TGSplash getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGSplash.class.getName(), new TGSingletonFactory<TGSplash>() {
			public TGSplash createInstance(TGContext context) {
				return new TGSplash(context);
			}
		});
	}
}
