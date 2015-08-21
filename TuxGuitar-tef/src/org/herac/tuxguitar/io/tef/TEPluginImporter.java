package org.herac.tuxguitar.io.tef;

import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.io.plugin.TGImporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class TEPluginImporter extends TGImporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-tef";
	
	protected TGRawImporter createImporter(TGContext context) {
		return new TESongImporter();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
