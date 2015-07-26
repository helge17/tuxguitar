package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.printer.PrintStyles;
import org.herac.tuxguitar.app.view.dialog.printer.PrintStylesDialog;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.models.TGSong;

public class PDFStylesHandler implements TGSongStreamSettingsHandler {

	public String getProviderId() {
		return PDFSongExporter.PROVIDER_ID;
	}

	public void handleSettings(TGSongStreamContext context, Runnable callback) {
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		PrintStyles styles = PrintStylesDialog.open(TuxGuitar.getInstance().getShell(), song);
		if( styles != null ) {
			context.setAttribute(PrintStyles.class.getName(), styles);
			callback.run();
		}
	}	
}
