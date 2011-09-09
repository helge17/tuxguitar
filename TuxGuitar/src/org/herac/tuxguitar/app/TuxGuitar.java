/*
 * Created on 25-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.actions.ActionLock;
import org.herac.tuxguitar.app.actions.ActionManager;
import org.herac.tuxguitar.app.actions.file.FileActionUtils;
import org.herac.tuxguitar.app.actions.file.NewFileAction;
import org.herac.tuxguitar.app.actions.system.DisposeAction;
import org.herac.tuxguitar.app.editors.EditorCache;
import org.herac.tuxguitar.app.editors.FretBoardEditor;
import org.herac.tuxguitar.app.editors.PianoEditor;
import org.herac.tuxguitar.app.editors.TGEditorManager;
import org.herac.tuxguitar.app.editors.TGRedrawListener;
import org.herac.tuxguitar.app.editors.TGUpdateListener;
import org.herac.tuxguitar.app.editors.TablatureEditor;
import org.herac.tuxguitar.app.editors.channel.TGChannelManagerDialog;
import org.herac.tuxguitar.app.editors.chord.CustomChordManager;
import org.herac.tuxguitar.app.editors.lyric.LyricEditor;
import org.herac.tuxguitar.app.editors.matrix.MatrixEditor;
import org.herac.tuxguitar.app.helper.FileHistory;
import org.herac.tuxguitar.app.helper.SyncThread;
import org.herac.tuxguitar.app.items.ItemManager;
import org.herac.tuxguitar.app.marker.MarkerList;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.system.config.TGConfigManagerImpl;
import org.herac.tuxguitar.app.system.icons.IconLoader;
import org.herac.tuxguitar.app.system.icons.IconManager;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.app.system.language.LanguageManager;
import org.herac.tuxguitar.app.system.plugins.TGPluginManager;
import org.herac.tuxguitar.app.table.TGTableViewer;
import org.herac.tuxguitar.app.tools.browser.dialog.TGBrowserDialog;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.app.tools.template.TGTemplate;
import org.herac.tuxguitar.app.tools.template.TGTemplateManager;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.app.transport.TGTransportListener;
import org.herac.tuxguitar.app.undo.UndoableManager;
import org.herac.tuxguitar.app.util.ArgumentParser;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.app.util.TGSplash;
import org.herac.tuxguitar.app.util.WindowTitleUtil;
import org.herac.tuxguitar.graphics.control.TGFactoryImpl;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.impl.sequencer.MidiSequencerProviderImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGLock;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TuxGuitar {
	
	public static final String APPLICATION_NAME = "TuxGuitar";
	
	public static final int MARGIN_WIDTH = 5;
	
	private static TuxGuitar instance;
	
	private boolean initialized;
	
	private TGLock lock;
	
	private Display display;
	
	private Shell shell;
	
	private MidiPlayer player;
	
	private TGSongManager songManager;
	
	private TGConfigManager configManager;
	
	private LanguageManager languageManager;
	
	private KeyBindingActionManager keyBindingManager;
	
	private IconManager iconManager;
	
	private EditorCache editorCache;
	
	private TablatureEditor tablatureEditor;
	
	private TGTableViewer table;
	
	private TGChannelManagerDialog channelManager;
	
	private TGTransport songTransport;
	
	private FretBoardEditor fretBoardEditor;
	
	private PianoEditor pianoEditor;
	
	private MatrixEditor matrixEditor;
	
	private LyricEditor lyricEditor;
	
	private TGEditorManager editorManager;
	
	private TGBrowserDialog browser;
	
	private UndoableManager undoableManager;
	
	private ScaleManager scaleManager;
	
	private ActionManager actionManager;
	
	private ItemManager itemManager;
	
	private CustomChordManager customChordManager;
	
	private FileHistory fileHistory;
	
	private TGTemplateManager templateManager;
	
	private TGPluginManager pluginManager;
	
	protected Sash sash;
	
	protected Composite sashComposite;
	
	public TuxGuitar() {
		this.lock = new TGLock();
		this.initialized = false;
	}
	
	public static TuxGuitar instance() {
		if (instance == null) {
			synchronized (TuxGuitar.class) {
				instance = new TuxGuitar();
			}
		}
		return instance;
	}
	
	private void initSynchronizer(){
		TGSynchronizer.instance().setController(new TGSynchronizer.TGSynchronizerController() {
			public void execute(final TGSynchronizer.TGSynchronizerTask task) {
				final Display display = getDisplay();
				if(display != null && !display.isDisposed()){
					display.syncExec(new Runnable() {
						public void run() {
							task.run();
						}
					});
				}
			}
			
			public void executeLater(final TGSynchronizer.TGSynchronizerTask task) {
				final Display display = getDisplay();
				if(display != null && !display.isDisposed()){
					display.asyncExec(new Runnable() {
						public void run() {
							task.run();
						}
					});
				}
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
		TGFileUtils.loadLibraries();
		TGFileUtils.loadClasspath();
		
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
		this.getPluginManager().openPlugins();
		this.restoreControlsConfig();
		this.restorePlayerConfig();
		this.updateCache(true);
		this.showTitle();
		
		TGSplash.instance().finish();
		
		// Priority 4 ----------------------------------------------//
		this.shell.addShellListener(getAction(DisposeAction.NAME));
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
		if(url != null){
			ActionLock.lock();
			new SyncThread(new Runnable() {
				public void run() {
					TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
					new Thread(new Runnable() {
						public void run() {
							if(!TuxGuitar.isDisposed()){
								FileActionUtils.open(url);
								TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
								ActionLock.unlock();
							}
						}
					}).start();
				}
			}).start();
		}else{
			getAction(NewFileAction.NAME).process(new ActionData());
		}
	}
	
	public void createComposites(Composite composite) {
		FormData data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(getItemManager().getCoolbar(),MARGIN_WIDTH);
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
		boolean maximized = config.getBooleanConfigValue(TGConfigKeys.MAXIMIZED);
		getShell().setMaximized(maximized);
		if(!maximized){
			int width = config.getIntConfigValue(TGConfigKeys.WIDTH);
			int height = config.getIntConfigValue(TGConfigKeys.HEIGHT);
			if(width > 0 && height > 0){
				getShell().setSize(width,height);
			}
		}
		getShell().setMinimumSize(640,480);
		//---Fretboard---
		if(config.getBooleanConfigValue(TGConfigKeys.SHOW_FRETBOARD)){
			getFretBoardEditor().showFretBoard();
		}else{
			getFretBoardEditor().hideFretBoard();
		}
		//---Mixer---
		if(config.getBooleanConfigValue(TGConfigKeys.SHOW_INSTRUMENTS)){
			new SyncThread(new Runnable() {
				public void run() {
					getChannelManager().show();
				}
			}).start();
		}
		//---Transport---
		if(config.getBooleanConfigValue(TGConfigKeys.SHOW_TRANSPORT)){
			new SyncThread(new Runnable() {
				public void run() {
					getTransport().show();
				}
			}).start();
		}
		//---Matrix---
		if(config.getBooleanConfigValue(TGConfigKeys.SHOW_MATRIX)){
			new SyncThread(new Runnable() {
				public void run() {
					getMatrixEditor().show();
				}
			}).start();
		}
		//---Piano---
		if(config.getBooleanConfigValue(TGConfigKeys.SHOW_PIANO)){
			new SyncThread(new Runnable() {
				public void run() {
					getPianoEditor().show();
				}
			}).start();
		}
		//---Markers---
		if(config.getBooleanConfigValue(TGConfigKeys.SHOW_MARKERS)){
			new SyncThread(new Runnable() {
				public void run() {
					MarkerList.instance().show();
				}
			}).start();
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
		if(this.table == null){
			this.table = new TGTableViewer();
		}
		return this.table;
	}
	
	public TablatureEditor getTablatureEditor(){
		if(this.tablatureEditor == null){
			this.tablatureEditor = new TablatureEditor();
		}
		return this.tablatureEditor;
	}
	
	public FretBoardEditor getFretBoardEditor(){
		if(this.fretBoardEditor == null){
			this.fretBoardEditor = new FretBoardEditor();
		}
		return this.fretBoardEditor;
	}
	
	public PianoEditor getPianoEditor(){
		if(this.pianoEditor == null){
			this.pianoEditor = new PianoEditor();
		}
		return this.pianoEditor;
	}
	
	public MatrixEditor getMatrixEditor(){
		if(this.matrixEditor == null){
			this.matrixEditor = new MatrixEditor();
		}
		return this.matrixEditor;
	}
	
	public TGSongManager getSongManager(){
		if(this.songManager == null){
			this.songManager = new TGSongManager(new TGFactoryImpl());
			this.songManager.setSong(this.songManager.newSong());
		}
		return this.songManager;
	}
	
	public TGChannelManagerDialog getChannelManager(){
		if( this.channelManager == null ){
			this.channelManager = new TGChannelManagerDialog();
		}
		return this.channelManager;
	}
	
	public TGTransport getTransport(){
		if(this.songTransport == null){
			this.songTransport = new TGTransport();
		}
		return this.songTransport;
	}
	
	public EditorCache getEditorCache(){
		if(this.editorCache == null){
			this.editorCache = new EditorCache();
		}
		return this.editorCache;
	}
	
	public TGEditorManager getEditorManager(){
		if(this.editorManager == null){
			this.editorManager = new TGEditorManager();
		}
		return this.editorManager;
	}
	
	public LyricEditor getLyricEditor(){
		if(this.lyricEditor == null){
			this.lyricEditor = new LyricEditor();
		}
		return this.lyricEditor;
	}
	
	public TGBrowserDialog getBrowser(){
		if(this.browser == null){
			this.browser = new TGBrowserDialog();
		}
		return this.browser;
	}
	
	public UndoableManager getUndoableManager(){
		if(this.undoableManager == null){
			this.undoableManager = new UndoableManager();
		}
		return this.undoableManager;
	}
	
	public ScaleManager getScaleManager(){
		if(this.scaleManager == null){
			this.scaleManager = new ScaleManager();
		}
		return this.scaleManager;
	}
	
	public TGPluginManager getPluginManager(){
		if(this.pluginManager == null){
			this.pluginManager = new TGPluginManager();
		}
		return this.pluginManager;
	}
	
	public IconManager getIconManager(){
		if(this.iconManager == null){
			this.iconManager = new IconManager();
			this.iconManager.addLoader( new IconLoader() {
				public void loadIcons() {
					getShell().setImage(getIconManager().getAppIcon());
					getShell().layout(true);
				}
			});
		}
		return this.iconManager;
	}
	
	public CustomChordManager getCustomChordManager(){
		if(this.customChordManager == null){
			this.customChordManager = new CustomChordManager();
		}
		return this.customChordManager;
	}
	
	public ItemManager getItemManager() {
		if(this.itemManager == null){
			this.itemManager = new ItemManager();
		}
		return this.itemManager;
	}
	
	public ActionManager getActionManager() {
		if(this.actionManager == null){
			this.actionManager = new ActionManager();
		}
		return this.actionManager;
	}
	
	public LanguageManager getLanguageManager() {
		if(this.languageManager == null){
			this.languageManager = new LanguageManager();
			this.loadLanguage();
		}
		return this.languageManager;
	}
	
	public TGConfigManager getConfig(){
		if(this.configManager == null){
			this.configManager = new TGConfigManagerImpl();
			this.configManager.init();
		}
		return this.configManager;
	}
	
	public KeyBindingActionManager getkeyBindingManager(){
		if(this.keyBindingManager == null){
			this.keyBindingManager = new KeyBindingActionManager();
		}
		return this.keyBindingManager;
	}
	
	public FileHistory getFileHistory(){
		if(this.fileHistory == null){
			this.fileHistory = new FileHistory();
		}
		return this.fileHistory;
	}
	
	public TGTemplateManager getTemplateManager(){
		if( this.templateManager == null ){
			this.templateManager = new TGTemplateManager();
		}
		return this.templateManager;
	}
	
	public MidiPlayer getPlayer(){
		if(this.player == null){
			this.player = new MidiPlayer();
			this.player.init(getSongManager());
			this.player.addListener( new TGTransportListener() );
			try {
				getPlayer().addSequencerProvider(new MidiSequencerProviderImpl(), false);
			} catch (MidiPlayerException e) {
				e.printStackTrace();
			}
		}
		return this.player;
	}
	
	public void restorePlayerConfig(){
		//check midi sequencer
		getPlayer().openSequencer(getConfig().getStringConfigValue(TGConfigKeys.MIDI_SEQUENCER), true);
		
		//check midi port
		getPlayer().openOutputPort(getConfig().getStringConfigValue(TGConfigKeys.MIDI_PORT), true);
	}
	
	public void showTitle(){
		new SyncThread(new Runnable() {
			public void run() {
				if(!isDisposed()){
					getShell().setText(WindowTitleUtil.parseTitle());
				}
			}
		}).start();
	}
	
	public void updateCache(final boolean updateItems){
		if(!this.isLocked()){
			this.lock();
			this.getEditorCache().updateEditMode();
			this.unlock();
			new SyncThread(new Runnable() {
				public void run() {
					if(!isDisposed() && !isLocked()){
						if(updateItems){
							lock();
							getEditorManager().doUpdate( TGUpdateListener.SELECTION );
							unlock();
						}
						redraw();
					}
				}
			}).start();
		}
	}
	
	protected void redraw(){
		if(!isDisposed() && !this.isLocked()){
			this.lock();
			this.getEditorManager().doRedraw( TGRedrawListener.NORMAL );
			this.unlock();
		}
	}
	
	public void redrawPlayingMode(){
		if(!isDisposed() && !this.isLocked()){
			this.lock();
			this.getEditorCache().updatePlayMode();
			this.getEditorManager().doRedraw( this.getEditorCache().shouldRedraw() ? TGRedrawListener.PLAYING_NEW_BEAT : TGRedrawListener.PLAYING_THREAD );
			this.unlock();
		}
	}
	
	public void showExternalBeat( TGBeat beat ){
		if(!isDisposed() && !this.isLocked()){
			this.lock();
			this.getEditorManager().showExternalBeat(beat);
			this.updateCache(true);
			this.unlock();
		}
	}
	
	public void hideExternalBeat(){
		if(!isDisposed() && !this.isLocked()){
			this.lock();
			this.getEditorManager().hideExternalBeat();
			this.updateCache(true);
			this.unlock();
		}
	}
	
	public Display getDisplay(){
		return this.display;
	}
	
	public Shell getShell(){
		return this.shell;
	}
	
	public Action getAction(String name) {
		return this.getActionManager().getAction(name);
	}
	
	public static String getProperty(String key) {
		return TuxGuitar.instance().getLanguageManager().getProperty(key);
	}
	
	public static String getProperty(String key,String[] arguments) {
		return  TuxGuitar.instance().getLanguageManager().getProperty(key,arguments);
	}
	
	public static boolean isDisposed(){
		return (TuxGuitar.instance().getDisplay().isDisposed() || TuxGuitar.instance().getShell().isDisposed());
	}
	
	public void loadLanguage(){
		this.lock();
		
		getLanguageManager().setLanguage(getConfig().getStringConfigValue(TGConfigKeys.LANGUAGE));
		
		this.unlock();
	}
	
	public void loadToolBars(){
		this.lock();
		
		getItemManager().createCoolbar();
		
		this.unlock();
	}
	
	public void loadStyles(){
		this.lock();
		
		getTablatureEditor().getTablature().reloadStyles();
		
		this.unlock();
	}
	
	public void loadSkin(){
		this.lock();
		
		getIconManager().reloadIcons();
		
		this.unlock();
	}
	
	public void newSong(){
		this.newSong(TuxGuitar.instance().getTemplateManager().getDefatulTemplate());
	}
	
	public void newSong(TGTemplate tgTemplate){
		TGSong tgSong = null;
		if( tgTemplate != null ){
			tgSong = TuxGuitar.instance().getTemplateManager().getTemplateAsSong(tgTemplate);
		}
		if( tgSong == null ){
			tgSong = TuxGuitar.instance().getSongManager().newSong();
		}
		TuxGuitar.instance().fireNewSong(tgSong,null);
	}
	
	public void fireNewSong(TGSong song,URL url){
		this.lock();
		
		TuxGuitar.instance().getSongManager().setSong(song);
		getFileHistory().reset(url);
		getPlayer().reset();
		getPlayer().getMode().clear();
		getEditorCache().reset();
		getUndoableManager().discardAllEdits();
		getEditorManager().doUpdate( TGUpdateListener.SONG_LOADED );
		
		this.unlock();
		
		updateCache(true);
		showTitle();
	}
	
	public void fireSaveSong(URL url){
		this.lock();
		
		getFileHistory().reset(url);
		getEditorCache().reset();
		getUndoableManager().discardAllEdits();
		getEditorManager().doUpdate( TGUpdateListener.SONG_SAVED );
		
		this.unlock();
		
		updateCache(true);
		showTitle();
	}
	
	public void fireUpdate(){
		this.lock();
		this.getEditorCache().reset();
		this.getEditorManager().doUpdate( TGUpdateListener.SONG_UPDATED );
		this.unlock();
	}
	
	public void loadCursor(int style){
		this.loadCursor(getShell(),style);
	}
	
	public void loadCursor(final Control control,final int style){
		try {
			TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable() {
				public void run() throws Throwable {
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
			public void run() {
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