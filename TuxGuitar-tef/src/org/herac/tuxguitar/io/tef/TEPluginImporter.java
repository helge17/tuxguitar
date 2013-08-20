package org.herac.tuxguitar.io.tef;

import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.io.plugin.TGImporterPlugin;

public class TEPluginImporter extends TGImporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-tef";
	
	protected TGRawImporter getImporter() {
		return new TESongImporter();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
