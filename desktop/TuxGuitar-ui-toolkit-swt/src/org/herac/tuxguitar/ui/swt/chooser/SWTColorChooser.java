package org.herac.tuxguitar.ui.swt.chooser;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.herac.tuxguitar.ui.chooser.UIColorChooser;
import org.herac.tuxguitar.ui.chooser.UIColorChooserHandler;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;

public class SWTColorChooser implements UIColorChooser {

	private SWTWindow window;
	private String text;
	private UIColorModel defaultModel;
	
	public SWTColorChooser(SWTWindow window) {
		this.window = window;
	}
	
	public void choose(UIColorChooserHandler selectionHandler) {
		ColorDialog dlg = new ColorDialog(this.window.getControl());
		if( this.text != null ) {
			dlg.setText(this.text);
		}
		if( this.defaultModel != null ) {
			dlg.setRGB(new RGB(this.defaultModel.getRed(), this.defaultModel.getGreen(), this.defaultModel.getBlue()));
		}
		
		RGB rgb = dlg.open();
		
		selectionHandler.onSelectColor(rgb != null ? new UIColorModel(rgb.red, rgb.green, rgb.blue) : null); 
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultModel(UIColorModel defaultModel) {
		this.defaultModel = defaultModel;
	}
}
