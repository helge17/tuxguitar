package org.herac.tuxguitar.android.storage.saf;

import android.content.Intent;
import android.net.Uri;

import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.action.impl.gui.TGStartActivityForResultAction;
import org.herac.tuxguitar.android.action.impl.storage.uri.TGUriReadAction;
import org.herac.tuxguitar.android.action.impl.storage.uri.TGUriWriteAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.view.dialog.chooser.TGChooserDialogController;
import org.herac.tuxguitar.android.view.dialog.chooser.TGChooserDialogHandler;
import org.herac.tuxguitar.android.view.dialog.chooser.TGChooserDialogOption;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.file.TGWriteSongAction;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.util.TGContext;

import java.util.List;

public class TGSafActionHandler {

	private TGContext context;

	public TGSafActionHandler(TGContext context) {
		this.context = context;
	}

	public TGActionProcessor createActionProcessor(String id) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, id);
		tgActionProcessor.setAttribute(TGActivity.class.getName(), TGActivityController.getInstance(this.context).getActivity());
		return tgActionProcessor;
	}

	public void callStartActivityForResult(Intent intent, TGSafBaseHandler resultHandler) {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGStartActivityForResultAction.NAME);
		tgActionProcessor.setAttribute(TGStartActivityForResultAction.ATTRIBUTE_INTENT, intent);
		tgActionProcessor.setAttribute(TGStartActivityForResultAction.ATTRIBUTE_REQUEST_CODE, resultHandler.getRequestCode());
		tgActionProcessor.process();
	}

	public void callReadUri(Uri uri) {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGUriReadAction.NAME);
		tgActionProcessor.setAttribute(TGUriReadAction.ATTRIBUTE_URI, uri);
		tgActionProcessor.process();
	}

	public void callWriteUri(Uri uri, TGFileFormat fileFormat) {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGUriWriteAction.NAME);
		tgActionProcessor.setAttribute(TGUriWriteAction.ATTRIBUTE_URI, uri);
		tgActionProcessor.setAttribute(TGWriteSongAction.ATTRIBUTE_FORMAT, fileFormat);
		tgActionProcessor.process();
	}

	public <T> void callChooserDialog(String title, List<TGChooserDialogOption<T>> options, TGChooserDialogHandler<T> handler) {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGChooserDialogController<T>());
		tgActionProcessor.setAttribute(TGChooserDialogController.ATTRIBUTE_TITLE, title);
		tgActionProcessor.setAttribute(TGChooserDialogController.ATTRIBUTE_OPTIONS, options);
		tgActionProcessor.setAttribute(TGChooserDialogController.ATTRIBUTE_HANDLER, handler);
		tgActionProcessor.process();
	}
}
