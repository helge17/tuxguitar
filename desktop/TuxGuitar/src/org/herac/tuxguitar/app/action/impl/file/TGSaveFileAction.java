package org.herac.tuxguitar.app.action.impl.file;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.document.TGDocumentFileManager;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.file.TGWriteSongAction;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.util.TGContext;

public class TGSaveFileAction extends TGActionBase {
	
	public static final String NAME = "action.file.save";
	
	public TGSaveFileAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		List<TGFileFormat> fileFormats = this.createFileFormats(context);
		
		TGDocumentFileManager tgDocumentFileManager = TGDocumentFileManager.getInstance(getContext());
		tgDocumentFileManager.findFileNameForSave(fileFormats, new TGFileChooserHandler() {
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
