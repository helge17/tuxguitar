package org.herac.tuxguitar.gui.editors.piano;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import org.herac.tuxguitar.gui.editors.tab.TGNoteImpl;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;

public class Piano extends Composite{
	
	private static final boolean TYPE_NOTES[] = new boolean[]{true,false,true,false,true,true,false,true,false,true,false,true};
	private static final int NATURAL_NOTES = 7;
	private static final int MAX_OCTAVES = 8;
	private static final int NATURAL_WIDTH = 15;
	private static final int SHARP_WIDTH = 8;
	private static final int NATURAL_HEIGHT = 60;
	private static final int SHARP_HEIGHT = 40;
	
	private int duration;
	private boolean changes;
	private PianoListener listener;
	private PianoConfig config;
	private Composite pianoComposite;
	private Composite toolComposite;
	private Label durationLabel;
	private Label scaleName;
	private Button scale;
	private Button settings;
	protected TGBeat beat;
	protected TGBeat externalBeat;
	protected Image image;
	
	public Piano(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.listener =  new PianoListener();
		this.config = new PianoConfig();
		this.config.load();
		this.initToolBar();
		this.makePiano();
		this.loadIcons();
		this.loadProperties();
		
		TuxGuitar.instance().getkeyBindingManager().appendListenersTo(this.toolComposite);
		TuxGuitar.instance().getkeyBindingManager().appendListenersTo(this.pianoComposite);
	}
	
	private void initToolBar() {
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		
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
		
		this.toolComposite.setLayout(layout);
		this.toolComposite.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
	}
	
	private void makeToolSeparator(Composite parent){
		Label separator = new Label(parent,SWT.SEPARATOR);
		separator.setLayoutData(new GridData(20,20));
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
	
	private void makePiano(){
		this.image = makePianoImage();
		this.pianoComposite = new Composite(this,SWT.BORDER | SWT.DOUBLE_BUFFERED);
		this.pianoComposite.setLayout(new GridLayout());
		this.pianoComposite.setLayoutData(new GridData((NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES) ),NATURAL_HEIGHT));
		this.pianoComposite.addPaintListener(this.listener);
		this.pianoComposite.addMouseListener(this.listener);
		this.pianoComposite.setFocus();
	}
	
	/**
	 * Crea la imagen del piano
	 *
	 * @return
	 */
	private Image makePianoImage(){
		Image image = new Image(getDisplay(),(NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES) ),NATURAL_HEIGHT);
		TGPainter painter = new TGPainter(new GC(image));
		
