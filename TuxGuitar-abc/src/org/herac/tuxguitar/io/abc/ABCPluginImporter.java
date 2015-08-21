package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.io.plugin.TGImporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class ABCPluginImporter extends TGImporterPlugin{
	
	protected TGRawImporter createImporter(TGContext context) {
		return new ABCSongImporter();
	}
	
	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}
}
