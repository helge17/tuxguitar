package org.herac.tuxguitar.app.view.dialog.matrix;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.app.graphics.TGFontImpl;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteAction;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.control.TGNoteImpl;
import org.herac.tuxguitar.player.base.MidiPercussionKey;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMatrixEditor implements TGEventListener {
	
	private static final int BORDER_HEIGHT = 20;
	private static final int SCROLL_INCREMENT = 50;
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_MATRIX);
	private static final MidiPercussionKey[] PERCUSSIONS = TuxGuitar.getInstance().getPlayer().getPercussionKeys();
	protected static final int[] DIVISIONS = new int[] {1,2,3,4,6,8,16};
	
	private TGContext context;
	private TGMatrixConfig config;
	private Shell dialog;
	private Composite composite;
	private Composite toolbar;
	private Composite editor;
	private Rectangle clientArea;
	private TGImage buffer;
	private BufferDisposer bufferDisposer;
	private Label durationLabel;
	private Label gridsLabel;
	private Button settings;
	private float width;
	private float height;
	private float bufferWidth;
	private float bufferHeight;
	private float timeWidth;
	private float lineHeight;
	private float leftSpacing;
	private int minNote;
	private int maxNote;
	private int duration;
	private int selection;
	private int grids;
	private int playedTrack;
	private int playedMeasure;
	private TGBeat playedBeat;
	
	public TGMatrixEditor(TGContext context){
		this.context = context;
		this.grids = this.loadGrids();
	}
	
	public void show(){
		this.config = new TGMatrixConfig();
		this.config.load();
		
		this.dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(),SWT.DIALOG_TRIM | SWT.RESIZE);
		this.dialog.setText(TuxGuitar.getProperty("matrix.editor"));
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		this.dialog.setLayout(new GridLayout());
		this.dialog.addDisposeListener(new DisposeListenerImpl());
		this.bufferDisposer = new BufferDisposer();
		
		this.composite = new Composite(this.dialog,SWT.NONE);
		this.composite.setLayout(new GridLayout());
		this.composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.initToolBar();
		this.initEditor();
		this.loadIcons();
		
		this.addListeners();
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
			}
		});
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER);
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.toolbar);
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.editor);
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener( this );
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getIconManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeRedrawListener( this );
	}
	
	private void initToolBar() {
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		
		this.toolbar = new Composite(this.composite, SWT.NONE);
		
		// position
		layout.numColumns ++;
		Button goLeft = new Button(this.toolbar, SWT.ARROW | SWT.LEFT);
		goLeft.addSelectionListener(new TGActionProcessorListener(this.context, TGGoLeftAction.NAME));
		
		layout.numColumns ++;
		Button goRight = new Button(this.toolbar, SWT.ARROW | SWT.RIGHT);
		goRight.addSelectionListener(new TGActionProcessorListener(this.context, TGGoRightAction.NAME));
		
		// separator
		layout.numColumns ++;
		makeToolSeparator(this.toolbar);
		
		// duration
		layout.numColumns ++;
		Button decrement = new Button(this.toolbar, SWT.ARROW | SWT.MIN);
		decrement.addSelectionListener(new TGActionProcessorListener(this.context, TGDecrementDurationAction.NAME));
		
		layout.numColumns ++;
		this.durationLabel = new Label(this.toolbar, SWT.BORDER);
		
		layout.numColumns ++;
		Button increment = new Button(this.toolbar, SWT.ARROW | SWT.MAX);
		increment.addSelectionListener(new TGActionProcessorListener(this.context, TGIncrementDurationAction.NAME));
		
		// separator
		layout.numColumns ++;
		makeToolSeparator(this.toolbar);
		
		// grids
		layout.numColumns ++;
		this.gridsLabel = new Label(this.toolbar,SWT.NONE);
		this.gridsLabel.setText(TuxGuitar.getProperty("matrix.grids"));
		
		layout.numColumns ++;
		final Combo divisionsCombo = new Combo(this.toolbar, SWT.DROP_DOWN | SWT.READ_ONLY);
		divisionsCombo.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false, true));
		for(int i = 0; i < DIVISIONS.length; i ++){
			divisionsCombo.add(Integer.toString(DIVISIONS[i]));
			if(this.grids == DIVISIONS[i]){
				divisionsCombo.select(i);
			}
		}
		if(this.grids == 0){
			divisionsCombo.select(0);
		}
		divisionsCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = divisionsCombo.getSelectionIndex();
				if(index >= 0 && index < DIVISIONS.length){
					setGrids(DIVISIONS[index]);
				}
			}
		});
		
		// settings
		layout.numColumns ++;
		this.settings = new Button(this.toolbar, SWT.PUSH);
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.settings.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		this.settings.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				configure();
			}
		});
		
		this.toolbar.setLayout(layout);
		this.toolbar.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
	}
	
	private void makeToolSeparator(Composite parent){
		Label separator = new Label(parent,SWT.SEPARATOR);
		separator.setLayoutData(new GridData(20,20));
	}
	
	private void loadDurationImage(boolean force) {
		int duration = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration().getValue();
		if(force || this.duration != duration){
			this.duration = duration;
			this.durationLabel.setImage(TuxGuitar.getInstance().getIconManager().getDuration(this.duration));
		}
	}
	
	public void initEditor(){
		TGMatrixMouseListener mouseListener = new TGMatrixMouseListener();
		
		this.selection = -1;
		this.editor = new Composite(this.composite,SWT.DOUBLE_BUFFERED | SWT.BORDER  | SWT.H_SCROLL | SWT.V_SCROLL);
		this.editor.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.editor.setLayout(new FillLayout());
		this.editor.setFocus();
		this.editor.addPaintListener(new TGBufferedPainterListenerLocked(this.context, new TGMatrixPainterListener()));
		this.editor.addMouseListener(mouseListener);
		this.editor.addMouseMoveListener(mouseListener);
		this.editor.addMouseTrackListener(mouseListener);
		this.editor.getHorizontalBar().setIncrement(SCROLL_INCREMENT);
		this.editor.getHorizontalBar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				redraw();
			}
		});
		this.editor.getVerticalBar().setIncrement(SCROLL_INCREMENT);
		this.editor.getVerticalBar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				redraw();
			}
		});
	}
	
	protected void updateScroll(){
		if( this.clientArea != null ){
			int borderWidth = this.editor.getBorderWidth();
			ScrollBar vBar = this.editor.getVerticalBar();
			ScrollBar hBar = this.editor.getHorizontalBar();
			vBar.setMaximum(Math.round(this.height + (borderWidth * 2)));
			vBar.setThumb(Math.round(Math.min(this.height + (borderWidth * 2), this.clientArea.height)));
			hBar.setMaximum(Math.round(this.width + (borderWidth * 2)));
			hBar.setThumb(Math.round(Math.min(this.width + (borderWidth * 2), this.clientArea.width)));
		}
	}
	
	protected int getValueAt(float y){
		if(this.clientArea == null || (y - BORDER_HEIGHT) < 0 || y + BORDER_HEIGHT > this.clientArea.height){
			return -1;
		}
		int scroll = this.editor.getVerticalBar().getSelection();
		int value = (this.maxNote -  ((int)(  (y + scroll - BORDER_HEIGHT)  / this.lineHeight)) );
		return value;
	}
	
	protected long getStartAt(float x){
		TGMeasure measure = getMeasure();
		float posX = (x + this.editor.getHorizontalBar().getSelection());
		long start =(long) (measure.getStart() + (((posX - this.leftSpacing) * measure.getLength()) / (this.timeWidth * measure.getTimeSignature().getNumerator())));
		return start;
	}
	
	protected void paintEditor(TGPainter painter){
		if(!TuxGuitar.getInstance().getPlayer().isRunning()){
			this.resetPlayed();
		}
		
		this.clientArea = this.editor.getClientArea();
		
		if( this.clientArea != null ){
			TGImage buffer = getBuffer();
			
			this.width = this.bufferWidth;
			this.height = (this.bufferHeight + (BORDER_HEIGHT *2));
			
			this.updateScroll();
			int scrollX = this.editor.getHorizontalBar().getSelection();
			int scrollY = this.editor.getVerticalBar().getSelection();
			
			painter.drawImage(buffer,-scrollX,(BORDER_HEIGHT - scrollY));
			this.paintMeasure(painter,(-scrollX), (BORDER_HEIGHT - scrollY) );
			this.paintBorders(painter,(-scrollX),0);
			this.paintPosition(painter,(-scrollX),0);
			this.paintSelection(painter, (-scrollX), (BORDER_HEIGHT - scrollY) );
		}
	}
	
	protected TGImage getBuffer(){
		if( this.clientArea != null ){
			this.bufferDisposer.update(this.clientArea.width, this.clientArea.height);
			if(this.buffer == null || this.buffer.isDisposed()){
				String[] names = null;
				TGMeasure measure = getMeasure();
				this.maxNote = 0;
				this.minNote = 127;
				if( TuxGuitar.getInstance().getSongManager().isPercussionChannel(getCaret().getSong(), measure.getTrack().getChannelId()) ){
					names = new String[PERCUSSIONS.length];
					for(int i = 0; i < names.length;i ++){
						this.minNote = Math.min(this.minNote,PERCUSSIONS[i].getValue());
						this.maxNote = Math.max(this.maxNote,PERCUSSIONS[i].getValue());
						names[i] = PERCUSSIONS[names.length - i -1].getName();
					}
				}else{
					for(int sNumber = 1; sNumber <= measure.getTrack().stringCount();sNumber ++){
						TGString string = measure.getTrack().getString(sNumber);
						this.minNote = Math.min(this.minNote,string.getValue());
						this.maxNote = Math.max(this.maxNote,(string.getValue() + 20));
					}
					names = new String[this.maxNote - this.minNote + 1];
					for(int i = 0; i < names.length;i ++){
						names[i] = (NOTE_NAMES[ (this.maxNote - i) % 12] + ((this.maxNote - i) / 12 ) );
					}
				}
				
				float minimumNameWidth = 110;
				float minimumNameHeight = 0;
				TGPainter painter = new TGPainterImpl(new GC(this.dialog.getDisplay()));
				painter.setFont(new TGFontImpl(this.config.getFont()));
				for(int i = 0; i < names.length;i ++){
					float fmWidth = painter.getFMWidth(names[i]);
					if( fmWidth > minimumNameWidth ){
						minimumNameWidth = fmWidth;
					}
					float fmHeight = painter.getFMHeight();
					if( fmHeight > minimumNameHeight ){
						minimumNameHeight = fmHeight;
					}
				}
				painter.dispose();
				
				int cols = measure.getTimeSignature().getNumerator();
				int rows = (this.maxNote - this.minNote);
				
				this.leftSpacing = minimumNameWidth + 10;
				this.lineHeight = Math.max(minimumNameHeight,( (this.clientArea.height - (BORDER_HEIGHT * 2.0f))/ (rows + 1.0f)));
				this.timeWidth = Math.max((10 * (TGDuration.SIXTY_FOURTH / measure.getTimeSignature().getDenominator().getValue())),( (this.clientArea.width-this.leftSpacing) / cols)  );
				this.bufferWidth = this.leftSpacing + (this.timeWidth * cols);
				this.bufferHeight = (this.lineHeight * (rows + 1));
				this.buffer = new TGImageImpl(this.editor.getDisplay(),Math.round( this.bufferWidth),Math.round(this.bufferHeight));
				
				painter = this.buffer.createPainter();
				painter.setFont(new TGFontImpl(this.config.getFont()));
				painter.setForeground(new TGColorImpl(this.config.getColorForeground()));
				
				for(int i = 0; i <= rows; i++){
					painter.setBackground(new TGColorImpl(this.config.getColorLine( i % 2 ) ));
					painter.initPath(TGPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle(0 ,(i * this.lineHeight),this.bufferWidth ,this.lineHeight);
					painter.closePath();
					painter.drawString(names[i],5, ((i * this.lineHeight) + (this.lineHeight / 2f) + painter.getFMMiddleLine()));
				}
				for(int i = 0; i < cols; i ++){
					float colX = this.leftSpacing + (i * this.timeWidth);
					float divisionWidth = ( this.timeWidth / this.grids );
					for( int j = 0; j < this.grids; j ++ ){
						if( j == 0 ){
							painter.setLineStyleSolid();
						}else{
							painter.setLineStyleDot();
						}
						painter.initPath();
						painter.setAntialias(false);
						painter.moveTo(Math.round( colX + (j * divisionWidth) ),0);
						painter.lineTo(Math.round( colX + (j * divisionWidth) ),this.bufferHeight);
						painter.closePath();
					}
				}
				painter.dispose();
			}
		}
		return this.buffer;
	}
	
	protected void paintMeasure(TGPainter painter,float fromX, float fromY){
		if( this.clientArea != null ){
			TGMeasure measure = getMeasure();
			if(measure != null){
				Iterator<TGBeat> it = measure.getBeats().iterator();
				while(it.hasNext()){
					TGBeat beat = (TGBeat)it.next();
					paintBeat(painter, measure, beat, fromX, fromY);
				}
			}
		}
	}
	
	protected void paintBeat(TGPainter painter,TGMeasure measure,TGBeat beat,float fromX, float fromY){
		if( this.clientArea != null ){
			int minimumY = BORDER_HEIGHT;
			int maximumY = (this.clientArea.height - BORDER_HEIGHT);
			
			for( int v = 0; v < beat.countVoices(); v ++ ){
				TGVoice voice = beat.getVoice(v);
				for( int i = 0 ; i < voice.countNotes() ; i ++){
					TGNoteImpl note = (TGNoteImpl)voice.getNote(i);
					float x1 = (fromX + this.leftSpacing + (((beat.getStart() - measure.getStart()) * (this.timeWidth * measure.getTimeSignature().getNumerator())) / measure.getLength()) + 1);
					float y1 = (fromY + (((this.maxNote - this.minNote) - (note.getRealValue() - this.minNote)) * this.lineHeight) + 1 );
					float x2 = (x1 + ((voice.getDuration().getTime() * this.timeWidth) / measure.getTimeSignature().getDenominator().getTime()) - 2 );
					float y2 = (y1 + this.lineHeight - 2 );
					
					if( y1 >= maximumY || y2 <= minimumY){
						continue;
					}
					
					y1 = ( y1 < minimumY ? minimumY : y1 );
					y2 = ( y2 > maximumY ? maximumY : y2 );
					
					if((x2 - x1) > 0 && (y2 - y1) > 0){
						painter.setBackground(new TGColorImpl( (note.getBeatImpl().isPlaying(TuxGuitar.getInstance().getTablatureEditor().getTablature().getViewLayout()) ? this.config.getColorPlay():this.config.getColorNote() ) ));
						painter.initPath(TGPainter.PATH_FILL);
						painter.setAntialias(false);
						painter.addRectangle(x1,y1, (x2 - x1), (y2 - y1));
						painter.closePath();
					}
				}
			}
		}
	}
	
	protected void paintBorders(TGPainter painter,float fromX, float fromY){
		if( this.clientArea != null ){
			painter.setBackground(new TGColorImpl(this.config.getColorBorder()));
			painter.initPath(TGPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.addRectangle(fromX,fromY,this.bufferWidth ,BORDER_HEIGHT);
			painter.addRectangle(fromX,fromY + (this.clientArea.height - BORDER_HEIGHT),this.bufferWidth ,BORDER_HEIGHT);
			painter.closePath();
			
			painter.initPath();
			painter.setAntialias(false);
			painter.addRectangle(fromX,fromY,this.width,this.clientArea.height);
			painter.closePath();
		}
	}
	
	protected void paintPosition(TGPainter painter,float fromX, float fromY){
		if( this.clientArea != null && !TuxGuitar.getInstance().getPlayer().isRunning()){
			Caret caret = getCaret();
			TGMeasure measure = getMeasure();
			TGBeat beat = caret.getSelectedBeat();
			if(beat != null){
				float x = (((beat.getStart() - measure.getStart()) * (this.timeWidth * measure.getTimeSignature().getNumerator())) / measure.getLength());
				float width = ((beat.getVoice(caret.getVoice()).getDuration().getTime() * this.timeWidth) / measure.getTimeSignature().getDenominator().getTime());
				painter.setBackground(new TGColorImpl(this.config.getColorPosition()));
				painter.initPath(TGPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle(fromX + (this.leftSpacing + x),fromY , width,BORDER_HEIGHT);
				painter.closePath();
				
				painter.initPath(TGPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle(fromX + (this.leftSpacing + x),fromY + (this.clientArea.height - BORDER_HEIGHT), width,BORDER_HEIGHT);
				painter.closePath();
			}
		}
	}
	
	protected void paintSelection(TGPainter painter, float fromX, float fromY){
		if( this.clientArea != null && !TuxGuitar.getInstance().getPlayer().isRunning()){
			if( this.selection >= 0 ){
				int x = Math.round( fromX );
				int y = Math.round( fromY + ((this.maxNote - this.selection) * this.lineHeight)  );
				int width = Math.round( this.bufferWidth );
				int height = Math.round( this.lineHeight );
				
				painter.setAlpha(100);
				painter.setBackground(new TGColorImpl(this.config.getColorLine(2)));
				painter.initPath(TGPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle(x,y,width,height);
				painter.closePath();
			}
		}
	}
	
	protected void updateSelection(float y){
		if(!TuxGuitar.getInstance().getPlayer().isRunning()){
			int previousSelection = this.selection;
			this.selection = getValueAt(y);
			
			if( this.selection != previousSelection ){
				this.redraw();
			}
		}
	}
	
	protected void hit(float x, float y){
		if(!TuxGuitar.getInstance().getPlayer().isRunning()){
			int value = getValueAt(y);
			long start = getStartAt(x);
			Caret caret = getCaret();
			TGMeasure measure = getMeasure();
			TGSongManager songManager = TGDocumentManager.getInstance(this.context).getSongManager();
			TGVoice voice = songManager.getMeasureManager().getVoiceIn(measure, start, caret.getVoice());
			
			if( value >= this.minNote && value <= this.maxNote ){
				if( start >= measure.getStart() && voice != null ){
					if(!removeNote(voice.getBeat(), value)){
						addNote(voice.getBeat(), start, value);
					}
				}else{
					play(value);
				}
			}
			else if( voice != null ){
				moveTo(voice.getBeat());
			}
		}
	}
	
	private boolean removeNote(TGBeat beat,int value) {
		TGMeasure measure = getMeasure();
		
		for(int v = 0; v < beat.countVoices(); v ++){
			TGVoice voice = beat.getVoice( v );
			Iterator<TGNote> it = voice.getNotes().iterator();
			while (it.hasNext()) {
				TGNoteImpl note = (TGNoteImpl) it.next();
				if( note.getRealValue() == value ) {
					TGString string = measure.getTrack().getString(note.getString());
					
					TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGDeleteNoteAction.NAME);
					tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);
					tgActionProcessor.process();
					
					this.moveTo(beat, string);
					
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean addNote(TGBeat beat, long start, int value) {
		if( beat != null ){
			TGMeasure measure = getMeasure();
			
			List<TGString> strings = measure.getTrack().getStrings();
			for(int i = 0;i < strings.size();i ++){
				TGString string = (TGString)strings.get(i);
				if(value >= string.getValue()){
					boolean emptyString = true;
					
					for(int v = 0; v < beat.countVoices(); v ++){
						TGVoice voice = beat.getVoice( v );
						Iterator<TGNote> it = voice.getNotes().iterator();
						while (it.hasNext()) {
							TGNoteImpl note = (TGNoteImpl) it.next();
							if (note.getString() == string.getNumber()) {
								emptyString = false;
								break;
							}
						}
					}
					if( emptyString ){
						TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGChangeNoteAction.NAME);
						tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, start);
						tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, (value - string.getValue()));
						tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
						tgActionProcessor.process();
						
						this.moveTo(beat, string);
						
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void moveTo(TGBeat beat) {
		this.moveTo(beat, null);
	}
	
	private void moveTo(TGBeat beat, TGString string) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGMoveToAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		if( string != null ) {
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		}
		tgActionProcessor.process();
	}
	
	protected void play(final int value){
		new Thread(new Runnable() {
			public void run() {
				TGTrack tgTrack = getMeasure().getTrack();
				TGChannel tgChannel = TuxGuitar.getInstance().getSongManager().getChannel(tgTrack.getSong(), tgTrack.getChannelId());
				if( tgChannel != null ){
					int volume = TGChannel.DEFAULT_VOLUME;
					int balance = TGChannel.DEFAULT_BALANCE;
					int chorus = tgChannel.getChorus();
					int reverb = tgChannel.getReverb();
					int phaser = tgChannel.getPhaser();
					int tremolo = tgChannel.getTremolo();
					int channel = tgChannel.getChannelId();
					int program = tgChannel.getProgram();
					int bank = tgChannel.getBank();
					int[][] beat = new int[][]{ new int[]{ (tgTrack.getOffset() + value) , TGVelocities.DEFAULT } };
					TuxGuitar.getInstance().getPlayer().playBeat(channel,bank,program, volume, balance,chorus,reverb,phaser,tremolo,beat);
				}
			}
		}).start();
	}
	
	protected int loadGrids(){
		int grids = TuxGuitar.getInstance().getConfig().getIntegerValue(TGConfigKeys.MATRIX_GRIDS);
		// check if is valid value
		for(int i = 0 ; i < DIVISIONS.length ; i ++ ){
			if(grids == DIVISIONS[i]){
				return grids;
			}
		}
		return DIVISIONS[1];
	}
	
	protected void setGrids(int grids){
		this.grids = grids;
		this.disposeBuffer();
		this.redraw();
	}
	
	public int getGrids(){
		return this.grids;
	}
	
	protected TGMeasure getMeasure(){
		if(TuxGuitar.getInstance().getPlayer().isRunning()){
			TGMeasure measure = TuxGuitar.getInstance().getEditorCache().getPlayMeasure();
			if(measure != null){
				return measure;
			}
		}
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure();
	}
	
	protected Caret getCaret(){
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	protected void resetPlayed(){
		this.playedBeat = null;
		this.playedMeasure = -1;
		this.playedTrack = -1;
	}
	
	public void redraw(){
		if(!isDisposed()){
			this.editor.redraw();
			this.loadDurationImage(false);
		}
	}
	
	public void redrawPlayingMode(){
		if(!isDisposed() && !TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked() && TuxGuitar.getInstance().getPlayer().isRunning()){
			TGMeasure measure = TuxGuitar.getInstance().getEditorCache().getPlayMeasure();
			TGBeat beat = TuxGuitar.getInstance().getEditorCache().getPlayBeat();
			if(measure != null && beat != null){
				int currentMeasure = measure.getNumber();
				int currentTrack = measure.getTrack().getNumber();
				boolean changed = (currentMeasure != this.playedMeasure || currentTrack != this.playedTrack);
				if(changed){
					this.resetPlayed();
					this.editor.redraw();
				}
				else{
					TGPainter painter = new TGPainterImpl(new GC(this.editor));
					int scrollX = this.editor.getHorizontalBar().getSelection();
					int scrollY = this.editor.getVerticalBar().getSelection();
					if(this.playedBeat != null){
						this.paintBeat(painter,measure,this.playedBeat,(-scrollX), (BORDER_HEIGHT - scrollY));
					}
					this.paintBeat(painter,measure,beat,(-scrollX), (BORDER_HEIGHT - scrollY));
					painter.dispose();
				}
				this.playedMeasure = currentMeasure;
				this.playedTrack = currentTrack;
				this.playedBeat = beat;
			}
		}
	}
	
	protected void configure(){
		this.config.configure(this.dialog);
		this.disposeBuffer();
		this.redraw();
	}
	
	private void layout(){
		if( !isDisposed() ){
			this.toolbar.layout();
			this.editor.layout();
			this.composite.layout(true,true);
		}
	}
	
	public void loadIcons(){
		if( !isDisposed() ){
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
			this.loadDurationImage(true);
			this.layout();
			this.redraw();
		}
	}
	
	public void loadProperties() {
		if( !isDisposed() ){
			this.dialog.setText(TuxGuitar.getProperty("matrix.editor"));
			this.gridsLabel.setText(TuxGuitar.getProperty("matrix.grids"));
			this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
			this.disposeBuffer();
			this.layout();
			this.redraw();
		}
	}
	
	public void dispose(){
		if(!isDisposed()){
			this.dialog.dispose();
		}
	}
	
	protected void disposeBuffer(){
		if( this.buffer != null && !this.buffer.isDisposed()){
			this.buffer.dispose();
			this.buffer = null;
		}
	}
	
	protected void dispose(Resource[] resources){
		if(resources != null){
			for(int i = 0; i < resources.length; i ++){
				dispose(resources[i]);
			}
		}
	}
	
	protected void dispose(Resource resource){
		if(resource != null){
			resource.dispose();
		}
	}
	
	protected void disposeAll(){
		this.disposeBuffer();
		this.config.dispose();
	}
	
	protected Composite getEditor(){
		return this.editor;
	}
	
	protected class BufferDisposer{
		private int numerator;
		private int denominator;
		private int track;
		private boolean percussion;
		
		private int width;
		private int height;
		
		public void update(int width, int height){
			TGMeasure measure = getMeasure();
			int track = measure.getTrack().getNumber();
			int numerator = measure.getTimeSignature().getNumerator();
			int denominator = measure.getTimeSignature().getDenominator().getValue();
			boolean percussion = TuxGuitar.getInstance().getSongManager().isPercussionChannel(measure.getTrack().getSong(), measure.getTrack().getChannelId());
			if( width != this.width || height != this.height || this.track != track || this.numerator != numerator || this.denominator != denominator || this.percussion != percussion ){
				disposeBuffer();
			}
			this.track = track;
			this.numerator = numerator;
			this.denominator = denominator;
			this.percussion = percussion;
			this.width = width;
			this.height = height;
		}
	}
	
	protected class DisposeListenerImpl implements DisposeListener{
		public void widgetDisposed(DisposeEvent e) {
			disposeAll();
		}
	}
	
	protected class TGMatrixMouseListener implements MouseListener, MouseMoveListener, MouseTrackListener {
		
		public TGMatrixMouseListener(){
			super();
		}
		
		public void mouseUp(MouseEvent e) {
			getEditor().setFocus();
			if( e.button == 1 ){
				if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
					hit(e.x,e.y);
				}
			}
		}
		
		public void mouseMove(MouseEvent e) {
			if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
				updateSelection(e.y);
			}
		}
		
		public void mouseExit(MouseEvent e) {
			if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
				updateSelection(-1);
			}
		}
		
		public void mouseEnter(MouseEvent e) {
			if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
				redraw();
			}
		}
		
		public void mouseDoubleClick(MouseEvent e) {
			// not implemented
		}
		
		public void mouseDown(MouseEvent e) {
			// not implemented
		}
		
		public void mouseHover(MouseEvent e) {
			// not implemented
		}
	}
	
	private class TGMatrixPainterListener implements TGBufferedPainterHandle {
		
		public TGMatrixPainterListener(){
			super();
		}

		public void paintControl(TGPainter painter) {
			TGMatrixEditor.this.paintEditor(painter);
		}

		public Composite getPaintableControl() {
			return TGMatrixEditor.this.editor;
		}
	}
	
	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redraw();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayingMode();
		}
	}

	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadIcons();
				}
				else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadProperties();
				}
				else if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					processRedrawEvent(event);
				}
			}
		});
	}
	
	public static TGMatrixEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMatrixEditor.class.getName(), new TGSingletonFactory<TGMatrixEditor>() {
			public TGMatrixEditor createInstance(TGContext context) {
				return new TGMatrixEditor(context);
			}
		});
	}
}
