/*
 * Created on 29-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.tuxguitar.graphics.control;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.resource.UIPainter;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TGTrackImpl extends TGTrack{

	private float tabHeight;
	private float scoreHeight;

	public TGTrackImpl(TGFactory factory) {
		super(factory);
	}

	public void update(TGLayout layout){
		this.calculateTabHeight(layout);
		this.calculateScoreHeight(layout);
	}
	/**
	 * Calcula el el ancho de la tablatura
	 */
	public void calculateTabHeight(TGLayout layout) {
		this.tabHeight = ((layout.getStyle() & TGLayout.DISPLAY_TABLATURE) != 0 ?((stringCount() - 1) * layout.getStringSpacing()):0);
	}

	/**
	 * Calcula el el ancho de la partitura
	 */
	public void calculateScoreHeight(TGLayout layout) {
		this.scoreHeight = ((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ?(layout.getScoreLineSpacing() * 4):0);
	}

	public float getTabHeight() {
		return this.tabHeight;
	}

	public float getScoreHeight() {
		return this.scoreHeight;
	}

	public void setTabHeight(float tabHeight) {
		this.tabHeight = tabHeight;
	}

	public void paintBeatSelection(TGLayout viewLayout, UIPainter painter, TGBeat from, TGBeat to) {
		TGMeasureImpl fromMeasure = (TGMeasureImpl) from.getMeasure();
		TGMeasureImpl toMeasure = (TGMeasureImpl) to.getMeasure();

		if (fromMeasure.getNumber() == toMeasure.getNumber()) {
			fromMeasure.paintInternalSelection(viewLayout, painter, from, to);
		} else {
			fromMeasure.paintSelectionStart(viewLayout, painter, from);
			for (int i = fromMeasure.getNumber(); i < toMeasure.getNumber() - 1; i++) {
				TGMeasureImpl measure = (TGMeasureImpl) this.getMeasure(i);
				measure.paintFullSelection(viewLayout, painter);
			}
			toMeasure.paintSelectionEnd(viewLayout, painter, to);
		}
	}
}
