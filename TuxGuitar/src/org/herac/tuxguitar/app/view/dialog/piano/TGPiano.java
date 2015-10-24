package org.herac.tuxguitar.app.view.dialog.piano;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.app.view.component.tab.Caret;
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
import org.herac.tuxguitar.graphics.control.TGNoteImpl;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;

public class TGPiano extends Composite {
	
	private static final boolean TYPE_NOTES[] = new boolean[]{true,false,true,false,true,true,false,true,false,true,false,true};
	private static final int NATURAL_NOTES = 7;
	private static final int MAX_OCTAVES = 8;
	private static final int NATURAL_WIDTH = 15;
	private static final int SHARP_WIDTH = 8;
	private static final int NATURAL_HEIGHT = 60;
	private static final int SHARP_HEIGHT = 40;
	
	private TGContext context;
	private int duration;
	private boolean changes;
	private TGPianoConfig config;
	private Composite pianoComposite;
	private Composite toolComposite;
	private Label durationLabel;
	private Label scaleName;
	private Button scale;
	private Button settings;
	private TGBeat beat;
	private TGBeat externalBeat;
	private TGImage image;
	
	public TGPiano(TGContext context, Composite parent, int style) {
		super(parent, style);
		this.context = context;
		this.config = new TGPianoConfig();
		this.config.load();
		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.initToolBar();
		this.makePiano();
		this.loadIcons();
		this.loadProperties();
		
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.toolComposite);
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.pianoComposite);
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
		
		this.toolComposite.setLayout(layout);
		this.toolComposite.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
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
	
	private void loadScaleName() {
		int scaleKey = TuxGuitar.getInstance().getScaleManager().getSelectionKey();
		int scaleIndex = TuxGuitar.getInstance().getScaleManager().getSelectionIndex();
		String key = TuxGuitar.getInstance().getScaleManager().getKeyName( scaleKey );
		String name = TuxGuitar.getInstance().getScaleManager().getScaleName( scaleIndex );
		this.scaleName.setText( ( key != null && name != null ) ? ( key + " - " + name ) : "" );
		this.scaleName.pack();
	}
	
	private void makePiano(){
		this.image = makePianoImage();
		this.pianoComposite = new Composite(this,SWT.BORDER);
		this.pianoComposite.setLayout(new GridLayout());
		this.pianoComposite.setLayoutData(new GridData((NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES) ),NATURAL_HEIGHT));
		this.pianoComposite.addPaintListener(new TGBufferedPainterListenerLocked(this.context, new TGPianoPainterListener()));
		this.pianoComposite.addMouseListener(new TGPianoMouseListener());
		this.pianoComposite.setFocus();
	}
	
	/**
	 * Crea la imagen del piano
	 *
	 * @return
	 */
	private TGImage makePianoImage(){
		TGImage image = new TGImageImpl(getDisplay(),(NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES) ), NATURAL_HEIGHT);
		TGPainter painter = image.createPainter();
		
		int x = 0;
		int y = 0;
		painter.setBackground(new TGColorImpl(this.config.getColorNatural()));
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(x,y,(NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES) ),NATURAL_HEIGHT);
		painter.closePath();
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			
			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				painter.setForeground(new TGColorImpl(this.config.getColorNotNatural()));
				painter.initPath();
				painter.setAntialias(false);
				painter.addRectangle(x,y,NATURAL_WIDTH,NATURAL_HEIGHT);
				painter.closePath();
				x += NATURAL_WIDTH;
			}else{
				painter.setBackground(new TGColorImpl(this.config.getColorNotNatural()));
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
		painter.setBackground(new TGColorImpl(this.config.getColorScale()));
		painter.setForeground(new TGColorImpl(this.config.getColorScale()));
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
			
			if(TuxGuitar.getInstance().getScaleManager().getScale().getNote(i)){
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
		painter.setBackground(new TGColorImpl(this.config.getColorNote()));
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
	
	protected void paintEditor(TGPainter painter) {
		this.updateEditor();
		
		painter.drawImage(this.image, 0, 0);
		
		// pinto notas
		if( this.beat != null ){
			for(int v = 0; v < this.beat.countVoices(); v ++){
				TGVoice voice = this.beat.getVoice( v );
				Iterator<TGNote> it = voice.getNotes().iterator();
				while(it.hasNext()){
					this.paintNote(painter, getRealNoteValue( it.next() ));
				}
			}
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
		
		if(!this.removeNote(value)) {
			this.addNote(value);
		}
	}
	
	private boolean removeNote(int value) {
		if(this.beat != null){
			for(int v = 0; v < this.beat.countVoices(); v ++){
				TGVoice voice = this.beat.getVoice( v );
				Iterator<TGNote> it = voice.getNotes().iterator();
				while (it.hasNext()) {
					TGNote note = (TGNote) it.next();
					if( getRealNoteValue(note) == value ) {
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
	
	private boolean addNote(int value) {
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		
		List<TGString> strings = caret.getTrack().getStrings();
		for(int i = 0;i < strings.size();i ++){
			TGString string = (TGString)strings.get(i);
			if(value >= string.getValue()){
				boolean emptyString = true;
				
				if(this.beat != null){
					for(int v = 0; v < this.beat.countVoices(); v ++){
						TGVoice voice = this.beat.getVoice( v );
						Iterator<TGNote> it = voice.getNotes().iterator();
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
					TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGChangeNoteAction.NAME);
					tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, (value - string.getValue()));
					tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
					tgActionProcessor.process();
					
					return true;
				}
			}
		}
		return false;
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
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
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
		if( isVisible() ){
			if( hasChanges() ){
				this.image.dispose();
				this.image = makePianoImage();
			}
			if(TuxGuitar.getInstance().getPlayer().isRunning()){
				this.beat = TuxGuitar.getInstance().getEditorCache().getPlayBeat();
			}else if(this.externalBeat != null){
				this.beat = this.externalBeat;
			}else{
				this.beat = TuxGuitar.getInstance().getEditorCache().getEditBeat();
			}
		}
	}
	
	public void redraw() {
		if(!super.isDisposed()){
			super.redraw();
			this.pianoComposite.redraw();
			this.loadDurationImage(false);
		}
	}
	
	public void redrawPlayingMode(){
		if(!super.isDisposed() /*&& !TuxGuitar.getInstance().isLocked()*/){
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
		this.getShell().setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
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
	
	private class TGPianoMouseListener implements MouseListener {
		
		public TGPianoMouseListener(){
			super();
		}
		
		public void mouseUp(MouseEvent e) {
			getPianoComposite().setFocus();
			if(e.button == 1){
				if(!TuxGuitar.getInstance().getPlayer().isRunning() && !TGEditorManager.getInstance(TGPiano.this.context).isLocked()){
					if( getExternalBeat() == null ){
						hit(e.x, e.y);
					}else{
						setExternalBeat( null );
						TuxGuitar.getInstance().updateCache(true);
					}
				}
			}else{
				new TGActionProcessor(TGPiano.this.context, TGGoRightAction.NAME).process();
			}
		}
		
		public void mouseDoubleClick(MouseEvent e) {
			//Not implemented
		}
		
		public void mouseDown(MouseEvent e) {
			//Not implemented
		}
	}
	
	private class TGPianoPainterListener implements TGBufferedPainterHandle {
		
		public TGPianoPainterListener(){
			super();
		}

		public void paintControl(TGPainter painter) {
			TGPiano.this.paintEditor(painter);
		}

		public Composite getPaintableControl() {
			return TGPiano.this.pianoComposite;
		}
	}
}
