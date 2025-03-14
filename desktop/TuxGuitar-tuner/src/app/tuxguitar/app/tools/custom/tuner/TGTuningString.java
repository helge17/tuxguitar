/**
 *
 */
package app.tuxguitar.app.tools.custom.tuner;

import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UIToggleButton;
import app.tuxguitar.util.TGMusicKeyUtils;


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
		this.stringButton.setText("--------- "+ TGMusicKeyUtils.sharpNoteFullName(string)+" ---------");
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
