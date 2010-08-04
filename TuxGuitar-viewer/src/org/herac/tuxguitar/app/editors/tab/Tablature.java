/*
 * Created on 29-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.editors.tab;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.TGPainter;
import org.herac.tuxguitar.app.editors.TGScrollBar;
import org.herac.tuxguitar.app.editors.tab.layout.PageViewLayout;
import org.herac.tuxguitar.app.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.app.system.config.TGConfig;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDuration;
/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Tablature{
	
	private Component component;
	private TGScrollBar scroll;
	
	private TGSongManager songManager;
	private Caret caret;
	//private int width;
	private int height;
	private ViewLayout viewLayout;
	
	private TGBeatImpl playedBeat;
	private TGMeasureImpl playedMeasure;
	
	//private int scrollX;
	private int scrollY;
	private boolean resetScroll;
	protected long lastVScrollTime;
	protected long lastHScrollTime;
	
	private boolean painting;
	
	public Tablature(Component component,TGScrollBar scroll) {
		this.component = component;
		this.scroll = scroll;
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
		TuxGuitar.instance().lock();
		this.setPainting(true);
		try{
			this.checkScroll();
			
			Rectangle area = this.component.getBounds();
			
			this.scrollY = this.scroll.getValue();
			
			this.getViewLayout().paint(painter, area, 0, -this.scrollY );
			
			//this.width = this.viewLayout.getWidth();
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
					
				moveScrollTo(getCaret().getMeasure() , area);
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		this.setPainting(false);
		TuxGuitar.instance().unlock();
	}
	
	public void resetScroll(){
		this.resetScroll = true;
	}
	
	public void checkScroll(){
		if(this.resetScroll){
			this.scroll.setValue(0);
			this.resetScroll = false;
		}
	}
	
	public void updateScroll(){
		this.scroll.setMaximum((int)Math.max(0, (this.height - this.component.getSize().getHeight()) ) );
	}
	
	public boolean moveScrollTo(TGMeasureImpl measure){
		return this.moveScrollTo(measure, this.component.getBounds());
	}
	
	public boolean moveScrollTo(TGMeasureImpl measure, Rectangle area){
		boolean success = false;
		if(measure != null && measure.getTs() != null){
			int mX = measure.getPosY();
			int mWidth = measure.getTs().getSize();
			int marginWidth = getViewLayout().getFirstTrackSpacing();
			boolean forceRedraw = false;
			
			//Solo se ajusta si es necesario
			//si el largo del compas es mayor al de la pantalla. nunca se puede ajustar a la medida.
			if( mX < 0 || ( (mX + mWidth ) > area.height && (area.height >= mWidth + marginWidth || mX > marginWidth) )  ){
				this.scroll.setValue((this.scrollY + mX) - marginWidth );
				success = true;
			}
			
			if(!success){
				// Si la seleccion "real" del scroll es distinta a la anterior, se fuerza el redraw
				forceRedraw = (this.scrollY != this.scroll.getValue() );
			}
			
			if(forceRedraw || success){
				this.component.repaint();
			}
		}
		return success;
	}
	
	public void beforeRedraw(){
		TuxGuitar.instance().lock();
		this.playedBeat = null;
		this.playedMeasure = null;
		this.setPainting(true);
		TuxGuitar.instance().unlock();
	}
	
	public void redrawPlayingMode(){
		if(!isPainting() && !TuxGuitar.instance().isLocked()){
			TuxGuitar.instance().lock();
			if(TuxGuitar.instance().getPlayer().isRunning()){
				this.setPainting(true);
				
				TGPainter painter = new TGPainter((Graphics2D)this.component.getGraphics());
				redrawPlayingMode(painter,false);
				painter.dispose();
				
				this.setPainting(false);
			}
			TuxGuitar.instance().unlock();
		}
	}
	
	private void redrawPlayingMode(TGPainter painter,boolean force){
		if(!TuxGuitar.instance().isLocked()){
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
		/*
		if(this.getHorizontalBar() != null){
			this.getHorizontalBar().setSelection(0);
		}
		if(this.getVerticalBar() != null){
			this.getVerticalBar().setSelection(0);
		}*/
		this.reloadStyles();
	}
	
	public void reloadStyles(){
		if(this.getViewLayout() != null){
			this.getViewLayout().reloadStyles();
			this.component.setBackground(getViewLayout().getResources().getBackgroundColor());
		}
	}
	
	public void reloadViewLayout(){
		setViewLayout(new PageViewLayout(this,TGConfig.LAYOUT_STYLE));
	}
	
	public void dispose(){
		this.getViewLayout().disposeLayout();
	}
}