package org.herac.tuxguitar.android.action.impl.view;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.view.keyboard.TGTabKeyboard;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGToggleTabKeyboardAction extends TGActionBase{
	
	public static final String NAME = "action.view.toogle-tab-keyboard";
	
	public TGToggleTabKeyboardAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		TGSynchronizer.getInstance(getContext()).executeLater(new Runnable() {
			public void run() throws TGException {
				TGTabKeyboard tgTabKeyboard = TGTabKeyboard.getInstance(getContext());
				if( tgTabKeyboard != null ) {
					tgTabKeyboard.toggleVisibility();
				}
			}
		});
	}
}
