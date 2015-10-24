package org.herac.tuxguitar.app.view.dialog.fretboard;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.app.graphics.TGFontImpl;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteAction;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;

public class TGFretBoard extends Composite {
	
	public static final int MAX_FRETS = 24;
	public static final int TOP_SPACING = 10;
	public static final int BOTTOM_SPACING = 10;
	
	private static final int STRING_SPACING = TuxGuitar.getInstance().getConfig().getIntegerValue(TGConfigKeys.FRETBOARD_STRING_SPACING);
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_FRETBOARD);
	
	private TGContext context;
	private TGFretBoardConfig config;
	private Composite toolComposite;
	private Label durationLabel;
	private Label scaleName;
	private Button scale;
	private Button settings;
	private TGImage fretBoard;
	
	private TGBeat beat;
	private TGBeat externalBeat;
	
	private int[] frets;
	private int[] strings;
	private int fretSpacing;
	private boolean changes;
	private Point lastSize;
	private int duration;
	protected Combo handSelector;
	protected Composite fretBoardComposite;
	
	public TGFretBoard(TGContext context, Composite parent) {
		super(parent, SWT.NONE);
		this.context = context;
		this.config = new TGFretBoardConfig();
		this.config.load();
		this.setLayout(new FormLayout());
		this.initToolBar();
		this.initEditor();
		this.loadIcons();
		this.loadProperties();
		
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.toolComposite);
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.fretBoardComposite);
	}
	
	private void initToolBar() {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0,0);
		
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 0;
		layout.marginWidth = 0;
		
		this.toolComposite = new Composite(this, SWT.NONE);
		
		// position
		layout.numColumns ++;
		Button goLeft = new Button(this.toolComposite, SWT.ARROW | SWT.LEFT);
		goLeft.addSelectionListener(new TGActionProcessorListener(this.context, TGGoLeftAction.NAME));
		
		layout.numColumns ++;
		Button goRight = new Button(this.toolComposite, SWT.ARROW | SWT.RIGHT);
		goRight.addSelectionListener(new TGActionProcessorListener(this.context, TGGoRightAction.NAME));
		
		// separator
		layout.numColumns ++;
		makeToolSeparator(this.toolComposite);
		
		// duration
		layout.numColumns ++;
		Button decrement = new Button(this.toolComposite, SWT.ARROW | SWT.MIN);
		decrement.addSelectionListener(new TGActionProcessorListener(this.context, TGDecrementDurationAction.NAME));
		
		layout.numColumns ++;
		this.durationLabel = new Label(this.toolComposite, SWT.BORDER);
		
		layout.numColumns ++;
		Button increment = new Button(this.toolComposite, SWT.ARROW | SWT.MAX);
		increment.addSelectionListener(new TGActionProcessorListener(this.context, TGIncrementDurationAction.NAME));
		
		// separator
		layout.numColumns ++;
		makeToolSeparator(this.toolComposite);
		
		// hand selector
		layout.numColumns ++;
		this.handSelector = new Combo(this.toolComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.handSelector.add(TuxGuitar.getProperty("fretboard.right-mode"));
		this.handSelector.add(TuxGuitar.getProperty("fretboard.left-mode"));
		this.handSelector.select( this.getDirection(this.config.getDirection()) );
		this.handSelector.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDirection(TGFretBoard.this.handSelector.getSelectionIndex());
			}
		});
		
		// separator
		layout.numColumns ++;
		makeToolSeparator(this.toolComposite);
		
		// scale
		layout.numColumns ++;
		this.scale = new Button(this.toolComposite, SWT.PUSH);
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.scale.addSelectionListener(new TGActionProcessorListener(this.context, TGOpenScaleDialogAction.NAME));
		
		// scale name
		layout.numColumns ++;
		this.scaleName = new Label(this.toolComposite, SWT.LEFT);
		
		// settings
		layout.numColumns ++;
		this.settings = new Button(this.toolComposite, SWT.PUSH);
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.settings.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		this.settings.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				configure();
			}
		});
		
		this.toolComposite.setLayoutData(data);
		this.toolComposite.setLayout(layout);
	}
	
	private void makeToolSeparator(Composite parent){
		Label separator = new Label(parent,SWT.SEPARATOR);
		separator.setLayoutData(new GridData(20,20));
	}
	
	private void initEditor() {
		this.lastSize = new Point(0,0);
		
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(this.toolComposite,0);
		data.bottom = new FormAttachment(100, 0);
		
		this.fretBoardComposite = new Composite(this, SWT.BORDER);
		this.fretBoardComposite.setLayoutData(data);
		this.fretBoardComposite.setBackground(this.config.getColorBackground());
		this.fretBoardComposite.addMouseListener(new TGFretBoardMouseListener());
		this.fretBoardComposite.addPaintListener(new TGBufferedPainterListenerLocked(this.context, new TGFretBoardPainterListener()));
	}
	
	private void loadDurationImage(boolean force) {
		int duration = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration().getValue();
		if(force || this.duration != duration){
			this.duration = duration;
			this.durationLabel.setImage(TuxGuitar.getInstance().getIconManager().getDuration(this.duration));
		}
	}
	
	private void loadScaleName() {
		int scaleKey = TuxGuitar.getInstance().getScaleManager().getSelectionKey();
		int scaleIndex = TuxGuitar.getInstance().getScaleManager().getSelectionIndex();
		String key = TuxGuitar.getInstance().getScaleManager().getKeyName( scaleKey );
		String name = TuxGuitar.getInstance().getScaleManager().getScaleName( scaleIndex );
		this.scaleName.setText( ( key != null && name != null ) ? ( key + " - " + name ) : "" );
		this.scaleName.pack();
	}
	
	private void calculateFretSpacing(int width) {
		this.fretSpacing = (width / MAX_FRETS);
		int aux = 0;
		for (int i = 0; i < MAX_FRETS; i++) {
			aux += (i * 2);
		}
		this.fretSpacing += (aux / MAX_FRETS) + 2;
	}
	
	private void disposeFretBoardImage(){
		if( this.fretBoard != null && !this.fretBoard.isDisposed() ){
			this.fretBoard.dispose();
		}
	}
	
	protected void initFrets(int fromX) {
		this.frets = new int[MAX_FRETS];
		int nextX = fromX;
		int direction = this.getDirection(this.config.getDirection());
		if (direction == TGFretBoardConfig.DIRECTION_RIGHT) {
			for (int i = 0; i < this.frets.length; i++) {
				this.frets[i] = nextX;
				nextX += (this.fretSpacing - ((i + 1) * 2));
			}
		} else if (direction == TGFretBoardConfig.DIRECTION_LEFT) {
			for (int i = this.frets.length - 1; i >= 0; i--) {
				this.frets[i] = nextX;
				nextX += (this.fretSpacing - (i * 2));
			}
		}
	}
	
	private int getDirection( int value ){
		int direction = value;
		if( direction != TGFretBoardConfig.DIRECTION_RIGHT && direction != TGFretBoardConfig.DIRECTION_LEFT ){
			direction = TGFretBoardConfig.DIRECTION_RIGHT;
		}
		return direction;
	}
	
	private void initStrings(int count) {
		int fromY = TOP_SPACING;
		this.strings = new int[count];
		
		for (int i = 0; i < this.strings.length; i++) {
			this.strings[i] = fromY + (STRING_SPACING * i);
		}
	}
	
	private void updateEditor(){
		if( isVisible() ){
			if(TuxGuitar.getInstance().getPlayer().isRunning()){
				this.beat = TuxGuitar.getInstance().getEditorCache().getPlayBeat();
			}else if(this.externalBeat != null){
				this.beat = this.externalBeat;
			}else{
				this.beat = TuxGuitar.getInstance().getEditorCache().getEditBeat();
			}
			
			if (this.strings.length != getStringCount()) {
				disposeFretBoardImage();
				initStrings(getStringCount());
				//Fuerzo a cambiar el ancho
				this.lastSize.y = 0;
			}
			
			int clientWidth = getClientArea().width;
			int clientHeight = getClientArea().height;
			
			if( this.lastSize.x != clientWidth || hasChanges() ){
				this.layout(getClientArea().width);
			}
			
			if(this.lastSize.y != clientHeight){
				TuxGuitar.getInstance().getFretBoardEditor().showFretBoard();
			}
			this.lastSize.x = clientWidth;
			this.lastSize.y = clientHeight;
		}
	}
	
	private void paintFretBoard(TGPainter painter){
		if(this.fretBoard == null || this.fretBoard.isDisposed()){
			Rectangle clientArea = getClientArea();
			
			this.fretBoard = new TGImageImpl(getDisplay(),clientArea.width,((STRING_SPACING) * (this.strings.length - 1)) + TOP_SPACING + BOTTOM_SPACING);
			
			TGPainter painterBuffer = this.fretBoard.createPainter();
			
			//fondo
			painterBuffer.setBackground(new TGColorImpl(this.config.getColorBackground()));
			painterBuffer.initPath(TGPainter.PATH_FILL);
			painterBuffer.addRectangle(clientArea.x,clientArea.y,clientArea.width,clientArea.height);
			painterBuffer.closePath();
			
			
			// pinto las cegillas
			TGImage fretImage = new TGImageImpl(TuxGuitar.getInstance().getIconManager().getFretboardFret());
			TGImage firstFretImage = new TGImageImpl(TuxGuitar.getInstance().getIconManager().getFretboardFirstFret());
			
			painterBuffer.drawImage(firstFretImage,0,0,firstFretImage.getWidth(),firstFretImage.getHeight(),this.frets[0] - 5,this.strings[0] - 5,firstFretImage.getWidth(),this.strings[this.strings.length - 1] );
			
			paintFretPoints(painterBuffer,0);
			for (int i = 1; i < this.frets.length; i++) {
				painterBuffer.drawImage(fretImage,0,0,fretImage.getWidth(),fretImage.getHeight(),this.frets[i],this.strings[0] - 5,fretImage.getWidth(),this.strings[this.strings.length - 1] );
				paintFretPoints(painterBuffer, i);
			}
			
			// pinto las cuerdas
			for (int i = 0; i < this.strings.length; i++) {
				painterBuffer.setForeground(new TGColorImpl(this.config.getColorString()));
				if(i > 2){
					painterBuffer.setLineWidth(2);
				}
				painterBuffer.initPath();
				painterBuffer.setAntialias(false);
				painterBuffer.moveTo(this.frets[0], this.strings[i]);
				painterBuffer.lineTo(this.frets[this.frets.length - 1], this.strings[i]);
				painterBuffer.closePath();
			}
			
			// pinto la escala
			paintScale(painterBuffer);
			
			painterBuffer.dispose();
		}
		painter.drawImage(this.fretBoard,0,0);
	}
	
	private void paintFretPoints(TGPainter painter, int fretIndex) {
		painter.setBackground(new TGColorImpl(this.config.getColorFretPoint()));
		if ((fretIndex + 1) < this.frets.length) {
			int fret = ((fretIndex + 1) % 12);
			painter.setLineWidth(10);
			if (fret == 0) {
				int size = getOvalSize();
				int x = this.frets[fretIndex] + ((this.frets[fretIndex + 1] - this.frets[fretIndex]) / 2);
				int y1 = this.strings[0] + ((this.strings[this.strings.length - 1] - this.strings[0]) / 2) - STRING_SPACING;
				int y2 = this.strings[0] + ((this.strings[this.strings.length - 1] - this.strings[0]) / 2) + STRING_SPACING;
				painter.initPath(TGPainter.PATH_FILL);
				painter.addOval(x - (size / 2), y1 - (size / 2), size, size);
				painter.addOval(x - (size / 2), y2 - (size / 2), size, size);
				painter.closePath();
			} else if (fret == 3 || fret == 5 || fret == 7 || fret == 9) {
				int size = getOvalSize();
				int x = this.frets[fretIndex] + ((this.frets[fretIndex + 1] - this.frets[fretIndex]) / 2);
				int y = this.strings[0] + ((this.strings[this.strings.length - 1] - this.strings[0]) / 2);
				painter.initPath(TGPainter.PATH_FILL);
				painter.addOval(x - (size / 2),y - (size / 2),size, size);
				painter.closePath();
			}
			painter.setLineWidth(1);
		}
	}
	
	private void paintScale(TGPainter painter) {
		TGTrack track = getTrack();
		
		for (int i = 0; i < this.strings.length; i++) {
			TGString string = track.getString(i + 1);
			for (int j = 0; j < this.frets.length; j++) {
				
				int noteIndex = ((string.getValue() + j) %  12 );
				if(TuxGuitar.getInstance().getScaleManager().getScale().getNote(noteIndex)){
					int x = this.frets[j];
					if(j > 0){
						x -= ((x - this.frets[j - 1]) / 2);
					}
					int y = this.strings[i];
					
					if( (this.config.getStyle() & TGFretBoardConfig.DISPLAY_TEXT_SCALE) != 0 ){
						paintKeyText(painter,this.config.getColorScale(),x,y,NOTE_NAMES[noteIndex]);
					}
					else{
						paintKeyOval(painter,this.config.getColorScale(),x,y);
					}
				}
			}
		}
		
		painter.setForeground(new TGColorImpl(this.config.getColorBackground()));
	}
	
	private void paintNotes(TGPainter painter) {
		if(this.beat != null){
			TGTrack track = getTrack();
			
			for(int v = 0; v < this.beat.countVoices(); v ++){
				TGVoice voice = this.beat.getVoice( v );
				Iterator<TGNote> it = voice.getNotes().iterator();
				while (it.hasNext()) {
					TGNote note = (TGNote) it.next();
					int fretIndex = note.getValue();
					int stringIndex = note.getString() - 1;
					if (fretIndex >= 0 && fretIndex < this.frets.length && stringIndex >= 0 && stringIndex < this.strings.length) {
						int x = this.frets[fretIndex];
						if (fretIndex > 0) {
							x -= ((this.frets[fretIndex] - this.frets[fretIndex - 1]) / 2);
						}
						int y = this.strings[stringIndex];
						
						if( (this.config.getStyle() & TGFretBoardConfig.DISPLAY_TEXT_NOTE) != 0 ){
							int realValue = track.getString(note.getString()).getValue() + note.getValue();
							paintKeyText(painter,this.config.getColorNote(), x, y, NOTE_NAMES[ (realValue % 12) ]);
						}
						else{
							paintKeyOval(painter,this.config.getColorNote(), x, y);
						}
					}
				}
			}
			painter.setLineWidth(1);
		}
	}
	
	private void paintKeyOval(TGPainter painter,Color background,int x, int y) {
		int size = getOvalSize();
		painter.setBackground(new TGColorImpl(background));
		painter.initPath(TGPainter.PATH_FILL);
		painter.moveTo(x - (size / 2),y - (size / 2));
		painter.addOval(x - (size / 2),y - (size / 2),size, size);
		painter.closePath();
	}
	
	private void paintKeyText(TGPainter painter,Color foreground,int x, int y,String text) {
		painter.setBackground(new TGColorImpl(getDisplay().getSystemColor(SWT.COLOR_WHITE)));
		painter.setForeground(new TGColorImpl(foreground));
		painter.setFont(new TGFontImpl(this.config.getFont()));
		
		float fmWidth = painter.getFMWidth(text);
		float fmHeight = painter.getFMHeight();
		
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(x - (fmWidth / 2f), y - (fmHeight / 2f), fmWidth, fmHeight);
		painter.closePath();
		painter.drawString(text, x - (fmWidth / 2f),y + painter.getFMMiddleLine(), true);
	}
	
	protected void paintEditor(TGPainter painter) {
		this.updateEditor();
		if (this.frets.length > 0 && this.strings.length > 0) {
			paintFretBoard(painter);
			paintNotes(painter);
		}
	}
	
	protected void hit(int x, int y) {
		int fretIndex = getFretIndex(x);
		int stringIndex = getStringIndex(y);
		int stringNumber = (stringIndex + 1);
		
		this.selectString(stringNumber);
		if(!this.removeNote(fretIndex, stringNumber)) {
			this.addNote(fretIndex, stringNumber);
		}
	}
	
	private void selectString(int number) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGMoveToAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, getTrack().getString(number));
		tgActionProcessor.process();
	}
	
	private int getStringIndex(int y) {
		int index = -1;
		for (int i = 0; i < this.strings.length; i++) {
			if (index < 0) {
				index = i;
			} else {
				int distanceY = Math.abs(y - this.strings[index]);
				int currDistanceY = Math.abs(y - this.strings[i]);
				if (currDistanceY < distanceY) {
					index = i;
				}
			}
		}
		return index;
	}
	
	private int getFretIndex(int x) {
		int length = this.frets.length;
		if ((x - 10) <= this.frets[0] && this.frets[0] < this.frets[length - 1]) {
			return 0;
		}
		if ((x + 10) >= this.frets[0] && this.frets[0] > this.frets[length - 1]) {
			return 0;
		}
		
		for (int i = 0; i < length; i++) {
			if ((i + 1) < length) {
				if (x > this.frets[i] && x <= this.frets[i + 1] || x > this.frets[i + 1] && x <= this.frets[i]) {
					return i + 1;
				}
			}
		}
		return length - 1;
	}
	
	private boolean removeNote(int fret, int string) {
		if(this.beat != null){
			for(int v = 0; v < this.beat.countVoices(); v ++){
				TGVoice voice = this.beat.getVoice( v );
				Iterator<TGNote> it = voice.getNotes().iterator();
				while (it.hasNext()) {
					TGNote note = (TGNote) it.next();
					if( note.getValue() == fret && note.getString() == string ) {
						TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGDeleteNoteAction.NAME);
						tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);
						tgActionProcessor.process();
						
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private TGTrack getTrack() {
		if( this.beat != null ){
			TGMeasure measure = this.beat.getMeasure();
			if( measure != null ){
				TGTrack track = measure.getTrack();
				if( track != null ){
					return track;
				}
			}
		}
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
	}
	
	private int getStringCount() {
		TGTrack track = getTrack();
		if( track != null ){
			return track.stringCount();
		}
		return 0;
	}
	
	private int getOvalSize(){
		return ((STRING_SPACING / 2) + (STRING_SPACING / 10));
	}
	
	private void addNote(int fret, int string) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGChangeNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, fret);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, getTrack().getString(string));
		tgActionProcessor.process();
	}
	
	protected void updateDirection( int direction ){
		this.config.saveDirection( this.getDirection(direction) );
		this.initFrets(10);
		this.setChanges(true);
		this.fretBoardComposite.redraw();
	}
	
	public boolean hasChanges(){
		return this.changes;
	}
	
	public void setChanges(boolean changes){
		this.changes = changes;
	}
	
	public void setExternalBeat(TGBeat externalBeat){
		this.externalBeat = externalBeat;
	}
	
	public TGBeat getExternalBeat(){
		return this.externalBeat;
	}
	
	public void redraw() {
		if(!super.isDisposed()){
			super.redraw();
			this.fretBoardComposite.redraw();
			this.loadDurationImage(false);
		}
	}
	
	public void redrawPlayingMode(){
		if(!super.isDisposed() /*&& !TuxGuitar.getInstance().isLocked()*/){
			this.fretBoardComposite.redraw();
		}
	 }
	
	public void dispose(){
		super.dispose();
		this.disposeFretBoardImage();
		this.config.dispose();
	}
	
	public void loadProperties(){
		int selection = this.handSelector.getSelectionIndex();
		this.handSelector.removeAll();
		this.handSelector.add(TuxGuitar.getProperty("fretboard.right-mode"));
		this.handSelector.add(TuxGuitar.getProperty("fretboard.left-mode"));
		this.handSelector.select(selection);
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.loadScaleName();
		this.setChanges(true);
		this.layout(true,true);
	}
	
	public void loadIcons(){
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		this.loadDurationImage(true);
		this.layout(true,true);
		this.layout(getClientArea().width);
	}
	
	public void loadScale(){
		this.loadScaleName();
		this.setChanges(true);
	}
	
	public int getHeight(){
		int borderWidth = (2 * this.fretBoardComposite.getBorderWidth());
		int toolBarHeight = (this.toolComposite.getBounds().height);
		int fretBoardHeight = (((STRING_SPACING) * (this.strings.length - 1)) + TOP_SPACING + BOTTOM_SPACING);
		return (borderWidth + toolBarHeight + fretBoardHeight);
	}
	
	public int getWidth(){
		return this.frets[this.frets.length - 1];
	}
	
	public void layout(){
		super.layout();
	}
	
	public void layout(int width){
		this.disposeFretBoardImage();
		this.calculateFretSpacing(width);
		this.initFrets(10);
		this.initStrings(getStringCount());
		this.setChanges(false);
	}
	
	protected void configure(){
		this.config.configure(getShell());
		this.handSelector.select( this.getDirection(this.config.getDirection()) );
		this.setChanges(true);
		this.redraw();
	}
	
	public Composite getFretBoardComposite(){
		return this.fretBoardComposite;
	}
	
	private class TGFretBoardMouseListener implements MouseListener {
		
		public TGFretBoardMouseListener(){
			super();
		}
		
		public void mouseUp(MouseEvent e) {
			getFretBoardComposite().setFocus();
			if( e.button == 1 ){
				if(!TuxGuitar.getInstance().getPlayer().isRunning() && !TGEditorManager.getInstance(TGFretBoard.this.context).isLocked()){
					if( getExternalBeat() == null ){
						hit(e.x, e.y);
					}else{
						setExternalBeat( null );
						TuxGuitar.getInstance().updateCache(true);
					}
				}
			}else{
				new TGActionProcessor(TGFretBoard.this.context, TGGoRightAction.NAME).process();
			}
		}
		
		public void mouseDown(MouseEvent e) {
			//Not implemented
		}
		
		public void mouseDoubleClick(MouseEvent e) {
			//Not implemented
		}
	}
	
	private class TGFretBoardPainterListener implements TGBufferedPainterHandle {
		
		public TGFretBoardPainterListener(){
			super();
		}

		public void paintControl(TGPainter painter) {
			TGFretBoard.this.paintEditor(painter);
		}

		public Composite getPaintableControl() {
			return TGFretBoard.this.fretBoardComposite;
		}
	}
}
