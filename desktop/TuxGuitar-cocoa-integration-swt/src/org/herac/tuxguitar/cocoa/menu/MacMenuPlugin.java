package org.herac.tuxguitar.cocoa.menu;

import org.herac.tuxguitar.cocoa.TGCocoaIntegrationPlugin;
import org.herac.tuxguitar.ui.resource.UIKey;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGKeyBindFormatter;
import org.herac.tuxguitar.util.TGKeyBindFormatter.TGKeyTranslator;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MacMenuPlugin implements TGPlugin {

	private MacMenu macMenu;
	private final static Map<String, String> macKeySymbols;

	static {
		Map<String, String> keys = new HashMap<>();
		keys.put(UIKey.ESC.toString(), "\u238b");
		keys.put(UIKey.PAUSE.toString(), "F15");
		keys.put(UIKey.PRINT_SCREEN.toString(), "F13");
		keys.put(UIKey.INSERT.toString(), "\uF746");
		keys.put(UIKey.DELETE.toString(), "\u2326");
		keys.put(UIKey.HOME.toString(), "\u2196");
		keys.put(UIKey.PAGE_UP.toString(), "\u21DE");
		keys.put(UIKey.PAGE_DOWN.toString(), "\u21DE");
		keys.put(UIKey.END.toString(), "\u2198");
		keys.put(UIKey.ALT.toString(), "\u2325");
		keys.put(UIKey.CONTROL.toString(), "\u2303");
		keys.put(UIKey.COMMAND.toString(), "\u2318");
		keys.put(UIKey.SHIFT.toString(), "\u21E7");
		keys.put(UIKey.TAB.toString(), "\u21E5");
		keys.put(UIKey.BACKSPACE.toString(), "\u232B");
		keys.put(UIKey.SPACE.toString(), "Space");
		keys.put(UIKey.ENTER.toString(), "\u21A9");
		keys.put(UIKey.UP.toString(), "\u2191");
		keys.put(UIKey.DOWN.toString(), "\u2193");
		keys.put(UIKey.LEFT.toString(), "\u2190");
		keys.put(UIKey.RIGHT.toString(), "\u2192");
		keys.put(UIKey.NUMPAD_ENTER.toString(), "\u2324");
		macKeySymbols = keys;
	}

	private final static class MacKeyTranslator implements TGKeyTranslator {
		public String translate(List<String> keyCombination) {
			StringBuffer fullMask = new StringBuffer();
			for (String key : keyCombination) {
				String translated = macKeySymbols.get(key);
				if (translated != null) {
					fullMask.append(translated);
				} else {
					fullMask.append(key.substring(0, 1).toUpperCase() + key.substring(1));
				}
			}
			return fullMask.toString();
		}
	}

	private final static MacKeyTranslator translator = new MacKeyTranslator();

	public void setEnabled(TGContext context, boolean enabled) throws TGPluginException {
		try {
			if( this.macMenu != null ){
				this.macMenu.setEnabled(enabled);
			}else if(enabled) {
				this.macMenu = new MacMenu(context);
				this.macMenu.setEnabled(true);
				this.macMenu.init();
			}
			TGKeyBindFormatter.getInstance().setTranslator(enabled ? translator : null);
		} catch( Throwable throwable ){
			throw new TGPluginException( throwable );
		}
	}

	public String getModuleId() {
		return TGCocoaIntegrationPlugin.MODULE_ID;
	}

	public void connect(TGContext context) throws TGPluginException {
		this.setEnabled(context, true);
	}

	public void disconnect(TGContext context) throws TGPluginException {
		this.setEnabled(context, false);
	}
}
