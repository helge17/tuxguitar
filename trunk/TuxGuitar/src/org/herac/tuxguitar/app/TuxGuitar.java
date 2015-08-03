package org.herac.tuxguitar.app;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.TGActionAdapterManager;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.file.TGLoadTemplateAction;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import org.herac.tuxguitar.app.action.impl.system.TGDisposeAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import org.herac.tuxguitar.app.editor.EditorCache;
import org.herac.tuxguitar.app.editor.TGEditorManager;
import org.herac.tuxguitar.app.helper.FileHistory;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.system.error.TGErrorAdapter;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.app.system.language.TGLanguageManager;
import org.herac.tuxguitar.app.system.properties.TGPropertiesAdapter;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.app.tools.template.TGTemplateManager;
import org.herac.tuxguitar.app.transport.TGTransportListener;
import org.herac.tuxguitar.app.util.ArgumentParser;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.app.util.TGSplash;
import org.herac.tuxguitar.app.util.WindowTitleUtil;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.app.view.dialog.browser.main.TGBrowserDialog;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import org.herac.tuxguitar.app.view.dialog.chord.TGCustomChordManager;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.dialog.lyric.TGLyricEditor;
import org.herac.tuxguitar.app.view.dialog.matrix.TGMatrixEditor;
import org.herac.tuxguitar.app.view.dialog.piano.TGPianoEditor;
import org.herac.tuxguitar.app.view.items.TGMenuManager;
import org.herac.tuxguitar.app.view.toolbar.TGToolBar;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.impl.sequencer.MidiSequencerProviderImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGLock;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.TGSynchronizer.TGSynchronizerController;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.plugin.TGPluginManager;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;

public class TuxGuitar {
	
	public static final String APPLICATION_NAME = "TuxGuitar";
	
	public static final int MARGIN_WIDTH = 5;
	
	private static TuxGuitar instance;
	
	private boolean initialized;
	
	private TGLock lock;
	
	private Display display;
	
	private Shell shell;
	
	private TGContext context;
	
	private TGLanguageManager languageManager;
	
	private KeyBindingActionManager keyBindingManager;
	
	private TGIconManager iconManager;
	
	private EditorCache editorCache;
	
	private TGMenuManager itemManager;
	
	private TGCustomChordManager customChordManager;
	
	private FileHistory fileHistory;
	
	protected Sash sash;
	
	protected Composite sashComposite;
	
	public TuxGuitar() {
		this.lock = new TGLock();
		this.context = new TGContext();
		this.initialized = false;
	}
	
	public static TuxGuitar getInstance() {
		if (instance == null) {
			synchronized (TuxGuitar.class) {
				instance = new TuxGuitar();
			}
		}
		return instance;
	}
	
	private void initSynchronizer(){
		TGSynchronizer.getInstance(this.context).setController(new TGSynchronizerController() {
			public void executeLater(final Runnable runnable) {
				new Thread(new Runnable() {
					public void run() {
						final Display display = getDisplay();
						if( display != null && !display.isDisposed()){
							display.asyncExec(new Runnable() {
								public void run() throws TGException {
									try {
										if(!isDisposed()) {
											runnable.run();
										}
									} catch (Throwable throwable) {
										throwable.printStackTrace();
									}
								}
							});
						}
					}
				}).start();
			}
		});
	}
	
	public void displayGUI(String[] args) {
		//checkeo los argumentos
		ArgumentParser argumentParser = new ArgumentParser(args);
		if(argumentParser.processAndExit()){
			return;
		}
		
		// Priority 1 ----------------------------------------------//
		TGFileUtils.loadLibraries(this.context);
		TGFileUtils.loadClasspath();
		TGErrorAdapter.initialize(this.context);
		TGPropertiesAdapter.initialize(this.context);
		
		// Priority 2 ----------------------------------------------//
		Display.setAppName(APPLICATION_NAME);
		
		this.display = new Display();
		this.initSynchronizer();
		
		TGSplash.instance().init();
		
		this.shell = new Shell(getDisplay());
		this.shell.setLayout(getShellLayout());
		this.shell.setImage(getIconManager().getAppIcon());
		
		this.createComposites(getShell());
		
		// Priority 3 ----------------------------------------------//
		this.initMidiPlayer();
		this.getEditorManager().setLockControl(this.lock);
		this.getActionAdapterManager().initialize();
		this.getPluginManager().openPlugins();
		this.restoreControlsConfig();
		this.restorePlayerConfig();
//		this.updateCache(true);
//		this.showTitle();
		
		TGSplash.instance().finish();
		
		// Priority 4 ----------------------------------------------//
		this.shell.addShellListener(new TGActionProcessorListener(this.context, TGDisposeAction.NAME));
		this.shell.open();
		this.startSong(argumentParser);
		this.setInitialized( true );
		
		while (!getDisplay().isDisposed() && !getShell().isDisposed()) {
			if (!getDisplay().readAndDispatch()) {
				getDisplay().sleep();
			}
		}
		getDisplay().dispose();
	}
	
