/*
 * Created on 25-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.tab;

import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TablatureUtil {
	/**
	 * Calcula la posicion inicial de una nota, dependiendo de el spacing
	 */
	public static final int getStartPosition(TGMeasure measure,long start,int spacing){
		double newStart = (double)start - (double)measure.getStart();
		double displayPosition = 0.0;
		if(newStart > 0){
			double position = (newStart / TGDuration.QUARTER_TIME);
			displayPosition = (position * spacing);
		}
		return (int)displayPosition;
	}
}
