package org.herac.tuxguitar.android.util;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.player.base.MidiRepeatController;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class MidiTickUtil {
	
	public static long getStart(TGContext context, long tick){
		long startPoint = getStartPoint(context);
		long start = startPoint;
		long length = 0;
		
		TGSong song = TGDocumentManager.getInstance(context).getSong();
		MidiRepeatController controller = new MidiRepeatController(song, getSHeader(context) , getEHeader(context) );
		while(!controller.finished()){
			TGMeasureHeader header = song.getMeasureHeader(controller.getIndex());
			controller.process();
			if(controller.shouldPlay()){
				
				start += length;
				length = header.getLength();
				
				//verifico si es el compas correcto
				if(tick >= start && tick < (start + length )){
					return header.getStart() + (tick - start);
				}
			}
		}
		return ( tick < startPoint ? startPoint : start );
	}
	
	public static long getTick(TGContext context, long start){
		long startPoint = getStartPoint(context);
		long tick = startPoint;
		long length = 0;
		
		TGSong song = TGDocumentManager.getInstance(context).getSong();
		MidiRepeatController controller = new MidiRepeatController(song, getSHeader(context) , getEHeader(context) );
		while(!controller.finished()){
			TGMeasureHeader header = song.getMeasureHeader(controller.getIndex());
			controller.process();
			if(controller.shouldPlay()){
				
				tick += length;
				length = header.getLength();
				
				//verifico si es el compas correcto
				if(start >= header.getStart() && start < (header.getStart() + length )){
					return tick;
				}
			}
		}
		return ( start < startPoint ? startPoint : tick );
	}
	
	private static long getStartPoint(TGContext context){
		TuxGuitar tuxguitar = TuxGuitar.getInstance(context);
		tuxguitar.getPlayer().updateLoop( false );
		return tuxguitar.getPlayer().getLoopSPosition();
	}
	
	public static int getSHeader(TGContext context) {
		return TuxGuitar.getInstance(context).getPlayer().getLoopSHeader();
	}
	
	public static int getEHeader(TGContext context) {
		return TuxGuitar.getInstance(context).getPlayer().getLoopEHeader();
	}
}
