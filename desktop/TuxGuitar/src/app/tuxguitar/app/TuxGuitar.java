package app.tuxguitar.app;

import java.net.URL;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.TGActionAdapterManager;
import app.tuxguitar.app.action.impl.file.TGReadURLAction;
import app.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import app.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import app.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import app.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import app.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import app.tuxguitar.app.document.TGDocumentListAttributes;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.app.synchronizer.TGSynchronizerControllerImpl;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.system.error.TGErrorAdapter;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.system.icons.TGSkinManager;
import app.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import app.tuxguitar.app.system.language.TGLanguageManager;
import app.tuxguitar.app.system.properties.TGPropertiesAdapter;
import app.tuxguitar.app.system.variables.TGVarAdapter;
import app.tuxguitar.app.tools.scale.ScaleManager;
import app.tuxguitar.app.transport.TGTransportListener;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.util.TGClassLoader;
import app.tuxguitar.app.util.TGFileUtils;
import app.tuxguitar.app.util.TGSplash;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.table.TGTableViewer;
import app.tuxguitar.app.view.dialog.browser.main.TGBrowserDialog;
import app.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import app.tuxguitar.app.view.dialog.chord.TGCustomChordManager;
import app.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import app.tuxguitar.app.view.dialog.lyric.TGLyricEditor;
import app.tuxguitar.app.view.dialog.matrix.TGMatrixEditor;
import app.tuxguitar.app.view.dialog.piano.TGPianoEditor;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.menu.TGMenuManager;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.impl.sequencer.MidiSequencerProviderImpl;
import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.song.helpers.tuning.TuningManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.thread.TGMultiThreadHandler;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.ui.UIApplication;
import app.tuxguitar.util.TGAbstractContext;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGLock;
import app.tuxguitar.util.TGMessagesManager;
import app.tuxguitar.util.TGSynchronizer;
import app.tuxguitar.util.error.TGErrorHandler;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.plugin.TGPluginManager;
import app.tuxguitar.util.properties.TGPropertiesManager;

public class TuxGuitar {

	private static TuxGuitar instance;

	private boolean initialized;

	private TGLock lock;

	private TGContext context;

	private TGLanguageManager languageManager;

	private TGMenuManager itemManager;

	private TGCustomChordManager customChordManager;

	public TuxGuitar() {
		this.context = new TGContext();
		this.lock = new TGLock(this.context);
		this.initialized = false;
	}

	public static TuxGuitar getInstance() {
		synchronized (TuxGuitar.class) {
			if (instance == null) {
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
		TGThreadManager.getInstance(this.context).setThreadHandler(new TGMultiThreadHandler());
		TGResourceManager.getInstance(this.context).setResourceLoader(TGClassLoader.getInstance(this.context));
		TGFileUtils.loadLibraries(this.context);
		TGFileUtils.loadClasspath(this.context);
		TGErrorAdapter.initialize(this.context);
		TGPropertiesAdapter.initialize(this.context);
		TGVarAdapter.initialize(this.context);
	}

	private void createUIContext(final URL url) {
		TGSynchronizer.getInstance(this.context).setController(new TGSynchronizerControllerImpl(this.context));

		UIApplication app = TGApplication.getInstance(TuxGuitar.this.context).getApplication();
		getPluginManager().earlyInitPlugins();

		app.start(new Runnable() {
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

	public TuningManager getTuningManager(){
		return TuningManager.getInstance(this.context);
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

	public TGSkinManager getSkinManager(){
		return TGSkinManager.getInstance(this.context);
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
		midiPlayer.addListener( new TGTransportListener(this.context) );
		try {
			getPlayer().addSequencerProvider(new MidiSequencerProviderImpl(this.context), false);
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
		TGEditorManager editorManager = TGEditorManager.getInstance(this.context);
		if( updateItems ) {
			editorManager.updateSelection(sourceContext);
		}
		editorManager.redraw(sourceContext);
	}

	public static String getProperty(String key) {
		return TGMessagesManager.getProperty(key);
	}

	public static String getProperty(String key,String[] arguments) {
		return TGMessagesManager.getProperty(key,arguments);
	}

	public boolean isDisposed(){
		return (TGApplication.getInstance(this.context).isDisposed() || TGWindow.getInstance(this.context).isDisposed());
	}

	public void playBeat( final TGBeat beat ){
		TGEditorManager.getInstance(this.context).asyncRunLocked(new Runnable() {
			public void run() throws TGException {
				if(!isDisposed() && !getPlayer().isRunning() ){
					getPlayer().playBeat(beat);
				}
			}
		});
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
