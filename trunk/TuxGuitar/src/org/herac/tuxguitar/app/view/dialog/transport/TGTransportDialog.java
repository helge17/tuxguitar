package org.herac.tuxguitar.app.view.dialog.transport;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.measure.TGGoFirstMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoLastMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoPreviousMeasureAction;
import org.herac.tuxguitar.app.action.impl.transport.TGOpenTransportModeDialogAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportMetronomeAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.app.editor.EditorCache;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.util.TGProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseMoveListener;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIProgressBar;
import org.herac.tuxguitar.ui.widget.UIToggleButton;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTransportDialog implements TGEventListener {
	
	private static final int PLAY_MODE_DELAY = 250;
	
	public static final int STATUS_STOPPED = 1;
	public static final int STATUS_PAUSED = 2;
	public static final int STATUS_RUNNING = 3;
	
	private TGContext context;
	private UIWindow dialog;
	private UILabel label;
	private UIProgressBar tickProgress;
	private UIToggleButton metronome;
	private UIButton mode;
	private UIToolBar toolBar;
	private UIToolActionItem first;
	private UIToolActionItem last;
	private UIToolActionItem previous;
	private UIToolActionItem next;
	private UIToolActionItem stop;
	private UIToolActionItem play;
	private TGProcess loadPropertiesProcess;
	private TGProcess loadIconsProcess;
	private TGProcess updateItemsProcess;
	private TGProcess redrawPlayModeProcess;
	private boolean editingTickScale;
	private long redrawTime;
	private int status;
	
	public TGTransportDialog(TGContext context) {
		this.context = context;
		this.createSyncProcesses();
	}
	
	public void show() {
		UIFactory factory = this.getUIFactory();
		
		this.dialog = factory.createWindow(TGWindow.getInstance(this.context).getWindow(), false, false);
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		this.dialog.setLayout(new UITableLayout());
		this.dialog.setText(TuxGuitar.getProperty("transport"));
		this.initComposites();
		this.initToolBar();
		this.redrawProgress();
		
		this.addListeners();
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeListeners();
				TuxGuitar.getInstance().updateCache(true);
			}
		});
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getIconManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeRedrawListener(this);
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
	}
	
	private void initComposites(){
		UIPanel composite = getUIFactory().createPanel(this.dialog, true);
		composite.setLayout(new UITableLayout(0f));
		
		UITableLayout parentLayout = (UITableLayout) this.dialog.getLayout();
		parentLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.initOptions(composite);
		this.initProgress(composite);
	}
	
	private void initOptions(UILayoutContainer parent) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = factory.createPanel(parent, false);
		composite.setLayout(compositeLayout);
		parentLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, null, null, null, null, 0f);
		
		this.metronome = factory.createToggleButton(composite);
		this.metronome.addSelectionListener(new TGActionProcessorListener(this.context , TGTransportMetronomeAction.NAME));
		compositeLayout.set(this.metronome, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);
		
		this.mode = factory.createButton(composite);
		this.mode.addSelectionListener(new TGActionProcessorListener(this.context , TGOpenTransportModeDialogAction.NAME));
		compositeLayout.set(this.mode, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);
		
		this.loadOptionIcons();
	}
	
	private void initProgress(UILayoutContainer parent){
		UIPanel composite = getUIFactory().createPanel(parent, false);
		composite.setLayout(new UITableLayout());
		
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		parentLayout.set(composite, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, null, null, null, null, 0f);
		
		initLabel(composite);
		initScale(composite);
	}
	
	private void initLabel(UILayoutContainer parent) {
		final UIFactory factory = this.getUIFactory();
		final UIFont font = factory.createFont("Minisystem", 36, false, false);
		final UIColor background = factory.createColor(0x00, 0x00, 0x00);
		final UIColor foreground = factory.createColor(0x00, 0x00, 0xff);
		
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		
		UITableLayout labelContainerLayout = new UITableLayout();
		UIPanel labelContainer = factory.createPanel(parent, false);
		labelContainer.setLayout(labelContainerLayout);
		labelContainer.setBgColor(background);
		parentLayout.set(labelContainer, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.label = factory.createLabel(labelContainer);
		this.label.setBgColor(background);
		this.label.setFgColor(foreground);
		this.label.setFont(font);
		
		labelContainer.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				font.dispose();
				background.dispose();
				foreground.dispose();
			}
		});
		labelContainerLayout.set(this.label, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
	}
	
	private void initScale(UILayoutContainer parent){
		UIFactory factory = this.getUIFactory();
		
		this.tickProgress = factory.createProgressBar(parent);
		this.tickProgress.setCursor(UICursor.HAND);
		this.tickProgress.setValue((int)TGDuration.QUARTER_TIME);
		this.tickProgress.addMouseDownListener(new UIMouseDownListener() {
			public void onMouseDown(UIMouseEvent event) {
				TGTransportDialog.this.setEditingTickScale(true);
				TGTransportDialog.this.updateProgressBar(event.getPosition().getX());
			}
		});
		this.tickProgress.addMouseUpListener(new UIMouseUpListener() {
			public void onMouseUp(UIMouseEvent event) {
				TGTransportDialog.this.gotoMeasure(getSongManager().getMeasureHeaderAt(getDocumentManager().getSong(), TGTransportDialog.this.tickProgress.getValue()),true);
				TGTransportDialog.this.setEditingTickScale(false);
			}
		});
		this.tickProgress.addMouseMoveListener(new UIMouseMoveListener() {
			public void onMouseMove(UIMouseEvent event) {
				TGTransportDialog.this.updateProgressBar(event.getPosition().getX());
			}
		});
		
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		parentLayout.set(this.tickProgress, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		parentLayout.set(this.tickProgress, UITableLayout.PACKED_HEIGHT, 10f);
	}
	
	private void updateProgressBar(float x){
		if( this.isEditingTickScale()){
			float width = this.tickProgress.getBounds().getWidth();
			float selection = (this.tickProgress.getMinimum() + (( x * (this.tickProgress.getMaximum() - this.tickProgress.getMinimum())) / width) );
			this.tickProgress.setValue(Math.round(Math.max(TGDuration.QUARTER_TIME, selection)));
			this.redrawProgress();
		}
	}
	
	private void initToolBar(){
		if( this.toolBar != null && !this.toolBar.isDisposed() ){
			this.toolBar.dispose();
		}
		this.toolBar = getUIFactory().createHorizontalToolBar(this.dialog);
		
		this.first = this.toolBar.createActionItem();
		this.first.addSelectionListener(new TGActionProcessorListener(this.context, TGGoFirstMeasureAction.NAME));
		
		this.previous = this.toolBar.createActionItem();
		this.previous.addSelectionListener(new TGActionProcessorListener(this.context, TGGoPreviousMeasureAction.NAME));
		
		this.stop = this.toolBar.createActionItem();
		this.stop.addSelectionListener(new TGActionProcessorListener(this.context, TGTransportStopAction.NAME));
		
		this.play = this.toolBar.createActionItem();
		this.play.addSelectionListener(new TGActionProcessorListener(this.context, TGTransportPlayAction.NAME));
		
		this.next = this.toolBar.createActionItem();
		this.next.addSelectionListener(new TGActionProcessorListener(this.context, TGGoNextMeasureAction.NAME));
		
		this.last = this.toolBar.createActionItem();
		this.last.addSelectionListener(new TGActionProcessorListener(this.context, TGGoLastMeasureAction.NAME));
		
		UITableLayout uiLayout = (UITableLayout) this.dialog.getLayout();
		uiLayout.set(this.toolBar, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.updateItems(true);
		this.loadProperties();
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void updateItems(){
		this.updateItems(false);
	}
	
	public void updateItems(boolean force){
		if(!isDisposed()){
			int lastStatus = getStatus();
			
			MidiPlayer player = MidiPlayer.getInstance(this.context);
			if( player.isRunning()){
				setStatus(STATUS_RUNNING);
			}else if( player.isPaused()){
				setStatus(STATUS_PAUSED);
			}else{
				setStatus(STATUS_STOPPED);
			}
			
			if( force || lastStatus != getStatus()){
				if(getStatus() == STATUS_RUNNING){
					this.first.setImage(TuxGuitar.getInstance().getIconManager().getTransportFirst2());
					this.last.setImage(TuxGuitar.getInstance().getIconManager().getTransportLast2());
					this.previous.setImage(TuxGuitar.getInstance().getIconManager().getTransportPrevious2());
					this.next.setImage(TuxGuitar.getInstance().getIconManager().getTransportNext2());
					this.stop.setImage(TuxGuitar.getInstance().getIconManager().getTransportStop2());
					this.play.setImage(TuxGuitar.getInstance().getIconManager().getTransportPause());
				}else if(getStatus() == STATUS_PAUSED){
					this.first.setImage(TuxGuitar.getInstance().getIconManager().getTransportFirst2());
					this.last.setImage(TuxGuitar.getInstance().getIconManager().getTransportLast2());
					this.previous.setImage(TuxGuitar.getInstance().getIconManager().getTransportPrevious2());
					this.next.setImage(TuxGuitar.getInstance().getIconManager().getTransportNext2());
					this.stop.setImage(TuxGuitar.getInstance().getIconManager().getTransportStop2());
					this.play.setImage(TuxGuitar.getInstance().getIconManager().getTransportPlay2());
				}else if(getStatus() == STATUS_STOPPED){
					this.first.setImage(TuxGuitar.getInstance().getIconManager().getTransportFirst1());
					this.last.setImage(TuxGuitar.getInstance().getIconManager().getTransportLast1());
					this.previous.setImage(TuxGuitar.getInstance().getIconManager().getTransportPrevious1());
					this.next.setImage(TuxGuitar.getInstance().getIconManager().getTransportNext1());
					this.stop.setImage(TuxGuitar.getInstance().getIconManager().getTransportStop1());
					this.play.setImage(TuxGuitar.getInstance().getIconManager().getTransportPlay1());
				}
				this.loadPlayText();
			}
			TGMeasureHeader first = getSongManager().getFirstMeasureHeader(getDocumentManager().getSong());
			TGMeasureHeader last = getSongManager().getLastMeasureHeader(getDocumentManager().getSong());
			this.tickProgress.setMinimum((int)first.getStart());
			this.tickProgress.setMaximum((int)(last.getStart() + last.getLength()) -1);
			this.metronome.setSelected(player.isMetronomeEnabled());
			
			this.redrawProgress();
		}
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			this.dialog.setText(TuxGuitar.getProperty("transport"));
			this.stop.setToolTipText(TuxGuitar.getProperty("transport.stop"));
			this.first.setToolTipText(TuxGuitar.getProperty("transport.first"));
			this.last.setToolTipText(TuxGuitar.getProperty("transport.last"));
			this.previous.setToolTipText(TuxGuitar.getProperty("transport.previous"));
			this.next.setToolTipText(TuxGuitar.getProperty("transport.next"));
			this.metronome.setToolTipText(TuxGuitar.getProperty("transport.metronome"));
			this.mode.setToolTipText(TuxGuitar.getProperty("transport.mode"));
			this.loadPlayText();
		}
	}
	
	public void loadPlayText(){
		String property = TuxGuitar.getProperty( (getStatus() == STATUS_RUNNING ? "transport.pause" : "transport.start") );
		this.play.setToolTipText(property);
	}
	
	public void loadIcons(){
		if(!isDisposed()){
			this.initToolBar();
			this.loadOptionIcons();
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			this.dialog.layout();
		}
	}
	
	private void loadOptionIcons(){
		this.metronome.setImage(TuxGuitar.getInstance().getIconManager().getTransportMetronome());
		this.mode.setImage(TuxGuitar.getInstance().getIconManager().getTransportMode());
	}
	
	public void dispose() {
		if(!isDisposed()){
			this.dialog.dispose();
		}
	}
	
	public boolean isDisposed() {
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public boolean isEditingTickScale() {
		return this.editingTickScale;
	}
	
	public void setEditingTickScale(boolean editingTickScale) {
		this.editingTickScale = editingTickScale;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	public TGDocumentManager getDocumentManager(){
		return TGDocumentManager.getInstance(this.context);
	}
	
	public TGSongManager getSongManager(){
		return getDocumentManager().getSongManager();
	}
	
	public void gotoMeasure(TGMeasureHeader header, boolean moveCaret){
		TGTransport.getInstance(this.context).gotoMeasure(header, moveCaret);
	}
	
	public void updateTickLabel(String value) {
		String oldValue = this.label.getText();
		
		this.label.setText(value);
		
		if( oldValue == null || oldValue.length() != value.length() ) {
			UIPanel uiPanel = (UIPanel) this.label.getParent();
			uiPanel.layout();
		}
	}
	
	public void redrawProgress(){
		if(!isDisposed() && !TuxGuitar.getInstance().isLocked()){
			if( isEditingTickScale() ){
				TGTransportDialog.this.updateTickLabel(Long.toString(TGTransportDialog.this.tickProgress.getValue()));
			}
			else if(!MidiPlayer.getInstance(this.context).isRunning()){
				long tickPosition = TablatureEditor.getInstance(this.context).getTablature().getCaret().getPosition();
				
				TGTransportDialog.this.updateTickLabel(Long.toString(tickPosition));
				TGTransportDialog.this.tickProgress.setValue((int)tickPosition);
			}
		}
	}
	
	public void redrawPlayingMode(){
		if(!isDisposed()){
			MidiPlayer player = MidiPlayer.getInstance(this.context);
			if(!isEditingTickScale() && player.isRunning()){
				EditorCache editorCache = TuxGuitar.getInstance().getEditorCache();
				
				long time = System.currentTimeMillis();
				if( time > this.redrawTime + PLAY_MODE_DELAY ){
					long position = (editorCache.getPlayStart() + (player.getTickPosition() - editorCache.getPlayTick()));
					this.updateTickLabel(Long.toString(position));
					this.tickProgress.setValue((int)position);
					this.redrawTime = time;
				}
			}
		}
	}
	
	public void createSyncProcesses() {
		this.loadPropertiesProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});
		
		this.loadIconsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});
		
		this.updateItemsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateItems();
			}
		});
		
		this.redrawPlayModeProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				redrawPlayingMode();
			}
		});
	}
	
	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.PLAYING_THREAD || type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayModeProcess.process();
		}
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateItemsProcess.process();
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
		else if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processRedrawEvent(event);
		}
		else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
	
	public static TGTransportDialog getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGTransportDialog.class.getName(), new TGSingletonFactory<TGTransportDialog>() {
			public TGTransportDialog createInstance(TGContext context) {
				return new TGTransportDialog(context);
			}
		});
	}
}
