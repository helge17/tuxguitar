/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.marker;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.marker.MarkerList;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ListMarkersAction extends TGActionBase{
	
	public static final String NAME = "action.marker.list";
	
	public ListMarkersAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE);
	}
	
	protected void processAction(TGActionContext context){
		if( MarkerList.instance().isDisposed() ){
			MarkerList.instance().show();
		}
		else{
			MarkerList.instance().dispose();
		}
	}
}
