package org.herac.tuxguitar.app.view.dialog.matrix;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.app.graphics.TGFontImpl;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TG2BufferedPainterHandle;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteAction;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
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
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIMouseEnterListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseExitListener;
import org.herac.tuxguitar.ui.event.UIMouseMoveListener;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMatrixEditor implements TGEventListener {
	
	private static final float DEFAULT_WIDTH = 640f;
	private static final float DEFAULT_HEIGHT = 480f;
	
	private static final int BORDER_HEIGHT = 20;
	private static final int SCROLL_INCREMENT = 50;
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_MATRIX);
	private static final MidiPercussionKey[] PERCUSSIONS = TuxGuitar.getInstance().getPlayer().getPercussionKeys();
	private static final int[] DIVISIONS = new int[] {1,2,3,4,6,8,16};
	
	private TGContext context;
	private TGMatrixConfig config;
	private UIWindow dialog;
	private UIPanel composite;
	private UIPanel toolbar;
	private UIScrollBarPanel canvasPanel;
	private UICanvas editor;
	private UIRectangle clientArea;
	private TGImage buffer;
	private BufferDisposer bufferDisposer;
	private UIImageView durationLabel;
	private UILabel gridsLabel;
	private UIButton goLeft;
	private UIButton goRight;
	private UIButton increment;
	private UIButton decrement;
	private UIButton settings;
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
	
	public TGMatrixEditor(TGContext context){
		this.context = context;
		this.grids = this.loadGrids();
	}
	
	public void show(){
		this.config = new TGMatrixConfig(this.context);
		this.config.load();
		
		this.dialog = getUIFactory().createWindow(TGWindow.getInstance(this.context).getWindow(), false, true);
		this.dialog.setText(TuxGuitar.getProperty("matrix.editor"));
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		this.dialog.setBounds(new UIRectangle(new UISize(DEFAULT_WIDTH, DEFAULT_HEIGHT)));
		this.dialog.addDisposeListener(new DisposeListenerImpl());
		this.bufferDisposer = new BufferDisposer();
		
		this.composite = getUIFactory().createPanel(this.dialog, false);
		
		this.initToolBar();
		this.initEditor();
		this.createWindowLayout();
		this.createControlLayout();
		this.loadIcons();
		this.addListeners();
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_LAYOUT);
	}
	
	public void createWindowLayout() {
		UITableLayout uiLayout = new UITableLayout();
		uiLayout.set(this.composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.dialog.setLayout(uiLayout);
	}
	
	public void createControlLayout() {
		UITableLayout uiLayout = new UITableLayout(0f);
		uiLayout.set(this.toolbar, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		uiLayout.set(this.canvasPanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.composite.setLayout(uiLayout);
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
		UIFactory uiFactory = getUIFactory();
		
		int column = 0;
		
		this.toolbar = uiFactory.createPanel(this.composite, false);
		this.createToolBarLayout();
		
		// position
		this.goLeft = uiFactory.createButton(this.toolbar);
		this.goLeft.addSelectionListener(new TGActionProcessorListener(this.context, TGGoLeftAction.NAME));
		this.createToolItemLayout(this.goLeft, ++column);
		
		this.goRight = uiFactory.createButton(this.toolbar);
		this.goRight.addSelectionListener(new TGActionProcessorListener(this.context, TGGoRightAction.NAME));
		this.createToolItemLayout(this.goRight, ++column);
		
		// separator
		this.createToolSeparator(uiFactory, ++column);
		
		// duration
		this.decrement = uiFactory.createButton(this.toolbar);
		this.decrement.addSelectionListener(new TGActionProcessorListener(this.context, TGDecrementDurationAction.NAME));
		this.createToolItemLayout(this.decrement, ++column);
		
		this.durationLabel = uiFactory.createImageView(this.toolbar);
		this.createToolItemLayout(this.durationLabel, ++column);
		
		this.increment = uiFactory.createButton(this.toolbar);
		this.increment.addSelectionListener(new TGActionProcessorListener(this.context, TGIncrementDurationAction.NAME));
		this.createToolItemLayout(this.increment, ++column);
		
		// separator
		this.createToolSeparator(uiFactory, ++column);
		
		// grids
		this.gridsLabel = uiFactory.createLabel(this.toolbar);
		this.gridsLabel.setText(TuxGuitar.getProperty("matrix.grids"));
		this.createToolItemLayout(this.gridsLabel, ++column, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<Integer> divisionsCombo = uiFactory.createDropDownSelect(this.toolbar);
		for(int i = 0; i < DIVISIONS.length; i ++){
			divisionsCombo.addItem(new UISelectItem<Integer>(Integer.toString(DIVISIONS[i]), DIVISIONS[i]));
		}
		divisionsCombo.setSelectedValue(this.grids > 0 ? this.grids : null);
		divisionsCombo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				Integer grids = divisionsCombo.getSelectedValue();
				if( grids != null ){
					setGrids(grids);
				}
			}
		});
		this.createToolItemLayout(divisionsCombo, ++column);
		
		// settings
		this.settings = uiFactory.createButton(this.toolbar);
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.settings.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				configure();
			}
		});
		this.createToolItemLayout(this.settings, ++column, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		
		this.toolbar.getLayout().set(goLeft, UITableLayout.MARGIN_LEFT, 0f);
		this.toolbar.getLayout().set(this.settings, UITableLayout.MARGIN_RIGHT, 0f);
	}
	
	private void createToolBarLayout(){
		UITableLayout uiLayout = new UITableLayout();
		uiLayout.set(UITableLayout.MARGIN_LEFT, 0f);
		uiLayout.set(UITableLayout.MARGIN_RIGHT, 0f);
		
		this.toolbar.setLayout(uiLayout);
	}
	
	private void createToolItemLayout(UIControl uiControl, int column){
		this.createToolItemLayout(uiControl, column, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
	}
	
	private void createToolItemLayout(UIControl uiControl, int column, Integer alignX, Integer alignY, Boolean fillX, Boolean fillY){
		UITableLayout uiLayout = (UITableLayout) this.toolbar.getLayout();
		uiLayout.set(uiControl, 1, column, alignX, alignY, fillX, fillX);
	}
	
	private void createToolSeparator(UIFactory uiFactory, int column){
		UISeparator uiSeparator = uiFactory.createVerticalSeparator(this.toolbar);
		UITableLayout uiLayout = (UITableLayout) this.toolbar.getLayout();
		uiLayout.set(uiSeparator, 1, column, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		uiLayout.set(uiSeparator, UITableLayout.PACKED_WIDTH, 20f);
		uiLayout.set(uiSeparator, UITableLayout.PACKED_HEIGHT, 20f);
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
		UIFactory uiFactory = this.getUIFactory();
		UITableLayout uiLayout = new UITableLayout(0f);
		
		this.canvasPanel = uiFactory.createScrollBarPanel(this.composite, true, true, true);
		this.canvasPanel.setLayout(uiLayout);
		
		this.selection = -1;
		this.editor = uiFactory.createCanvas(this.canvasPanel, false);
		this.editor.setFocus();
		this.editor.addPaintListener(new TGBufferedPainterListenerLocked(this.context, new TGMatrixPainterListener()));
		this.editor.addMouseUpListener(mouseListener);
		this.editor.addMouseEnterListener(mouseListener);
		this.editor.addMouseExitListener(mouseListener);
		this.editor.addMouseMoveListener(mouseListener);
		
		this.canvasPanel.getHScroll().setIncrement(SCROLL_INCREMENT);
		this.canvasPanel.getHScroll().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				redraw();
			}
		});

		this.canvasPanel.getVScroll().setIncrement(SCROLL_INCREMENT);
		this.canvasPanel.getVScroll().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				redraw();
			}
		});
		
		uiLayout.set(this.editor, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f); 
	}
	
	private void updateScroll(){
		if( this.clientArea != null ){
			UIScrollBar vBar = this.canvasPanel.getVScroll();
			UIScrollBar hBar = this.canvasPanel.getHScroll();
			vBar.setMaximum(Math.max(Math.round(this.height - this.clientArea.getHeight()), 0));
			vBar.setThumb(Math.round(this.clientArea.getHeight()));
			
			hBar.setMaximum(Math.max(Math.round(this.width - this.clientArea.getWidth()), 0));
			hBar.setThumb(Math.round(this.clientArea.getWidth()));
		}
	}
	
	private int getValueAt(float y){
		if(this.clientArea == null || (y - BORDER_HEIGHT) < 0 || y + BORDER_HEIGHT > this.clientArea.getHeight()){
			return -1;
		}
		int scroll = this.canvasPanel.getVScroll().getValue();
		int value = (this.maxNote -  ((int)(  (y + scroll - BORDER_HEIGHT)  / this.lineHeight)) );
		return value;
	}
	
	private long getStartAt(float x){
		TGMeasure measure = getMeasure();
		float posX = (x + this.canvasPanel.getHScroll().getValue());
		long start =(long) (measure.getStart() + (((posX - this.leftSpacing) * measure.getLength()) / (this.timeWidth * measure.getTimeSignature().getNumerator())));
		return start;
	}
	
	private void paintEditor(TGPainter painter) {
		this.clientArea = this.editor.getBounds();
		
		if( this.clientArea != null ){
			TGImage buffer = getBuffer();
			
			this.width = this.bufferWidth;
			this.height = (this.bufferHeight + (BORDER_HEIGHT *2));
			
			this.updateScroll();
			int scrollX = this.canvasPanel.getHScroll().getValue();
			int scrollY = this.canvasPanel.getVScroll().getValue();
			
			painter.drawImage(buffer,-scrollX,(BORDER_HEIGHT - scrollY));
			this.paintMeasure(painter,(-scrollX), (BORDER_HEIGHT - scrollY) );
			this.paintBorders(painter,(-scrollX),0);
			this.paintPosition(painter,(-scrollX),0);
			this.paintSelection(painter, (-scrollX), (BORDER_HEIGHT - scrollY) );
		}
	}
	
	private TGImage getBuffer(){
		if( this.clientArea != null ){
			this.bufferDisposer.update(this.clientArea.getWidth(), this.clientArea.getHeight());
			if(this.buffer == null || this.buffer.isDisposed()){
				UIFactory uiFactory = getUIFactory();
				
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
				float minimumNameHeight = 1;
				TGImage auxImage = new TGImageImpl(uiFactory, uiFactory.createImage(1f, 1f));
				TGPainter auxPainter = auxImage.createPainter();
				auxPainter.setFont(new TGFontImpl(this.config.getFont()));
				for(int i = 0; i < names.length;i ++){
					float fmWidth = auxPainter.getFMWidth(names[i]);
					if( fmWidth > minimumNameWidth ){
						minimumNameWidth = fmWidth;
					}
					float fmHeight = auxPainter.getFMHeight();
					if( fmHeight > minimumNameHeight ){
						minimumNameHeight = fmHeight;
					}
				}
				auxPainter.dispose();
				auxImage.dispose();
				
				int cols = measure.getTimeSignature().getNumerator();
				int rows = (this.maxNote - this.minNote);
				
				this.leftSpacing = minimumNameWidth + 10;
				this.lineHeight = Math.max(minimumNameHeight,( (this.clientArea.getHeight() - (BORDER_HEIGHT * 2.0f))/ (rows + 1.0f)));
				this.timeWidth = Math.max((10 * (TGDuration.SIXTY_FOURTH / measure.getTimeSignature().getDenominator().getValue())),( (this.clientArea.getWidth() - this.leftSpacing) / cols)  );
				this.bufferWidth = this.leftSpacing + (this.timeWidth * cols);
				this.bufferHeight = (this.lineHeight * (rows + 1));
				this.buffer = new TGImageImpl(uiFactory, uiFactory.createImage(this.bufferWidth, this.bufferHeight));
				
				TGPainter painter = this.buffer.createPainter();
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
	
	private void paintMeasure(TGPainter painter,float fromX, float fromY){
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
	
	private void paintBeat(TGPainter painter,TGMeasure measure,TGBeat beat,float fromX, float fromY){
		if( this.clientArea != null ){
			float minimumY = BORDER_HEIGHT;
			float maximumY = (this.clientArea.getHeight() - BORDER_HEIGHT);
			
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
	
	private void paintBorders(TGPainter painter,float fromX, float fromY){
		if( this.clientArea != null ){
			painter.setBackground(new TGColorImpl(this.config.getColorBorder()));
			painter.initPath(TGPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.addRectangle(fromX,fromY,this.bufferWidth ,BORDER_HEIGHT);
			painter.addRectangle(fromX,fromY + (this.clientArea.getHeight() - BORDER_HEIGHT),this.bufferWidth ,BORDER_HEIGHT);
			painter.closePath();
			
			painter.initPath();
			painter.setAntialias(false);
			painter.addRectangle(fromX,fromY,this.width,this.clientArea.getHeight());
			painter.closePath();
		}
	}
	
	private void paintPosition(TGPainter painter,float fromX, float fromY){
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
				painter.addRectangle(fromX + (this.leftSpacing + x),fromY + (this.clientArea.getHeight() - BORDER_HEIGHT), width,BORDER_HEIGHT);
				painter.closePath();
			}
		}
	}
	
	private void paintSelection(TGPainter painter, float fromX, float fromY){
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
	
	private void updateSelection(float y){
		if(!TuxGuitar.getInstance().getPlayer().isRunning()){
			int previousSelection = this.selection;
			this.selection = getValueAt(y);
			
			if( this.selection != previousSelection ){
				this.redraw();
			}
		}
	}
	
	private void hit(float x, float y){
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
	
	private void play(final int value){
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
	
	private int loadGrids(){
		int grids = TuxGuitar.getInstance().getConfig().getIntegerValue(TGConfigKeys.MATRIX_GRIDS);
		// check if is valid value
		for(int i = 0 ; i < DIVISIONS.length ; i ++ ){
			if(grids == DIVISIONS[i]){
				return grids;
			}
		}
		return DIVISIONS[1];
	}
	
	private void setGrids(int grids){
		this.grids = grids;
		this.disposeBuffer();
		this.redraw();
	}
	
	public int getGrids(){
		return this.grids;
	}
	
	private TGMeasure getMeasure(){
		if(TuxGuitar.getInstance().getPlayer().isRunning()){
			TGMeasure measure = TuxGuitar.getInstance().getEditorCache().getPlayMeasure();
			if(measure != null){
				return measure;
			}
		}
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure();
	}
	
	private Caret getCaret(){
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public void redraw(){
		if(!isDisposed()){
			this.editor.redraw();
			this.loadDurationImage(false);
		}
	}
	
	public void redrawPlayingMode() {
		this.redraw();
	}
	
	private void configure(){
		this.config.configure(this.dialog);
	}
	
	public void reloadFromConfig(){
		this.disposeBuffer();
		this.redraw();
	}
	
	private void layout(){
		if(!this.isDisposed() ){
			this.composite.layout();
		}
	}
	
	public void loadIcons(){
		if(!this.isDisposed() ){
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			this.goLeft.setImage(TuxGuitar.getInstance().getIconManager().getArrowLeft());
			this.goRight.setImage(TuxGuitar.getInstance().getIconManager().getArrowRight());
			this.decrement.setImage(TuxGuitar.getInstance().getIconManager().getArrowUp());
			this.increment.setImage(TuxGuitar.getInstance().getIconManager().getArrowDown());
			this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
			this.loadDurationImage(true);
			this.layout();
			this.redraw();
		}
	}
	
	public void loadProperties() {
		if(!this.isDisposed() ){
			this.dialog.setText(TuxGuitar.getProperty("matrix.editor"));
			this.gridsLabel.setText(TuxGuitar.getProperty("matrix.grids"));
			this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
			this.disposeBuffer();
			this.layout();
			this.redraw();
		}
	}
	
	public void dispose(){
		if(!this.isDisposed()){
			this.dialog.dispose();
		}
	}
	
	private void disposeBuffer(){
		if( this.buffer != null && !this.buffer.isDisposed()){
			this.buffer.dispose();
			this.buffer = null;
		}
	}
	
	private void disposeAll() {
		this.disposeBuffer();
		this.config.dispose();
	}
	
	private UICanvas getEditor() {
		return this.editor;
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
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	public static TGMatrixEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMatrixEditor.class.getName(), new TGSingletonFactory<TGMatrixEditor>() {
			public TGMatrixEditor createInstance(TGContext context) {
				return new TGMatrixEditor(context);
			}
		});
	}
	
	private class BufferDisposer {
		private int numerator;
		private int denominator;
		private int track;
		private boolean percussion;
		
		private float width;
		private float height;
		
		public void update(float width, float height){
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
	
	private class DisposeListenerImpl implements UIDisposeListener {
		
		public void onDispose(UIDisposeEvent event) {
			TGMatrixEditor.this.disposeAll();
			TGMatrixEditor.this.removeListeners();
			TuxGuitar.getInstance().updateCache(true);
		}
	}
	
	private class TGMatrixMouseListener implements UIMouseUpListener, UIMouseEnterListener, UIMouseExitListener, UIMouseMoveListener {
		
		public TGMatrixMouseListener(){
			super();
		}
		
		public void onMouseUp(UIMouseEvent event) {
			getEditor().setFocus();
			if( event.getButton() == 1 ){
				if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
					hit(event.getPosition().getX(), event.getPosition().getY());
				}
			}
		}
		
		public void onMouseMove(UIMouseEvent event) {
			if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
				updateSelection(event.getPosition().getY());
			}
		}
		
		public void onMouseExit(UIMouseEvent event) {
			if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
				updateSelection(-1);
			}
		}
		
		public void onMouseEnter(UIMouseEvent event) {
			if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
				redraw();
			}
		}
	}
	
	private class TGMatrixPainterListener implements TG2BufferedPainterHandle {
		
		public TGMatrixPainterListener(){
			super();
		}

		public void paintControl(TGPainter painter) {
			TGMatrixEditor.this.paintEditor(painter);
		}

		public UICanvas getPaintableControl() {
			return TGMatrixEditor.this.editor;
		}
	}
}
