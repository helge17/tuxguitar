package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.widget.UICanvas;

public class TGTableHeaderMeasures implements TGTableHeader, TGBufferedPainterHandle {

	private TGTable table;
	private UICanvas canvas;
	private UIImage imgMarker;

	public TGTableHeaderMeasures(TGTable table) {
		this.table = table;
		this.canvas = this.table.getUIFactory().createCanvas(this.table.getColumnControl(), false);
		this.canvas.addPaintListener(new TGBufferedPainterListenerLocked(this.table.getContext(), this));
		this.canvas.addMouseMoveListener(this::headerMouseOver);
		this.canvas.addMouseExitListener(this::headerMouseExit);
		this.canvas.addMouseDownListener(this::headerClicked);

		this.table.appendListeners(this.canvas);
		loadIcons();
	}

	public UICanvas getControl() {
		return this.canvas;
	}

	private void headerMouseOver(UIMouseEvent event) {
		TGMeasureHeader header = getMeasureHeaderAt(event.getPosition().getX());
		if (header != null && header.hasMarker()) {
			this.canvas.setToolTipText(header.getMarker().getTitle());
		} else {
			this.canvas.setToolTipText(null);
		}
		this.canvas.redraw();
	}

	private void headerMouseExit(UIMouseEvent event) {
		this.canvas.redraw();
	}

	private void headerClicked(UIMouseEvent event) {
		if (event.getButton() == 1) {
			TGMeasureHeader header = getMeasureHeaderAt(event.getPosition().getX());
			if (header != null) {
				Tablature tablature = table.getViewer().getEditor().getTablature();
				Caret caret = tablature.getCaret();
				TGMeasure measure = caret.getTrack().getMeasure(header.getNumber() - 1);
				TGBeat beat = tablature.getSongManager().getMeasureManager().getFirstBeat(measure.getBeats());

				TGActionProcessor processor = new TGActionProcessor(table.getContext(), TGMoveToAction.NAME);
				processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, caret.getTrack());
				processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
				processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
				processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, caret.getSelectedString());
				processor.process();
			}
		}
	}

	private TGMeasureHeader getMeasureHeaderAt(float x) {
		Tablature tablature = table.getViewer().getEditor().getTablature();

		int scrollX = this.table.getViewer().getHScrollSelection();
		float cellSize = this.table.getRowHeight();

		int index = (int) Math.floor((x + scrollX) / cellSize);
		return tablature.getSongManager().getMeasureHeader(tablature.getSong(), index + 1);
	}

	public void paintControl(UIPainter painter) {
		// table might not be initialized yet
		if (!this.table.rowsAreInitialized()) {
			return;
		}

		int scrollX = this.table.getViewer().getHScrollSelection();
		float cellSize = this.table.getRowHeight();
		float width = this.canvas.getBounds().getWidth();
		Tablature tablature = this.table.getViewer().getEditor().getTablature();
		TGSong song = tablature.getSong();
		float markerMargin = 1.0f;
		float markerSize = cellSize - 2 * markerMargin;
		UIColor colorBackground = this.table.getViewer().getColorModel().getColor(TGTableColorModel.HEADER);

		painter.setLineWidth(UIPainter.THINNEST_LINE_WIDTH);
		painter.setBackground(colorBackground);
		painter.initPath(UIPainter.PATH_FILL);
		painter.setAntialias(false);
		painter.addRectangle(0, 0, width, cellSize);
		painter.closePath();

		painter.setAntialias(true);

		int count = song.countMeasureHeaders();
		int j = (int) Math.floor(scrollX / cellSize);
		for (float x = -scrollX + j * cellSize; j < count && x < width; j++, x += cellSize) {
			TGMeasureHeader header = song.getMeasureHeader(j);
			// additional check (markerSize): is there enough room to draw marker?
			if (header.hasMarker() && this.imgMarker!=null && markerSize>10.0f) {
				painter.drawImage(this.imgMarker, 0, 0, this.imgMarker.getWidth(), this.imgMarker.getHeight(),
						x + markerMargin, markerMargin, markerSize, markerSize);
			}
		}
	}

	public UICanvas getPaintableControl() {
		return getControl();
	}

	public void loadIcons()  {
		TGIconManager iconManager = TGIconManager.getInstance(this.table.getViewer().getContext());
		this.imgMarker = iconManager.getMarker();
	}
}
