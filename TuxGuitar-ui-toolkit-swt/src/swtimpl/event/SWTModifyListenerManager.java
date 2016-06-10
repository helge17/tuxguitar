package swtimpl.event;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.herac.tuxguitar.ui.event.UIModifyEvent;
import org.herac.tuxguitar.ui.event.UIModifyListenerManager;

import swtimpl.SWTComponent;

public class SWTModifyListenerManager extends UIModifyListenerManager implements ModifyListener {
	
	private SWTComponent<?> control;
	
	public SWTModifyListenerManager(SWTComponent<?> control) {
		this.control = control;
	}
	
	public void modifyText(ModifyEvent e) {
		this.onModify(new UIModifyEvent(this.control));
	}
}
