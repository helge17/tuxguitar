package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.player.base.MidiRepeatController;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

public class MidiTickUtil {
	
	public static long getStart(long tick){
		long start = TGDuration.QUARTER_TIME;
		long length = 0;
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		MidiRepeatController controller = new MidiRepeatController(manager.getSong());
		while(!controller.finished()){
			TGMeasureHeader header = manager.getSong().getMeasureHeader(controller.getIndex());
			controller.process();
			if(controller.shouldPlay()){
				length = header.getLength();
				//verifico si es el compas correcto
				if(tick >= start && tick < (start + length )){
					return header.getStart() + (tick - start);
				}
				start += length;
			}
		}
		return start;
	}
	
	public static long getTick(long start){
		long tick = TGDuration.QUARTER_TIME;
		long length = 0;
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		MidiRepeatController controller = new MidiRepeatController(manager.getSong());
		while(!controller.finished()){
			TGMeasureHeader header = manager.getSong().getMeasureHeader(controller.getIndex());
			controller.process();
			if(controller.shouldPlay()){
				length = header.getLength();
				//verifico si es el compas correcto
				if(start >= header.getStart() && start < (header.getStart() + length )){
					return tick;
				}
				tick += length;
			}
		}
		return tick;
	}
}
