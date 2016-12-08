package org.herac.tuxguitar.app;

import java.net.URL;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.TGActionAdapterManager;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.editor.EditorCache;
import org.herac.tuxguitar.app.synchronizer.TGSynchronizerControllerImpl;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.system.error.TGErrorAdapter;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.app.system.language.TGLanguageManager;
import org.herac.tuxguitar.app.system.properties.TGPropertiesAdapter;
import org.herac.tuxguitar.app.system.variables.TGVarAdapter;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.app.transport.TGTransportListener;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGClassLoader;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.app.util.TGSplash;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.app.view.dialog.browser.main.TGBrowserDialog;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import org.herac.tuxguitar.app.view.dialog.chord.TGCustomChordManager;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.dialog.lyric.TGLyricEditor;
import org.herac.tuxguitar.app.view.dialog.matrix.TGMatrixEditor;
import org.herac.tuxguitar.app.view.dialog.piano.TGPianoEditor;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.menu.TGMenuManager;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.impl.sequencer.MidiSequencerProviderImpl;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGLock;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.error.TGErrorHandler;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.plugin.TGPluginManager;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;

public class TuxGuitar {
	
	private static TuxGuitar instance;
	
	private boolean initialized;
	
	private TGLock lock;
	
	private TGContext context;
	
	private TGLanguageManager languageManager;
	
	private EditorCache editorCache;
	
	private TGMenuManager itemManager;
	
	private TGCustomChordManager customChordManager;
	
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
	
	public void createApplication(final URL url) {
		this.createMainContext();
		this.createUIContext(url);
	}
	
	private void createMainContext() {
		TGResourceManager.getInstance(this.context).setResourceLoader(TGClassLoader.getInstance(this.context));
		TGFileUtils.loadLibraries(this.context);
		TGFileUtils.loadClasspath(this.context);
		TGErrorAdapter.initialize(this.context);
		TGPropertiesAdapter.initialize(this.context);
		TGVarAdapter.initialize(this.context);
	}
	
	private void createUIContext(final URL url) {
		TGSynchronizer.getInstance(this.context).setController(new TGSynchronizerControllerImpl(this.context));
		TGApplication.getInstance(TuxGuitar.this.context).getApplication().start(new Runnable() {
			public void run() {
				// display splash screen
				TGSplash.getInstance(TuxGuitar.this.context).init();
				
				TGSynchronizer.getInstance(TuxGuitar.this.context).executeLater(new Runnable() {
					public void run() {
						TuxGuitar.this.startUIContext(url);
						
						// close splash screen
						TGSplash.getInstance(TuxGuitar.this.context).finish();
					}
				});
			}
		});
	}
	
	private void startUIContext(URL url) {		
		TGWindow.getInstance(TuxGuitar.this.context).createWindow();
		
		// Priority 3 ----------------------------------------------//
		this.initMidiPlayer();
		this.getEditorManager().setLockControl(TuxGuitar.this.lock);
		this.getActionAdapterManager().initialize();
		this.getPluginManager().connectEnabled();
		this.restoreControlsConfig();
		this.restorePlayerConfig();
		
		// Priority 4 ----------------------------------------------//
		TGWindow.getInstance(TuxGuitar.this.context).open();
		
		this.startSong(url);
		this.setInitialized(true);
	}
	
	private void startSong(URL url){
		TGDocumentListManager.getInstance(this.context).findCurrentDocument().setUnwanted(true);
		if( url != null ){
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGReadURLAction.NAME);
			tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, url);
			tgActionProcessor.setAttribute(TGErrorHandler.class.getName(), new TGErrorHandler() {
				public void handleError(Throwable throwable) {
					startDefaultSong();
					
					TGErrorManager.getInstance(getContext()).handleError(throwable);
				}
			});
			tgActionProcessor.process();
		} else {
			this.startDefaultSong();
		}
	}
	
	private void startDefaultSong() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGLoadTemplateAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentListAttributes.ATTRIBUTE_UNWANTED, true);
		tgActionProcessor.process();
	}
	
	public void restoreControlsConfig(){
		TGConfigManager config = getConfig();
		
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
		return TGIconManager.getInstance(this.context);
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
		return KeyBindingActionManager.getInstance(this.context);
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
	
	public static String getProperty(String key) {
		return TuxGuitar.getInstance().getLanguageManager().getProperty(key);
	}
	
	public static String getProperty(String key,String[] arguments) {
		return  TuxGuitar.getInstance().getLanguageManager().getProperty(key,arguments);
	}
	
	public boolean isDisposed(){
		return (TGApplication.getInstance(this.context).isDisposed() || TGWindow.getInstance(this.context).isDisposed());
	}
	
	public void updateSong(){
		this.lock();
		this.getEditorCache().reset();
		this.getEditorManager().updateSong();
		this.unlock();
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
		this.lock.unlock();
	}
	
	public boolean isLocked(){
		return this.lock.isLocked();
	}
}