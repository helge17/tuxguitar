package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.app.system.plugins.base.TGImporterPlugin;
import org.herac.tuxguitar.io.base.TGRawImporter;

public class ABCPluginImporter extends TGImporterPlugin{
	
	protected TGRawImporter getImporter() {
		return new ABCSongImporter();
	}
	
	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}
}
