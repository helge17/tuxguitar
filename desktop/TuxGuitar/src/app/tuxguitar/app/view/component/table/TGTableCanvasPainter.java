package app.tuxguitar.app.view.component.table;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGMeasureImpl;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.util.TGBeatRange;

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
			UIColor cellBackgroundColor = trackColor;
			TGMeasureImpl measure = (TGMeasureImpl) this.track.getMeasure(j);
			if(isRestMeasure(measure)){
				cellBackgroundColor = viewer.getColorModel().getColor(TGTableColorModel.CELL_REST_MEASURE);
			}
			painter.setBackground(cellBackgroundColor);
			painter.setForeground(cellBackgroundColor);
			painter.initPath(UIPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.addRectangle(x,y,size - 1,size );
			painter.closePath();

			boolean hasCaret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure().equals(measure);
			if((playing && measure.isPlaying(this.viewer.getEditor().getTablature().getViewLayout())) || (!playing && hasCaret)){
				painter.setBackground(getCaretColor(cellBackgroundColor));
				painter.initPath(UIPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRoundedRectangle(x + 4, y + 4, size - 9, size - 8, 3f);
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

	private UIColor getCaretColor(UIColor backgroundColor) {
		int brightness = backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue();
		// arbitrary threshold
		int index = (brightness > 3*0x50 ? TGTableColorModel.CARET_DARK : TGTableColorModel.CARET_LIGHT);
		return viewer.getColorModel().getColor(index);
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
