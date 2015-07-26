package org.herac.tuxguitar.app.view.component.table;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGTableCanvasPainter {
	
	private TGTableViewer viewer;
	private TGTrack track;
	
	public TGTableCanvasPainter(TGTableViewer viewer,TGTrack track){
		this.viewer = viewer;
		this.track = track;
	}
	
	protected void paintTrack(TGTableRow row, TGPainter painter){
		int x = -this.viewer.getHScrollSelection();
		int y = 0;
		int size = this.viewer.getTable().getRowHeight();
		int width = row.getPainter().getBounds().width;
		boolean playing = TuxGuitar.getInstance().getPlayer().isRunning();
		
		painter.setBackground(new TGColorImpl(row.getPainter().getDisplay().getSystemColor(SWT.COLOR_GRAY)));
		painter.initPath(TGPainter.PATH_FILL);
		painter.setAntialias(false);
		painter.addRectangle(0,y,width,size);
		painter.closePath();
		
		TGColor trackColor = painter.createColor(this.track.getColor().getR(),this.track.getColor().getG(),this.track.getColor().getB());
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
			boolean hasCaret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure().equals(measure);
			if((playing && measure.isPlaying(this.viewer.getEditor().getTablature().getViewLayout())) || (!playing && hasCaret)){
				painter.setBackground(new TGColorImpl(row.getPainter().getDisplay().getSystemColor(SWT.COLOR_BLACK)));
				painter.initPath(TGPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle(x + 4,y + 4,size - 9,size - 8);
				painter.closePath();
				painter.setBackground(trackColor);
			}
			x += size;
		}
		trackColor.dispose();
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
