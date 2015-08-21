package org.herac.tuxguitar.app.tools.custom.converter;

import org.herac.tuxguitar.util.TGContext;


public class TGConverterPlugin extends org.herac.tuxguitar.app.tools.custom.TGToolItemPlugin {
	
	public static final String MODULE_ID = "tuxguitar-converter";
	
	protected void doAction(TGContext context) {
		new TGConverterDialog(context).show();
	}
	
	protected String getItemName() {
		return "File format batch converter";
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
