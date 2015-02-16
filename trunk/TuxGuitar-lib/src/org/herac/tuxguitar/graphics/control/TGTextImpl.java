package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.song.models.TGText;

public class TGTextImpl extends TGText{
	
	public void paint(TGLayout layout,TGPainter painter,float fromX, float fromY){
		TGBeatImpl beat = (TGBeatImpl)getBeat();
		TGMeasureImpl measure = beat.getMeasureImpl();
		float x = fromX + beat.getSpacing(layout) + beat.getPosX();
		float y = fromY + measure.getTs().getPosition(TGTrackSpacing.POSITION_TEXT);
		layout.setTextStyle(painter);
		painter.drawString(getValue(), x, y);
	}
}
