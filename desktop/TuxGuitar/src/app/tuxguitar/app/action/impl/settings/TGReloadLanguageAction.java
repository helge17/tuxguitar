package app.tuxguitar.app.action.impl.settings;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.system.language.TGLanguageManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGMessagesManager;

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
			String languageLoaded = TGMessagesManager.getInstance().getLanguage();
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