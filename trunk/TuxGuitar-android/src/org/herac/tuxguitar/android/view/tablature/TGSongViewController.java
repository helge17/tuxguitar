package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.android.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.android.transport.TGTransport;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;
import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGSongViewController implements TGController {

	public static final float EMPTY_SCALE = 0f;
	
	private boolean disposed;
	private TGContext context;
	private TGResourceFactory resourceFactory;
	private TGLayout layout;
	private TGSongViewStyles songStyles;
	private TGSongViewBufferController bufferController;
	private TGSongViewLayoutPainter layoutPainter;
	private TGSongViewAxisSelector axisSelector;
	private TGCaret caret;
	private TGScroll scroll;
	private TGSongView songView;
	private float scalePreview;

	public TGSongViewController(TGContext context) {
		this.context = context;
		this.songStyles = new TGSongViewStyles();
		this.resourceFactory = new TGResourceFactoryImpl();
		this.bufferController = new TGSongViewBufferController(this);
		this.layoutPainter = new TGSongViewLayoutPainter(this);
		this.layout = new TGLayoutVertical(this, TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_COMPACT);
		this.caret = new TGCaret(this);
		this.scroll = new TGScroll();
		this.axisSelector = new TGSongViewAxisSelector(this);
		
		this.resetCaret();
		this.resetScroll();
		this.updateTablature();
		this.appendListeners();
	}
	
	public void appendListeners() {
		TGSongViewEventListener listener = new TGSongViewEventListener(this);
		TGEditorManager.getInstance(this.context).addRedrawListener(listener);
		TGEditorManager.getInstance(this.context).addUpdateListener(listener);
		TGEditorManager.getInstance(this.context).addDestroyListener(listener);
	}
	
	public void resetCaret() {
		this.getCaret().update(1, TGDuration.QUARTER_TIME, 1);
	}

	public void resetScroll() {
		this.getScroll().getX().reset(false, 0, 0, 0);
		this.getScroll().getY().reset(true, 0, 0, 0);
	}

	public void disposeUnregisteredResources() {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
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
		this.bufferController.updateSelection();
		this.layoutPainter.refreshBuffer();
	}

	public void scale(float scale) {
		this.getLayout().loadStyles(scale);
		this.setScalePreview(EMPTY_SCALE);
	}

	public void redraw() {
		if( this.getSongView() != null ) {
			this.getSongView().redraw();
		}
	}

	public void redrawPlayingMode() {
		if( this.getSongView() != null && !this.getSongView().isPainting() && MidiPlayer.getInstance(this.context).isRunning()) {
			this.redraw();
		}
	}
	
	public TGSongManager getSongManager() {
		return TGDocumentManager.getInstance(getContext()).getSongManager();
	}

	public TGSong getSong() {
		return TGDocumentManager.getInstance(getContext()).getSong();
	}

	public TGResourceFactory getResourceFactory() {
		return this.resourceFactory;
	}

	public TGResourceBuffer getResourceBuffer() {
		return this.bufferController.getResourceBuffer();
	}
	
	public TGSongViewLayoutPainter getLayoutPainter() {
		return layoutPainter;
	}

	public TGLayoutStyles getStyles() {
		return this.songStyles;
	}

	public TGLayout getLayout() {
		return layout;
	}

	public TGCaret getCaret() {
		return caret;
	}

	public TGContext getContext() {
		return this.context;
	}

	public TGScroll getScroll() {
		return scroll;
	}
	
	public TGSongViewAxisSelector getAxisSelector() {
		return axisSelector;
	}

	public TGSongView getSongView() {
		return songView;
	}
	
	public void setSongView(TGSongView tgSongView) {
		this.songView = tgSongView;
	}
	
	public float getScalePreview() {
		return scalePreview;
	}

	public void setScalePreview(float scalePreview) {
		this.scalePreview = scalePreview;
	}
	
	public int getTrackSelection() {
		if ((getLayout().getStyle() & TGLayout.DISPLAY_MULTITRACK) == 0) {
			return getCaret().getTrack().getNumber();
		}
		return -1;
	}

	public boolean isRunning(TGBeat beat) {
		return (isRunning(beat.getMeasure()) && TGTransport.getInstance(this.context).getCache().isPlaying(beat.getMeasure(), beat));
	}

	public boolean isRunning(TGMeasure measure) {
		return (measure.getTrack().equals(getCaret().getTrack()) && TGTransport.getInstance(this.context).getCache().isPlaying(measure));
	}

	public boolean isLoopSHeader(TGMeasureHeader measureHeader) {
		MidiPlayerMode pm = MidiPlayer.getInstance(this.context).getMode();
		return (pm.isLoop() && pm.getLoopSHeader() == measureHeader.getNumber());
	}

	public boolean isLoopEHeader(TGMeasureHeader measureHeader) {
		MidiPlayerMode pm = MidiPlayer.getInstance(this.context).getMode();
		return (pm.isLoop() && pm.getLoopEHeader() == measureHeader.getNumber());
	}
	
	public boolean isScaleActionAvailable() {
		return (!TGEditorManager.getInstance(getContext()).isLocked() && !MidiPlayer.getInstance(getContext()).isRunning());
	}
	
	public boolean isScrollActionAvailable() {
		return (!TGEditorManager.getInstance(getContext()).isLocked());
	}
	
	public boolean isDisposed() {
		return this.disposed;
	}
	
	public void dispose() {
		this.getCaret().dispose();
		this.getLayout().disposeLayout();
		this.getResourceBuffer().disposeAllResources();
		this.disposed = true;
	}
	
	public static TGSongViewController getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGSongViewController.class.getName(), new TGSingletonFactory<TGSongViewController>() {
			public TGSongViewController createInstance(TGContext context) {
				return new TGSongViewController(context);
			}
		});
	}
}
