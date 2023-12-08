package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.herac.tuxguitar.ui.event.UIModifyEvent;
import org.herac.tuxguitar.ui.event.UIModifyListenerManager;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTModifyListenerManager extends UIModifyListenerManager implements ModifyListener {
	
	private SWTEventReceiver<?> control;
	
	public SWTModifyListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}
	
	public void modifyText(ModifyEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.onModify(new UIModifyEvent(this.control));
		}
	}
}
