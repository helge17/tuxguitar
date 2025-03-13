package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.util.TGBeatRange;

public class TGTableCanvasPainter {

	private TGTableViewer viewer;
	private TGTrack track;
	private static final int SELECTION_ALPHA = 128;

	public TGTableCanvasPainter(TGTableViewer viewer,TGTrack track){
		this.viewer = viewer;
		this.track = track;
	}

	protected void paintTrack(TGTableRow row, UIPainter painter){
		int x = -this.viewer.getHScrollSelection();
		int y = 0;
		float size = this.viewer.getTable().getRowHeight();
		float width = row.getPainter().getBounds().getWidth();
		boolean playing = TuxGuitar.getInstance().getPlayer().isRunning();

		UIColor colorBackground = this.viewer.getColorModel().getColor(TGTableColorModel.CELL_BACKGROUND);

		painter.setLineWidth(UIPainter.THINNEST_LINE_WIDTH);
		painter.setBackground(colorBackground);
		painter.initPath(UIPainter.PATH_FILL);
		painter.setAntialias(false);
		painter.addRectangle(0, y, width, size);
		painter.closePath();

		UIColor trackColor = this.viewer.getUIFactory().createColor(this.track.getColor().getR(), this.track.getColor().getG(), this.track.getColor().getB());

		TGLayout layout = viewer.getEditor().getTablature().getViewLayout();
		TGBeatRange beatRange = viewer.getEditor().getTablature().getSelector().getBeatRange();


		int count = this.track.countMeasures();
		for(int j = 0;j < count;j++){
			TGMeasureImpl measure = (TGMeasureImpl) this.track.getMeasure(j);
			if(isRestMeasure(measure)){
				painter.setBackground(viewer.getColorModel().getColor(TGTableColorModel.CELL_REST_MEASURE));
				painter.setForeground(viewer.getColorModel().getColor(TGTableColorModel.CELL_REST_MEASURE));
			}else{
				painter.setBackground(trackColor);
				painter.setForeground(trackColor);
			}
			painter.initPath(UIPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.addRectangle(x,y,size - 1,size );
			painter.closePath();

			boolean hasCaret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure().equals(measure);
			if((playing && measure.isPlaying(this.viewer.getEditor().getTablature().getViewLayout())) || (!playing && hasCaret)){
				painter.setBackground(viewer.getColorModel().getColor(TGTableColorModel.CARET));
				painter.initPath(UIPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle(x + 4, y + 4, size - 9, size - 8);
				painter.closePath();
				painter.setBackground(trackColor);
			}

			if (beatRange.containsMeasure(measure)) {
				painter.setBackground(layout.getResources().getSelectionColor());
				painter.setAlpha(SELECTION_ALPHA);
				painter.initPath(UIPainter.PATH_FILL);
				painter.addRectangle(x + 1.5f, y + 1.5f, size - 3f, size - 3f);
				painter.closePath();
				painter.setAlpha(255);
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
