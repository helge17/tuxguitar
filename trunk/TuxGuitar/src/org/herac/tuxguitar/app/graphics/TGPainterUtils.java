package org.herac.tuxguitar.app.graphics;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;

public class TGPainterUtils {
	
	/** On swt-carbon (and maybe another platform) advanced mode must be allways true **/
	public static final boolean FORCE_OS_DEFAULTS = getValue(TGConfigKeys.FORCE_OS_DEFAULTS);
	
	private static boolean getValue(String key){
		return TuxGuitar.getInstance().getConfig().getBooleanValue(key);
	}
}
