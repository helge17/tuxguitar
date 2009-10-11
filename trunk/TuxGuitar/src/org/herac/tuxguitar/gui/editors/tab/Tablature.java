/*
 * Created on 29-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.edit.EditorKit;
import org.herac.tuxguitar.gui.editors.tab.layout.LinearViewLayout;
import org.herac.tuxguitar.gui.editors.tab.layout.PageViewLayout;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDuration;
/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Tablature extends Composite {
	
	private static final int SCROLL_DELAY = 15;
	private static final int SCROLL_INCREMENT = 50;
	
	private TGSongManager songManager;
	private Caret caret;
	private int width;
	private int height;
	private ViewLayout viewLayout;
	private EditorKit editorKit;
	
	private TGBeatImpl playedBeat;
	private TGMeasureImpl playedMeasure;
	
	private int scrollX;
	private int scrollY;
	private boolean resetScroll;
	protected long lastVScrollTime;
	protected long lastHScrollTime;
	
	private boolean painting;
	
	public Tablature(final Composite parent) {
		this(parent,SWT.NONE);
	}
	
	public Tablature(final Composite parent,int style) {
		super(parent, style);
		this.editorKit = new EditorKit(this);
	}
	
	public void initGUI(){
		this.addPaintListener(new TablaturePaintListener(this));
		
		final ScrollBar hBar = getHorizontalBar();
		hBar.setIncrement(SCROLL_INCREMENT);
		hBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if(Tablature.this.lastHScrollTime + SCROLL_DELAY < System.currentTimeMillis()){
					redraw();
					Tablature.this.lastHScrollTime = System.currentTimeMillis();
				}
			}
		});
		
		final ScrollBar vBar = getVerticalBar();
		vBar.setIncrement(SCROLL_INCREMENT);
		vBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if(Tablature.this.lastVScrollTime + SCROLL_DELAY < System.currentTimeMillis()){
					redraw();
					Tablature.this.lastVScrollTime = System.currentTimeMillis();
				}
			}
		});
		
		this.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent arg0) {
				updateScroll();
			}
		});
	}
	
	public void initDefaults(){
		this.caret = new Caret(this);
	}
	
	public void updateTablature(){
		this.playedBeat = null;
		this.playedMeasure = null;
		getViewLayout().updateSong();
	}
	
	public void initCaret(){
		this.caret.update(1,TGDuration.QUARTER_TIME,1);
	}
	
	public synchronized void paintTablature(TGPainter painter){
		if(!TuxGuitar.instance().isLocked()){
			TuxGuitar.instance().lock();
			this.setPainting(true);
			try{
				this.checkScroll();
				
				Rectangle area = getClientArea();
				ScrollBar xScroll = getHorizontalBar();
				ScrollBar yScroll = getVerticalBar();
				this.scrollX = xScroll.getSelection();
				this.scrollY = yScroll.getSelection();
				
				this.getViewLayout().paint(painter,area,-this.scrollX,-this.scrollY);
				
				this.width = this.viewLayout.getWidth();
				this.height = this.viewLayout.getHeight();
				
				this.updateScroll();
				
				if(TuxGuitar.instance().getPlayer().isRunning()){
					redrawPlayingMode(painter,true);
				}
				// Si no estoy reproduciendo y hay cambios
				// muevo el scroll al compas que tiene el caret
				else if(getCaret().hasChanges()){
					// Mover el scroll puede necesitar redibujar
					// por eso es importante desmarcar los cambios antes de hacer el moveScrollTo
					getCaret().setChanges(false);
					
					moveScrollTo(getCaret().getMeasure(), xScroll, yScroll,area);
				}
			}catch(Throwable throwable){
				throwable.printStackTrace();
			}
			this.setPainting(false);
			TuxGuitar.instance().unlock();
		}
	}
	
	public void resetScroll(){
		this.resetScroll = true;
	}
	
	public void checkScroll(){
		if(this.resetScroll){
			getHorizontalBar().setSelection(0);
			getVerticalBar().setSelection(0);
			this.resetScroll = false;
		}
	}
	
	public void updateScroll(){
		Rectangle bounds = getBounds();
		Rectangle client = getClientArea();
		ScrollBar hBar = getHorizontalBar();
		ScrollBar vBar = getVerticalBar();
		hBar.setMaximum(this.width);
		vBar.setMaximum(this.height);
		hBar.setThumb(Math.min(bounds.width, client.width));
		vBar.setThumb(Math.min(bounds.height, client.height));
	}
	
	public boolean moveScrollTo(TGMeasureImpl measure){
		return moveScrollTo(measure,getHorizontalBar(),getVerticalBar(),getClientArea());
	}
	
	public boolean moveScrollTo(TGMeasureImpl measure,ScrollBar xScroll,ScrollBar yScroll,Rectangle area){
		boolean success = false;
		if(measure != null && measure.getTs() != null){
			int mX = measure.getPosX();
			int mY = measure.getPosY();
			int mWidth = measure.getWidth(getViewLayout());
			int mHeight = measure.getTs().getSize();
			int marginWidth = getViewLayout().getFirstMeasureSpacing();
			int marginHeight = getViewLayout().getFirstTrackSpacing();
			boolean forceRedraw = false;
			
			//Solo se ajusta si es necesario
			//si el largo del compas es mayor al de la pantalla. nunca se puede ajustar a la medida.
			if( mX < 0 || ( (mX + mWidth ) > area.width && (area.width >= mWidth + marginWidth || mX > marginWidth) )  ){
				xScroll.setSelection((this.scrollX + mX) - marginWidth );
				success = true;
			}
			
			//Solo se ajusta si es necesario
			//si el alto del compas es mayor al de la pantalla. nunca se puede ajustar a la medida.
			if( mY < 0 || ( (mY + mHeight ) > area.height && (area.height >= mHeight + marginHeight || mY > marginHeight) )  ){
				yScroll.setSelection( (this.scrollY + mY)  - marginHeight );
				success = true;
			}
			
			if(!success){
				// Si la seleccion "real" del scroll es distinta a la anterior, se fuerza el redraw
				forceRedraw = (this.scrollX != xScroll.getSelection() || this.scrollY != yScroll.getSelection());
			}
			
			if(forceRedraw || success){
				redraw();
			}
		}
		return success;
	}
	
	public void redraw(){
		if(!super.isDisposed() && !TuxGuitar.instance().isLocked()){
			this.playedBeat = null;
			this.playedMeasure = null;
			this.editorKit.tryBack();
			this.setPainting(true);
			super.redraw();
		}
	}
	
	public void redrawPlayingMode(){
		if(!super.isDisposed() && !isPainting() && !TuxGuitar.instance().isLocked()){
			//TuxGuitar.instance().lock();
			if(TuxGuitar.instance().getPlayer().isRunning()){
				this.editorKit.tryBack();
				this.setPainting(true);
				
				TGPainter painter = new TGPainter(new GC(this));
				redrawPlayingMode(painter,false);
				painter.dispose();
				
				this.setPainting(false);
			}
			//TuxGuitar.instance().unlock();
		}
	}
	
	private void redrawPlayingMode(TGPainter painter,boolean force){
		if(!super.isDisposed() && !TuxGuitar.instance().isLocked()){
			try{
				TGMeasureImpl measure = TuxGuitar.instance().getEditorCache().getPlayMeasure();
				TGBeatImpl beat = TuxGuitar.instance().getEditorCache().getPlayBeat();
				if(measure != null && beat != null && measure.hasTrack(getCaret().getTrack().getNumber())){
					if(!moveScrollTo(measure) || force){
						boolean paintMeasure = (force || this.playedMeasure == null || !this.playedMeasure.equals(measure));
						if(this.playedMeasure != null && this.playedBeat != null && !this.playedMeasure.isOutOfBounds() && this.playedMeasure.hasTrack(getCaret().getTrack().getNumber())){
							getViewLayout().paintPlayMode(painter, this.playedMeasure, this.playedBeat,paintMeasure);
						}
						if(!measure.isOutOfBounds()){
							getViewLayout().paintPlayMode(painter, measure, beat,paintMeasure);
						}
						this.playedBeat = beat;
						this.playedMeasure =  measure;
					}
				}
			}catch(Throwable throwable){
				throwable.printStackTrace();
			}
		}
	}
	
	public boolean isPainting() {
		return this.painting;
	}
	
	public void setPainting(boolean painting) {
		this.painting = painting;
	}
	
	public Caret getCaret(){
		return this.caret;
	}
	
	public EditorKit getEditorKit() {
		return this.editorKit;
	}
	
	public TGSongManager getSongManager() {
		return this.songManager;
	}
	
	public void setSongManager(TGSongManager songManager) {
		this.songManager = songManager;
	}
	
	public ViewLayout getViewLayout(){
		return this.viewLayout;
	}
	
	public void setViewLayout(ViewLayout viewLayout){
		if(getViewLayout() != null){
			getViewLayout().disposeLayout();
		}
		this.viewLayout = viewLayout;
		if(this.getHorizontalBar() != null){
			this.getHorizontalBar().setSelection(0);
		}
		if(this.getVerticalBar() != null){
			this.getVerticalBar().setSelection(0);
		}
		this.reloadStyles();
	}
	
	public void reloadStyles(){
		if(this.getViewLayout() != null){
			this.getViewLayout().reloadStyles();
			this.setBackground(getViewLayout().getResources().getBackgroundColor());
		}
	}
	
	public void reloadViewLayout(){
		int style =  TuxGuitar.instance().getConfig().getIntConfigValue(TGConfigKeys.LAYOUT_STYLE);
		int mode = TuxGuitar.instance().getConfig().getIntConfigValue(TGConfigKeys.LAYOUT_MODE);
		this.loadViewLayout(style, mode);
	}
	
	private void loadViewLayout( int style, int mode ){
		switch(mode){
			case ViewLayout.MODE_PAGE:
				setViewLayout(new PageViewLayout(this,style));
			break;
			case ViewLayout.MODE_LINEAR:
				setViewLayout(new LinearViewLayout(this,style));
			break;
			default:
				if( mode != ViewLayout.DEFAULT_MODE ){
					this.loadViewLayout( style, ViewLayout.DEFAULT_MODE );
				}
			break;
		}
	}
	
	public void dispose(){
		super.dispose();
		this.getViewLayout().disposeLayout();
	}
}