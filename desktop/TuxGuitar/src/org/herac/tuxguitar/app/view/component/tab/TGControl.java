package org.herac.tuxguitar.app.view.component.tab;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIFocusEvent;
import org.herac.tuxguitar.ui.event.UIFocusGainedListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.util.TGContext;

public class TGControl {

	private static final int SCROLL_INCREMENT = 50;

	private TGContext context;
	private UIScrollBarPanel container;
	private UICanvas canvas;
	private UIScrollBar hScroll;
	private UIScrollBar vScroll;

	private Tablature tablature;
	private int width;
	private int height;

	private int scrollX;
	private int scrollY;
	private int lastScrollX;
	private int lastScrollY;
	private TGMeasureImpl lastPaintedPlayedMeasure = null;
	private TGBeatImpl lastPaintedPLayedBeat = null;
	private float lastCanvasWidth;
	private float lastCanvasHeight;
	private float lastScale;
	private int lastLayoutStyle;
	private int lastLayoutMode;

	private boolean painting;
	private boolean wasPlaying;

	public TGControl(TGContext context, UIContainer parent) {
		this.context = context;
		this.tablature = TablatureEditor.getInstance(this.context).getTablature();
		this.initialize(parent);
	}

	private void initialize(UIContainer parent) {
		UIFactory factory = TGApplication.getInstance(this.context).getFactory();
		UITableLayout layout = new UITableLayout(0f);

		this.container = factory.createScrollBarPanel(parent, true, true, false);
		this.container.setLayout(layout);
		this.container.addFocusGainedListener(new UIFocusGainedListener() {
			public void onFocusGained(UIFocusEvent event) {
				TGControl.this.setFocus();
			}
		});

		this.canvas = factory.createCanvas(this.container, false);
		this.hScroll = this.container.getHScroll();
		this.vScroll = this.container.getVScroll();

		this.canvas.addPaintListener(new TGBufferedPainterListenerLocked(this.context, new TGControlPaintListener(this)));
		this.canvas.addMouseDownListener(this.tablature.getEditorKit().getMouseKit());
		this.canvas.addMouseUpListener(this.tablature.getEditorKit().getMouseKit());
		this.canvas.addMouseMoveListener(this.tablature.getEditorKit().getMouseKit());
		this.canvas.addMouseExitListener(this.tablature.getEditorKit().getMouseKit());
		this.canvas.addMouseDragListener(this.tablature.getEditorKit().getMouseKit());
		this.canvas.addZoomListener(this.tablature.getEditorKit().getMouseKit());

		this.hScroll.setIncrement(SCROLL_INCREMENT);
		this.hScroll.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGControl.this.redraw();
			}
		});

		this.vScroll.setIncrement(SCROLL_INCREMENT);
		this.vScroll.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGControl.this.redraw();
			}
		});

		KeyBindingActionManager.getInstance(this.context).appendListenersTo(this.canvas);

		this.canvas.setPopupMenu(TuxGuitar.getInstance().getItemManager().getPopupMenu());
		this.canvas.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				TGControl.this.canvas.setPopupMenu(null);
			}
		});

		layout.set(this.canvas, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
	}

	public void paintTablature(UIPainter painter) {
		boolean isPlaying;
		boolean moved = false;
		TGMeasureImpl playedMeasure = null;

		this.painting = true;
		try{
			isPlaying = MidiPlayer.getInstance(this.context).isRunning();
			float canvasWidth = this.canvas.getBounds().getWidth();
			float canvasHeight = this.canvas.getBounds().getHeight();
			float scale = this.tablature.getScale();

			// determine position in tab (which part shall be displayed): scroll x, y
			if (isPlaying) {
				playedMeasure = TGTransport.getInstance(this.context).getCache().getPlayMeasure();
				this.scrollX = this.hScroll.getValue();
				this.scrollY = this.vScroll.getValue();
				// if user did not move scrollbars, follow player
				if ((this.scrollX==this.lastScrollX) && (this.scrollY==this.lastScrollY)
						&& (playedMeasure != null) && playedMeasure.hasTrack(this.tablature.getCaret().getTrack().getNumber())){
					moveTo(playedMeasure);
				}
			} else {
				// if (wasPlaying): keep position in tab unchanged
				// new scrollbar attributes shall be defined from position in tab, not the opposite
				// else :
				if (!wasPlaying) {
					// follow caret movement or user actions on scrollbars
					if(this.tablature.getCaret().hasChanges()){
						this.tablature.getCaret().setChanges(false);
						this.moveTo(this.tablature.getCaret().getMeasure());
						moved = true;
					} else {
						this.scrollX = this.hScroll.getValue();
						this.scrollY = this.vScroll.getValue();
					}
				}
			}
			// performance optimization: while playing, only paint full tablature when needed
			// i.e. scrolled OR playedMeasure changed OR canvas size changed (window resized, track table visible state changed)
			// OR zoom in/out OR changed layout
			// else, painting only the playedMeasure is sufficient, don't repaint everything
			if ((!isPlaying) || (this.scrollX!=this.lastScrollX) || (this.scrollY!=this.lastScrollY)
					|| (playedMeasure!=this.lastPaintedPlayedMeasure)
					|| (canvasWidth!=this.lastCanvasWidth) || (canvasHeight!=this.lastCanvasHeight)
					|| (scale != this.lastScale) || (this.tablature.getViewLayout().getStyle() != this.lastLayoutStyle)
					|| (this.tablature.getViewLayout().getMode() != this.lastLayoutMode)) {
				int lastWidth = this.width;
				int lastHeight = this.height;
				this.tablature.paintTablature(painter, this.canvas.getBounds(), -this.scrollX, -this.scrollY);
				this.width = Math.round(this.tablature.getViewLayout().getWidth());
				this.height = Math.round(this.tablature.getViewLayout().getHeight());
				this.lastPaintedPlayedMeasure = playedMeasure;
				// if measure position changed AND scale or size changed, need to reconsider measure position (can only be done *after* tablature is updated)
				if (moved && (scale != this.lastScale) || (lastWidth != this.width) || (lastHeight != this.height)) {
					// move to caret measure and redraw a second time
					this.moveTo(this.tablature.getCaret().getMeasure());
					this.tablature.paintTablature(painter, this.canvas.getBounds(), -this.scrollX, -this.scrollY);
				}
			}

			// highlight played beat
			if ( (playedMeasure != null) && playedMeasure.hasTrack(this.tablature.getCaret().getTrack().getNumber())
					&& !playedMeasure.isOutOfBounds() ){
				TGBeatImpl playedBeat = TGTransport.getInstance(this.context).getCache().getPlayBeat();
				if (playedBeat != lastPaintedPLayedBeat) {
					this.tablature.getViewLayout().paintPlayMode(painter, playedMeasure, playedBeat);
					this.lastPaintedPLayedBeat = playedBeat;
				}
			}

			// update scrollbars
			this.updateScrollBars();
			if (wasPlaying || moved) {
				// player just stopped, or caret moved
				// redefine scrollbars positions considering current position
				this.hScroll.setValue(this.scrollX);
				this.vScroll.setValue(this.scrollY);
			}
			this.lastScale = scale;
			this.lastCanvasWidth = canvasWidth;
			this.lastCanvasHeight = canvasHeight;
			this.lastScrollX = this.scrollX;
			this.lastScrollY = this.scrollY;
			if (!isPlaying ) {
				this.lastPaintedPlayedMeasure = null;
				this.lastPaintedPLayedBeat = null;
			}
			this.lastLayoutStyle = this.tablature.getViewLayout().getStyle();
			this.lastLayoutMode = this.tablature.getViewLayout().getMode();
			this.wasPlaying = isPlaying;

		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		this.painting = false;
	}

	/* Warning: only update scrollbars if at least one attribute has changed
	 * else it creates a significant performance issue in Linux/SWT configuration:
	 * - updating scrollbar generates a SWT event to repaint this.canvas, because scrollbars are transparent
	 * - it calls this.paintTablature
	 * - which in turn call this.updateScroll
	 * if scrollbars are updated here, it creates a recursive loop: paintTablature -> updateScroll -> paintTablature -> ...
	 * this leads to repainting the tab about 60 times per second, creating a significant CPU load
	 * see https://github.com/helge17/tuxguitar/issues/403
	 */
	private void updateScrollBars(){
		UIRectangle bounds = this.canvas.getBounds();

		int hMax = Math.max(Math.round(this.width - bounds.getWidth()), 0);
		int hThumb = Math.round(bounds.getWidth());
		if (hMax>0) {
			this.hScroll.setVisible(true);
			if (this.hScroll.getMaximum() != hMax) {
				this.hScroll.setMaximum(hMax);
			}
			if (this.hScroll.getThumb() != hThumb) {
				this.hScroll.setThumb(hThumb);
			}
		} else {
			this.hScroll.setVisible(false);
			this.scrollX = 0;
		}
		int vMax = Math.max(Math.round(this.height - bounds.getHeight()), 0);
		int vThumb = Math.round(bounds.getHeight());
		if (vMax>0) {
			this.vScroll.setVisible(true);
			if (this.vScroll.getMaximum() != vMax) {
				this.vScroll.setMaximum(vMax);
			}
			if (this.vScroll.getThumb() != vThumb) {
				this.vScroll.setThumb(vThumb);
			}
		} else {
			this.vScroll.setVisible(false);
			this.scrollY = 0;
		}
	}

	private void moveTo(TGMeasureImpl measure) {
		if( measure != null && measure.getTs() != null ){
			int mX = Math.round(measure.getPosX());
			int mY = Math.round(measure.getPosY());
			int mWidth = Math.round(measure.getWidth(this.tablature.getViewLayout()));
			int mHeight = Math.round(measure.getTs().getSize());
			int marginWidth = Math.round(this.tablature.getViewLayout().getFirstMeasureSpacing());
			int marginHeight = Math.round(this.tablature.getViewLayout().getFirstTrackSpacing());
			boolean playMode = MidiPlayer.getInstance(this.context).isRunning();

			Integer hScrollValue = this.computeScrollValue(this.scrollX, mX, mWidth, marginWidth, Math.round(this.canvas.getBounds().getWidth()), this.width, playMode);
			if( hScrollValue != null ) {
				this.scrollX = hScrollValue;
			}
			Integer vScrollValue = this.computeScrollValue(this.scrollY, mY, mHeight, marginHeight, Math.round(this.canvas.getBounds().getHeight()), this.height, playMode);
			if( vScrollValue != null ) {
				this.scrollY = vScrollValue;
			}
		}
	}

	private Integer computeScrollValue(int scrollPos, int mPos, int mSize, int mMargin, int areaSize, int fullSize, boolean playMode) {
		Integer value = null;

		// when position is less than scroll
		if( mPos < 0 && (areaSize >= (mSize + mMargin) || ((mPos + mSize - mMargin) <= 0))) {
			value = ((scrollPos + mPos) - mMargin);
		}

		// when position is greater than scroll
		else if((mPos + mSize) > areaSize && (areaSize >= (mSize + mMargin) || mPos > areaSize)){
			value = (scrollPos + mPos + mSize + mMargin - areaSize);

			if( playMode ) {
				value += Math.min((fullSize - (scrollPos + mPos + mSize + mMargin)), (areaSize - mSize - (mMargin * 2)));
			}
		}
		return (value != null ? Math.max(value, 0) : null);
	}

	public void setFocus() {
		if(!this.isDisposed() ){
			this.canvas.setFocus();
		}
	}

	public void redraw(){
		if(!this.isDisposed() ){
			this.painting = true;
			this.canvas.redraw();
		}
	}

	public void redrawPlayingMode() {
		if(!this.isDisposed() && !this.painting && MidiPlayer.getInstance(this.context).isRunning()) {
			this.redraw();
		}
	}

	public UICanvas getCanvas() {
		return canvas;
	}

	public boolean isDisposed() {
		return (this.container == null || this.container.isDisposed() || this.canvas == null || this.canvas.isDisposed());
	}

}