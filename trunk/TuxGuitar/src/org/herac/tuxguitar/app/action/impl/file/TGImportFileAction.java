package org.herac.tuxguitar.app.action.impl.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.FileChooser;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public class TGImportFileAction extends TGSongStreamActionBase {
	
	public static final String NAME = "action.file.import";
	
	public TGImportFileAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context){
		try {
			final TGSongStreamContext streamContext = this.findSongStreamContext(context);
			final TGLocalFileImporter importer = context.getAttribute(ATTRIBUTE_PROVIDER);
			
			final String path = FileChooser.instance().open(TuxGuitar.getInstance().getShell(),importer.getFileFormat());
			if(!isValidFile(path) /*|| !importer.configure(false)*/){
				return;
			}
			
			final InputStream stream = new BufferedInputStream(new FileInputStream(new File(path)));
			
			streamContext.setAttribute(InputStream.class.getName(), stream);
			
			new Thread(new Runnable() {
				public void run() {
					TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
					tgActionManager.execute(TGImportSongAction.NAME, context);
				}
			}).start();
		} catch (FileNotFoundException e) {
			throw new TGActionException(e);
		}
	}
	
	protected boolean isValidFile( String path ){
		if( path != null ){
			File file = new File( path );
			return ( file.exists() && file.isFile() );
		}
		return false;
	}
}