	private FormLayout getShellLayout(){
		FormLayout layout = new FormLayout();
		layout.marginWidth = MARGIN_WIDTH;
		layout.marginHeight = MARGIN_WIDTH;
		return layout;
	}
	
	private void startSong(final ArgumentParser parser){
		final URL url = parser.getURL();
		if( url != null ){
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGReadURLAction.NAME);
			tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, url);
			tgActionProcessor.process();
		}else{
			new TGActionProcessor(this.context, TGLoadTemplateAction.NAME).process();			
		}
	}
	
	public void createComposites(Composite composite) {
		TGToolBar.getInstance(this.getContext()).createToolBar(getShell());
		
		FormData data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(TGToolBar.getInstance(this.getContext()).getControl(), MARGIN_WIDTH);
		data.bottom = new FormAttachment(100,0);
		this.sashComposite = new Composite(composite,SWT.NONE);
		this.sashComposite.setLayout(new FormLayout());
		this.sashComposite.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.bottom = new FormAttachment(100,-150);
		data.height = MARGIN_WIDTH;
		this.sash = new Sash(this.sashComposite, SWT.HORIZONTAL);
		this.sash.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(0,0);
		data.bottom = new FormAttachment(this.sash, 0);
		getTablatureEditor().showTablature(this.sashComposite);
		getTablatureEditor().getTablature().setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(this.sash,0);
		data.bottom = new FormAttachment(100,0);
		getTable().init(this.sashComposite);
		getTable().getComposite().setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(this.sashComposite,0);
		data.bottom = new FormAttachment(100,0);
		
		Composite footer = new Composite(composite,SWT.NONE);
		footer.setLayout(new FormLayout());
		footer.setLayoutData(data);
		getFretBoardEditor().showFretBoard(footer);
		
		this.sash.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				TuxGuitar.this.sashComposite.layout(true,true);
			}
		});
		this.sash.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int maximumHeight = (TuxGuitar.this.sashComposite.getBounds().height - TuxGuitar.this.sash.getBounds().height);
				int height = (maximumHeight - event.y);
				height = Math.max(height,0);
				height = Math.min(height,maximumHeight);
				((FormData) TuxGuitar.this.sash.getLayoutData()).bottom = new FormAttachment(100, -height);
			}
		});
		this.sash.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent e) {
				TuxGuitar.this.sash.setCursor( getDisplay().getSystemCursor( SWT.CURSOR_SIZENS ) );
			}
		});
		this.sashComposite.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event arg0) {
				FormData data = ((FormData) TuxGuitar.this.sash.getLayoutData());
				int height = -data.bottom.offset;
				int maximumHeight = (TuxGuitar.this.sashComposite.getBounds().height - TuxGuitar.this.sash.getBounds().height);
				if(height > maximumHeight){
					data.bottom = new FormAttachment(100, -maximumHeight);
				}
			}
		});
	}
	
	public void restoreControlsConfig(){
		final TGConfigManager config = getConfig();
		
		//---Main Shell---
		boolean maximized = config.getBooleanValue(TGConfigKeys.MAXIMIZED);
		getShell().setMaximized(maximized);
		if(!maximized){
			int width = config.getIntegerValue(TGConfigKeys.WIDTH);
			int height = config.getIntegerValue(TGConfigKeys.HEIGHT);
			if(width > 0 && height > 0){
				getShell().setSize(width,height);
			}
		}
		getShell().setMinimumSize(640,480);
		//---Fretboard---
		if(config.getBooleanValue(TGConfigKeys.SHOW_FRETBOARD)){
			getFretBoardEditor().showFretBoard();
		}else{
			getFretBoardEditor().hideFretBoard();
		}
		//---Instruments---
		if( config.getBooleanValue(TGConfigKeys.SHOW_INSTRUMENTS) ){
			new TGActionProcessor(this.context, TGToggleChannelsDialogAction.NAME).process();
		}
		//---Transport---
		if(config.getBooleanValue(TGConfigKeys.SHOW_TRANSPORT)){
			new TGActionProcessor(this.context, TGToggleTransportDialogAction.NAME).process();
		}
		//---Matrix---
		if(config.getBooleanValue(TGConfigKeys.SHOW_MATRIX)){
			new TGActionProcessor(this.context, TGToggleMatrixEditorAction.NAME).process();
		}
		//---Piano---
		if(config.getBooleanValue(TGConfigKeys.SHOW_PIANO)){
			new TGActionProcessor(this.context, TGTogglePianoEditorAction.NAME).process();
		}
		//---Markers---
		if(config.getBooleanValue(TGConfigKeys.SHOW_MARKERS)){
			new TGActionProcessor(this.context, TGToggleMarkerListAction.NAME).process();
		}
	}
	
	public void setTableHeight(int value){
		int offset = ((FormData) getTable().getComposite().getLayoutData()).top.offset;
		int sashHeight = this.sash.getBounds().height;
		int maximumHeight = (this.sashComposite.getBounds().height - sashHeight);
		int height = (value + offset);
		height = Math.max( height,0);
		height = Math.min( height,maximumHeight);
		((FormData) TuxGuitar.this.sash.getLayoutData()).bottom = new FormAttachment(100, -height);
		this.sashComposite.layout(true,true);
	}
	
	public void updateShellFooter(int offset,int minimumWith,int minimumHeight){
		FormData data = ((FormData)this.sashComposite.getLayoutData());
		data.bottom.offset = -offset;
		getShell().setMinimumSize(Math.max(640,minimumWith),Math.max(480,minimumHeight));
		getShell().layout(true,true);
		getShell().redraw();
	}
	
	public TGTableViewer getTable(){
		return TGTableViewer.getInstance(this.context);
	}
	
	public TablatureEditor getTablatureEditor(){
		return TablatureEditor.getInstance(this.context);
	}
	
	public TGFretBoardEditor getFretBoardEditor(){
		return TGFretBoardEditor.getInstance(this.context);
	}
	
	public TGPianoEditor getPianoEditor(){
		return TGPianoEditor.getInstance(this.context);
	}
	
	public TGMatrixEditor getMatrixEditor(){
		return TGMatrixEditor.getInstance(this.context);
	}
	
	public TGChannelManagerDialog getChannelManager(){
		return TGChannelManagerDialog.getInstance(this.context);
	}
	
	public EditorCache getEditorCache(){
		if( this.editorCache == null ){
			this.editorCache = new EditorCache();
		}
		return this.editorCache;
	}
	
	public TGEditorManager getEditorManager(){
		return TGEditorManager.getInstance(this.context);
	}
	
	public TGLyricEditor getLyricEditor(){
		return TGLyricEditor.getInstance(this.context);
	}
	
	public TGBrowserDialog getBrowser(){
		return TGBrowserDialog.getInstance(this.context);
	}
	
	public TGUndoableManager getUndoableManager(){
		return TGUndoableManager.getInstance(this.context);
	}
	
	public ScaleManager getScaleManager(){
		return ScaleManager.getInstance(this.context);
	}
	
	public TGSongManager getSongManager(){
		return getDocumentManager().getSongManager();
	}
	
	public TGDocumentManager getDocumentManager(){
		return TGDocumentManager.getInstance(this.context);
	}
	
	public TGPluginManager getPluginManager(){
		return TGPluginManager.getInstance(this.context);
	}
	
	public TGErrorManager getErrorManager(){
		return TGErrorManager.getInstance(this.context);
	}
	
	public TGEventManager getEventManager(){
		return TGEventManager.getInstance(this.context);
	}
	
	public TGPropertiesManager getPropertiesManager(){
		return TGPropertiesManager.getInstance(this.context);
	}
	
	public TGConfigManager getConfig(){
		return TGConfigManager.getInstance(this.context);
	}
	
	public TGFileFormatManager getFileFormatManager(){
		return TGFileFormatManager.getInstance(this.context);
	}
	
	public TGIconManager getIconManager(){
		if( this.iconManager == null ){
			this.iconManager = new TGIconManager(this.context);
			this.iconManager.addLoader(new TGEventListener() {
				public void processEvent(TGEvent event) {
					getShell().setImage(getIconManager().getAppIcon());
					getShell().layout(true);
				}
			});
		}
		return this.iconManager;
	}
	
	public TGCustomChordManager getCustomChordManager(){
		if( this.customChordManager == null ){
			this.customChordManager = new TGCustomChordManager();
		}
		return this.customChordManager;
	}
	
	public TGMenuManager getItemManager() {
		if( this.itemManager == null ){
			this.itemManager = new TGMenuManager(this.context);
		}
		return this.itemManager;
	}
	
	public TGActionAdapterManager getActionAdapterManager() {
		return TGActionAdapterManager.getInstance(this.context);
	}
	
	public TGActionManager getActionManager(){
		return TGActionManager.getInstance(this.context);
	}
	
	public TGLanguageManager getLanguageManager() {
		if( this.languageManager == null ){
			this.languageManager = new TGLanguageManager(this.context);
			this.languageManager.setLanguage(getConfig().getStringValue(TGConfigKeys.LANGUAGE));
		}
		return this.languageManager;
	}

	public KeyBindingActionManager getKeyBindingManager(){
		if( this.keyBindingManager == null ){
			this.keyBindingManager = new KeyBindingActionManager();
		}
		return this.keyBindingManager;
	}
	
	public FileHistory getFileHistory(){
		if( this.fileHistory == null ){
			this.fileHistory = new FileHistory();
		}
		return this.fileHistory;
	}
	
	public TGTemplateManager getTemplateManager(){
		return TGTemplateManager.getInstance(this.context);
	}
	
	public MidiPlayer getPlayer(){
		return MidiPlayer.getInstance(this.context);
	}
	
	public void initMidiPlayer(){
		MidiPlayer midiPlayer = MidiPlayer.getInstance(this.context);
		midiPlayer.init(getDocumentManager());
		midiPlayer.addListener( new TGTransportListener(this.context) );
		try {
			getPlayer().addSequencerProvider(new MidiSequencerProviderImpl(), false);
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void restorePlayerConfig(){
		//try to open first device when the configured port not found.
		getPlayer().setTryOpenFistDevice( true );
		
		//check midi sequencer
		getPlayer().openSequencer(getConfig().getStringValue(TGConfigKeys.MIDI_SEQUENCER), true);
		
		//check midi port
		getPlayer().openOutputPort(getConfig().getStringValue(TGConfigKeys.MIDI_PORT), true);
	}
	
	public void showTitle(){
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() throws TGException {
				if(!isDisposed()){
					getShell().setText(WindowTitleUtil.parseTitle());
				}
			}
		});
	}
	
	public void updateCache(final boolean updateItems){
		this.updateCache(updateItems, null);
	}
	
	public void updateCache(final boolean updateItems, final TGAbstractContext sourceContext){
		this.lock();
		this.getEditorCache().updateEditMode();
		this.unlock();
		
		if( updateItems ){
			getEditorManager().updateSelection(sourceContext);
		}
		getEditorManager().redraw(sourceContext);
	}
	
	public void redrawPlayingMode(){
		if(!isDisposed() && !this.isLocked()){
			this.lock();
			this.getEditorCache().updatePlayMode();
			
			if( this.getEditorCache().shouldRedraw() ) {
				this.getEditorManager().redrawPlayingNewBeat();
			} else {
				this.getEditorManager().redrawPlayingThread();
			}
			
			this.unlock();
		}
	}
	
	public Display getDisplay(){
		return this.display;
	}
	
	public Shell getShell(){
		return this.shell;
	}
	
	public static String getProperty(String key) {
		return TuxGuitar.getInstance().getLanguageManager().getProperty(key);
	}
	
	public static String getProperty(String key,String[] arguments) {
		return  TuxGuitar.getInstance().getLanguageManager().getProperty(key,arguments);
	}
	
	public static boolean isDisposed(){
		return (TuxGuitar.getInstance().getDisplay().isDisposed() || TuxGuitar.getInstance().getShell().isDisposed());
	}
	
	public void updateSong(){
		this.lock();
		this.getEditorCache().reset();
		this.getEditorManager().updateSong();
		this.unlock();
	}
	
	public void loadCursor(int style){
		this.loadCursor(getShell(),style);
	}
	
	public void loadCursor(final Control control,final int style){
		try {
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() throws TGException {
					if(!control.isDisposed()){
						control.setCursor(getDisplay().getSystemCursor(style));
					}
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void playBeat( final TGBeat beat ){
		new Thread(new Runnable() {
			public void run() throws TGException {
				if(!isDisposed() && !getPlayer().isRunning() ){
					getPlayer().playBeat(beat);
				}
			}
		}).start();
	}
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public TGContext getContext() {
		return context;
	}

	public void lock(){
		this.lock.lock();
	}
	
	public void unlock(){
		this.lock.unlock(false);
	}
	
	public boolean isLocked(){
		return this.lock.isLocked();
	}
}