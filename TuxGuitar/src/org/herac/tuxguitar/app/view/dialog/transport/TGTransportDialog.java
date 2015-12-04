package org.herac.tuxguitar.app.view.dialog.transport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
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
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
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
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTransportDialog implements TGEventListener {
	
	private static final int PLAY_MODE_DELAY = 250;
	
	public static final int STATUS_STOPPED = 1;
	public static final int STATUS_PAUSED = 2;
	public static final int STATUS_RUNNING = 3;
	
	private TGContext context;
	private Shell dialog;
	private Label label;
	private ProgressBar tickProgress;
	private Button metronome;
	private Button mode;
	private ToolBar toolBar;
	private ToolItem first;
	private ToolItem last;
	private ToolItem previous;
	private ToolItem next;
	private ToolItem stop;
	private ToolItem play;
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
		this.dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM);
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		this.dialog.setLayout(new GridLayout());
		this.dialog.setText(TuxGuitar.getProperty("transport"));
		this.initComposites();
		this.initToolBar();
		this.redrawProgress();
		
		this.addListeners();
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
				TuxGuitar.getInstance().updateCache(true);
			}
		});
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
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
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		
		Composite composite = new Composite(this.dialog,SWT.BORDER);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		initOptions(composite);
		initProgress(composite);
	}
	
	private void initOptions(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true));
		
		this.metronome = new Button(composite,SWT.TOGGLE);
		this.metronome.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.metronome.addSelectionListener(new TGActionProcessorListener(this.context , TGTransportMetronomeAction.NAME));
		
		this.mode = new Button(composite,SWT.PUSH);
		this.mode.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.mode.addSelectionListener(new TGActionProcessorListener(this.context , TGOpenTransportModeDialogAction.NAME));
		
		this.loadOptionIcons();
	}
	
	private void initProgress(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		initLabel(composite);
		initScale(composite);
	}
	
	private void initLabel(Composite parent){
		final Font font = new Font(parent.getDisplay(),"Minisystem",36,SWT.NORMAL);
		this.label = new Label(parent,SWT.RIGHT);
		this.label.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		this.label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		this.label.setFont(font);
		this.label.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				font.dispose();
			}
		});
	}
	
	private void initScale(Composite parent){
		GridData data = new GridData(SWT.FILL,SWT.CENTER,true,false);
		data.heightHint = 10;
		
		this.tickProgress = new ProgressBar(parent, SWT.BORDER | SWT.HORIZONTAL | SWT.SMOOTH);
		this.tickProgress.setCursor(this.tickProgress.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		this.tickProgress.setLayoutData(data);
		this.tickProgress.setSelection((int)TGDuration.QUARTER_TIME);
		this.tickProgress.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				setEditingTickScale(true);
				updateProgressBar(e.x);
			}
			public void mouseUp(MouseEvent e) {
				gotoMeasure(getSongManager().getMeasureHeaderAt(getDocumentManager().getSong(), TGTransportDialog.this.tickProgress.getSelection()),true);
				setEditingTickScale(false);
			}
		});
		this.tickProgress.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				updateProgressBar(e.x);
			}
		});
	}
	
	protected void updateProgressBar(int x){
		if(isEditingTickScale()){
			int selection = (this.tickProgress.getMinimum() + (( x * (this.tickProgress.getMaximum() - this.tickProgress.getMinimum())) / this.tickProgress.getSize().x) );
			this.tickProgress.setSelection(Math.max((int)TGDuration.QUARTER_TIME,selection));
			this.redrawProgress();
		}
	}
	
	private void initToolBar(){
		if( this.toolBar != null){
			this.toolBar.dispose();
		}
		this.toolBar = new ToolBar(this.dialog,SWT.FLAT);
		
		this.first = new ToolItem(this.toolBar,SWT.PUSH);
		this.first.addSelectionListener(new TGActionProcessorListener(this.context, TGGoFirstMeasureAction.NAME));
		
		this.previous = new ToolItem(this.toolBar,SWT.PUSH);
		this.previous.addSelectionListener(new TGActionProcessorListener(this.context, TGGoPreviousMeasureAction.NAME));
		
		this.stop = new ToolItem(this.toolBar,SWT.PUSH);
		this.stop.addSelectionListener(new TGActionProcessorListener(this.context, TGTransportStopAction.NAME));
		
		this.play = new ToolItem(this.toolBar,SWT.PUSH);
		this.play.addSelectionListener(new TGActionProcessorListener(this.context, TGTransportPlayAction.NAME));
		
		this.next = new ToolItem(this.toolBar,SWT.PUSH);
		this.next.addSelectionListener(new TGActionProcessorListener(this.context, TGGoNextMeasureAction.NAME));
		
		this.last = new ToolItem(this.toolBar,SWT.PUSH);
		this.last.addSelectionListener(new TGActionProcessorListener(this.context, TGGoLastMeasureAction.NAME));
		
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
			
			if(TuxGuitar.getInstance().getPlayer().isRunning()){
				setStatus(STATUS_RUNNING);
			}else if(TuxGuitar.getInstance().getPlayer().isPaused()){
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
			this.metronome.setSelection(TuxGuitar.getInstance().getPlayer().isMetronomeEnabled());
			
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
			this.dialog.layout(true);
			this.dialog.pack(true);
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
	
	protected TGSongManager getSongManager(){
		return TuxGuitar.getInstance().getSongManager();
	}
	
	protected TGDocumentManager getDocumentManager(){
		return TuxGuitar.getInstance().getDocumentManager();
	}
	
	public void gotoMeasure(TGMeasureHeader header, boolean moveCaret){
		TGTransport.getInstance(this.context).gotoMeasure(header, moveCaret);
	}
	
	public void redrawProgress(){
		if(!isDisposed() && !TuxGuitar.getInstance().isLocked()){
			if( isEditingTickScale() ){
				TGTransportDialog.this.label.setText(Long.toString(TGTransportDialog.this.tickProgress.getSelection()));
			}
			else if(!MidiPlayer.getInstance(this.context).isRunning()){
				long tickPosition = TablatureEditor.getInstance(this.context).getTablature().getCaret().getPosition();
				
				TGTransportDialog.this.label.setText(Long.toString(tickPosition));
				TGTransportDialog.this.tickProgress.setSelection((int)tickPosition);
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
					this.label.setText(Long.toString(position));
					this.tickProgress.setSelection((int)position);
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
