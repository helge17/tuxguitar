package org.herac.tuxguitar.app.view.component.tab;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
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
	private boolean resetScroll;
	protected long lastVScrollTime;
	protected long lastHScrollTime;
	
	private boolean painting;
	
	public TGControl(TGContext context, UIContainer parent) {
		this.context = context;
		this.tablature = TablatureEditor.getInstance(this.context).getTablature();
		this.initialize(parent);
	}
	
	public void initialize(UIContainer parent) {
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
	
	public void paintTablature(TGPainter painter){
		this.setPainting(true);
		try{
			this.checkScroll();
			
			int oldWidth = this.width;
			int oldHeight = this.height;
			
			TGRectangle area = createRectangle(this.canvas.getBounds());
			
			this.scrollX = this.hScroll.getValue();
			this.scrollY = this.vScroll.getValue();
			
			this.tablature.paintTablature(painter, area, -this.scrollX, -this.scrollY);
			
			this.width = Math.round(this.tablature.getViewLayout().getWidth());
			this.height = Math.round(this.tablature.getViewLayout().getHeight());
			
			this.updateScroll();
			
			if( MidiPlayer.getInstance(this.context).isRunning()){
				this.paintTablaturePlayMode(painter);
			}
			// Si no estoy reproduciendo y hay cambios
			// muevo el scroll al compas que tiene el caret
			else if(this.tablature.getCaret().hasChanges() || (this.width != oldWidth || this.height != oldHeight)){
				// Mover el scroll puede necesitar redibujar
				// por eso es importante desmarcar los cambios antes de hacer el moveScrollTo
				this.tablature.getCaret().setChanges(false);
				
				this.moveScrollTo(this.tablature.getCaret().getMeasure(), area);
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		this.setPainting(false);
	}
	
	private void paintTablaturePlayMode(TGPainter painter){
		try{
			TGMeasureImpl measure = TuxGuitar.getInstance().getEditorCache().getPlayMeasure();
			TGBeatImpl beat = TuxGuitar.getInstance().getEditorCache().getPlayBeat();
			if(measure != null && measure.hasTrack(this.tablature.getCaret().getTrack().getNumber())){
				this.moveScrollTo(measure);
				
				if(!measure.isOutOfBounds()){
					this.tablature.getViewLayout().paintPlayMode(painter, measure, beat);
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	public void resetScroll(){
		this.resetScroll = true;
	}
	
	public void checkScroll(){
		if( this.resetScroll ){
			this.hScroll.setValue(0);
			this.vScroll.setValue(0);
			this.resetScroll = false;
		}
	}
	
	public void updateScroll(){
		UIRectangle bounds = this.canvas.getBounds();
		
		this.hScroll.setMaximum(Math.max(Math.round(this.width - bounds.getWidth()), 0));
		this.vScroll.setMaximum(Math.max(Math.round(this.height - bounds.getHeight()), 0));
		this.hScroll.setThumb(Math.round(bounds.getWidth()));
		this.vScroll.setThumb(Math.round(bounds.getHeight()));
	}
	
	public void moveScrollTo(TGMeasureImpl measure){
		this.moveScrollTo(measure, createRectangle(this.canvas.getBounds()));
	}
	
	public void moveScrollTo(TGMeasureImpl measure, TGRectangle area) {
		if( measure != null && measure.getTs() != null ){
			int mX = Math.round(measure.getPosX());
			int mY = Math.round(measure.getPosY());
			int mWidth = Math.round(measure.getWidth(this.tablature.getViewLayout()));
			int mHeight = Math.round(measure.getTs().getSize());
			int marginWidth = Math.round(this.tablature.getViewLayout().getFirstMeasureSpacing());
			int marginHeight = Math.round(this.tablature.getViewLayout().getFirstTrackSpacing());
			boolean playMode = MidiPlayer.getInstance(this.context).isRunning();
			
			//Solo se ajusta si es necesario
			Integer hScrollValue = this.computeScrollValue(this.scrollX, mX, mWidth, marginWidth, Math.round(area.getWidth()), this.width, playMode);
			if( hScrollValue != null ) {
				this.hScroll.setValue(hScrollValue);
			}
			
			//Solo se ajusta si es necesario
			Integer vScrollValue = this.computeScrollValue(this.scrollY, mY, mHeight, marginHeight, Math.round(area.getHeight()), this.height, playMode);
			if( vScrollValue != null ) {
				this.vScroll.setValue(vScrollValue);
			}
			
			// Si cambio el valor de algun scroll redibuja la pantalla
			if( this.scrollX != this.hScroll.getValue() || this.scrollY != this.vScroll.getValue() ){
				redraw();
			}
		}
	}
	
	public Integer computeScrollValue(int scrollPos, int mPos, int mSize, int mMargin, int areaSize, int fullSize, boolean playMode) {
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
			this.setPainting(true);
			this.canvas.redraw();
		}
	}
	
	public void redrawPlayingMode() {
		if(!this.isDisposed() && !this.isPainting() && MidiPlayer.getInstance(this.context).isRunning()) {
			this.redraw();
		}
	}
	
	public boolean isPainting() {
		return this.painting;
	}
	
	public void setPainting(boolean painting) {
		this.painting = painting;
	}

	public TGContext getContext() {
		return this.context;
	}
	
	public Tablature getTablature() {
		return tablature;
	}
	
	public UIContainer getContainer() {
		return container;
	}

	public UICanvas getCanvas() {
		return canvas;
	}

	public boolean isDisposed() {
		return (this.container == null || this.container.isDisposed() || this.canvas == null || this.canvas.isDisposed());
	}
	
	public TGRectangle createRectangle(UIRectangle rectangle){
		return new TGRectangle(rectangle.getX(),rectangle.getY(),rectangle.getWidth(),rectangle.getHeight());
	}
}