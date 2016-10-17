package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.android.graphics.TGPainterImpl;
import org.herac.tuxguitar.android.transport.TGTransport;
import org.herac.tuxguitar.android.transport.TGTransportCache;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.error.TGErrorManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TGSongView extends View {
	
	private TGContext context;
	private TGSongViewController controller;
	private TGSongViewGestureDetector gestureDetector;
	
	private Bitmap bufferedBitmap;
	private boolean painting;
	
	public TGSongView(Context context) {
		super(context);
	}

	public TGSongView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TGSongView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onFinishInflate() {
		this.context = TGApplicationUtil.findContext(this);		
		this.controller = TGSongViewController.getInstance(this.context);
		this.controller.setSongView(this);
		this.controller.getLayout().loadStyles(this.getDefaultScale());
		this.controller.updateTablature();
		this.gestureDetector = new TGSongViewGestureDetector(getContext(), this);
	}
	
	public float getDefaultScale() {
		return this.getResources().getDisplayMetrics().density;
	}
	
	public float getMinimumScale() {
		return (this.getDefaultScale() / 2f);
	}
	
	public float getMaximumScale() {
		return (this.getDefaultScale() * 2f);
	}
	
	public void redraw() {
		this.setPainting(true);
		this.postInvalidate();
	}
	
	public void paintBuffer(Canvas canvas) {
		try {
			TGRectangle area = createClientArea(canvas);

			TGPainter painter = createBufferedPainter(area);

			this.paintArea(painter, area);

			if (this.controller.getScalePreview() != TGSongViewController.EMPTY_SCALE) {
				float currentSale = (1 / (this.controller.getLayout().getScale()) * this.controller.getScalePreview());
				((TGPainterImpl) painter).getCanvas().scale(currentSale, currentSale);
			}

			this.paintTablature(painter, area);

			painter.dispose();
		} catch (Throwable throwable) {
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}

	public void paintArea(TGPainter painter, TGRectangle area) {
		painter.setBackground(this.controller.getResourceFactory().createColor(255, 255, 255));
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight());
		painter.closePath();
	}

	public void paintTablature(TGPainter painter, TGRectangle area) {
		if (this.controller.getSong() != null) {
			this.controller.getLayoutPainter().paint(painter, area, -this.getPaintableScrollX(), -this.getPaintableScrollY());
			this.controller.getCaret().paintCaret(this.controller.getLayout(), painter);

			this.controller.updateScroll(area);

			if (MidiPlayer.getInstance(this.context).isRunning()) {
				this.paintTablaturePlayMode(painter, area);
			}
			// Si no estoy reproduciendo y hay cambios
			// muevo el scroll al compas seleccionado
			else if (this.controller.getCaret().hasChanges()) {
				// Mover el scroll puede necesitar redibujar
				// por eso es importante desmarcar los cambios antes de hacer el
				// moveScrollTo
				this.controller.getCaret().setChanges(false);

				this.moveScrollTo(this.controller.getCaret().getMeasure(), area);
			}
		}
	}

	public void paintTablaturePlayMode(TGPainter painter, TGRectangle area) {
		TGTransportCache transportCache = TGTransport.getInstance(this.context).getCache();
		TGMeasureImpl measure = transportCache.getPlayMeasure();
		TGBeatImpl beat = transportCache.getPlayBeat();
		if (measure != null && measure.hasTrack(this.controller.getCaret().getTrack().getNumber())) {
			this.moveScrollTo(measure, area);

			if(!measure.isOutOfBounds() ) {
				this.controller.getLayout().paintPlayMode(painter, measure, beat);
			}
		}
	}

	public boolean moveScrollTo(TGMeasureImpl measure, TGRectangle area) {
		boolean success = false;
		if (measure != null && measure.getTs() != null) {
			int scrollX = getPaintableScrollX();
			int scrollY = getPaintableScrollY();

			float mX = measure.getPosX();
			float mY = measure.getPosY();
			float mWidth = measure.getWidth(this.controller.getLayout());
			float mHeight = measure.getTs().getSize();
			float marginWidth = this.controller.getLayout().getFirstMeasureSpacing();
			float marginHeight = this.controller.getLayout().getFirstTrackSpacing();

			// Solo se ajusta si es necesario
			// si el largo del compas es mayor al de la pantalla. nunca se puede
			// ajustar a la medida.
			if (mX < 0 || ((mX + mWidth) > area.getWidth() && (area.getWidth() >= mWidth + marginWidth || mX > marginWidth))) {
				this.controller.getScroll().getX().setValue((scrollX + mX) - marginWidth);
				success = true;
			}

			// Solo se ajusta si es necesario
			// si el alto del compas es mayor al de la pantalla. nunca se puede
			// ajustar a la medida.
			if (mY < 0 || ((mY + mHeight) > area.getHeight() && (area.getHeight() >= mHeight + marginHeight || mY > marginHeight))) {
				this.controller.getScroll().getY().setValue((scrollY + mY) - marginHeight);
				success = true;
			}

			if (success) {
				redraw();
			}
		}
		return success;
	}

	public void onDraw(Canvas canvas) {
		try {
			if(!this.controller.isDisposed()) {
				TGEditorManager editor = TGEditorManager.getInstance(this.context);
				if (editor.tryLock()) {
					try {
						this.setPainting(true);
		
						this.paintBuffer(canvas);
		
						this.setPainting(false);
					} finally {
						editor.unlock();
					}
				} else {
					// try later
					this.postInvalidate();
				}
			}
			
			if (this.bufferedBitmap != null) {
				canvas.drawBitmap(this.bufferedBitmap, 0, 0, null);
			}
		} catch (Throwable throwable) {
			this.handleError(throwable);
		}
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		this.gestureDetector.processTouchEvent(event);
		this.redraw();
		
		return true;
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		this.controller.getCaret().setChanges(true);
		this.controller.resetScroll();
		this.redraw();
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		this.recycleBuffer();
		this.controller.getLayoutPainter().dispose();
	}
	
	public TGPainter createPainter(Canvas canvas) {
		return new TGPainterImpl(canvas);
	}

	public TGPainter createBufferedPainter(TGRectangle area) {
		if( this.bufferedBitmap == null || this.bufferedBitmap.getWidth() != area.getWidth() || this.bufferedBitmap.getHeight() != area.getHeight() ) {
			this.recycleBuffer();
			this.bufferedBitmap = Bitmap.createBitmap(Math.round(area.getWidth()), Math.round(area.getHeight()), Bitmap.Config.ARGB_4444);
		}
		return createPainter(new Canvas(this.bufferedBitmap));
	}

	public void recycleBuffer() {
		if( this.bufferedBitmap != null && !this.bufferedBitmap.isRecycled() ) {
			this.bufferedBitmap.recycle();
			this.bufferedBitmap = null;
		}
	}
	
	public TGRectangle createClientArea(Canvas canvas) {
		Rect rect = canvas.getClipBounds();
		return new TGRectangle(rect.left, rect.top, rect.right, rect.bottom);
	}

	public void handleError(Throwable throwable) {
		TGErrorManager.getInstance(this.context).handleError(new TGException(throwable));
	}
	
	public int getPaintableScrollX() {
		if (this.controller.getScroll().getX().isEnabled()) {
			return Math.round(this.controller.getScroll().getX().getValue());
		}
		return 0;
	}

	public int getPaintableScrollY() {
		if (this.controller.getScroll().getY().isEnabled()) {
			return Math.round(this.controller.getScroll().getY().getValue());
		}
		return 0;
	}
	
	public TGSongViewController getController() {
		return controller;
	}
	
	public boolean isPainting() {
		return this.painting;
	}

	public void setPainting(boolean painting) {
		this.painting = painting;
	}
}
