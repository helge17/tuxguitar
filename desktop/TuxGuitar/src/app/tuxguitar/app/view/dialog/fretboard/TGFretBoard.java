package app.tuxguitar.app.view.dialog.fretboard;

import java.util.Iterator;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import app.tuxguitar.app.action.impl.caret.TGGoRightAction;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import app.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import app.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGScale;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIMouseUpListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICanvas;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UIImageView;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UISeparator;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGMusicKeyUtils;

public class TGFretBoard {

	public static final int MAX_FRETS = 24;
	public static final int TOP_SPACING = 10;
	public static final int BOTTOM_SPACING = 10;

	private static final int STRING_SPACING_MIN = 10;
	private static final int STRING_SPACING_MAX = 60;
	private static final int STRING_SPACING_INCREMENT = 2;

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
	private UIButton smaller;
	private UIButton bigger;
	private UIImage fretBoard;
	private TGBeat beat;
	private TGBeat externalBeat;
	private int[] frets;
	private int[] strings;
	private float fretSpacing;
	private boolean changes;
	private UISize lastSize;
	private int stringSpacing;
	private int lastStringSpacing;
	private int duration;
	protected UIDropDownSelect<Integer> handSelector;
	protected UICanvas fretBoardComposite;

	public TGFretBoard(TGContext context, UIContainer parent) {
		this.context = context;
		this.config = new TGFretBoardConfig(context);
		this.config.load();
		this.stringSpacing = TuxGuitar.getInstance().getConfig().getIntegerValue(TGConfigKeys.FRETBOARD_STRING_SPACING);
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
		this.increment = uiFactory.createButton(this.toolComposite);
		this.increment.addSelectionListener(new TGActionProcessorListener(this.context, TGIncrementDurationAction.NAME));
		this.createToolItemLayout(increment, ++column);

		this.durationLabel = uiFactory.createImageView(this.toolComposite);
		this.createToolItemLayout(this.durationLabel, ++column);

		this.decrement = uiFactory.createButton(this.toolComposite);
		this.decrement.addSelectionListener(new TGActionProcessorListener(this.context, TGDecrementDurationAction.NAME));
		this.createToolItemLayout(decrement, ++column);

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

		// fretboard height
		this.smaller = uiFactory.createButton(this.toolComposite);
		this.smaller.setImage(TuxGuitar.getInstance().getIconManager().getFretboardSmaller());
		this.smaller.setToolTipText(TuxGuitar.getProperty("fretboard.smaller"));
		this.smaller.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGFretBoard.this.updateStringSpacing(-STRING_SPACING_INCREMENT);
			}
		});
		this.createToolItemLayout(this.smaller, ++column, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		this.bigger = uiFactory.createButton(this.toolComposite);
		this.bigger.setImage(TuxGuitar.getInstance().getIconManager().getFretboardBigger());
		this.bigger.setToolTipText(TuxGuitar.getProperty("fretboard.bigger"));
		this.bigger.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGFretBoard.this.updateStringSpacing(STRING_SPACING_INCREMENT);
			}
		});
		this.createToolItemLayout(this.bigger, ++column, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);

		// settings
		this.settings = uiFactory.createButton(this.toolComposite);
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.SETTINGS));
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.settings.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				configure();
			}
		});
		this.createToolItemLayout(this.settings, ++column, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);

		this.toolComposite.getLayout().set(goLeft, UITableLayout.MARGIN_LEFT, 0f);
		this.toolComposite.getLayout().set(this.settings, UITableLayout.MARGIN_RIGHT, 0f);
	}

	private void updateStringSpacing(int increment) {
		this.lastStringSpacing = this.stringSpacing;
		this.stringSpacing += increment;
		this.stringSpacing = Math.min(this.stringSpacing, STRING_SPACING_MAX);
		this.stringSpacing = Math.max(this.stringSpacing, STRING_SPACING_MIN);
		this.smaller.setEnabled(this.stringSpacing > STRING_SPACING_MIN);
		this.bigger.setEnabled(this.stringSpacing < STRING_SPACING_MAX);
		if (this.stringSpacing != this.lastStringSpacing) {
			TuxGuitar.getInstance().getConfig().setValue(TGConfigKeys.FRETBOARD_STRING_SPACING,this.stringSpacing);
			this.setChanges(true);
			this.updateEditor();
		}
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
		this.fretBoardComposite.addMouseUpListener(new TGFretBoardMouseListener(this.context));
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
		int scaleKeyIndex = TuxGuitar.getInstance().getScaleManager().getSelectionKeyIndex();
		int scaleIndex = TuxGuitar.getInstance().getScaleManager().getScaleIndex();
		String key = TuxGuitar.getInstance().getScaleManager().getKeyName( scaleKeyIndex );
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
			this.strings[i] = fromY + (this.stringSpacing * i);
		}
	}

	private void updateEditor(){
		if( isVisible() ){
			if( MidiPlayer.getInstance(this.context).isRunning()){
				this.beat = TGTransport.getInstance(this.context).getCache().getPlayBeat();
			}else if(this.externalBeat != null){
				this.beat = this.externalBeat;
			}else{
				this.beat = TablatureEditor.getInstance(this.context).getTablature().getCaret().getSelectedBeat();
			}

			if ((this.strings.length != getStringCount()) || (this.stringSpacing != this.lastStringSpacing)) {
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
			this.lastStringSpacing = this.stringSpacing;
		}
	}

	private void paintFretBoard(UIPainter painter){
		if(this.fretBoard == null || this.fretBoard.isDisposed()){
			UIFactory factory = getUIFactory();
			UIRectangle area = this.control.getChildArea();

			this.fretBoard = factory.createImage(area.getWidth(), (this.stringSpacing * (this.strings.length - 1)) + TOP_SPACING + BOTTOM_SPACING);

			UIPainter painterBuffer = this.fretBoard.createPainter();

			//fondo
			painterBuffer.setBackground(this.config.getColorBackground());
			painterBuffer.initPath(UIPainter.PATH_FILL);
			painterBuffer.addRectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight());
			painterBuffer.closePath();


			// pinto las cegillas
			TGIconManager iconManager = TGIconManager.getInstance(this.context);
			UIImage fretImage = iconManager.getFretboardFret();
			UIImage firstFretImage = iconManager.getFretboardFirstFret();

			painterBuffer.drawImage(firstFretImage, 0, 0, firstFretImage.getWidth(), firstFretImage.getHeight(), this.frets[0] - 5,this.strings[0] - 5, firstFretImage.getWidth(),this.strings[this.strings.length - 1] );

			paintFretPoints(painterBuffer,0);
			for (int i = 1; i < this.frets.length; i++) {
				painterBuffer.drawImage(fretImage, 0, 0, fretImage.getWidth(), fretImage.getHeight(), this.frets[i], this.strings[0] - 5,fretImage.getWidth(),this.strings[this.strings.length - 1] );
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

	private void paintFretPoints(UIPainter painter, int fretIndex) {
		painter.setBackground(this.config.getColorFretPoint());
		if ((fretIndex + 1) < this.frets.length) {
			int fret = ((fretIndex + 1) % 12);
			painter.setLineWidth(10);
			if (fret == 0) {
				int size = getOvalSize();
				int x = this.frets[fretIndex] + ((this.frets[fretIndex + 1] - this.frets[fretIndex]) / 2);
				int y1 = this.strings[0] + ((this.strings[this.strings.length - 1] - this.strings[0]) / 2) - this.stringSpacing;
				int y2 = this.strings[0] + ((this.strings[this.strings.length - 1] - this.strings[0]) / 2) + this.stringSpacing;
				painter.initPath(UIPainter.PATH_FILL);
				painter.addCircle(x, y1, size);
				painter.addCircle(x, y2, size);
				painter.closePath();
			} else if (fret == 3 || fret == 5 || fret == 7 || fret == 9) {
				int size = getOvalSize();
				int x = this.frets[fretIndex] + ((this.frets[fretIndex + 1] - this.frets[fretIndex]) / 2);
				int y = this.strings[0] + ((this.strings[this.strings.length - 1] - this.strings[0]) / 2);
				painter.initPath(UIPainter.PATH_FILL);
				painter.addCircle(x, y, size);
				painter.closePath();
			}
			painter.setLineWidth(1);
		}
	}

	private void paintScale(UIPainter painter) {
		TGTrack track = getTrack();
		TGScale scale = TuxGuitar.getInstance().getScaleManager().getScale();
		int keySignature = TGMusicKeyUtils.getKeySignature(scale);

		for (int i = 0; i < this.strings.length; i++) {
			TGString string = track.getString(i + 1);
			for (int j = 0; j < this.frets.length; j++) {

				int noteValue = string.getValue() + j;
				if(scale.getNote(noteValue)){
					int x = this.frets[j];
					if(j > 0){
						x -= ((x - this.frets[j - 1]) / 2);
					}
					int y = this.strings[i];

					if( (this.config.getStyle() & TGFretBoardConfig.DISPLAY_TEXT_SCALE) != 0 ){
						String noteName = TGMusicKeyUtils.noteName(noteValue, keySignature);
						paintKeyText(painter,this.config.getColorScaleText(), this.config.getColorScale(),x,y,noteName);
					}
					else{
						paintKeyOval(painter,this.config.getColorScale(),x,y);
					}
				}
			}
		}

		painter.setForeground(this.config.getColorBackground());
	}

	private void paintNotes(UIPainter painter) {
		if(this.beat != null){
			TGTrack track = getTrack();
			int keySignature = this.beat.getMeasure().getKeySignature();

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
							paintKeyText(painter,this.config.getColorNoteText(), this.config.getColorNote(), x, y, TGMusicKeyUtils.noteName(realValue, keySignature, note.isAltEnharmonic()));
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

	private void paintKeyOval(UIPainter painter, UIColor background,int x, int y) {
		this.paintKeyOval(painter, background, x, y, this.getOvalSize());
	}
	private void paintKeyOval(UIPainter painter, UIColor background,int x, int y, int ovalSize) {
		painter.setBackground(background);
		painter.initPath(UIPainter.PATH_FILL);
		painter.moveTo(x, y);
		painter.addCircle(x, y, ovalSize);
		painter.closePath();
	}

	private void paintKeyText(UIPainter painter, UIColor foreground, UIColor background, int x, int y, String text) {
		if (!getTrack().isPercussion()) {
			painter.setBackground(background);
			painter.setForeground(foreground);
			painter.setFont(this.config.getFont());

			float fmWidth = painter.getFMWidth(text);
			float fmHeight = painter.getFMHeight();
			int ovalSize = (int)Math.max(fmWidth, fmHeight) + this.stringSpacing/10;
			ovalSize = Math.min(ovalSize, this.getMaxOvalSize());
			this.paintKeyOval(painter, background, x, y, ovalSize);
			painter.drawString(text, x - (fmWidth / 2f),y + painter.getFMMiddleLine());
		}
	}

	protected void paintEditor(UIPainter painter) {
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
		return ((this.stringSpacing / 2) + (this.stringSpacing / 10));
	}

	private int getMaxOvalSize() {
		return (this.stringSpacing - this.stringSpacing/10);
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

		this.smaller.setToolTipText(TuxGuitar.getProperty("fretboard.smaller"));
		this.bigger.setToolTipText(TuxGuitar.getProperty("fretboard.bigger"));
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.loadScaleName();
		this.setChanges(true);
		this.control.layout();
	}

	public void loadIcons(){
		this.goLeft.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_LEFT));
		this.goRight.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_RIGHT));
		this.decrement.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_DOWN));
		this.increment.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_UP));
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.SETTINGS));
		this.smaller.setImage(TuxGuitar.getInstance().getIconManager().getFretboardSmaller());
		this.bigger.setImage(TuxGuitar.getInstance().getIconManager().getFretboardBigger());
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
		this.control.getLayout().set(this.fretBoardComposite, UITableLayout.PACKED_HEIGHT, Float.valueOf((this.stringSpacing * (this.strings.length - 1)) + TOP_SPACING + BOTTOM_SPACING));
		this.control.computePackedSize(null, null);
	}

	public void layout(float width){
		this.disposeFretBoardImage();
		this.calculateFretSpacing(width);
		this.initFrets(10);
		this.initStrings(getStringCount());
		this.setChanges(false);
	}

	public void configure(){
		this.config.configure(TGWindow.getInstance(this.context).getWindow(), getTrack().isPercussion());
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

		private TGContext context;

		public TGFretBoardMouseListener(TGContext context){
			this.context = context;
		}

		public void onMouseUp(final UIMouseEvent event) {
			getFretBoardComposite().setFocus();
			if( event.getButton() == 1 ) {
				if(!MidiPlayer.getInstance(this.context).isRunning()) {
					TGEditorManager.getInstance(this.context).asyncRunLocked(new Runnable() {
						public void run() {
							if( getExternalBeat() == null ){
								hit(event.getPosition().getX(), event.getPosition().getY());
							}else{
								setExternalBeat( null );
								TuxGuitar.getInstance().updateCache(true);
							}
						}
					});
				}
			}else{
				new TGActionProcessor(TGFretBoard.this.context, TGGoRightAction.NAME).process();
			}
		}
	}

	private class TGFretBoardPainterListener implements TGBufferedPainterHandle {

		public TGFretBoardPainterListener(){
			super();
		}

		public void paintControl(UIPainter painter) {
			TGFretBoard.this.paintEditor(painter);
		}

		public UICanvas getPaintableControl() {
			return TGFretBoard.this.fretBoardComposite;
		}
	}
}
