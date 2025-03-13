package app.tuxguitar.android.action.impl.storage.uri;

import android.app.Activity;
import android.net.Uri;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.util.TGContext;

import java.io.OutputStream;

public class TGUriWriteAction extends TGActionBase {

	public static final String NAME = "action.storage.uri.write-uri";

	public static final String ATTRIBUTE_ACTIVITY = TGActivity.class.getName();
	public static final String ATTRIBUTE_URI = Uri.class.getName();

	public TGUriWriteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		try{
			Uri uri = context.getAttribute(ATTRIBUTE_URI);
			Activity activity = context.getAttribute(ATTRIBUTE_ACTIVITY);

			OutputStream outputStream = activity.getContentResolver().openOutputStream(uri);
			try {
				context.setAttribute(TGWriteSongAction.ATTRIBUTE_OUTPUT_STREAM, outputStream);

				TGActionManager.getInstance(getContext()).execute(TGWriteSongAction.NAME, context);
			} finally {
				outputStream.close();
			}
		} catch(Throwable throwable){
			throw new TGActionException(throwable);
		}
	}
}