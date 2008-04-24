package org.herac.tuxguitar.gui.editors.tab.painters;

import org.herac.tuxguitar.gui.editors.TGPainter;

public class TGNotePainter {
	
	public static void paintFooter(TGPainter painter, float x, float y,int dir,float scale){
		painter.moveTo(( x + (0.64375f * scale) ),( y + ((0.00625f * scale) * dir) ));
		painter.cubicTo(( x + (0.659375f * scale) ),( y + ((0.0f * scale) * dir) ),( x + (0.69375f * scale) ),( y + ((0.00625f * scale) * dir) ),( x + (0.70625f * scale) ),( y + ((0.0125f * scale) * dir) ));
		painter.cubicTo(( x + (0.725f * scale) ),( y + ((0.025f * scale) * dir) ),( x + (0.73125f * scale) ),( y + ((0.03125f * scale) * dir) ),( x + (0.75f * scale) ),( y + ((0.065625f * scale) * dir) ));
		painter.cubicTo(( x + (0.815625f * scale) ),( y + ((0.1875f * scale) * dir) ),( x + (0.86875f * scale) ),( y + ((0.3375f * scale) * dir) ),( x + (0.890625f * scale) ),( y + ((0.4625f * scale) * dir) ));
		painter.cubicTo(( x + (0.934375f * scale) ),( y + ((0.70937496f * scale) * dir) ),( x + (0.903125f * scale) ),( y + ((0.890625f * scale) * dir) ),( x + (0.778125f * scale) ),( y + ((1.096875f * scale) * dir) ));
		painter.cubicTo(( x + (0.721875f * scale) ),( y + ((1.19375f * scale) * dir) ),( x + (0.653125f * scale) ),( y + ((1.28125f * scale) * dir) ),( x + (0.5f * scale) ),( y + ((1.453125f * scale) * dir) ));
		painter.cubicTo(( x + (0.340625f * scale) ),( y + ((1.6375f * scale) * dir) ),( x + (0.290625f * scale) ),( y + ((1.703125f * scale) * dir) ),( x + (0.228125f * scale) ),( y + ((1.790625f * scale) * dir) ));
		painter.cubicTo(( x + (0.165625f * scale) ),( y + ((1.8875f * scale) * dir) ),( x + (0.121875f * scale) ),( y + ((1.978125f * scale) * dir) ),( x + (0.09375f * scale) ),( y + ((2.06875f * scale) * dir) ));
		painter.cubicTo(( x + (0.078125f * scale) ),( y + ((2.125f * scale) * dir) ),( x + (0.065625f * scale) ),( y + ((2.209375f * scale) * dir) ),( x + (0.065625f * scale) ),( y + ((2.25625f * scale) * dir) ));
		painter.lineTo(( x + (0.065625f * scale) ),( y + ((2.271875f * scale) * dir) ));
		painter.lineTo(( x + (0.034375f * scale) ),( y + ((2.271875f * scale) * dir) ));
		painter.lineTo(( x + (0.0f * scale) ),( y + ((2.271875f * scale) * dir) ));
		painter.lineTo(( x + (0.0f * scale) ),( y + ((1.88125f * scale) * dir) ));
		painter.lineTo(( x + (0.0f * scale) ),( y + ((1.490625f * scale) * dir) ));
		painter.lineTo(( x + (0.034375f * scale) ),( y + ((1.490625f * scale) * dir) ));
		painter.lineTo(( x + (0.06875f * scale) ),( y + ((1.490625f * scale) * dir) ));
		painter.lineTo(( x + (0.15f * scale) ),( y + ((1.434375f * scale) * dir) ));
		painter.cubicTo(( x + (0.38125f * scale) ),( y + ((1.28125f * scale) * dir) ),( x + (0.521875f * scale) ),( y + ((1.15625f * scale) * dir) ),( x + (0.621875f * scale) ),( y + ((1.021875f * scale) * dir) ));
		painter.cubicTo(( x + (0.74375f * scale) ),( y + ((0.85625f * scale) * dir) ),( x + (0.778125f * scale) ),( y + ((0.71874994f * scale) * dir) ),( x + (0.74375f * scale) ),( y + ((0.5124999f * scale) * dir) ));
		painter.cubicTo(( x + (0.721875f * scale) ),( y + ((0.38125f * scale) * dir) ),( x + (0.66875f * scale) ),( y + ((0.246875f * scale) * dir) ),( x + (0.6f * scale) ),( y + ((0.128125f * scale) * dir) ));
		painter.cubicTo(( x + (0.584375f * scale) ),( y + ((0.10625f * scale) * dir) ),( x + (0.58125f * scale) ),( y + ((0.096875f * scale) * dir) ),( x + (0.58125f * scale) ),( y + ((0.0875f * scale) * dir) ));
		painter.cubicTo(( x + (0.58125f * scale) ),( y + ((0.05f * scale) * dir) ),( x + (0.60625f * scale) ),( y + ((0.01875f * scale) * dir) ),( x + (0.64375f * scale) ),( y + ((0.00625f * scale) * dir) ));
	}
	
	public static void paintNote(TGPainter painter, float x, float y,float scale){
		painter.moveTo( x ,( y + (0.66f * scale) ));
		painter.cubicTo( x,( y + (0.83f * scale) ),( x + (0.166f * scale) ),( y + (1.0f * scale) ),( x + (0.33f * scale) ),( y + (1.0f * scale) ));
		painter.cubicTo(( x + (0.83f * scale) ),( y + (1.0f * scale) ),( x + (1.33f * scale) ),( y + (0.66f * scale) ),( x + (1.33f * scale) ),( y + (0.33f * scale) ));
		painter.cubicTo(( x + (1.33f * scale) ),( y + (0.166f * scale) ),( x + (1.16f * scale) ), y ,( x + (1.0f * scale) ), y );
		painter.cubicTo(( x + (0.5f * scale) ), y , x ,( y + (0.33f * scale) ), x ,( y + (0.66f * scale) ));
	}
	
	public static void paintHarmonic(TGPainter painter, float x, float y,float scale){
		painter.moveTo(x ,(y + (0.5f * scale)));
		painter.lineTo((x + (0.665f * scale )) ,(y + (1.0f * scale)));
		painter.lineTo((x + (1.33f * scale)), (y + (0.5f*scale)));
		painter.lineTo((x + (0.665f * scale)) ,y);
		painter.lineTo(x ,(y + (0.5f * scale) ));
	}
}
