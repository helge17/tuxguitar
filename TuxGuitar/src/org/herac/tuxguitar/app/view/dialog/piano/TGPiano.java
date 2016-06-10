package org.herac.tuxguitar.app.view.dialog.piano;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TG2BufferedPainterHandle;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.TGEditorManager;
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
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGPiano {
	
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
	private UIPanel control;
	private UIPanel toolComposite;
	private UICanvas canvas;
	private UIImageView durationLabel;
	private UILabel scaleName;
	private UIButton scale;
	private UIButton goLeft;
	private UIButton goRight;
	private UIButton increment;
	private UIButton decrement;
	private UIButton settings;
	private TGBeat beat;
	private TGBeat externalBeat;
	private TGImage image;
	
	public TGPiano(TGContext context, UIWindow parent) {
		this.context = context;
		this.config = new TGPianoConfig(context);
		this.config.load();
		this.control = getUIFactory().createPanel(parent, false);
		this.initToolBar();
		this.initCanvas();
		this.createControlLayout();
		this.loadIcons();
		this.loadProperties();
		
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.toolComposite);
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.canvas);
	}
	
	public void createControlLayout() {
		UITableLayout uiLayout = new UITableLayout(0f);
		uiLayout.set(this.toolComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		uiLayout.set(this.canvas, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		uiLayout.set(this.canvas, UITableLayout.PACKED_WIDTH, Float.valueOf(NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES)));
		uiLayout.set(this.canvas, UITableLayout.PACKED_HEIGHT, Float.valueOf(NATURAL_HEIGHT));
		
		this.control.setLayout(uiLayout);
	}
	
	private void initToolBar() {
		UIFactory uiFactory = getUIFactory();
		
		int column = 0;
		
		this.toolComposite = uiFactory.createPanel(this.control, false);
		this.createToolBarLayout();
		
		// position
		this.goLeft = uiFactory.createButton(this.toolComposite);
		this.goLeft.addSelectionListener(new TGActionProcessorListener(this.context, TGGoLeftAction.NAME));
		this.createToolItemLayout(this.goLeft, ++column);
		
		this.goRight = uiFactory.createButton(this.toolComposite);
		this.goRight.addSelectionListener(new TGActionProcessorListener(this.context, TGGoRightAction.NAME));
		this.createToolItemLayout(this.goRight, ++column);
		
		// separator
		this.createToolSeparator(uiFactory, ++column);
		
		// duration
		this.decrement = uiFactory.createButton(this.toolComposite);
		this.decrement.addSelectionListener(new TGActionProcessorListener(this.context, TGDecrementDurationAction.NAME));
		this.createToolItemLayout(this.decrement, ++column);
		
		this.durationLabel = uiFactory.createImageView(this.toolComposite);
		this.createToolItemLayout(this.durationLabel, ++column);
		
		this.increment = uiFactory.createButton(this.toolComposite);
		this.increment.addSelectionListener(new TGActionProcessorListener(this.context, TGIncrementDurationAction.NAME));
		this.createToolItemLayout(this.increment, ++column);
		
		// separator
		this.createToolSeparator(uiFactory, ++column);
		
		// scale
		this.scale = uiFactory.createButton(this.toolComposite);
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.scale.addSelectionListener(new TGActionProcessorListener(this.context, TGOpenScaleDialogAction.NAME));
		this.createToolItemLayout(this.scale, ++column);
		
		// scale name
		this.scaleName = uiFactory.createLabel(this.toolComposite);
		this.createToolItemLayout(this.scaleName, ++column, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		// settings
		this.settings = uiFactory.createButton(this.toolComposite);
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.settings.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				configure();
			}
		});
		this.createToolItemLayout(this.settings, ++column, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		
		this.toolComposite.getLayout().set(goLeft, UITableLayout.MARGIN_LEFT, 0f);
		this.toolComposite.getLayout().set(this.settings, UITableLayout.MARGIN_RIGHT, 0f);
	}
	
	private void createToolBarLayout(){
		UITableLayout uiLayout = new UITableLayout();
		uiLayout.set(UITableLayout.MARGIN_LEFT, 0f);
		uiLayout.set(UITableLayout.MARGIN_RIGHT, 0f);
		
		this.toolComposite.setLayout(uiLayout);
	}
	
	private void createToolItemLayout(UIControl uiControl, int column){
		this.createToolItemLayout(uiControl, column, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
	}
	
	private void createToolItemLayout(UIControl uiControl, int column, Integer alignX, Integer alignY, Boolean fillX, Boolean fillY){
		UITableLayout uiLayout = (UITableLayout) this.toolComposite.getLayout();
		uiLayout.set(uiControl, 1, column, alignX, alignY, fillX, fillX);
	}
	
	private void createToolSeparator(UIFactory uiFactory, int column){
		UISeparator uiSeparator = uiFactory.createVerticalSeparator(this.toolComposite);
		UITableLayout uiLayout = (UITableLayout) this.toolComposite.getLayout();
		uiLayout.set(uiSeparator, 1, column, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		uiLayout.set(uiSeparator, UITableLayout.PACKED_WIDTH, 20f);
		uiLayout.set(uiSeparator, UITableLayout.PACKED_HEIGHT, 20f);
	}
	
	private void loadDurationImage(boolean force) {
		int duration = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration().getValue();
		if( force || this.duration != duration ){
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
	}
	
	private void initCanvas(){
		this.image = makePianoImage();
		this.canvas = getUIFactory().createCanvas(this.control, true);
		this.canvas.addPaintListener(new TGBufferedPainterListenerLocked(this.context, new TGPianoPainterListener()));
		this.canvas.addMouseUpListener(new TGPianoMouseListener());
		this.canvas.setFocus();
	}
	
	/**
	 * Crea la imagen del piano
	 *
	 * @return
	 */
	private TGImage makePianoImage(){
		UIFactory factory = getUIFactory();
		TGImage image = new TGImageImpl(factory, factory.createImage((NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES)), NATURAL_HEIGHT));
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
	private int getSelection(float x){
		float posX = 0;
		
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			float width = 0f;
			
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
			
			if( x >= posX && x < (posX + width)  ){
				return i;
			}
			
			posX += width;
		}
		return -1;
	}
	
	protected void hit(float x, float y) {
		int value = this.getSelection(x);
		
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
		if(!this.isDisposed()){
			this.control.redraw();
			this.canvas.redraw();
			this.loadDurationImage(false);
		}
	}
	
	public void redrawPlayingMode(){
		if(!this.isDisposed() ){
			this.canvas.redraw();
		}
	}
	
	public void setVisible(boolean visible) {
		this.control.setVisible(visible);
	}
	
	public boolean isVisible() {
		return (this.control.isVisible());
	}
	
	public boolean isDisposed() {
		return (this.control.isDisposed());
	}
	
	public void dispose(){
		this.control.dispose();
		this.image.dispose();
		this.config.dispose();
	}
	
	public void loadProperties(){
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.loadScaleName();
		this.control.layout();
	}
	
	public void loadIcons(){
		this.goLeft.setImage(TuxGuitar.getInstance().getIconManager().getArrowLeft());
		this.goRight.setImage(TuxGuitar.getInstance().getIconManager().getArrowRight());
		this.decrement.setImage(TuxGuitar.getInstance().getIconManager().getArrowUp());
		this.increment.setImage(TuxGuitar.getInstance().getIconManager().getArrowDown());
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		this.loadDurationImage(true);
		this.control.layout();
	}
	
	public void loadScale(){
		this.loadScaleName();
		this.setChanges(true);
		this.control.layout();
	}
	
	public void configure(){
		this.config.configure((UIWindow) this.control.getParent());
	}
	
	public void reloadFromConfig() {
		this.setChanges(true);
		this.redraw();
	}
	
	public UIPanel getControl(){
		return this.control;
	}
	
	public UICanvas getCanvas() {
		return this.canvas;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	private class TGPianoMouseListener implements UIMouseUpListener {
		
		public TGPianoMouseListener(){
			super();
		}
		
		public void onMouseUp(UIMouseEvent event) {
			getCanvas().setFocus();
			if( event.getButton() == 1 ){
				if(!TuxGuitar.getInstance().getPlayer().isRunning() && !TGEditorManager.getInstance(TGPiano.this.context).isLocked()){
					if( getExternalBeat() == null ){
						hit(event.getPosition().getX(), event.getPosition().getY());
					}else{
						setExternalBeat( null );
						TuxGuitar.getInstance().updateCache(true);
					}
				}
			}else{
				new TGActionProcessor(TGPiano.this.context, TGGoRightAction.NAME).process();
			}
		}
	}
	
	private class TGPianoPainterListener implements TG2BufferedPainterHandle {
		
		public TGPianoPainterListener(){
			super();
		}

		public void paintControl(TGPainter painter) {
			TGPiano.this.paintEditor(painter);
		}

		public UICanvas getPaintableControl() {
			return TGPiano.this.canvas;
		}
	}
}
