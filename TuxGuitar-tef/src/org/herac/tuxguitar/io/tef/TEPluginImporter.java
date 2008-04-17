package org.herac.tuxguitar.io.tef;

import org.herac.tuxguitar.gui.system.plugins.base.TGImporterPlugin;
import org.herac.tuxguitar.io.base.TGSongImporter;

public class TEPluginImporter extends TGImporterPlugin{

	protected TGSongImporter getImporter() {
		return new TESongImporter();
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}

	public String getName() {
		return "TEF file format importer";
	}	
	
	public String getDescription() {
		return "TEF file format importer";
	}

	public String getVersion() {
		return "1.0-rc3";
	}
	
}
