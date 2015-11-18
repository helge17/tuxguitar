package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.player.base.MidiRepeatController;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

public class MidiTickUtil {
	
	public static long getStart(long tick){
		long startPoint = getStartPoint();
		long start = startPoint;
		long length = 0;
		
		TGDocumentManager manager = TuxGuitar.instance().getDocumentManager();
		MidiRepeatController controller = new MidiRepeatController(manager.getSong(), getSHeader() , getEHeader() );
		while(!controller.finished()){
			TGMeasureHeader header = manager.getSong().getMeasureHeader(controller.getIndex());
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
	
	public static long getTick(long start){
		long startPoint = getStartPoint();
		long tick = startPoint;
		long length = 0;
		
		TGDocumentManager manager = TuxGuitar.instance().getDocumentManager();
		MidiRepeatController controller = new MidiRepeatController(manager.getSong(), getSHeader() , getEHeader() );
		while(!controller.finished()){
			TGMeasureHeader header = manager.getSong().getMeasureHeader(controller.getIndex());
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
	
	private static long getStartPoint(){
		TuxGuitar.instance().getPlayer().updateLoop( false );
		return TuxGuitar.instance().getPlayer().getLoopSPosition();
	}
	
	public static int getSHeader() {
		return TuxGuitar.instance().getPlayer().getLoopSHeader();
	}
	
	public static int getEHeader() {
		return TuxGuitar.instance().getPlayer().getLoopEHeader();
	}
}
