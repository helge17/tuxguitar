package app.tuxguitar.app.util;

import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.widget.UISplashWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

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
