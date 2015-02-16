package org.herac.tuxguitar.graphics.control.painters;

import org.herac.tuxguitar.graphics.TGPainter;

public class TGTripletFeelPainter {
	
	public static void paintTripletFeel8(TGPainter painter, float x, float y,float scale) {
		float topSpacing = (1.0f * scale);
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintNote(painter,x, y + (topSpacing + verticalSpacing) , scale );
		painter.closePath();
		
		painter.initPath();
		painter.setAntialias(false);
		painter.moveTo( x + Math.round(ovalWidth) ,y + Math.round(topSpacing + verticalSpacing + (0.33f * scale)) );
		painter.lineTo( x + Math.round(ovalWidth) ,y + topSpacing);
		painter.closePath();
		
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintNote(painter,x + horizontalSpacing,y + (topSpacing + verticalSpacing)  , scale );
		painter.closePath();
		
		painter.initPath();
		painter.setAntialias(false);
		painter.moveTo(x + Math.round(ovalWidth + horizontalSpacing) ,y +Math.round(topSpacing + verticalSpacing + (0.33f * scale)) );
		painter.lineTo(x + Math.round(ovalWidth + horizontalSpacing) ,y +topSpacing);
		painter.closePath();
		
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintFooter(painter,x + Math.round(ovalWidth + horizontalSpacing),y + (topSpacing + (scale * 2.3f)), -1 , scale);
		painter.closePath();
		
		painter.initPath();
		painter.moveTo( x, y + (1.2f * scale) );
		painter.cubicTo(x + (horizontalSpacing / 2f), y, x + ( (ovalWidth * 2f) + (horizontalSpacing / 2f)), y, x + ( (ovalWidth * 2f) + horizontalSpacing), y + (1.2f * scale));
		painter.moveTo( x + ( (ovalWidth * 2f) + horizontalSpacing),y + (1.2f * scale) );
		painter.closePath();
	}
	
	public static void paintTripletFeelNone8(TGPainter painter, float x, float y,float scale) {
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintNote(painter,x, y + verticalSpacing , scale );
		painter.closePath();
		
		painter.initPath();
		painter.setAntialias(false);
		painter.moveTo( x + Math.round(ovalWidth) ,y + Math.round(verticalSpacing + (0.33f * scale)) );
		painter.lineTo( x + Math.round(ovalWidth) ,y);
		painter.lineTo( x + Math.round(ovalWidth + horizontalSpacing) ,y);
		painter.lineTo( x + Math.round(ovalWidth + horizontalSpacing) ,y + Math.round(verticalSpacing + (0.33f * scale)) );
		painter.closePath();
		
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintNote(painter,x + horizontalSpacing,y + verticalSpacing , scale );
		painter.closePath();
	}
	
	public static int paintTripletFeel16(TGPainter painter, float x, float y,float scale) {
		float topSpacing = (1.0f * scale);
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintNote(painter,x,y + (topSpacing + verticalSpacing) , scale );
		painter.closePath();
		
		painter.initPath();
		painter.setAntialias(false);
		painter.moveTo(x + Math.round(ovalWidth) ,y + Math.round(topSpacing + verticalSpacing + (0.33f * scale)) );
		painter.lineTo(x + Math.round(ovalWidth) ,y + topSpacing);
		painter.lineTo(x + Math.round(ovalWidth + horizontalSpacing) ,y + topSpacing);
		painter.lineTo(x + Math.round(ovalWidth + horizontalSpacing) ,y + Math.round(topSpacing + verticalSpacing + (0.33f * scale)) );
		painter.moveTo(x + Math.round(ovalWidth + horizontalSpacing - (0.55f * scale) ) ,y + Math.round(topSpacing + (0.5f * scale)));
		painter.lineTo(x + Math.round(ovalWidth + horizontalSpacing) ,y + Math.round(topSpacing + (0.5f * scale)));
		painter.closePath();
		
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintNote(painter,x + horizontalSpacing,y + (topSpacing + verticalSpacing) , scale );
		painter.closePath();
		
		painter.initPath();
		painter.moveTo( x, y + (1.2f * scale) );
		painter.cubicTo(x + (horizontalSpacing / 2f), y, x + ( (ovalWidth * 2f) + (horizontalSpacing / 2f)), y, x + ( (ovalWidth * 2f) + horizontalSpacing), y + (1.2f * scale));
		painter.moveTo( x + ( (ovalWidth * 2f) + horizontalSpacing),y + (1.2f * scale) );
		painter.closePath();
		
		return Math.round( (ovalWidth * 2f) + horizontalSpacing );
	}
	
	public static int paintTripletFeelNone16(TGPainter painter, float x, float y,float scale) {
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintNote(painter,x,y + verticalSpacing , scale );
		painter.closePath();
		
		painter.initPath();
		painter.setAntialias(false);
		painter.moveTo(x + Math.round(ovalWidth) ,y + Math.round(verticalSpacing + (0.33f * scale)) );
		painter.lineTo(x + Math.round(ovalWidth) ,y);
		painter.lineTo(x + Math.round(ovalWidth + horizontalSpacing) ,y);
		painter.lineTo(x + Math.round(ovalWidth + horizontalSpacing) ,y + Math.round(verticalSpacing + (0.33f * scale)) );
		painter.moveTo(x + Math.round(ovalWidth) ,y + Math.round(0.5f * scale));
		painter.lineTo(x + Math.round(ovalWidth + horizontalSpacing) ,y + Math.round(0.5f * scale));
		painter.closePath();
		
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintNote(painter,x + horizontalSpacing,y + verticalSpacing , scale );
		painter.closePath();
		
		return Math.round( ovalWidth + horizontalSpacing );
	}
}
