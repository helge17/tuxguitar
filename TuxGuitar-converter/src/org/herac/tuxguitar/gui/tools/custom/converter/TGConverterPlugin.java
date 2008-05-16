package org.herac.tuxguitar.gui.tools.custom.converter;

public class TGConverterPlugin extends org.herac.tuxguitar.gui.system.plugins.base.TGToolItemPlugin {
	
	protected void doAction() {
		new TGConverterDialog().show();
	}
	
	protected String getItemName() {
		return "File format batch converter";
	}
	
	public String getName() {
		return "BatchConverter";
	}
	
	public String getAuthor() {
		return "Nikola Kolarovic & Julian Casadesus";
	}
	
	public String getDescription() {
		return "Converts folder containing various tab formats into wanted file format.\n" +
		       "Depending on your loaded file format plugins, you can read and write.";
	}
	
	public String getVersion() {
		return "1.0";
	}
}
