package org.herac.tuxguitar.cocoa.opendoc;

import java.io.File;
import java.net.MalformedURLException;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

public class OpenDocAction {
	
	public static void saveAndOpen(final String file){
		try {
			TuxGuitar.getInstance().getPlayer().reset();
			
			TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGReadURLAction.NAME);
			tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, new File(file).toURI().toURL());
			tgActionProcessor.process();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
