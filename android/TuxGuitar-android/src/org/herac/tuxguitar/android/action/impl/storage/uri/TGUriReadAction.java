package org.herac.tuxguitar.android.action.impl.storage.uri;

import android.app.Activity;
import android.net.Uri;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.util.TGStreamUtil;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.io.base.TGFileFormatUtils;
import org.herac.tuxguitar.util.TGContext;

import java.io.InputStream;

public class TGUriReadAction extends TGActionBase {

	public static final String NAME = "action.storage.uri.read-uri";

	public static final String ATTRIBUTE_ACTIVITY = TGActivity.class.getName();
	public static final String ATTRIBUTE_URI = Uri.class.getName();

	public TGUriReadAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		try{
			Uri uri = context.getAttribute(ATTRIBUTE_URI);
			Activity activity = context.getAttribute(ATTRIBUTE_ACTIVITY);

			InputStream bufferedStream = null;
			InputStream inputStream = activity.getContentResolver().openInputStream(uri);
			try {
				bufferedStream = TGStreamUtil.getInputStream(inputStream);
			} finally {
				inputStream.close();
			}
			if( bufferedStream != null ) {
				context.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, bufferedStream);
				context.setAttribute(TGReadSongAction.ATTRIBUTE_FORMAT_CODE, TGFileFormatUtils.getFileFormatCode(uri.getLastPathSegment()));

				TGActionManager.getInstance(getContext()).execute(TGReadSongAction.NAME, context);
			}
		} catch(Throwable throwable){
			throw new TGActionException(throwable);
		}
	}
}