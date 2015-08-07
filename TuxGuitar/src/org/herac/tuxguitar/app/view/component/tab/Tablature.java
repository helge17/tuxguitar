/*
 * Created on 29-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.component.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutHorizontal;
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
/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Tablature extends Composite implements TGController {
	
	private static final int SCROLL_DELAY = 15;
	private static final int SCROLL_INCREMENT = 50;
	
	private TGContext context; 
	private TGResourceFactory resourceFactory;
	private TGDocumentManager documentManager;
	private TGResourceBuffer resourceBuffer;
	private TGSyncProcess disposeUnregisteredResources;
	
	private Caret caret;
	private int width;
	private int height;
	private TGLayout viewLayout;
	private EditorKit editorKit;
	
	private TGBeatImpl playedBeat;
	private TGMeasureImpl playedMeasure;
	private int scrollX;
	private int scrollY;
	private boolean resetScroll;
	protected long lastVScrollTime;
	protected long lastHScrollTime;
	
	private boolean painting;
	
	public Tablature(TGContext context, Composite parent, int style, TGDocumentManager documentManager ) {
		super(parent, style);
		this.context = context;
		this.documentManager = documentManager;
		this.caret = new Caret(this);
		this.editorKit = new EditorKit(this);
		this.createSyncProcesses();
	}
	
	public void initGUI(){
		this.addPaintListener(new TGBufferedPainterListenerLocked(this.context, new TablaturePaintListener(this)) );
		
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
	
	public void createSyncProcesses() {
		this.disposeUnregisteredResources = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				getResourceBuffer().disposeUnregisteredResources();
			}
		});
	}
	
	public void updateTablature(){
		this.playedBeat = null;
		this.playedMeasure = null;
		this.getViewLayout().updateSong();
		this.getCaret().update();
		this.disposeUnregisteredResources.process();
	}
	
	public void updateMeasure(int number){
		this.playedBeat = null;
		this.playedMeasure = null;
		this.getViewLayout().updateMeasureNumber(number);
		this.getCaret().update();
		this.disposeUnregisteredResources.process();
	}
	
	public void resetCaret(){
		this.caret.update(1, TGDuration.QUARTER_TIME,1);
	}
	
	public void paintTablature(TGPainter painter){
		this.setPainting(true);
		try{
			this.checkScroll();
			
			TGRectangle area = createRectangle(getClientArea());
			ScrollBar xScroll = getHorizontalBar();
			ScrollBar yScroll = getVerticalBar();
			this.scrollX = xScroll.getSelection();
			this.scrollY = yScroll.getSelection();
			
			this.fillBackground(painter, area);
			this.getViewLayout().paint(painter, area, -this.scrollX, -this.scrollY);
			this.getCaret().paintCaret(this.getViewLayout(), painter);
			this.getEditorKit().paintSelection(this.getViewLayout(), painter);
			
			this.width = Math.round(this.viewLayout.getWidth());
			this.height = Math.round(this.viewLayout.getHeight());
			
			this.updateScroll();
			
			if(TuxGuitar.getInstance().getPlayer().isRunning()){
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
	}
	
	public void fillBackground(TGPainter painter, TGRectangle area) {
		painter.setBackground(this.getViewLayout().getResources().getBackgroundColor());
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(0, 0, area.getWidth(), area.getHeight());
		painter.closePath();
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
		return moveScrollTo(measure, getHorizontalBar(), getVerticalBar(), createRectangle(getClientArea()));
	}
	
	public boolean moveScrollTo(TGMeasureImpl measure,ScrollBar xScroll,ScrollBar yScroll,TGRectangle area){
		boolean success = false;
		if( measure != null && measure.getTs() != null ){
			int mX = Math.round(measure.getPosX());
			int mY = Math.round(measure.getPosY());
			int mWidth = Math.round(measure.getWidth(getViewLayout()));
			int mHeight = Math.round(measure.getTs().getSize());
			int marginWidth = Math.round(getViewLayout().getFirstMeasureSpacing());
			int marginHeight = Math.round(getViewLayout().getFirstTrackSpacing());
			boolean forceRedraw = false;
			
			//Solo se ajusta si es necesario
			//si el largo del compas es mayor al de la pantalla. nunca se puede ajustar a la medida.
			if( mX < 0 || ( (mX + mWidth ) > area.getWidth() && (area.getWidth() >= mWidth + marginWidth || mX > marginWidth) ) ){
				xScroll.setSelection((this.scrollX + mX) - marginWidth );
				success = true;
			}
			
			//Solo se ajusta si es necesario
			//si el alto del compas es mayor al de la pantalla. nunca se puede ajustar a la medida.
			if( mY < 0 || ( (mY + mHeight ) > area.getHeight() && (area.getHeight() >= mHeight + marginHeight || mY > marginHeight) ) ){
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
		if(!super.isDisposed() ){
			this.playedBeat = null;
			this.playedMeasure = null;
			this.setPainting(true);
			super.redraw();
		}
	}
	
	public void redrawPlayingMode(){
		if(!super.isDisposed() && !isPainting()){
			if( TuxGuitar.getInstance().getPlayer().isRunning() ){
				this.setPainting(true);
				
				TGPainter painter = new TGPainterImpl(new GC(this));
				redrawPlayingMode(painter, false);
				painter.dispose();
				
				this.setPainting(false);
			}
		}
	}
	
	private void redrawPlayingMode(TGPainter painter, boolean force){
		if(!super.isDisposed()){
			try{
				TGMeasureImpl measure = TuxGuitar.getInstance().getEditorCache().getPlayMeasure();
				TGBeatImpl beat = TuxGuitar.getInstance().getEditorCache().getPlayBeat();
				if(measure != null && measure.hasTrack(getCaret().getTrack().getNumber())){
					if(!moveScrollTo(measure) || force){
						boolean paintMeasure = (force || this.playedMeasure == null || !this.playedMeasure.equals(measure));
						if(this.playedMeasure != null && this.playedBeat != null && !this.playedMeasure.isOutOfBounds() && this.playedMeasure.hasTrack(getCaret().getTrack().getNumber())){
							getViewLayout().paintPlayMode(painter, this.playedMeasure, this.playedBeat,paintMeasure);
						}
						if(!measure.isOutOfBounds()){
							getViewLayout().paintPlayMode(painter, measure, beat, paintMeasure);
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
	
	public TGContext getContext() {
		return this.context;
	}
	
	public TGSongManager getSongManager() {
		return this.documentManager.getSongManager();
	}
	
	public TGSong getSong() {
		return this.documentManager.getSong();
	}
	
	public TGLayout getViewLayout(){
		return this.viewLayout;
	}
	
	public void setViewLayout(TGLayout viewLayout){
		if( getViewLayout() != null ){
			getViewLayout().disposeLayout();
		}
		this.viewLayout = viewLayout;
		if( this.getHorizontalBar() != null ){
			this.getHorizontalBar().setSelection(0);
		}
		if( this.getVerticalBar() != null ){
			this.getVerticalBar().setSelection(0);
		}
		this.reloadStyles();
	}
	
	public TGRectangle createRectangle( Rectangle rectangle ){
		return new TGRectangle(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
	}
	
	public void reloadStyles(){
		if( this.getViewLayout() != null ){
			this.getViewLayout().loadStyles(1f);
		}
	}
	
	public void reloadViewLayout(){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		
		this.loadViewLayout(config.getIntegerValue(TGConfigKeys.LAYOUT_STYLE), config.getIntegerValue(TGConfigKeys.LAYOUT_MODE));
	}
	
	private void loadViewLayout( int style, int mode ){
		switch(mode){
			case TGLayout.MODE_VERTICAL:
				setViewLayout(new TGLayoutVertical(this, style));
			break;
			case TGLayout.MODE_HORIZONTAL:
				setViewLayout(new TGLayoutHorizontal(this, style));
			break;
			default:
				if( mode != TGLayout.DEFAULT_MODE ){
					this.loadViewLayout( style, TGLayout.DEFAULT_MODE );
				}
			break;
		}
	}
	
	public void dispose(){
		super.dispose();
		this.getCaret().dispose();
		this.getViewLayout().disposeLayout();
		this.getResourceBuffer().disposeAllResources();
	}
	
	public TGResourceFactory getResourceFactory(){
		if( this.resourceFactory == null ){
			this.resourceFactory = new TGResourceFactoryImpl(this.getDisplay());
		}
		return this.resourceFactory;
	}
	
	public TGResourceBuffer getResourceBuffer(){
		if( this.resourceBuffer == null ){
			this.resourceBuffer = new TGResourceBuffer();
		}
		return this.resourceBuffer;
	}
	
	public int getTrackSelection(){
		if( (getViewLayout().getStyle() & TGLayout.DISPLAY_MULTITRACK) == 0 ){
			return getCaret().getTrack().getNumber();
		}
		return -1;
	}
	
	public boolean isRunning(TGBeat beat) {
		return ( isRunning( beat.getMeasure() ) && TuxGuitar.getInstance().getEditorCache().isPlaying(beat.getMeasure(),beat) );
	}
	
	public boolean isRunning(TGMeasure measure) {
		return ( measure.getTrack().equals(getCaret().getTrack()) && TuxGuitar.getInstance().getEditorCache().isPlaying( measure ) );
	}
	
	public boolean isLoopSHeader(TGMeasureHeader measureHeader){
		MidiPlayerMode pm = TuxGuitar.getInstance().getPlayer().getMode();
		return ( pm.isLoop() && pm.getLoopSHeader() == measureHeader.getNumber() );
	}
	
	public boolean isLoopEHeader(TGMeasureHeader measureHeader){
		MidiPlayerMode pm = TuxGuitar.getInstance().getPlayer().getMode();
		return ( pm.isLoop() && pm.getLoopEHeader() == measureHeader.getNumber() );
	}
	
	public void configureStyles(TGLayoutStyles styles){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		
		styles.setBufferEnabled(true);
		styles.setStringSpacing(config.getIntegerValue(TGConfigKeys.TAB_LINE_SPACING));
		styles.setScoreLineSpacing(config.getIntegerValue(TGConfigKeys.SCORE_LINE_SPACING));
		styles.setFirstMeasureSpacing(20);
		styles.setMinBufferSeparator(20);
		styles.setMinTopSpacing(30);
		styles.setMinScoreTabSpacing(config.getIntegerValue(TGConfigKeys.MIN_SCORE_TABLATURE_SPACING));
		styles.setFirstTrackSpacing(config.getIntegerValue(TGConfigKeys.FIRST_TRACK_SPACING));
		styles.setTrackSpacing(config.getIntegerValue(TGConfigKeys.TRACK_SPACING));
		styles.setChordFretIndexSpacing(8);
		styles.setChordStringSpacing(5);
		styles.setChordFretSpacing(6);
		styles.setChordNoteSize(4);
		styles.setChordLineWidth(1);
		styles.setRepeatEndingSpacing(20);
		styles.setTextSpacing(15);
		styles.setMarkerSpacing(15);
		styles.setLoopMarkerSpacing(5);
		styles.setDivisionTypeSpacing(10);
		styles.setEffectSpacing(8);
		
		styles.setDefaultFont(config.getFontModelConfigValue(TGConfigKeys.FONT_DEFAULT));
		styles.setNoteFont(config.getFontModelConfigValue(TGConfigKeys.FONT_NOTE));
		styles.setTimeSignatureFont(config.getFontModelConfigValue(TGConfigKeys.FONT_TIME_SIGNATURE));
		styles.setLyricFont(config.getFontModelConfigValue(TGConfigKeys.FONT_LYRIC));
		styles.setTextFont(config.getFontModelConfigValue(TGConfigKeys.FONT_TEXT));
		styles.setMarkerFont(config.getFontModelConfigValue(TGConfigKeys.FONT_MARKER));
		styles.setGraceFont(config.getFontModelConfigValue(TGConfigKeys.FONT_GRACE));
		styles.setChordFont(config.getFontModelConfigValue(TGConfigKeys.FONT_CHORD));
		styles.setChordFretFont(config.getFontModelConfigValue(TGConfigKeys.FONT_CHORD_FRET));
		styles.setBackgroundColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_BACKGROUND));
		styles.setLineColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LINE));
		styles.setScoreNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_SCORE_NOTE));
		styles.setTabNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_TAB_NOTE));
		styles.setPlayNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_PLAY_NOTE));
		styles.setLoopSMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_S_MARKER));
		styles.setLoopEMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_E_MARKER));
		
		getCaret().setColor1(config.getColorModelConfigValue(TGConfigKeys.COLOR_CARET_1));
		getCaret().setColor2(config.getColorModelConfigValue(TGConfigKeys.COLOR_CARET_2));
	}
}