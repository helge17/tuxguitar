package app.tuxguitar.android.action.impl.intent;

import android.content.Intent;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.action.impl.storage.uri.TGUriReadAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.util.TGContext;

public class TGProcessIntentAction extends TGActionBase{

	public static final String NAME = "action.intent.process";

	public static final String ATTRIBUTE_ACTIVITY = TGActivity.class.getName();

	public TGProcessIntentAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		try{
			TGActivity activity = context.getAttribute(ATTRIBUTE_ACTIVITY);

			Intent intent = activity.getIntent();
			if( intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
				context.setAttribute(TGUriReadAction.ATTRIBUTE_URI, intent.getData());

				TGActionManager.getInstance(getContext()).execute(TGUriReadAction.NAME, context);
			}
		} catch(Throwable throwable){
			throw new TGActionException(throwable);
		}
	}
}