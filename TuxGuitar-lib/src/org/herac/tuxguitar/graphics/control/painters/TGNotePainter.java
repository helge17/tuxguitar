package org.herac.tuxguitar.graphics.control.painters;

import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.command.TGCubicTo;
import org.herac.tuxguitar.graphics.command.TGLineTo;
import org.herac.tuxguitar.graphics.command.TGMoveTo;
import org.herac.tuxguitar.graphics.command.TGPaintCommand;
import org.herac.tuxguitar.graphics.command.TGPaintModel;

public class TGNotePainter {
	
	private static final TGPaintCommand NOTE_MODEL = new TGPaintModel(
		new TGMoveTo(0.0f, 0.66f),
		new TGCubicTo(0.0f, 0.83f, 0.166f, 1.0f, 0.33f, 1.0f),
		new TGCubicTo(0.83f, 1.0f, 1.33f, 0.66f, 1.33f, 0.33f),
		new TGCubicTo(1.33f, 0.166f, 1.16f, 0.0f, 1.0f, 0.0f),
		new TGCubicTo(0.5f, 0.0f, 0.0f, 0.33f, 0.0f, 0.66f)
	);
	
	private static final TGPaintCommand HARMONIC_MODEL = new TGPaintModel(
		new TGMoveTo(0.1f, 0.5f),
		new TGLineTo(0.65000004f, 1.0f),
		new TGLineTo(1.2f, 0.5f),
		new TGLineTo(0.65000004f, 0.0f),
		new TGLineTo(0.1f, 0.5f),
		new TGLineTo(0.65000004f, 1.0f)
	);
	
	private static final TGPaintCommand FOOTER_UP_MODEL = new TGPaintModel(
		new TGMoveTo(0.64375f, -0.00625f),
		new TGCubicTo(0.659375f, 0.0f, 0.69375f, -0.00625f, 0.70625f, -0.0125f),
		new TGCubicTo(0.725f, -0.025f, 0.73125f, -0.03125f, 0.75f, -0.065625f),
		new TGCubicTo(0.815625f, -0.1875f, 0.86875f, -0.3375f, 0.890625f, -0.4625f),
		new TGCubicTo(0.934375f, -0.70937496f, 0.903125f, -0.890625f, 0.778125f, -1.096875f),
		new TGCubicTo(0.721875f, -1.19375f, 0.653125f, -1.28125f, 0.5f, -1.453125f),
		new TGCubicTo(0.340625f, -1.6375f, 0.290625f, -1.703125f, 0.228125f, -1.790625f),
		new TGCubicTo(0.165625f, -1.8875f, 0.121875f, -1.978125f, 0.09375f, -2.06875f),
		new TGCubicTo(0.078125f, -2.125f, 0.065625f, -2.209375f, 0.065625f, -2.25625f),
		new TGLineTo(0.065625f, -2.271875f),
		new TGLineTo(0.034375f, -2.271875f),
		new TGLineTo(0.0f, -2.271875f),
		new TGLineTo(0.0f, -1.88125f),
		new TGLineTo(0.0f, -1.490625f),
		new TGLineTo(0.034375f, -1.490625f),
		new TGLineTo(0.06875f, -1.490625f),
		new TGLineTo(0.15f, -1.434375f),
		new TGCubicTo(0.38125f, -1.28125f, 0.521875f, -1.15625f, 0.621875f, -1.021875f),
		new TGCubicTo(0.74375f, -0.85625f, 0.778125f, -0.71874994f, 0.74375f, -0.5124999f),
		new TGCubicTo(0.721875f, -0.38125f, 0.66875f, -0.246875f, 0.6f, -0.128125f),
		new TGCubicTo(0.584375f, -0.10625f, 0.58125f, -0.096875f, 0.58125f, -0.0875f),
		new TGCubicTo(0.58125f, -0.05f, 0.60625f, -0.01875f, 0.64375f, -0.00625f)
	);
	
	private static final TGPaintCommand FOOTER_DOWN_MODEL = new TGPaintModel(
		new TGMoveTo(0.64375f, 0.00625f),
		new TGCubicTo(0.659375f, 0.0f, 0.69375f, 0.00625f, 0.70625f, 0.0125f),
		new TGCubicTo(0.725f, 0.025f, 0.73125f, 0.03125f, 0.75f, 0.065625f),
		new TGCubicTo(0.815625f, 0.1875f, 0.86875f, 0.3375f, 0.890625f, 0.4625f),
		new TGCubicTo(0.934375f, 0.70937496f, 0.903125f, 0.890625f, 0.778125f, 1.096875f),
		new TGCubicTo(0.721875f, 1.19375f, 0.653125f, 1.28125f, 0.5f, 1.453125f),
		new TGCubicTo(0.340625f, 1.6375f, 0.290625f, 1.703125f, 0.228125f, 1.790625f),
		new TGCubicTo(0.165625f, 1.8875f, 0.121875f, 1.978125f, 0.09375f, 2.06875f),
		new TGCubicTo(0.078125f, 2.125f, 0.065625f, 2.209375f, 0.065625f, 2.25625f),
		new TGLineTo(0.065625f, 2.271875f),
		new TGLineTo(0.034375f, 2.271875f),
		new TGLineTo(0.0f, 2.271875f),
		new TGLineTo(0.0f, 1.88125f),
		new TGLineTo(0.0f, 1.490625f),
		new TGLineTo(0.034375f, 1.490625f),
		new TGLineTo(0.06875f, 1.490625f),
		new TGLineTo(0.15f, 1.434375f),
		new TGCubicTo(0.38125f, 1.28125f, 0.521875f, 1.15625f, 0.621875f, 1.021875f),
		new TGCubicTo(0.74375f, 0.85625f, 0.778125f, 0.71874994f, 0.74375f, 0.5124999f),
		new TGCubicTo(0.721875f, 0.38125f, 0.66875f, 0.246875f, 0.6f, 0.128125f),
		new TGCubicTo(0.584375f, 0.10625f, 0.58125f, 0.096875f, 0.58125f, 0.0875f),
		new TGCubicTo(0.58125f, 0.05f, 0.60625f, 0.01875f, 0.64375f, 0.00625f)
	);
	
	public static void paintNote(TGPainter painter, float x, float y, float scale) {
		NOTE_MODEL.paint(painter, x, y, scale);
	}
	
	public static void paintHarmonic(TGPainter painter, float x, float y, float scale){
		HARMONIC_MODEL.paint(painter, x, y, scale);
	}
	
	public static void paintFooter(TGPainter painter, float x, float y,int dir,float scale) {
		TGPaintCommand tgPaintCommand = (dir > 0 ? FOOTER_DOWN_MODEL : FOOTER_UP_MODEL);
		tgPaintCommand.paint(painter, x, y, scale);
	}
}
