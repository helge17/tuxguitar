package org.herac.tuxguitar.app.view.component.tab;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGResourceFactoryImpl;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutHorizontal;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;
import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class Tablature implements TGController {
	
	private TGContext context; 
	private TGResourceFactory resourceFactory;
	private TGDocumentManager documentManager;
	private TGResourceBuffer resourceBuffer;
	private TGSyncProcess disposeUnregisteredResources;
	
	private Caret caret;
	private TGLayout viewLayout;
	private EditorKit editorKit;
	
	public Tablature(TGContext context, TGDocumentManager documentManager) {
		this.context = context;
		this.documentManager = documentManager;
		this.caret = new Caret(this);
		this.editorKit = new EditorKit(this);
		this.createSyncProcesses();
	}
	
	public void createSyncProcesses() {
		this.disposeUnregisteredResources = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				getResourceBuffer().disposeUnregisteredResources();
			}
		});
	}
	
	public void updateTablature(){
		this.getViewLayout().updateSong();
		this.getCaret().update();
		this.disposeUnregisteredResources.process();
	}
	
	public void updateMeasure(int number){
		this.getViewLayout().updateMeasureNumber(number);
		this.getCaret().update();
		this.disposeUnregisteredResources.process();
	}
	
	public void resetCaret(){
		this.caret.update(1, TGDuration.QUARTER_TIME,1);
	}
	
	public void paintTablature(TGPainter painter, TGRectangle area, float fromX, float fromY){
		this.fillBackground(painter, area);
		this.getViewLayout().paint(painter, area, fromX, fromY);
		this.getCaret().paintCaret(this.getViewLayout(), painter);
		this.getEditorKit().paintSelection(this.getViewLayout(), painter);
	}
	
	public void fillBackground(TGPainter painter, TGRectangle area) {
		painter.setBackground(this.getViewLayout().getResources().getBackgroundColor());
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(0, 0, area.getWidth(), area.getHeight());
		painter.closePath();
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
		this.reloadStyles();
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
		this.getCaret().dispose();
		this.getViewLayout().disposeLayout();
		this.getResourceBuffer().disposeAllResources();
	}
	
	public TGResourceFactory getResourceFactory(){
		if( this.resourceFactory == null ){
			this.resourceFactory = new TGResourceFactoryImpl(TGApplication.getInstance(this.context).getFactory());
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