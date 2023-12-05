package org.herac.tuxguitar.ui.swt.chooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.FontDialog;
import org.herac.tuxguitar.ui.chooser.UIFontChooser;
import org.herac.tuxguitar.ui.chooser.UIFontChooserHandler;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.swt.resource.SWTFont;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;

public class SWTFontChooser implements UIFontChooser {

	private SWTWindow window;
	private String text;
	private UIFontModel defaultModel;
	
	public SWTFontChooser(SWTWindow window) {
		this.window = window;
	}
	
	public void choose(UIFontChooserHandler selectionHandler) {
		FontDialog dlg = new FontDialog(this.window.getControl());
		if( this.text != null ) {
			dlg.setText(this.text);
		}
		
		Font defaultFont = null;
		if( this.defaultModel != null ) {
			defaultFont = new SWTFont(this.window.getControl().getDisplay(), this.defaultModel).getControl();
			dlg.setFontList(defaultFont.getFontData());
		}
		
		FontData fd = dlg.open();
		
		if( defaultFont != null ) {
			defaultFont.dispose();
		}
		
		selectionHandler.onSelectFont(fd != null ? new UIFontModel(fd.getName(), fd.getHeight(), ((fd.getStyle() & SWT.BOLD) != 0), ((fd.getStyle() & SWT.ITALIC) != 0)) : null); 
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultModel(UIFontModel defaultModel) {
		this.defaultModel = defaultModel;
	}
}
