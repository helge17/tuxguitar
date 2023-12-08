package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class TGTextImpl extends TGText{
	
	public void paint(TGLayout layout,UIPainter painter,float fromX, float fromY){
		TGBeatImpl beat = (TGBeatImpl)getBeat();
		TGMeasureImpl measure = beat.getMeasureImpl();
		float x = fromX + beat.getSpacing(layout) + beat.getPosX();
		float y = fromY + measure.getTs().getPosition(TGTrackSpacing.POSITION_TEXT);
		layout.setTextStyle(painter);
		painter.drawString(getValue(), x, y);
	}
}
