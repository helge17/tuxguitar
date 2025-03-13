package app.tuxguitar.app.action.impl.file;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.document.TGDocumentFileManager;
import app.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.util.TGContext;

public class TGSaveAsFileAction extends TGActionBase {

	public static final String NAME = "action.file.save-as";

	public TGSaveAsFileAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		List<TGFileFormat> fileFormats = this.createFileFormats(context);

		TGDocumentFileManager tgDocumentFileManager = TGDocumentFileManager.getInstance(getContext());
		tgDocumentFileManager.chooseFileNameForSave(fileFormats, new TGFileChooserHandler() {
			public void updateFileName(final String fileName) {
				new Thread(new Runnable() {
					public void run() {
						context.setAttribute(TGWriteFileAction.ATTRIBUTE_FILE_NAME, fileName);

						TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
						tgActionManager.execute(TGWriteFileAction.NAME, context);
					}
				}).start();
			}
		});
	}

	public List<TGFileFormat> createFileFormats(final TGActionContext context) {
		TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(getContext());
		TGFileFormat fileFormat = context.getAttribute(TGWriteSongAction.ATTRIBUTE_FORMAT);
		if( fileFormat == null ) {
			return fileFormatManager.findWriteFileFormats(true);
		}

		List<TGFileFormat> fileFormats = new ArrayList<TGFileFormat>();
		fileFormats.add(fileFormat);

		return fileFormats;
	}
}
