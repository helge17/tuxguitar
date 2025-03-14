package app.tuxguitar.app.printer;

import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.graphics.control.print.TGPrintController;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.util.TGContext;

public class PrintController extends TGPrintController {

	public PrintController(TGContext context, TGSong song, TGSongManager songManager, UIResourceFactory resourceFactory){
		super(song, songManager, resourceFactory, new PrintLayoutStyles(TGConfigManager.getInstance(context)));
	}
}
