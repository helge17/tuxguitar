package org.herac.tuxguitar.app.action.impl.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.document.TGDocumentFileManager;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public class TGImportFileAction extends TGSongStreamActionBase {
	
	public static final String NAME = "action.file.import";
	
	public TGImportFileAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context){
		final TGSongStreamContext streamContext = this.findSongStreamContext(context);
		final TGLocalFileImporter importer = context.getAttribute(ATTRIBUTE_PROVIDER);
		TGDocumentFileManager tgDocumentFileManager = TGDocumentFileManager.getInstance(getContext());
		tgDocumentFileManager.chooseFileNameForOpen(importer.getFileFormat(), new TGFileChooserHandler() {
			public void updateFileName(final String fileName) {
				final InputStream stream = createInputStream(fileName);
				if( stream != null ) {
					streamContext.setAttribute(InputStream.class.getName(), stream);
					
					new Thread(new Runnable() {
						public void run() {
							TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
							tgActionManager.execute(TGImportSongAction.NAME, context);
						}
					}).start();
				}
			}
		});
	}
	
	public InputStream createInputStream(String fileName) {
		try {
			File file = new File(fileName);
			if( file.exists() && file.isFile() ){
				return new BufferedInputStream(new FileInputStream(file));
			}
			return null;
		} catch (Throwable e) {
			throw new TGActionException(e.getMessage(), e);
		}
	}
}
