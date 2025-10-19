package app.tuxguitar.app.system.icons;

import app.tuxguitar.app.system.config.TGConfigDefaults;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGSkinManager {

	private TGContext context;
	private String currentSkin;

	private boolean darkModeInitialized = false;
	// naming convention for skins
	private static final String SUFFIX_DARK = "-dark";

	private TGSkinManager(TGContext context){
		this.context = context;
		this.loadSkin();
	}

	public void addLoader(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGSkinEvent.EVENT_TYPE, listener);
	}

	public void removeLoader(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGSkinEvent.EVENT_TYPE, listener);
	}

	private void fireChanges(){
		TGEventManager.getInstance(this.context).fireEvent(new TGSkinEvent());
	}

	public void loadSkin() {
		this.currentSkin = this.getCurrentSkin();
	}

	public void reloadSkin() {
		this.loadSkin();

		TGIconManager.getInstance(this.context).onSkinChange();
		TGColorManager.getInstance(this.context).onSkinChange();

		this.fireChanges();
	}

	public boolean shouldReload(){
		return (this.currentSkin == null || !this.currentSkin.equals(this.getCurrentSkin()));
	}

	public String getCurrentSkin() {
		String configuredSkin = TGConfigManager.getInstance(this.context).getStringValue(TGConfigKeys.SKIN);
		Boolean skinDarkAuto = Boolean.TRUE.equals(TGConfigManager.getInstance(this.context).getBooleanValue(TGConfigKeys.SKIN_DARK_AUTO));
		
		// try to switch light/dark theme automatically (just once, when app starts)
		if (skinDarkAuto && !this.darkModeInitialized && (TGWindow.getInstance(context).getWindow() != null)) {
			this.darkModeInitialized = true;
			UIColor background = TGWindow.getInstance(context).getWindow().getBgColor();
			// not a good brightness indicator, but simple and efficient enough to distinguish light/dark theme
			int bgSum = background.getRed() + background.getGreen() + background.getBlue();
			Boolean forceTheme = bgSum < 150 ? Boolean.TRUE : null;
			forceTheme = bgSum > 600 ? Boolean.FALSE : forceTheme;
			String selectedSkin = null;
			if ((Boolean.FALSE.equals(forceTheme)) && configuredSkin.endsWith(SUFFIX_DARK)) {
				selectedSkin = configuredSkin.substring(0, configuredSkin.length() - SUFFIX_DARK.length());
			}
			else if ((Boolean.TRUE.equals(forceTheme)) && !configuredSkin.endsWith(SUFFIX_DARK)) {
				selectedSkin = configuredSkin + SUFFIX_DARK;
			}
			if ((selectedSkin != null) && (getSkinInfo(selectedSkin).getValue("name") != null)) {
				TGConfigManager.getInstance(this.context).setValue(TGConfigKeys.SKIN, selectedSkin);
				this.reloadSkin();
				return selectedSkin;
			}
		}
		
		// does skin exist?
		TGProperties skinInfo = getSkinInfo(configuredSkin);
		if (skinInfo.getValue("name") != null) {
			return configuredSkin;
		} else {
			// Use case: user has upgraded TuxGuitar, and configured skin was deleted in the new version
			// overwrite configured skin: replace by default
			TGConfigManager.getInstance(this.context).setValue(TGConfigKeys.SKIN, TGConfigDefaults.DEFAULT_SKIN);
			return TGConfigDefaults.DEFAULT_SKIN;
		}
	}

	public TGProperties getCurrentSkinInfo() {
		return this.getSkinInfo(this.getCurrentSkin());
	}

	public TGProperties getSkinInfo(String skin) {
		TGPropertiesManager propertiesManager =  TGPropertiesManager.getInstance(this.context);
		TGProperties properties = propertiesManager.createProperties();
		propertiesManager.readProperties(properties, TGSkinInfoHandler.RESOURCE, skin);

		return properties;
	}

	public TGProperties getCurrentSkinProperties() {
		return this.getSkinProperties(this.getCurrentSkin());
	}

	public TGProperties getSkinProperties(String skin) {
		TGPropertiesManager propertiesManager =  TGPropertiesManager.getInstance(this.context);
		TGProperties properties = propertiesManager.createProperties();
		propertiesManager.readProperties(properties, TGSkinPropertiesHandler.RESOURCE, skin);

		return properties;
	}

	public void dispose() {
		TGIconManager.getInstance(this.context).onSkinDisposed();
		TGColorManager.getInstance(this.context).onSkinDisposed();
	}

	public static TGSkinManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGSkinManager.class.getName(), new TGSingletonFactory<TGSkinManager>() {
			public TGSkinManager createInstance(TGContext context) {
				return new TGSkinManager(context);
			}
		});
	}
}
