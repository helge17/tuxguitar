package org.herac.tuxguitar.gui.editors;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;

public class TGPainterUtils {
	
	/** On swt-carbon (and maybe another platform) advanced mode must be allways true **/
	public static final boolean FORCE_OS_DEFAULTS = getValue(TGConfigKeys.FORCE_OS_DEFAULTS);
	
	private static boolean getValue(String key){
		return TuxGuitar.instance().getConfig().getBooleanConfigValue(key);
	}
}
