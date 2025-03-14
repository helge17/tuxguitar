package app.tuxguitar.graphics.control.painters;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.ui.resource.UIPainter;

public class TGTempoPainter {

	public static void paintTempo(UIPainter painter, float x, float y, float scale, int base, boolean dotted) {
		int width = Math.round(scale * 1.33f);
		int height = Math.round(scale * (1.0f + 2.5f));
		float dotSize = scale * 0.4f;

		int path = UIPainter.PATH_FILL;
		if ((base==TGDuration.HALF) || (base==TGDuration.WHOLE)) {
			path = UIPainter.PATH_DRAW;
		}
		painter.initPath(path);
		TGNotePainter.paintNote(painter,x + (width - (scale * 1.33f)),y + ( height - (1.0f * scale) ), scale );
		painter.closePath();

		if (base != TGDuration.WHOLE) {
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo(x + width,y);
			painter.lineTo(x + width,y + (height - (0.66f * scale)) );
			painter.closePath();
		}

		if ((base==TGDuration.EIGHTH) || (base==TGDuration.SIXTEENTH)) {
			painter.initPath(UIPainter.PATH_FILL);
			TGNotePainter.paintFooter(painter,x + width, y + (scale * 2.3f) , -1 , scale);
			painter.closePath();
		}

		if (base==TGDuration.SIXTEENTH) {
			painter.initPath(UIPainter.PATH_FILL);
			TGNotePainter.paintFooter(painter,x + width, y + (scale * 3.0f) , -1 , scale);
			painter.closePath();
		}

		if (dotted) {
			painter.initPath(UIPainter.PATH_FILL);
			painter.moveTo(x + 2.0f*scale, y + height - (0.66f * scale));
			painter.addCircle(x + 2.0f*scale, y + height - (0.66f * scale), dotSize);
			painter.closePath();
		}

	}
}
