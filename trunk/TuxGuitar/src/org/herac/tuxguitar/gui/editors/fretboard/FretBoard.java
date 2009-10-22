/*
 * Created on 28-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.fretboard;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.actions.caret.GoLeftAction;
import org.herac.tuxguitar.gui.actions.caret.GoRightAction;
import org.herac.tuxguitar.gui.actions.duration.DecrementDurationAction;
import org.herac.tuxguitar.gui.actions.duration.IncrementDurationAction;
import org.herac.tuxguitar.gui.actions.tools.ScaleAction;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.gui.util.TGMusicKeyUtils;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class FretBoard extends Composite {
	
	public static final int MAX_FRETS = 24;
	public static final int TOP_SPACING = 10;
	public static final int BOTTOM_SPACING = 10;
	
	private static final int STRING_SPACING = TuxGuitar.instance().getConfig().getIntConfigValue(TGConfigKeys.FRETBOARD_STRING_SPACING);
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_FRETBOARD);
	
	private FretBoardListener listener;
	private FretBoardConfig config;
	private Composite toolComposite;
	private Label durationLabel;
	private Label scaleName;
	private Button scale;
	private Button settings;
	private Image fretBoard;
	
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
	
	public FretBoard(Composite parent) {
		super(parent, SWT.NONE);
		this.setLayout(new FormLayout());
		this.listener = new FretBoardListener();
		this.config = new FretBoardConfig();
		this.config.load();
		this.initToolBar();
		this.initEditor();
		this.loadIcons();
		this.loadProperties();
		
		TuxGuitar.instance().getkeyBindingManager().appendListenersTo(this.toolComposite);
		TuxGuitar.instance().getkeyBindingManager().appendListenersTo(this.fretBoardComposite);
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
		goLeft.addSelectionListener(TuxGuitar.instance().getAction(GoLeftAction.NAME));
		
		layout.numColumns ++;
		Button goRight = new Button(this.toolComposite, SWT.ARROW | SWT.RIGHT);
		goRight.addSelectionListener(TuxGuitar.instance().getAction(GoRightAction.NAME));
		
		// separator
		layout.numColumns ++;
		makeToolSeparator(this.toolComposite);
		
		// duration
		layout.numColumns ++;
		Button decrement = new Button(this.toolComposite, SWT.ARROW | SWT.MIN);
		decrement.addSelectionListener(TuxGuitar.instance().getAction(DecrementDurationAction.NAME));
		
		layout.numColumns ++;
		this.durationLabel = new Label(this.toolComposite, SWT.BORDER);
		
		layout.numColumns ++;
		Button increment = new Button(this.toolComposite, SWT.ARROW | SWT.MAX);
		increment.addSelectionListener(TuxGuitar.instance().getAction(IncrementDurationAction.NAME));
		
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
				updateDirection(FretBoard.this.handSelector.getSelectionIndex());
			}
		});
		
		// separator
		layout.numColumns ++;
		makeToolSeparator(this.toolComposite);
		
		// scale
		layout.numColumns ++;
		this.scale = new Button(this.toolComposite, SWT.PUSH);
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.scale.addSelectionListener(TuxGuitar.instance().getAction(ScaleAction.NAME));
		
		// scale name
		layout.numColumns ++;
		this.scaleName = new Label(this.toolComposite, SWT.LEFT);
		
		// settings
		layout.numColumns ++;
		this.settings = new Button(this.toolComposite, SWT.PUSH);
		this.settings.setImage(TuxGuitar.instance().getIconManager().getSettings());
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
		
		this.fretBoardComposite = new Composite(this, SWT.BORDER | SWT.DOUBLE_BUFFERED);
		this.fretBoardComposite.setLayoutData(data);
		this.fretBoardComposite.setBackground(this.config.getColorBackground());
		this.fretBoardComposite.addMouseListener(this.listener);
		this.fretBoardComposite.addPaintListener(this.listener);
	}
	
	private void loadDurationImage(boolean force) {
		int duration = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getDuration().getValue();
		if(force || this.duration != duration){
			this.duration = duration;
			this.durationLabel.setImage(TuxGuitar.instance().getIconManager().getDuration(this.duration));
		}
	}
	
	private void loadScaleName() {
		int scaleKey = TuxGuitar.instance().getScaleManager().getSelectionKey();
		int scaleIndex = TuxGuitar.instance().getScaleManager().getSelectionIndex();
		String key = TuxGuitar.instance().getScaleManager().getKeyName( scaleKey );
		String name = TuxGuitar.instance().getScaleManager().getScaleName( scaleIndex );
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
		if(this.fretBoard != null && !this.fretBoard.isDisposed()){
			this.fretBoard.dispose();
		}
	}
	
	protected void initFrets(int fromX) {
		this.frets = new int[MAX_FRETS];
		int nextX = fromX;
		int direction = this.getDirection(this.config.getDirection());
		if (direction == FretBoardConfig.DIRECTION_RIGHT) {
			for (int i = 0; i < this.frets.length; i++) {
				this.frets[i] = nextX;
				nextX += (this.fretSpacing - ((i + 1) * 2));
			}
		} else if (direction == FretBoardConfig.DIRECTION_LEFT) {
			for (int i = this.frets.length - 1; i >= 0; i--) {
				this.frets[i] = nextX;
				nextX += (this.fretSpacing - (i * 2));
			}
		}
	}
	
	private int getDirection( int value ){
		int direction = value;
		if( direction != FretBoardConfig.DIRECTION_RIGHT && direction != FretBoardConfig.DIRECTION_LEFT ){
			direction = FretBoardConfig.DIRECTION_RIGHT;
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
		if(isVisible()){
			if(TuxGuitar.instance().getPlayer().isRunning()){
				this.beat = TuxGuitar.instance().getEditorCache().getPlayBeat();
			}else if(this.externalBeat != null){
				this.beat = this.externalBeat;
			}else{
				this.beat = TuxGuitar.instance().getEditorCache().getEditBeat();
			}
			
			if (this.strings.length != getStringCount()) {
				disposeFretBoardImage();
				initStrings(getStringCount());
				//Fuerzo a cambiar el ancho
				this.lastSize.y = 0;
			}
			
			int clientWidth = getClientArea().width;
			int clientHeight = getClientArea().height;
			
			if(this.lastSize.x != clientWidth || hasChanges()){
				this.layout(getClientArea().width);
			}
			
			if(this.lastSize.y != clientHeight){
				TuxGuitar.instance().getFretBoardEditor().showFretBoard();
			}
			this.lastSize.x = clientWidth;
			this.lastSize.y = clientHeight;
		}
	}
	
	private void paintFretBoard(TGPainter painter){
		if(this.fretBoard == null || this.fretBoard.isDisposed()){
			this.fretBoard = new Image(getDisplay(),getClientArea().width,((STRING_SPACING) * (this.strings.length - 1)) + TOP_SPACING + BOTTOM_SPACING);
			
			TGPainter painterBuffer = new TGPainter(new GC(this.fretBoard));
			
			//fondo
			painterBuffer.setBackground(this.config.getColorBackground());
			painterBuffer.initPath(TGPainter.PATH_FILL);
			painterBuffer.addRectangle(getClientArea());
			painterBuffer.closePath();
			
			
			// pinto las cegillas
			Image fretImage = TuxGuitar.instance().getIconManager().getFretboardFret();
			Image firstFretImage = TuxGuitar.instance().getIconManager().getFretboardFirstFret();
			
			painterBuffer.drawImage(firstFretImage,0,0,firstFretImage.getBounds().width,firstFretImage.getBounds().height,this.frets[0] - 5,this.strings[0] - 5,firstFretImage.getBounds().width,this.strings[this.strings.length - 1] );
			
			paintFretPoints(painterBuffer,0);
			for (int i = 1; i < this.frets.length; i++) {
				painterBuffer.drawImage(fretImage,0,0,fretImage.getBounds().width,fretImage.getBounds().height,this.frets[i],this.strings[0] - 5,fretImage.getBounds().width,this.strings[this.strings.length - 1] );
				paintFretPoints(painterBuffer, i);
			}
			
			// pinto las cuerdas
			for (int i = 0; i < this.strings.length; i++) {
				painterBuffer.setForeground(this.config.getColorString());
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
		painter.setBackground(this.config.getColorFretPoint());
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
				if(TuxGuitar.instance().getScaleManager().getScale().getNote(noteIndex)){
					int x = this.frets[j];
					if(j > 0){
						x -= ((x - this.frets[j - 1]) / 2);
					}
					int y = this.strings[i];
					
					if( (this.config.getStyle() & FretBoardConfig.DISPLAY_TEXT_SCALE) != 0 ){
						paintKeyText(painter,this.config.getColorScale(),x,y,NOTE_NAMES[noteIndex]);
					}
					else{
						paintKeyOval(painter,this.config.getColorScale(),x,y);
					}
				}
			}
		}
		
		painter.setForeground(this.config.getColorBackground());
	}
	
	private void paintNotes(TGPainter painter) {
		if(this.beat != null){
			TGTrack track = getTrack();
			
			for(int v = 0; v < this.beat.countVoices(); v ++){
				TGVoice voice = this.beat.getVoice( v );
				Iterator it = voice.getNotes().iterator();
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
						
						if( (this.config.getStyle() & FretBoardConfig.DISPLAY_TEXT_NOTE) != 0 ){
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
		painter.setBackground(background);
		painter.initPath(TGPainter.PATH_FILL);
		painter.moveTo(x - (size / 2),y - (size / 2));
		painter.addOval(x - (size / 2),y - (size / 2),size, size);
		painter.closePath();
	}
	
	private void paintKeyText(TGPainter painter,Color foreground,int x, int y,String text) {
		painter.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		painter.setForeground(foreground);
		painter.setFont(this.config.getFont());
		Point size = painter.getStringExtent(text);
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(x - (size.x / 2),y - (size.y / 2),size.x, size.y);
		painter.closePath();
		painter.drawString(text,x - (size.x / 2),y - (size.y / 2),true);
	}
	
	protected void paintEditor(TGPainter painter) {
		if(!TuxGuitar.instance().isLocked()){
			TuxGuitar.instance().lock();
			this.updateEditor();
			if (this.frets.length > 0 && this.strings.length > 0) {
				paintFretBoard(painter);
				paintNotes(painter);
			}
			TuxGuitar.instance().unlock();
		}
	}
	
	protected void hit(int x, int y) {
		int fretIndex = getFretIndex(x);
		int stringIndex = getStringIndex(y);
		
		TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().setStringNumber(stringIndex + 1);
		if (!removeNote(fretIndex, stringIndex + 1)) {
			addNote(fretIndex, stringIndex + 1);
		}
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
				Iterator it = voice.getNotes().iterator();
				while (it.hasNext()) {
					TGNote note = (TGNote) it.next();
					if (note.getValue() == fret && note.getString() == string) {
						//comienza el undoable
						UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
						
						TGSongManager manager = TuxGuitar.instance().getSongManager();
						manager.getMeasureManager().removeNote(note);
						
						//termia el undoable
						TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo());
						TuxGuitar.instance().getFileHistory().setUnsavedFile();
						
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
		return TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getTrack();
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
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		
		Caret caret = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
		
		TGNote note = manager.getFactory().newNote();
		note.setValue(fret);
		note.setVelocity(caret.getVelocity());
		note.setString(string);
		
		TGDuration duration = manager.getFactory().newDuration();
		caret.getDuration().copy(duration);
		
		manager.getMeasureManager().addNote(caret.getMeasure(),caret.getPosition(),note,duration, caret.getVoice());
		
		//termia el undoable
		TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo());
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		//reprodusco las notas en el pulso
		caret.getSelectedBeat().play();
	}
	
	protected void afterAction() {
		int measure = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getMeasure().getNumber();
		TuxGuitar.instance().getTablatureEditor().getTablature().getViewLayout().fireUpdate(measure);
		TuxGuitar.instance().updateCache(true);
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
		if(!super.isDisposed() && !TuxGuitar.instance().isLocked()){
			super.redraw();
			this.fretBoardComposite.redraw();
			this.loadDurationImage(false);
		}
	}
	
	public void redrawPlayingMode(){
		if(!super.isDisposed() && !TuxGuitar.instance().isLocked()){
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
		this.settings.setImage(TuxGuitar.instance().getIconManager().getSettings());
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
	
	private class FretBoardListener implements PaintListener,MouseListener {
		
		public FretBoardListener(){
			super();
		}
		
		public void paintControl(PaintEvent e) {
			TGPainter painter = new TGPainter(e.gc);
			paintEditor(painter);
		}
		
		public void mouseUp(MouseEvent e) {
			getFretBoardComposite().setFocus();
			if(e.button == 1){
				if(!TuxGuitar.instance().getPlayer().isRunning() && !TuxGuitar.instance().isLocked() && !ActionLock.isLocked()){
					ActionLock.lock();
					if( getExternalBeat() == null ){
						hit(e.x, e.y);
					}else{
						setExternalBeat( null );
					}
					afterAction();
					ActionLock.unlock();
				}
			}else{
				TuxGuitar.instance().getAction(GoRightAction.NAME).process(e);
			}
		}
		
		public void mouseDown(MouseEvent e) {
			//Not implemented
		}
		
		public void mouseDoubleClick(MouseEvent e) {
			//Not implemented
		}
	}
}
