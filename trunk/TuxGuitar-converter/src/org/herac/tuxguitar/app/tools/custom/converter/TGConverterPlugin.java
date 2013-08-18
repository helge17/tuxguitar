package org.herac.tuxguitar.app.tools.custom.converter;


public class TGConverterPlugin extends org.herac.tuxguitar.app.system.plugins.base.TGToolItemPlugin {
	
	public static final String MODULE_ID = "tuxguitar-converter";
	
	protected void doAction() {
		new TGConverterDialog().show();
	}
	
	protected String getItemName() {
		return "File format batch converter";
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
