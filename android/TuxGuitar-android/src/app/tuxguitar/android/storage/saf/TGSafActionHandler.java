package app.tuxguitar.android.storage.saf;

import android.content.Intent;
import android.net.Uri;

import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.action.impl.gui.TGStartActivityForResultAction;
import app.tuxguitar.android.action.impl.storage.uri.TGUriReadAction;
import app.tuxguitar.android.action.impl.storage.uri.TGUriWriteAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogController;
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogHandler;
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogOption;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.util.TGContext;

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
