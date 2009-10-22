package org.herac.tuxguitar.gui.editors.tab.painters;

import org.herac.tuxguitar.gui.editors.TGPainter;

public class TGTempoPainter {
	
	public static void paintTempo(TGPainter painter, int x, int y,float scale) {
		int width = Math.round(scale * 1.33f);
		int height = Math.round(scale * (1.0f + 2.5f));
		
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintNote(painter,x + (width - (scale * 1.33f)),y + ( height - (1.0f * scale) ), scale );
		painter.closePath();
		
		painter.initPath();
		painter.setAntialias(false);
		painter.moveTo(x + width,y);
		painter.lineTo(x + width,y + (height - (0.66f * scale)) );
		painter.closePath();
	}
}
