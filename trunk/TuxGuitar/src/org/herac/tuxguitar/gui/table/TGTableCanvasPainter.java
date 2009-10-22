package org.herac.tuxguitar.gui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGTableCanvasPainter implements PaintListener{
	
	private TGTableViewer viewer;
	private TGTrack track;
	
	public TGTableCanvasPainter(TGTableViewer viewer,TGTrack track){
		this.viewer = viewer;
		this.track = track;
	}
	
	public void paintControl(PaintEvent e) {
		TGPainter painter = new TGPainter(e.gc);
		paintTrack(painter);
	}
	
	protected void paintTrack(TGPainter painter){
		if(!TuxGuitar.instance().isLocked()){
			TuxGuitar.instance().lock();
			
			int x = -this.viewer.getHScrollSelection();
			int y = 0;
			int size = this.viewer.getTable().getRowHeight();
			int width = painter.getGC().getDevice().getBounds().width;
			boolean playing = TuxGuitar.instance().getPlayer().isRunning();
			
			painter.setBackground(painter.getGC().getDevice().getSystemColor(SWT.COLOR_GRAY));
			painter.initPath(TGPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.addRectangle(0,y,width,size);
			painter.closePath();
			
			Color trackColor = new Color(painter.getGC().getDevice(),this.track.getColor().getR(),this.track.getColor().getG(),this.track.getColor().getB());
			painter.setBackground(trackColor);
			painter.setForeground(trackColor);
			
			int count = this.track.countMeasures();
			for(int j = 0;j < count;j++){
				TGMeasureImpl measure = (TGMeasureImpl)this.track.getMeasure(j);
				if(isRestMeasure(measure)){
					painter.initPath();
					painter.setAntialias(false);
					painter.addRectangle(x,y,size - 2,size - 1);
					painter.closePath();
				}else{
					painter.initPath(TGPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle(x,y,size - 1,size );
					painter.closePath();
				}
				boolean hasCaret = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getMeasure().equals(measure);
				if((playing && measure.isPlaying(this.viewer.getEditor().getTablature().getViewLayout())) || (!playing && hasCaret)){
					painter.setBackground(painter.getGC().getDevice().getSystemColor(SWT.COLOR_BLACK));
					painter.initPath(TGPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle(x + 4,y + 4,size - 9,size - 8);
					painter.closePath();
					painter.setBackground(trackColor);
				}
				x += size;
			}
			trackColor.dispose();
			
			TuxGuitar.instance().unlock();
		}
	}
	
	private boolean isRestMeasure(TGMeasureImpl measure){
		int beatCount = measure.countBeats();
		for(int i = 0; i < beatCount; i++){
			if( !measure.getBeat(i).isRestBeat() ){
				return false;
			}
		}
		return true;
	}
	
}
