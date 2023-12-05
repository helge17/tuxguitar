/**
 * 
 */
package org.herac.tuxguitar.app.tools.custom.tuner;

import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIToggleButton;

/**
 * @author Nikola Kolarovic <johnny47ns@yahoo.com>
 *
 */
public class TGTuningString {

	private int string;
	private UIToggleButton stringButton = null;
	private TGTunerListener listener = null;
	
	
	
	TGTuningString(UIFactory factory, UIContainer parent, TGTunerListener listener, int string) {
		this.string = string;
		this.listener = listener;
		
		this.stringButton = factory.createToggleButton(parent);
		this.stringButton.setText("--------- "+TGTunerRoughWidget.TONESSTRING[string%12]+(int)Math.floor(string/12)+" ---------");
	}

	
	
	void addListener() {
		this.stringButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTuningString.this.stringButton.setSelected(true);
				TGTuningString.this.listener.fireCurrentString(TGTuningString.this.string);
			}
		});
	}
	
	public int getString() {
		return this.string;
	}
	
	public UIToggleButton getStringButton() {
		return this.stringButton;
	}
	
}
