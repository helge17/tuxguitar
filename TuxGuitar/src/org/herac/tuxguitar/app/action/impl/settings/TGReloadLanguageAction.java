package org.herac.tuxguitar.app.action.impl.settings;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.system.language.TGLanguageManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGReloadLanguageAction extends TGActionBase {
	
	public static final String NAME = "action.system.reload-language";
	
	public TGReloadLanguageAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGConfigManager config = TGConfigManager.getInstance(this.getContext());
		TGLanguageManager languageManager = TuxGuitar.getInstance().getLanguageManager();
		
		boolean changed = Boolean.TRUE.equals(context.getAttribute(TGReloadSettingsAction.ATTRIBUTE_FORCE));
		if(!changed){
			String languageLoaded = languageManager.getLanguage();
			String languageConfigured = config.getStringValue(TGConfigKeys.LANGUAGE);
			if( languageLoaded == null && languageConfigured == null ){
				changed = false;
			}
			else if(languageLoaded != null && languageConfigured != null){
				changed = ( !languageLoaded.equals( languageConfigured ) );
			}
			else {
				changed = true;
			}
		}
		
		if( changed ){
			languageManager.setLanguage(config.getStringValue(TGConfigKeys.LANGUAGE));
		}
	}
}