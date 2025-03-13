package app.tuxguitar.app.action.impl.file;

import java.io.InputStream;
import java.net.URL;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.app.util.TGFileUtils;
import app.tuxguitar.app.view.dialog.message.TGMessageDialog;
import app.tuxguitar.app.view.dialog.message.TGMessageDialogController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.file.TGLoadSongAction;
import app.tuxguitar.editor.action.file.TGReadSongAction;
import app.tuxguitar.io.base.TGFileFormatUtils;
import app.tuxguitar.util.TGContext;

public class TGReadURLAction extends TGActionBase {

	public static final String NAME = "action.url.read";

	public static final String ATTRIBUTE_URL = URL.class.getName();

	public TGReadURLAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGDocumentListManager docListManager = TGDocumentListManager.getInstance(getContext());
		try {
			URL url = context.getAttribute(ATTRIBUTE_URL);
			TGDocument alreadyOpenedDoc = null;
			for (TGDocument doc : docListManager.getDocuments()) {
				if (url.toURI().equals(doc.getUri())) {
					alreadyOpenedDoc = doc;
					break;
				}
			}
			if (alreadyOpenedDoc != null) {
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, alreadyOpenedDoc.getSong());
				TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
				tgActionManager.execute(TGLoadSongAction.NAME, context);
			} else {
				InputStream stream = (TGFileUtils.isLocalFile(url) ? url.openStream() : TGFileFormatUtils.getInputStream(url.openStream()));
				context.setAttribute(TGReadSongAction.ATTRIBUTE_FORMAT_CODE, TGFileFormatUtils.getFileFormatCode(url.getFile()));
				context.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, stream);

				TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
				tgActionManager.execute(TGReadSongAction.NAME, context);

				// if a newer version of file format was detected, warn user (recommend app upgrade)
				if (Boolean.TRUE.equals(context.getAttribute(TGReadSongAction.IS_NEWER_FILE_FORMAT))) {
					context.setAttribute(TGMessageDialog.ATTRIBUTE_TITLE, TuxGuitar.getProperty("warning"));
					context.setAttribute(TGMessageDialog.ATTRIBUTE_STYLE, TGMessageDialog.STYLE_WARNING);
					context.setAttribute(TGMessageDialog.ATTRIBUTE_MESSAGE,TuxGuitar.getProperty("warning.new-minor-version"));
					context.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGMessageDialogController());
					tgActionManager.execute(TGOpenViewAction.NAME, context);
				}

			}
		} catch (Throwable e) {
			throw new TGActionException(e.getMessage(), e);
		}
	}
}
