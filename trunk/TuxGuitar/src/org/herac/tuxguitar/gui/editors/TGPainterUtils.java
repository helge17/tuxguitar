package org.herac.tuxguitar.gui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;

public class TGPainterUtils {
	
	/** On swt-carbon (and maybe another platform) advanced mode must be allways true **/
	private static final boolean FORCE_OS_DEFAULTS = getValue(TGConfigKeys.FORCE_OS_DEFAULTS);
	
	public static void beforePath(GC gc){
		if(!FORCE_OS_DEFAULTS){
			/** Not all swt platforms set antialias as default **/
			gc.setAntialias(SWT.ON);
		}
	}
	
	public static void beforeString(GC gc){
		if(!FORCE_OS_DEFAULTS){
			/** Draw an advanced String to another advanced GC cause problems on some platforms **/
			gc.setAdvanced(false);
		}
	}
	
	public static void beforeImage(GC gc){
		if(!FORCE_OS_DEFAULTS){
			/** Draw an advanced Image to another advanced GC cause problems on some platforms **/
			gc.setAdvanced(false);
		}
	}
	
	private static boolean getValue(String key){
		return TuxGuitar.instance().getConfig().getBooleanConfigValue(key);
	}
}
