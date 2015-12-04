package org.herac.tuxguitar.android.action.impl.caret;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGMoveToAxisPositionAction extends TGActionBase{
	
	public static final String NAME = "action.caret.move-to-axis-position";
	
	public static final String ATTRIBUTE_X = "positionX";
	public static final String ATTRIBUTE_Y = "positionY";
	
	public TGMoveToAxisPositionAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		Float x = context.getAttribute(ATTRIBUTE_X);
		Float y = context.getAttribute(ATTRIBUTE_Y);
		
		if( x != null && y != null ) {
			getEditor().getAxisSelector().select(x, y);
		}
	}
}
