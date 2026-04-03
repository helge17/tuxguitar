package app.tuxguitar.app.action.impl.tools;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.tools.scale.ScaleManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGSelectScaleAction extends TGActionBase{

	public static final String NAME = "action.tools.select-scale";

	public static final String ATTRIBUTE_INDEX = "scaleIndex";
	public static final String ATTRIBUTE_KEY = "scaleKey";

	public TGSelectScaleAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		Integer index = context.getAttribute(ATTRIBUTE_INDEX);
		Integer key = context.getAttribute(ATTRIBUTE_KEY);

		ScaleManager scaleManager = ScaleManager.getInstance(getContext());
		scaleManager.selectScale(index != null ? index : ScaleManager.NONE_SELECTION, key != null ? key : 0);
	}
}
