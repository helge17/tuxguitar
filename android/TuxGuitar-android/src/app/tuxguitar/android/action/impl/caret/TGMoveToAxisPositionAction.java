package app.tuxguitar.android.action.impl.caret;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.view.tablature.TGSongViewSmartMenu;
import app.tuxguitar.util.TGContext;

public class TGMoveToAxisPositionAction extends TGActionBase{

	public static final String NAME = "action.caret.move-to-axis-position";

	public static final String ATTRIBUTE_X = "positionX";
	public static final String ATTRIBUTE_Y = "positionY";
	public static final String ATTRIBUTE_REQUEST_SMART_MENU = TGSongViewSmartMenu.REQUEST_SMART_MENU;

	public TGMoveToAxisPositionAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		Float x = context.getAttribute(ATTRIBUTE_X);
		Float y = context.getAttribute(ATTRIBUTE_Y);
		Boolean requestSmartMenu = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_REQUEST_SMART_MENU));

		if( x != null && y != null ) {
			getEditor().getAxisSelector().select(x, y, requestSmartMenu);
		}
	}
}
