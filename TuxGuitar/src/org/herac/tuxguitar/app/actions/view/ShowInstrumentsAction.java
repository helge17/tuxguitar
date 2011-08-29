/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.view;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ShowInstrumentsAction extends Action{
	public static final String NAME = "action.view.show-instruments";
	
	public ShowInstrumentsAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		if(TuxGuitar.instance().getChannelManager().isDisposed()){
			TuxGuitar.instance().getChannelManager().show();
		}else{
			TuxGuitar.instance().getChannelManager().dispose();
		}
		return 0;
	}
}
