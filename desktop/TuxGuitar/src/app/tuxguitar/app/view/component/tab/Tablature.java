package app.tuxguitar.app.view.component.tab;

import java.util.Collections;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tab.edit.EditorKit;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.util.TGSyncProcess;
import app.tuxguitar.graphics.control.TGController;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGLayoutHorizontal;
import app.tuxguitar.graphics.control.TGLayoutStyles;
import app.tuxguitar.graphics.control.TGLayoutVertical;
import app.tuxguitar.graphics.control.TGResourceBuffer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class Tablature implements TGController {

	public static final Float DEFAULT_SCALE = 1f;

	private TGContext context;
	private UIResourceFactory resourceFactory;
	private TGDocumentManager documentManager;
	private TGResourceBuffer resourceBuffer;
	private TGSyncProcess disposeUnregisteredResources;

	private Caret caret;
	private Selector selector;
	private TGLayout viewLayout;
	private EditorKit editorKit;
	private Float scale;

	public Tablature(TGContext context, TGDocumentManager documentManager) {
		this.context = context;
		this.documentManager = documentManager;
		this.scale = DEFAULT_SCALE;
		this.caret = new Caret(this);
		this.selector = new Selector(this);
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

	public void updateMeasures(List<Integer> numbers, boolean updateCaret){
		this.getViewLayout().updateMeasureNumbers(numbers);
		if (!numbers.isEmpty() || updateCaret) {
			this.getCaret().update();
		}
		this.disposeUnregisteredResources.process();
	}

	public void resetCaret(){
		this.caret.update(1, TGDuration.QUARTER_TIME, 1);
	}

	public void paintTablature(UIPainter painter, UIRectangle area, float fromX, float fromY){
		this.getViewLayout().fillBackground(painter, area);
		this.getViewLayout().paint(painter, area, fromX, fromY);
		this.getCaret().paintCaret(this.getViewLayout(), painter);
		this.getEditorKit().paintSelection(this.getViewLayout(), painter);
		this.getSelector().paintSelectedArea(this.getViewLayout(), painter);
	}

	public Float getScale() {
		return scale;
	}

	public Caret getCaret(){
		return this.caret;
	}

	public Selector getSelector() {
		return selector;
	}

	public TGBeatRange getCurrentBeatRange() {
		if (getSelector().isActive()) {
			return getSelector().getBeatRange();
		}
		TGBeat beat = getCaret().getSelectedBeat();
		if (beat != null) {
			return TGBeatRange.single(beat);
		}
		return TGBeatRange.empty();
	}

	public TGNoteRange getCurrentNoteRange() {
		int voice = getCaret().getVoice();
		if (getSelector().isActive()) {
			return getSelector().getNoteRange(Collections.singletonList(voice));
		} else {
			TGNote defaultNote = getCaret().getSelectedNote();
			if (defaultNote != null && defaultNote.getVoice().getIndex() == voice) {
				return TGNoteRange.single(defaultNote);
			}
		}
		return TGNoteRange.empty();
	}

	public void restoreStateFrom(TGDocument document) {
		this.getCaret().restoreStateFrom(document);
		this.getSelector().restoreStateFrom(document);
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

	public void reloadStyles() {
		if( this.getViewLayout() != null ){
			this.getViewLayout().loadStyles(this.scale);
		}
		this.loadCaretStyles();
	}

	public void reloadViewLayout(){
		TGConfigManager config = TGConfigManager.getInstance(this.context);

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

	public void loadCaretStyles() {
		TGConfigManager config = TGConfigManager.getInstance(this.context);

		getCaret().setColorCurrentVoice(config.getColorModelConfigValue(TGConfigKeys.COLOR_CARET_CURRENT_VOICE));
		getCaret().setColorOtherVoice(config.getColorModelConfigValue(TGConfigKeys.COLOR_CARET_OTHER_VOICE));
		getCaret().setAlpha(config.getIntegerValue(TGConfigKeys.COLOR_CARET_ALPHA));
	}

	public void scale(Float scale) {
		if(!this.scale.equals(scale)) {
			this.scale = (scale != null ? scale : DEFAULT_SCALE);
			this.reloadStyles();
		}
	}

	public void dispose(){
		this.getCaret().dispose();
		this.getViewLayout().disposeLayout();
		this.getResourceBuffer().disposeAllResources();
	}

	public UIResourceFactory getResourceFactory(){
		if( this.resourceFactory == null ){
			this.resourceFactory = TGApplication.getInstance(this.context).getFactory();
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
		return ( isRunning( beat.getMeasure() ) && TGTransport.getInstance(this.context).getCache().isPlaying(beat.getMeasure(),beat) );
	}

	public boolean isRunning(TGMeasure measure) {
		return ( measure.getTrack().equals(getCaret().getTrack()) && TGTransport.getInstance(this.context).getCache().isPlaying( measure ) );
	}

	public boolean isLoopSHeader(TGMeasureHeader measureHeader){
		MidiPlayerMode pm = TuxGuitar.getInstance().getPlayer().getMode();
		return ( pm.isLoop() &&
				(pm.getLoopSHeader() == measureHeader.getNumber()
					|| (pm.getLoopSHeader() == -1 && measureHeader.getNumber()==1)) );
	}

	public boolean isLoopEHeader(TGMeasureHeader measureHeader){
		MidiPlayerMode pm = TuxGuitar.getInstance().getPlayer().getMode();
		return ( pm.isLoop() &&
				(pm.getLoopEHeader() == measureHeader.getNumber()
					|| (pm.getLoopEHeader() == -1 && measureHeader.getNumber()==measureHeader.getSong().countMeasureHeaders())) );
	}

	public TGLayoutStyles getStyles() {
		return new TablatureStyles(TGConfigManager.getInstance(this.context));
	}

}