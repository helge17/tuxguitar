package org.herac.tuxguitar.app.view.dialog.fretboard;

import java.util.Iterator;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.app.graphics.TGFontImpl;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.main.TGWindow;
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
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.util.TGContext;

public class TGFretBoard {
	
	public static final int MAX_FRETS = 24;
	public static final int TOP_SPACING = 10;
	public static final int BOTTOM_SPACING = 10;
	
	private static final int STRING_SPACING = TuxGuitar.getInstance().getConfig().getIntegerValue(TGConfigKeys.FRETBOARD_STRING_SPACING);
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_FRETBOARD);
	
	private TGContext context;
	private TGFretBoardConfig config;
	private UIPanel control;
	private UIPanel toolComposite;
	private UIImageView durationLabel;
	private UILabel scaleName;
	private UIButton scale;
	private UIButton goLeft;
	private UIButton goRight;
	private UIButton increment;
	private UIButton decrement;
	private UIButton settings;
	private TGImage fretBoard;
	
	private TGBeat beat;
	private TGBeat externalBeat;
	
	private int[] frets;
	private int[] strings;
	private float fretSpacing;
	private boolean changes;
	private UISize lastSize;
	private int duration;
	protected UIDropDownSelect<Integer> handSelector;
	protected UICanvas fretBoardComposite;
	
	public TGFretBoard(TGContext context, UIContainer parent) {
		this.context = context;
		this.config = new TGFretBoardConfig(context);
		this.config.load();
		this.control = getUIFactory().createPanel(parent, false);
		
		this.initToolBar();
		this.initEditor();
		this.createControlLayout();
		this.loadIcons();
		this.loadProperties();
		
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.toolComposite);
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.fretBoardComposite);
	}
	
	public void createControlLayout() {
		UITableLayout uiLayout = new UITableLayout(0f);
		uiLayout.set(this.toolComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		uiLayout.set(this.fretBoardComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
		
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
		this.createToolItemLayout(decrement, ++column);
		
		this.durationLabel = uiFactory.createImageView(this.toolComposite);
		this.createToolItemLayout(this.durationLabel, ++column);
		
		this.increment = uiFactory.createButton(this.toolComposite);
		this.increment.addSelectionListener(new TGActionProcessorListener(this.context, TGIncrementDurationAction.NAME));
		this.createToolItemLayout(increment, ++column);
		
		// separator
		this.createToolSeparator(uiFactory, ++column);
		
		// hand selector
		this.handSelector = uiFactory.createDropDownSelect(this.toolComposite);
		this.handSelector.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("fretboard.right-mode"), TGFretBoardConfig.DIRECTION_RIGHT));
		this.handSelector.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("fretboard.left-mode"), TGFretBoardConfig.DIRECTION_LEFT));
		this.handSelector.setSelectedItem(new UISelectItem<Integer>(null, this.getDirection(this.config.getDirection())));
		this.handSelector.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				Integer direction = TGFretBoard.this.handSelector.getSelectedValue();
				if( direction != null ) {
					updateDirection(direction);
				}
			}
		});
		this.createToolItemLayout(this.handSelector, ++column);
		
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
	
	private void initEditor() {
		this.lastSize = new UISize();
		this.fretBoardComposite = getUIFactory().createCanvas(this.control, false);
		this.fretBoardComposite.setBgColor(this.config.getColorBackground());
		this.fretBoardComposite.addMouseUpListener(new TGFretBoardMouseListener());
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
	}
	
	private void calculateFretSpacing(float width) {
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
				this.lastSize.setHeight(0);
			}
			
			UIRectangle childArea = this.control.getChildArea();
			float clientWidth = childArea.getWidth();
			float clientHeight = childArea.getHeight();
			
			if( this.lastSize.getWidth() != clientWidth || hasChanges() ){
				this.layout(clientWidth);
			}
			
			if( this.lastSize.getHeight() != clientHeight ) {
				TuxGuitar.getInstance().getFretBoardEditor().showFretBoard();
			}
			this.lastSize.setWidth(clientWidth);
			this.lastSize.setHeight(clientHeight);
		}
	}
	
	private void paintFretBoard(TGPainter painter){
		if(this.fretBoard == null || this.fretBoard.isDisposed()){
			UIFactory factory = getUIFactory();
			UIRectangle area = this.control.getChildArea();
			
			this.fretBoard = new TGImageImpl(factory, factory.createImage(area.getWidth(), ((STRING_SPACING) * (this.strings.length - 1)) + TOP_SPACING + BOTTOM_SPACING));
			
			TGPainter painterBuffer = this.fretBoard.createPainter();
			
			//fondo
			painterBuffer.setBackground(new TGColorImpl(this.config.getColorBackground()));
			painterBuffer.initPath(TGPainter.PATH_FILL);
			painterBuffer.addRectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight());
			painterBuffer.closePath();
			
			
			// pinto las cegillas
			TGIconManager iconManager = TGIconManager.getInstance(this.context);
			TGImage fretImage = new TGImageImpl(factory, iconManager.getFretboardFret());
			TGImage firstFretImage = new TGImageImpl(factory, iconManager.getFretboardFirstFret());
			
			painterBuffer.drawImage(firstFretImage, 0, 0, firstFretImage.getWidth(), firstFretImage.getHeight(), this.frets[0] - 5,this.strings[0] - 5, firstFretImage.getWidth(),this.strings[this.strings.length - 1] );
			
			paintFretPoints(painterBuffer,0);
			for (int i = 1; i < this.frets.length; i++) {
				painterBuffer.drawImage(fretImage, 0, 0, fretImage.getWidth(), fretImage.getHeight(), this.frets[i], this.strings[0] - 5,fretImage.getWidth(),this.strings[this.strings.length - 1] );
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
	
	private void paintKeyOval(TGPainter painter, UIColor background,int x, int y) {
		int size = getOvalSize();
		painter.setBackground(new TGColorImpl(background));
		painter.initPath(TGPainter.PATH_FILL);
		painter.moveTo(x - (size / 2),y - (size / 2));
		painter.addOval(x - (size / 2),y - (size / 2),size, size);
		painter.closePath();
	}
	
	private void paintKeyText(TGPainter painter, UIColor foreground, int x, int y, String text) {
		painter.setBackground(new TGColorImpl(this.config.getColorKeyTextBackground()));
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
	
	protected void hit(float x, float y) {
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
	
	private int getStringIndex(float y) {
		int index = -1;
		for (int i = 0; i < this.strings.length; i++) {
			if (index < 0) {
				index = i;
			} else {
				float distanceY = Math.abs(y - this.strings[index]);
				float currDistanceY = Math.abs(y - this.strings[i]);
				if( currDistanceY < distanceY) {
					index = i;
				}
			}
		}
		return index;
	}
	
	private int getFretIndex(float x) {
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
		if(!this.isDisposed()){
			this.control.redraw();
			this.fretBoardComposite.redraw();
			this.loadDurationImage(false);
		}
	}
	
	public void redrawPlayingMode(){
		if(!this.isDisposed()){
			this.fretBoardComposite.redraw();
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
		this.disposeFretBoardImage();
		this.config.dispose();
	}
	
	public void loadProperties(){
		int selection = this.handSelector.getSelectedItem().getValue();
		this.handSelector.removeItems();
		this.handSelector.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("fretboard.right-mode"), TGFretBoardConfig.DIRECTION_RIGHT));
		this.handSelector.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("fretboard.left-mode"), TGFretBoardConfig.DIRECTION_LEFT));
		this.handSelector.setSelectedItem(new UISelectItem<Integer>(null, selection));
		
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.loadScaleName();
		this.setChanges(true);
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
		this.layout(this.control.getChildArea().getWidth());
	}
	
	public void loadScale(){
		this.loadScaleName();
		this.setChanges(true);
		this.control.layout();
	}
	
	public int getWidth(){
		return this.frets[this.frets.length - 1];
	}
	
	public void computePackedSize() {
		this.control.getLayout().set(this.fretBoardComposite, UITableLayout.PACKED_HEIGHT, Float.valueOf(((STRING_SPACING) * (this.strings.length - 1)) + TOP_SPACING + BOTTOM_SPACING));
		this.control.computePackedSize();
	}
	
	public void layout(float width){
		this.disposeFretBoardImage();
		this.calculateFretSpacing(width);
		this.initFrets(10);
		this.initStrings(getStringCount());
		this.setChanges(false);
	}
	
	public void configure(){
		this.config.configure(TGWindow.getInstance(this.context).getWindow());
	}
	
	public void reloadFromConfig(){
		this.handSelector.setSelectedItem(new UISelectItem<Integer>(null, this.getDirection(this.config.getDirection())));
		this.setChanges(true);
		this.redraw();
	}
	
	public UIPanel getControl(){
		return this.control;
	}
	
	public UICanvas getFretBoardComposite(){
		return this.fretBoardComposite;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	private class TGFretBoardMouseListener implements UIMouseUpListener {
		
		public TGFretBoardMouseListener(){
			super();
		}
		
		public void onMouseUp(UIMouseEvent event) {
			getFretBoardComposite().setFocus();
			if( event.getButton() == 1 ){
				if(!TuxGuitar.getInstance().getPlayer().isRunning() && !TGEditorManager.getInstance(TGFretBoard.this.context).isLocked()){
					if( getExternalBeat() == null ){
						hit(event.getPosition().getX(), event.getPosition().getY());
					}else{
						setExternalBeat( null );
						TuxGuitar.getInstance().updateCache(true);
					}
				}
			}else{
				new TGActionProcessor(TGFretBoard.this.context, TGGoRightAction.NAME).process();
			}
		}
	}
	
	private class TGFretBoardPainterListener implements TG2BufferedPainterHandle {
		
		public TGFretBoardPainterListener(){
			super();
		}

		public void paintControl(TGPainter painter) {
			TGFretBoard.this.paintEditor(painter);
		}

		public UICanvas getPaintableControl() {
			return TGFretBoard.this.fretBoardComposite;
		}
	}
}
