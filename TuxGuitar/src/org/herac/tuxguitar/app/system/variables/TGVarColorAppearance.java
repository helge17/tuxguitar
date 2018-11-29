package org.herac.tuxguitar.app.system.variables;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.ui.appearance.UIColorAppearance;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.util.TGContext;

public class TGVarColorAppearance {
	
	private TGContext context;
	private UIColorAppearance colorAppearance;
	
	public TGVarColorAppearance(TGContext context, UIColorAppearance colorAppearance) {
		this.context = context;
		this.colorAppearance = colorAppearance;
	}
	
	public String getName() {
		return ("ui" + this.colorAppearance.name());
	}
	
	public UIColorModel getValue() {
		return TGApplication.getInstance(this.context).getApplication().getAppearance().getColorModel(this.colorAppearance);
	}
	
	public String toString() {
		UIColorModel colorModel = getValue();
		
		return (colorModel.getRed() + "," + colorModel.getGreen() + "," + colorModel.getBlue());
	}
	
	public static List<TGVarColorAppearance> createVars(TGContext context) {
		List<TGVarColorAppearance> varColorAppearances = new ArrayList<TGVarColorAppearance>();
		
		UIColorAppearance[] colorAppearances = UIColorAppearance.values();
		for(UIColorAppearance colorAppearance : colorAppearances) {
			varColorAppearances.add(new TGVarColorAppearance(context, colorAppearance));
		}
		return varColorAppearances;
	}
}
