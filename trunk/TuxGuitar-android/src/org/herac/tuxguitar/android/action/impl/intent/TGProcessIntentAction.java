package org.herac.tuxguitar.android.action.impl.intent;

import java.io.InputStream;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.util.TGContext;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

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
				ContentResolver resolver = activity.getContentResolver();
				Uri uri = intent.getData();
				
				InputStream stream = resolver.openInputStream(uri);
				try {
					context.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, stream);
					
					TGActionManager.getInstance(getContext()).execute(TGReadSongAction.NAME, context);
				} finally {
					stream.close();
				}
			}
		} catch(Throwable throwable){
			throw new TGActionException(throwable);
		}
	}
}