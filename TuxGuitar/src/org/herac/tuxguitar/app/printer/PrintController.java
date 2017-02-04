package org.herac.tuxguitar.app.printer;

import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.print.TGPrintController;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class PrintController extends TGPrintController {
	
	public PrintController(TGContext context, TGSong song, TGSongManager songManager, TGResourceFactory resourceFactory){
		super(song, songManager, resourceFactory, new PrintLayoutStyles(TGConfigManager.getInstance(context)));
	}
}
