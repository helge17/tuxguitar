package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.io.plugin.TGImporterPlugin;

public class ABCPluginImporter extends TGImporterPlugin{
	
	protected TGRawImporter getImporter() {
		return new ABCSongImporter();
	}
	
	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}
}
