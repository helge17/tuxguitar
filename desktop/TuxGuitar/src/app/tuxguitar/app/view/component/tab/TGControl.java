package app.tuxguitar.app.view.component.tab;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import app.tuxguitar.graphics.control.TGBeatImpl;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGMeasureImpl;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIFocusEvent;
import app.tuxguitar.ui.event.UIFocusGainedListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.widget.UICanvas;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UIScrollBar;
import app.tuxguitar.ui.widget.UIScrollBarPanel;
import app.tuxguitar.util.TGContext;

public class TGControl {

	private static final int SCROLL_INCREMENT = 50;

	private TGContext context;
	private UIScrollBarPanel container;
	private UICanvas canvas;
	private UIScrollBar hScroll;
	private UIScrollBar vScroll;

	private Tablature tablature;
	private TablatureScrollPlaying tabScroll;
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
	private int discreteScrollingNbMeasuresAnticipation;
	private int horizontalMarginPercent;
	private int verticalMarginPercent;

	private boolean painting;
	private boolean wasPlaying;

	public TGControl(TGContext context, UIContainer parent) {
		this.context = context;
		this.tablature = TablatureEditor.getInstance(this.context).getTablature();
		this.tabScroll = new TablatureScrollPlaying(context);
		this.discreteScrollingNbMeasuresAnticipation = TGConfigManager.getInstance(context).getIntegerValue(TGConfigKeys.SCROLLING_DISCRETE_ANTICIPATION);
		this.horizontalMarginPercent = TGConfigManager.getInstance(context).getIntegerValue(TGConfigKeys.SCROLLING_HORIZONTAL_MARGIN_PERCENT);
		this.verticalMarginPercent = TGConfigManager.getInstance(context).getIntegerValue(TGConfigKeys.SCROLLING_VERTICAL_MARGIN_PERCENT);
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
					if ((this.tablature.getViewLayout().getStyle() & TGLayout.CONTINUOUS_SCROLL) != 0) {
						if (this.scrollPlayingTo(playedMeasure, MidiPlayer.getInstance(this.context).getMode(), this.canvas.getBounds())) {
							this.lastPaintedPLayedBeat = null; // force redraw of current played beat
						};
					}
					else {
						// discrete scrolling, anticipate by a few measures
						boolean anticipateScrolling = false;
						int lastPlayableIndex = playedMeasure.getTrack().countMeasures()-1;
						MidiPlayerMode mode = MidiPlayer.getInstance(this.context).getMode();
						if (mode.isLoop()) {
							lastPlayableIndex = Math.min(lastPlayableIndex, mode.getLoopEHeader()-1);
						}
						for (int i=Math.min(lastPlayableIndex, playedMeasure.getNumber() + this.discreteScrollingNbMeasuresAnticipation); i>playedMeasure.getNumber(); i--) {
							TGMeasureImpl followingMeasure = (TGMeasureImpl) playedMeasure.getTrack().getMeasure(i-1);
							if ((followingMeasure != null) && !this.tablature.getViewLayout().isFullyVisible(followingMeasure, this.canvas.getBounds()) ) {
								anticipateScrolling = true;
								break;
							}
						}
						jumpTo(playedMeasure, anticipateScrolling);
					}
				}
			} else {
				// if (wasPlaying): keep position in tab unchanged
				// new scrollbar attributes shall be defined from position in tab, not the opposite
				// else :
				if (!wasPlaying) {
					// follow caret movement or user actions on scrollbars
					if(this.tablature.getCaret().hasChanges()){
						this.tablature.getCaret().setChanges(false);
						this.jumpTo(this.tablature.getCaret().getMeasure(), false);
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
					this.jumpTo(this.tablature.getCaret().getMeasure(), false);
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

	// returns true if a scroll value was updated
	private boolean scrollPlayingTo(TGMeasureImpl playedMeasure, MidiPlayerMode mode, UIRectangle area) {
		int direction = this.tablature.getViewLayout().getMode();
		boolean updated = false;
		int pos = 0;
		int target = 0;
		if (direction == TGLayout.MODE_HORIZONTAL) {
			pos = Math.round(playedMeasure.getPosX());
			target = Math.round(this.tablature.getViewLayout().getFirstMeasureSpacing());
			target += this.horizontalMarginPercent * area.getWidth() / 100;
		}
		else if (direction == TGLayout.MODE_VERTICAL) {
			pos = Math.round(playedMeasure.getPosY());
			target = Math.round(this.tablature.getViewLayout().getFirstTrackSpacing());
			target += this.verticalMarginPercent * area.getHeight() / 100;
		}
		else {	// ??
			return false;
		}
		Integer scrollIncrement = this.tabScroll.scrollTo(playedMeasure, pos, target, mode, area, tablature.getViewLayout());
		if (scrollIncrement != null) {
			if (direction == TGLayout.MODE_HORIZONTAL) {
				this.scrollX += scrollIncrement;
			}
			else if (direction == TGLayout.MODE_VERTICAL) {
				this.scrollY += scrollIncrement;
			}
			updated = true;
		}
		return updated;
		
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

	private void jumpTo(TGMeasureImpl measure, boolean anticipateScrolling) {
		if( measure != null && measure.getTs() != null ){
			int mX = Math.round(measure.getPosX());
			int mY = Math.round(measure.getPosY());
			int mWidth = Math.round(measure.getWidth(this.tablature.getViewLayout()));
			int mHeight = Math.round(measure.getTs().getSize());
			int marginWidth = Math.round(this.tablature.getViewLayout().getFirstMeasureSpacing());
			int marginHeight = Math.round(this.tablature.getViewLayout().getFirstTrackSpacing());
			boolean playMode = MidiPlayer.getInstance(this.context).isRunning();

			Integer hScrollValue = this.computeScrollValue(this.scrollX, mX, mWidth, marginWidth, Math.round(this.canvas.getBounds().getWidth()), this.width, playMode,
					anticipateScrolling && (this.tablature.getViewLayout().getMode()==TGLayout.MODE_HORIZONTAL));
			if( hScrollValue != null ) {
				this.scrollX = hScrollValue;
			}
			Integer vScrollValue = this.computeScrollValue(this.scrollY, mY, mHeight, marginHeight, Math.round(this.canvas.getBounds().getHeight()), this.height, playMode,
					anticipateScrolling && (this.tablature.getViewLayout().getMode()==TGLayout.MODE_VERTICAL));
			if( vScrollValue != null ) {
				this.scrollY = vScrollValue;
			}
			this.tabScroll.reset(this.tablature.getViewLayout().getMode());
		}
	}

	private Integer computeScrollValue(int scrollPos, int mPos, int mSize, int mMargin, int areaSize, int fullSize, boolean playMode, boolean anticipateScrolling) {
		Integer value = null;

		// when position is greater than scroll, or anticipation is needed
		if(((mPos + mSize) > areaSize || anticipateScrolling) && (areaSize >= (mSize + mMargin) || mPos > areaSize)){
			value = (scrollPos + mPos + mSize + mMargin - areaSize);

			if( playMode ) {
				value += Math.min((fullSize - (scrollPos + mPos + mSize + mMargin)), (areaSize - mSize - (mMargin * 2)));
			}
		}
		// when position is less than scroll
		else if( mPos < 0 && (areaSize >= (mSize + mMargin) || ((mPos + mSize - mMargin) <= 0))) {
			value = ((scrollPos + mPos) - mMargin);
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
