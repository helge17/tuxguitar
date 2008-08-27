/**
 * 
 */
package org.herac.tuxguitar.gui.tools.custom.tuner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Nikola Kolarovic <johnny47ns@yahoo.com>
 *
 */
public class TGTuningString {

	private int string;
	private Button stringButton = null;
	private TGTunerListener listener = null;
	
	
	
	TGTuningString(int string, Composite parent, TGTunerListener listener) {
		this.string = string;
		this.listener = listener;
		
		this.stringButton = new Button(parent,SWT.TOGGLE);
		this.stringButton.setText("--------- "+TGTunerRoughWidget.TONESSTRING[string%12]+(int)Math.floor(string/12)+" ---------");
	}

	
	
	void addListener() {
		this.stringButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
					TGTuningString.this.stringButton.setSelection(true);
					TGTuningString.this.listener.fireCurrentString(TGTuningString.this.string);
			}
			
		});
	}
	
	public int getString() {
		return this.string;
	}
	
	public Button getStringButton() {
		return this.stringButton;
	}
	
}