		int x = 0;
		int y = 0;
		painter.setBackground(this.config.getColorNatural());
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(x,y,(NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES) ),NATURAL_HEIGHT);
		painter.closePath();
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			
			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				painter.setForeground(this.config.getColorNotNatural());
				painter.initPath();
				painter.setAntialias(false);
				painter.addRectangle(x,y,NATURAL_WIDTH,NATURAL_HEIGHT);
				painter.closePath();
				x += NATURAL_WIDTH;
			}else{
				painter.setBackground(this.config.getColorNotNatural());
				painter.initPath(TGPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle(x - (SHARP_WIDTH / 2),y,SHARP_WIDTH,SHARP_HEIGHT);
				painter.closePath();
			}
		}
		paintScale(painter);
		
		painter.dispose();
		return image;
	}
	
	/**
	 * Pinta la nota a partir del indice
	 * 	 
	 * @param gc
	 * @param value
	 */
	private void paintScale(TGPainter painter){
		painter.setBackground(this.config.getColorScale());
		painter.setForeground(this.config.getColorScale());
		int posX = 0;
		
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			int width = 0;
			
			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				width = NATURAL_WIDTH;
				if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
				if(!TYPE_NOTES[(i + 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
			}else{
				width = SHARP_WIDTH;
			}
			
			if(TuxGuitar.instance().getScaleManager().getScale().getNote(i)){
				if(TYPE_NOTES[i % TYPE_NOTES.length] ){
					int x = posX;
					if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
						x -= ((SHARP_WIDTH / 2));
					}
					
					int size = SHARP_WIDTH;
					painter.initPath(TGPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle( (x + 1 + (((NATURAL_WIDTH - size) / 2))) ,(NATURAL_HEIGHT - size - (((NATURAL_WIDTH - size) / 2))),size,size);
					painter.closePath();
				}else{
					painter.initPath(TGPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle(posX + 1, SHARP_HEIGHT - SHARP_WIDTH + 1,SHARP_WIDTH - 2,SHARP_WIDTH - 2);
					painter.closePath();
				}
			}
			
			posX += width;
		}
	}
	
	/**
	 * Pinta la nota a partir del indice
	 * 	 
	 * @param gc
	 * @param value
	 */
	protected void paintNote(TGPainter painter,int value){
		painter.setBackground(this.config.getColorNote());
		int posX = 0;
		int y = 0;
		
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			int width = 0;
			
			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				width = NATURAL_WIDTH;
				if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
				if(!TYPE_NOTES[(i + 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
			}else{
				width = SHARP_WIDTH;
			}
			
			if(i == value){
				if(TYPE_NOTES[i % TYPE_NOTES.length]){
					painter.initPath(TGPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle(posX + 1,y + 1,width - 1,SHARP_HEIGHT);
					
					int x = posX;
					if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
						x -= ((SHARP_WIDTH / 2));
					}
					painter.addRectangle(x + 1,(y + SHARP_HEIGHT) + 1,NATURAL_WIDTH - 1,(NATURAL_HEIGHT - SHARP_HEIGHT) - 1);
					painter.closePath();
				}else{
					painter.initPath(TGPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle(posX + 1,y + 1,width - 1,SHARP_HEIGHT - 1);
					painter.closePath();
				}
				
			}
			
			posX += width;
		}
	}
	
	/**
	 * Retorna el indice de la nota seleccionada
	 * 
	 * @param point
	 * @return
	 */
	private int getSelection(Point point){
		int posX = 0;
		
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			int width = 0;
			
			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				width = NATURAL_WIDTH;
				if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
				if(!TYPE_NOTES[(i + 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
			}else{
				width = SHARP_WIDTH;
			}
			
			if(point.x >= posX && point.x < (posX + width)  ){
				return i;
			}
			
			posX += width;
		}
		return -1;
	}
	
	protected void hit(int x, int y) {
		int value = getSelection(new Point(x,y));
		
		if (!removeNote(value)) {
			addNote(value);
		}
	}
	
	private boolean removeNote(int value) {
		if(this.beat != null){
			for(int v = 0; v < this.beat.countVoices(); v ++){
				TGVoice voice = this.beat.getVoice( v );
				Iterator it = voice.getNotes().iterator();
				while (it.hasNext()) {
					TGNote note = (TGNote) it.next();
					if (getRealNoteValue(note) == value) {
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
	
	private boolean addNote(int value) {
		Caret caret = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
		
		List strings = caret.getTrack().getStrings();
		for(int i = 0;i < strings.size();i ++){
			TGString string = (TGString)strings.get(i);
			if(value >= string.getValue()){
				boolean emptyString = true;
				
				if(this.beat != null){
					for(int v = 0; v < this.beat.countVoices(); v ++){
						TGVoice voice = this.beat.getVoice( v );
						Iterator it = voice.getNotes().iterator();
						while (it.hasNext()) {
							TGNoteImpl note = (TGNoteImpl) it.next();
							if (note.getString() == string.getNumber()) {
								emptyString = false;
								break;
							}
						}
					}
				}
				if(emptyString){
					TGSongManager manager = TuxGuitar.instance().getSongManager();
					
					//comienza el undoable
					UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
					
					TGNote note = manager.getFactory().newNote();
					note.setValue((value - string.getValue()));
					note.setVelocity(caret.getVelocity());
					note.setString(string.getNumber());
					
					TGDuration duration = manager.getFactory().newDuration();
					caret.getDuration().copy(duration);
					
					manager.getMeasureManager().addNote(caret.getMeasure(),caret.getPosition(),note,duration,caret.getVoice());
					
					//termia el undoable
					TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo());
					TuxGuitar.instance().getFileHistory().setUnsavedFile();
					
					//reprodusco las notas en el pulso
					caret.getSelectedBeat().play();
					
					return true;
				}
			}
		}
		return false;
	}
	
	protected void afterAction() {
		int measure = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getMeasure().getNumber();
		TuxGuitar.instance().getTablatureEditor().getTablature().getViewLayout().fireUpdate(measure);
		TuxGuitar.instance().updateCache(true);
	}
	
	protected int getRealNoteValue(TGNote note){
		TGVoice voice = note.getVoice();
		if( voice != null ){
			TGBeat beat = voice.getBeat();
			if( beat != null ){
				TGMeasure measure = beat.getMeasure();
				if( measure != null ){
					TGTrack track = measure.getTrack();
					if( track != null ){
						return ( note.getValue() + track.getString( note.getString() ).getValue() );
					}
				}
			}
		}
		// If note have no parents, uses current track strings.
		Caret caret = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
		TGTrack track = caret.getTrack();
		if( track != null ){
			return ( note.getValue() + track.getString( note.getString() ).getValue() );
		}
		return 0;
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
	
	protected void updateEditor(){
		if(isVisible()){
			if(hasChanges()){
				this.image.dispose();
				this.image = makePianoImage();
			}
			if(TuxGuitar.instance().getPlayer().isRunning()){
				this.beat = TuxGuitar.instance().getEditorCache().getPlayBeat();
			}else if(this.externalBeat != null){
				this.beat = this.externalBeat;
			}else{
				this.beat = TuxGuitar.instance().getEditorCache().getEditBeat();
			}
		}
	}
	
	public void redraw() {
		if(!super.isDisposed() && !TuxGuitar.instance().isLocked()){
			super.redraw();
			this.pianoComposite.redraw();
			this.loadDurationImage(false);
		}
	}
	
	public void redrawPlayingMode(){
		if(!super.isDisposed() && !TuxGuitar.instance().isLocked()){
			this.pianoComposite.redraw();
		}
	}
	
	public void dispose(){
		super.dispose();
		this.image.dispose();
		this.config.dispose();
	}
	
	public void loadProperties(){
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.loadScaleName();
		this.layout(true,true);
	}
	
	public void loadIcons(){
		this.getShell().setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		this.settings.setImage(TuxGuitar.instance().getIconManager().getSettings());
		this.loadDurationImage(true);
		this.layout(true,true);
	}
	
	public void loadScale(){
		this.loadScaleName();
		this.setChanges(true);
	}
	
	protected void configure(){
		this.config.configure(getShell());
		this.setChanges(true);
		this.redraw();
	}
	
	public Composite getPianoComposite() {
		return this.pianoComposite;
	}
	
	private class PianoListener implements PaintListener,MouseListener {
		
		public PianoListener(){
			super();
		}
		
		public void paintControl(PaintEvent e) {
			if(!TuxGuitar.instance().isLocked()){
				TuxGuitar.instance().lock();
				updateEditor();
				
				TGPainter painter = new TGPainter(e.gc);
				painter.drawImage(Piano.this.image,0,0);
				
				//pinto notas
				if(Piano.this.beat != null){
					for(int v = 0; v < Piano.this.beat.countVoices(); v ++){
						TGVoice voice = Piano.this.beat.getVoice( v );
						Iterator it = voice.getNotes().iterator();
						while(it.hasNext()){
							TGNote note = (TGNote)it.next();
							paintNote(painter, getRealNoteValue( note ) );
						}
					}
				}
				TuxGuitar.instance().unlock();
			}
		}
		
		public void mouseUp(MouseEvent e) {
			getPianoComposite().setFocus();
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
		
		public void mouseDoubleClick(MouseEvent e) {
			//Not implemented
		}
		
		public void mouseDown(MouseEvent e) {
			//Not implemented
		}
	}
}
