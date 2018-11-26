package org.herac.tuxguitar.app.system.icons;

import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGSkinManager {
	
	private TGContext context;
	private String currentSkin;
	
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
		return TGConfigManager.getInstance(this.context).getStringValue(TGConfigKeys.SKIN);
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
