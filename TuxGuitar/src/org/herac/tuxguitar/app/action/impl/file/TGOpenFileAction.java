package org.herac.tuxguitar.app.action.impl.file;

import java.io.File;
import java.net.URL;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.FileChooser;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenFileAction extends TGActionBase {
	
	public static final String NAME = "action.file.open";
	
	public TGOpenFileAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context){
		try {
			String path = FileChooser.instance().open(TuxGuitar.getInstance().getShell(), TuxGuitar.getInstance().getFileFormatManager().getInputFormats());
			if( path != null ){
				File file = new File(path);
				if( file.exists() && file.isFile() ){
					final URL url = file.toURI().toURL();
					new Thread(new Runnable() {
						public void run() {
							context.setAttribute(TGReadURLAction.ATTRIBUTE_URL, url);
							
							TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
							tgActionManager.execute(TGReadURLAction.NAME, context);
						}
					}).start();
				}
			}
		} catch (Throwable e) {
			throw new TGActionException(e.getMessage(), e);
		}
	}
}