package app.tuxguitar.app.action.impl.file;

import java.io.File;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.document.TGDocumentFileManager;
import app.tuxguitar.app.util.TGMessageDialogUtil;
import app.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGSetBackingTrackAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGSelectBackingTrackAction extends TGActionBase {

	public static final String NAME = "action.file.select-backing-track";

	public TGSelectBackingTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext actionContext) {
		TGFileFormat fileFormat = new TGFileFormat(TuxGuitar.getProperty("file.backing-track.audio"), "audio/*", new String[] {"mp3", "wav", "aif", "aiff", "au"});
		TGDocumentFileManager tgDocumentFileManager = TGDocumentFileManager.getInstance(getContext());
		tgDocumentFileManager.chooseFileNameForOpen(fileFormat, new TGFileChooserHandler() {
			public void updateFileName(final String fileName) {
				File file = new File(fileName);
				if( !file.isFile() || !file.canRead() ) {
					UIWindow window = TGWindow.getInstance(getContext()).getWindow();
					TGMessageDialogUtil.errorMessage(getContext(), window, TuxGuitar.getProperty("file.backing-track.error", new String[] {file.getName()}));
					return;
				}
				TGActionProcessor tgActionProcessor = new TGActionProcessor(getContext(), TGSetBackingTrackAction.NAME);
				tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, TGDocumentManager.getInstance(getContext()).getSong());
				tgActionProcessor.setAttribute(TGSetBackingTrackAction.ATTRIBUTE_PATH, file.getAbsolutePath());
				tgActionProcessor.processOnNewThread();
			}
		});
	}
}
