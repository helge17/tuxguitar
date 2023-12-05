package org.herac.tuxguitar.android.action.impl.intent;

import android.content.Intent;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.action.impl.storage.uri.TGUriReadAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.util.TGContext;

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