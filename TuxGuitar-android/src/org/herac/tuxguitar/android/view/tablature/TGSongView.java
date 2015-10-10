package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.application.TGApplication;
import org.herac.tuxguitar.android.graphics.TGPainterImpl;
import org.herac.tuxguitar.android.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.android.transport.TGTransportCache;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.error.TGErrorManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TGSongView extends View implements TGController {

	public static final float EMPTY_SCALE = 0f;
	
	private TGContext tgContext;
	private TGResourceFactory tgResourceFactory;
	private TGLayout tgLayout;
	private TGSongViewStyles tgSongStyles;
	private TGSongViewGestureDetector tgGestureDetector;
	private TGSongViewBufferController tgBufferController;
	private TGSongViewLayoutPainter tgLayoutPainter;
	private TGCaret tgCaret;
	private TGScroll tgScroll;

	private Bitmap bufferedBitmap;
	private boolean painting;
	private float scalePreview;

	public TGSongView(Context context) {
		super(context);
		this.initializeContext(context);
	}

	public TGSongView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initializeContext(context);
	}

	public TGSongView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.initializeContext(context);
	}

	private void initializeContext(Context context) {
		this.tgContext = ((TGApplication) context.getApplicationContext()).getContext();
		this.tgContext.setAttribute(TGSongView.class.getName(), this);
		this.tgGestureDetector = new TGSongViewGestureDetector(context, this);
		this.tgSongStyles = new TGSongViewStyles();
		this.tgResourceFactory = new TGResourceFactoryImpl();
		this.tgBufferController = new TGSongViewBufferController(this);
		this.tgLayoutPainter = new TGSongViewLayoutPainter(this);
		this.tgLayout = new TGLayoutVertical(this, TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_COMPACT);
		this.tgLayout.loadStyles(this.getDefaultScale());
		this.tgCaret = new TGCaret(this);
		this.tgScroll = new TGScroll();

		this.resetCaret();
		this.resetScroll();
		this.updateTablature();

		TGSongViewEventListener listener = new TGSongViewEventListener(this);
		TuxGuitar.getInstance(this.tgContext).getEditorManager().addRedrawListener(listener);
		TuxGuitar.getInstance(this.tgContext).getEditorManager().addUpdateListener(listener);
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
	
	public void resetCaret() {
		this.getCaret().update(1, TGDuration.QUARTER_TIME, 1);
	}

	public void resetScroll() {
		this.getScroll().getX().reset(false, 0, 0, 0);
		this.getScroll().getY().reset(true, 0, 0, 0);
	}

	public void disposeUnregisteredResources() {
		TGSynchronizer.getInstance(this.tgContext).executeLater(new Runnable() {
			public void run() {
				getResourceBuffer().disposeUnregisteredResources();
			}
		});
	}
	
	public void updateTablature() {
		this.getLayout().updateSong();
		this.getCaret().update();
		this.disposeUnregisteredResources();
	}

	public void updateMeasure(int number) {
		this.getLayout().updateMeasureNumber(number);
		this.getCaret().update();
		this.disposeUnregisteredResources();
	}

	public void updateScroll(TGRectangle bounds) {
		this.getScroll().getX().setMaximum(Math.max((this.getLayout().getWidth() - bounds.getWidth()), 0));
		this.getScroll().getY().setMaximum(Math.max((this.getLayout().getHeight() - bounds.getHeight()), 0));
	}

	public void updateSelection() {
		this.tgBufferController.updateSelection();
		this.tgLayoutPainter.refreshBuffer();
	}
	
	public void configureStyles(TGLayoutStyles styles) {
		this.tgSongStyles.configureStyles(styles);
	}

	public void scale(float scale) {
		this.getLayout().loadStyles(scale);
		this.setScalePreview(EMPTY_SCALE);
	}

	public void redraw() {
		this.setPainting(true);
		this.postInvalidate();
	}

	public void redrawPlayingMode() {
		TuxGuitar tuxguitar = TuxGuitar.getInstance(this.tgContext);
		if (!this.isPainting() && tuxguitar.getPlayer().isRunning()) {
			this.redraw();
		}
	}

	public void paintBuffer(Canvas canvas) {
		try {
			TGRectangle area = createClientArea(canvas);

			TGPainter painter = createBufferedPainter(area);

			this.paintArea(painter, area);

			if (this.getScalePreview() != EMPTY_SCALE) {
				float currentSale = (1 / (getLayout().getScale()) * this.getScalePreview());
				((TGPainterImpl) painter).getCanvas().scale(currentSale, currentSale);
			}

			this.paintTablature(painter, area);

			painter.dispose();
		} catch (Throwable throwable) {
			TGErrorManager.getInstance(this.tgContext).handleError(throwable);
		}
	}

	public void paintArea(TGPainter painter, TGRectangle area) {
		painter.setBackground(getResourceFactory().createColor(255, 255, 255));
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight());
		painter.closePath();
	}

	public void paintTablature(TGPainter painter, TGRectangle area) {
		TuxGuitar tuxguitar = TuxGuitar.getInstance(this.tgContext);

		if (this.getSong() != null) {
			this.tgLayoutPainter.paint(painter, area, -this.getPaintableScrollX(), -this.getPaintableScrollY());
			this.getCaret().paintCaret(this.getLayout(), painter);

			this.updateScroll(area);

			if (tuxguitar.getPlayer().isRunning()) {
				this.paintTablaturePlayMode(painter, area);
			}
			// Si no estoy reproduciendo y hay cambios
			// muevo el scroll al compas seleccionado
			else if (getCaret().hasChanges()) {
				// Mover el scroll puede necesitar redibujar
				// por eso es importante desmarcar los cambios antes de hacer el
				// moveScrollTo
				getCaret().setChanges(false);

				moveScrollTo(getCaret().getMeasure(), area);
			}
		}
	}

	public void paintTablaturePlayMode(TGPainter painter, TGRectangle area) {
		TuxGuitar tuxguitar = TuxGuitar.getInstance(this.tgContext);

		TGTransportCache transportCache = tuxguitar.getTransport().getCache();
		TGMeasureImpl measure = transportCache.getPlayMeasure();
		TGBeatImpl beat = transportCache.getPlayBeat();
		if (measure != null && measure.hasTrack(getCaret().getTrack().getNumber())) {
			this.moveScrollTo(measure, area);

			if(!measure.isOutOfBounds() ) {
				getLayout().paintPlayMode(painter, measure, beat, true);
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
			float mWidth = measure.getWidth(getLayout());
			float mHeight = measure.getTs().getSize();
			float marginWidth = getLayout().getFirstMeasureSpacing();
			float marginHeight = getLayout().getFirstTrackSpacing();

			// Solo se ajusta si es necesario
			// si el largo del compas es mayor al de la pantalla. nunca se puede
			// ajustar a la medida.
			if (mX < 0 || ((mX + mWidth) > area.getWidth() && (area.getWidth() >= mWidth + marginWidth || mX > marginWidth))) {
				getScroll().getX().setValue((scrollX + mX) - marginWidth);
				success = true;
			}

			// Solo se ajusta si es necesario
			// si el alto del compas es mayor al de la pantalla. nunca se puede
			// ajustar a la medida.
			if (mY < 0 || ((mY + mHeight) > area.getHeight() && (area.getHeight() >= mHeight + marginHeight || mY > marginHeight))) {
				getScroll().getY().setValue((scrollY + mY) - marginHeight);
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
			TuxGuitar tuxguitar = TuxGuitar.getInstance(this.tgContext);
			if (tuxguitar.tryLock()) {
				try {
					this.setPainting(true);
	
					this.paintBuffer(canvas);
	
					this.setPainting(false);
				} finally {
					tuxguitar.unlock(false);
				}
			} else {
				// try later
				this.postInvalidate();
			}
	
			if (this.bufferedBitmap != null) {
				canvas.drawBitmap(this.bufferedBitmap, 0, 0, null);
			}
		} catch (Throwable throwable) {
			this.handleError(throwable);
		}
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		boolean success = this.tgGestureDetector.processTouchEvent(event);
		if (success) {
			this.redraw();
		}
		return (success || super.onTouchEvent(event));
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		this.getCaret().setChanges(true);
		this.resetScroll();
		this.redraw();
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		this.recycleBuffer();
		this.tgLayoutPainter.dispose();
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
		TGErrorManager.getInstance(this.tgContext).handleError(new TGException(throwable));
	}
	
	public TGSongManager getSongManager() {
		return TGDocumentManager.getInstance(getTGContext()).getSongManager();
	}

	public TGSong getSong() {
		return TGDocumentManager.getInstance(getTGContext()).getSong();
	}

	public TGResourceFactory getResourceFactory() {
		return this.tgResourceFactory;
	}

	public TGResourceBuffer getResourceBuffer() {
		return this.tgBufferController.getResourceBuffer();
	}
	
	public TGLayout getLayout() {
		return tgLayout;
	}

	public TGCaret getCaret() {
		return tgCaret;
	}

	public TGContext getTGContext() {
		return this.tgContext;
	}

	public TGScroll getScroll() {
		return tgScroll;
	}

	public int getPaintableScrollX() {
		if (this.getScroll().getX().isEnabled()) {
			return Math.round(this.getScroll().getX().getValue());
		}
		return 0;
	}

	public int getPaintableScrollY() {
		if (this.getScroll().getY().isEnabled()) {
			return Math.round(this.getScroll().getY().getValue());
		}
		return 0;
	}

	public int getTrackSelection() {
		if ((getLayout().getStyle() & TGLayout.DISPLAY_MULTITRACK) == 0) {
			return getCaret().getTrack().getNumber();
		}
		return -1;
	}

	public boolean isRunning(TGBeat beat) {
		return (isRunning(beat.getMeasure()) && TuxGuitar.getInstance(this.tgContext).getTransport().getCache().isPlaying(beat.getMeasure(), beat));
	}

	public boolean isRunning(TGMeasure measure) {
		return (measure.getTrack().equals(getCaret().getTrack()) && TuxGuitar.getInstance(this.tgContext).getTransport().getCache().isPlaying(measure));
	}

	public boolean isLoopSHeader(TGMeasureHeader measureHeader) {
		MidiPlayerMode pm = TuxGuitar.getInstance(this.tgContext).getPlayer().getMode();
		return (pm.isLoop() && pm.getLoopSHeader() == measureHeader.getNumber());
	}

	public boolean isLoopEHeader(TGMeasureHeader measureHeader) {
		MidiPlayerMode pm = TuxGuitar.getInstance(this.tgContext).getPlayer().getMode();
		return (pm.isLoop() && pm.getLoopEHeader() == measureHeader.getNumber());
	}

	public boolean isPainting() {
		return this.painting;
	}

	public void setPainting(boolean painting) {
		this.painting = painting;
	}

	public float getScalePreview() {
		return scalePreview;
	}

	public void setScalePreview(float scalePreview) {
		this.scalePreview = scalePreview;
	}

	public void dispose(){
		this.getCaret().dispose();
		this.getLayout().disposeLayout();
		this.getResourceBuffer().disposeAllResources();
	}
	
	public static TGSongView getInstance(TGContext context) {
		return (TGSongView) context.getAttribute(TGSongView.class.getName());
	}
}
