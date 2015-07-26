package org.herac.tuxguitar.app.action.impl.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.util.TGFileFormatUtils;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public class TGExportFileAction extends TGSongStreamActionBase {
	
	public static final String NAME = "action.file.export";

	public TGExportFileAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context){
		try {
			final TGSongStreamContext streamContext = this.findSongStreamContext(context);
			final TGLocalFileExporter exporter = context.getAttribute(ATTRIBUTE_PROVIDER);
			
			final String fileName = TGFileFormatUtils.chooseFileName(exporter.getFileFormat());
			if( fileName == null ){
				return;
			}
			
			final OutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
			
			streamContext.setAttribute(OutputStream.class.getName(), stream);
			
			new Thread(new Runnable() {
				public void run() {
					TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
					tgActionManager.execute(TGExportSongAction.NAME, context);
				}
			}).start();
		} catch (FileNotFoundException e) {
			throw new TGActionException(e);
		}
	}
}
