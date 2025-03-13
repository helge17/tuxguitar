package app.tuxguitar.app.tools.custom.converter;

import app.tuxguitar.util.TGContext;


public class TGConverterPlugin extends app.tuxguitar.app.tools.custom.TGToolItemPlugin {

	public static final String MODULE_ID = "tuxguitar-converter";

	protected void doAction(TGContext context) {
		new TGConverterDialog(context).show();
	}

	protected String getItemName() {
		return "batch.converter";
	}

	public String getModuleId(){
		return MODULE_ID;
	}
}
