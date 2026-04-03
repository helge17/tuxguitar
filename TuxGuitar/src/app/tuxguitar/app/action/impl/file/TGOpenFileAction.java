package app.tuxguitar.app.action.impl.file;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.document.TGDocumentFileManager;
import app.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.file.TGReadSongAction;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.util.TGContext;

public class TGOpenFileAction extends TGActionBase {

	public static final String NAME = "action.file.open";

	public TGOpenFileAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		List<TGFileFormat> fileFormats = this.createFileFormats(context);

		TGDocumentFileManager tgDocumentFileManager = TGDocumentFileManager.getInstance(getContext());
		tgDocumentFileManager.chooseFileNameForOpen(fileFormats, new TGFileChooserHandler() {
			public void updateFileName(final String fileName) {
				final URL url = createUrl(fileName);
				if( url != null ) {
					new Thread(new Runnable() {
						public void run() {
							context.setAttribute(TGReadURLAction.ATTRIBUTE_URL, url);

							TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
							tgActionManager.execute(TGReadURLAction.NAME, context);
						}
					}).start();
				}
			}
		});
	}

	public URL createUrl(String fileName) {
		try {
			File file = new File(fileName);
			if( file.exists() && file.isFile() ){
				return file.toURI().toURL();
			}
			return null;
		} catch (Throwable e) {
			throw new TGActionException(e.getMessage(), e);
		}
	}

	public List<TGFileFormat> createFileFormats(final TGActionContext context) {
		TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(getContext());
		TGFileFormat fileFormat = context.getAttribute(TGReadSongAction.ATTRIBUTE_FORMAT);
		if( fileFormat == null ) {
			return fileFormatManager.findReadFileFormats(true);
		}

		List<TGFileFormat> fileFormats = new ArrayList<TGFileFormat>();
		fileFormats.add(fileFormat);

		return fileFormats;
	}
}