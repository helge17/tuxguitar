/*
 * Created on 25-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.actions.ActionManager;
import org.herac.tuxguitar.gui.actions.file.FileActionUtils;
import org.herac.tuxguitar.gui.actions.system.DisposeAction;
import org.herac.tuxguitar.gui.editors.EditorCache;
import org.herac.tuxguitar.gui.editors.FretBoardEditor;
import org.herac.tuxguitar.gui.editors.PianoEditor;
import org.herac.tuxguitar.gui.editors.TablatureEditor;
import org.herac.tuxguitar.gui.editors.chord.CustomChordManager;
import org.herac.tuxguitar.gui.editors.lyric.LyricEditor;
import org.herac.tuxguitar.gui.editors.matrix.MatrixEditor;
import org.herac.tuxguitar.gui.editors.tab.TGFactoryImpl;
import org.herac.tuxguitar.gui.helper.FileHistory;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.items.ItemManager;
import org.herac.tuxguitar.gui.marker.MarkerList;
import org.herac.tuxguitar.gui.mixer.TGMixer;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.gui.system.config.TGConfigManager;
import org.herac.tuxguitar.gui.system.config.TGConfigManagerImpl;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.icons.IconManager;
import org.herac.tuxguitar.gui.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.gui.system.language.LanguageManager;
import org.herac.tuxguitar.gui.system.plugins.TGPluginManager;
import org.herac.tuxguitar.gui.table.TGTableViewer;
import org.herac.tuxguitar.gui.tools.browser.dialog.TGBrowserDialog;
import org.herac.tuxguitar.gui.tools.scale.ScaleManager;
import org.herac.tuxguitar.gui.transport.TGTransport;
import org.herac.tuxguitar.gui.undo.UndoableManager;
import org.herac.tuxguitar.gui.util.ArgumentParser;
import org.herac.tuxguitar.gui.util.TGFileUtils;
import org.herac.tuxguitar.gui.util.TGSplash;
import org.herac.tuxguitar.gui.util.WindowTitleUtil;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.impl.sequencer.MidiSequencerProviderImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGLock;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TuxGuitar {
	
	public static final String APPLICATION_NAME = "TuxGuitar";

	public static final int MARGIN_WIDTH = 5;
	
	private static TuxGuitar instance;

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
	
	private TGMixer songMixer;
	
	private TGTransport songTransport;
	
	private FretBoardEditor fretBoardEditor;
	
	private PianoEditor pianoEditor;
	
	private MatrixEditor matrixEditor;
	
	private LyricEditor lyricEditor;
	
	private TGBrowserDialog browser;
	
	private UndoableManager undoableManager;
	
	private ScaleManager scaleManager;
	
	private ActionManager actionManager;

	private ItemManager itemManager;

	private CustomChordManager customChordManager;
	
	private FileHistory fileHistory;

	private TGPluginManager pluginManager;
	
	protected Sash sash;
	
	protected Composite sashComposite;			

	public TuxGuitar() {
		this.lock = new TGLock();
	}

	public static TuxGuitar instance() {
		if (instance == null) {
			synchronized (TuxGuitar.class) {
				instance = new TuxGuitar();
			}
		}
		return instance;
	}
	
	public void displayGUI(String[] args) {		
		//checkeo los argumentos
		ArgumentParser argumentParser = new ArgumentParser(args);
		if(argumentParser.processAndExit()){
			return;
		}
		
		TGFileUtils.loadLibraries();
		TGFileUtils.loadClasspath();		

		this.display = new Display();

		TGSplash.instance().init();

		this.shell = new Shell(getDisplay());		
		this.shell.setLayout(getShellLayout());		
		this.shell.setImage(getIconManager().getAppIcon());

		this.createComposites(getShell());

		boolean maximized = getConfig().getBooleanConfigValue(TGConfigKeys.MAXIMIZED);
		this.shell.setMaximized(maximized);
		if(!maximized){
			int width = getConfig().getIntConfigValue(TGConfigKeys.WIDTH); 
			int height = getConfig().getIntConfigValue(TGConfigKeys.HEIGHT);
			if(width > 0 && height > 0){
				this.shell.setSize(width,height);
			}
		}
		this.shell.setMinimumSize(640,480);
		
		this.showDefaultControls();
		this.updateCache(true);
		this.showTitle();
		this.getPluginManager().openPlugins();
		
		TGSplash.instance().finish();
		
		this.shell.addShellListener(getAction(DisposeAction.NAME));				
		this.shell.open();
		this.startSong(argumentParser);
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
		}
	}
	
	public void createComposites(Composite composite) {
		this.sashComposite = new Composite(composite,SWT.NONE);
		this.sashComposite.setLayout(new FormLayout());
		FormData data = new FormData();
		data.left = new FormAttachment(0,0); 
		data.right = new FormAttachment(100,0); 
		data.top = new FormAttachment(getItemManager().getCoolbar(),MARGIN_WIDTH);
		data.bottom = new FormAttachment(100,0);
		this.sashComposite.setLayoutData(data);
				
		this.sash = new Sash(this.sashComposite, SWT.HORIZONTAL);
	    data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.bottom = new FormAttachment(100,-150);
		
		this.sash.setLayoutData(data);
	    
	    getTablatureEditor().showTablature(this.sashComposite); 
	    data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(0,0);
		data.bottom = new FormAttachment(this.sash, 0);
		getTablatureEditor().getTablature().setLayoutData(data);	    

		getTable().init(this.sashComposite);
	    data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(this.sash,10);
		data.bottom = new FormAttachment(100,0);
		getTable().getComposite().setLayoutData(data);

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
	    	    	    
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(this.sashComposite,0);
		data.bottom = new FormAttachment(100,0);
		
		Composite footer = new Composite(composite,SWT.NONE);
		footer.setLayout(new FormLayout());
		footer.setLayoutData(data);		
	    getFretBoardEditor().showFretBoard(footer);
	}

	public void showDefaultControls(){	    	    
		final TGConfigManager config = getConfig();
		//---Fretboard---
	    if(config.getBooleanConfigValue(TGConfigKeys.SHOW_FRETBOARD)){
	    	getFretBoardEditor().showFretBoard();
	    }else{
	    	getFretBoardEditor().hideFretBoard();
	    }
	    //---Mixer---
		if(config.getBooleanConfigValue(TGConfigKeys.SHOW_MIXER)){
			new SyncThread(new Runnable() {
				public void run() {
					getMixer().show();
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
		int height = (value + sashHeight + offset);
		height = Math.max( height,0);
		height = Math.min( height,maximumHeight);
        ((FormData) TuxGuitar.this.sash.getLayoutData()).bottom = new FormAttachment(100, -height);
        this.sashComposite.layout(true,true);		
	}

	public void updateShellFooter(int offset,int minimunWith,int minimunHeight){		
	    FormData data = ((FormData)this.sashComposite.getLayoutData());
	    data.bottom.offset = -offset;
	    getShell().setMinimumSize(Math.max(640,minimunWith),Math.max(480,minimunHeight));
	    getShell().layout();
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
	
	public TGMixer getMixer(){
		if(this.songMixer == null){
			this.songMixer = new TGMixer();
		}
		return this.songMixer;
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
			} );			
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
	
	public MidiPlayer getPlayer(){
		if(this.player == null){
			this.player = new MidiPlayer();
			this.player.init(getSongManager());
			try {
				//check midi sequencer
				getPlayer().openSequencer(getConfig().getStringConfigValue(TGConfigKeys.MIDI_SEQUENCER));

				//check midi port
				getPlayer().openPort(getConfig().getStringConfigValue(TGConfigKeys.MIDI_PORT));

				getPlayer().addSequencerProvider(new MidiSequencerProviderImpl());
			} catch (MidiPlayerException e) {
				e.printStackTrace();
			}				
		}
		return this.player;
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
			getEditorCache().updateEditMode();
			this.unlock();
			new SyncThread(new Runnable() {
				public void run() {
					if(!isDisposed() && !isLocked()){
						lock();
						if(updateItems){
							getItemManager().updateItems();
							getTransport().updateItems();
							getMixer().updateItems();
							getLyricEditor().updateItems();
							getTable().updateItems();
						}
						unlock();
						redraw();
					}
				}
			}).start();
		}
	}

	protected void redraw(){
		if(!isDisposed() && !this.isLocked()){
			this.lock();
			
			this.getTablatureEditor().getTablature().redraw();
	        this.getFretBoardEditor().redraw();
	        this.getPianoEditor().redraw();
	        this.getTable().redraw();
	        this.getMatrixEditor().redraw();
	        
	        this.unlock();
	    }
	}
	
	public void redrawPayingMode(){
	    if(!isDisposed() && !this.isLocked()){
	    	this.lock();
	    	
	    	this.getEditorCache().updatePlayMode();
	    	if(this.getEditorCache().shouldRedraw()){
	    		this.getTablatureEditor().getTablature().redrawPlayingMode();
	    		this.getFretBoardEditor().redrawPlayingMode();
	    		this.getPianoEditor().redrawPlayingMode();
	    		this.getTable().redrawPlayingMode();
	    		this.getMatrixEditor().redrawPlayingMode();
	    	}
	    	this.getTransport().redrawPlayingMode();
	    	
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
		getShell().layout(true);
		
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
		TuxGuitar.instance().fireNewSong(TuxGuitar.instance().getSongManager().newSong(),null);
	}
	
    public void fireNewSong(TGSong song,URL url){
    	this.lock();
    	
    	TuxGuitar.instance().getSongManager().setSong(song);
    	getFileHistory().reset(url);
    	getPlayer().reset();
    	getEditorCache().reset();
    	getUndoableManager().discardAllEdits();
        getTablatureEditor().getTablature().updateTablature();
        getTablatureEditor().getTablature().resetScroll();
        getTablatureEditor().getTablature().initCaret();
        getTable().fireUpdate(true);
        getMixer().update();
        getLyricEditor().update();
        MarkerList.instance().update();
        
        this.unlock();

        updateCache(true);
        showTitle();
    }
	
    public void fireSaveSong(URL url){
    	this.lock();
    	
    	getFileHistory().reset(url);
    	getEditorCache().reset();    	
    	getUndoableManager().discardAllEdits();

    	this.unlock();
    	
        updateCache(true);
        showTitle();
    }
	
	public void fireUpdate(){
		this.lock();
		
		this.getEditorCache().reset();
		this.getTablatureEditor().getTablature().updateTablature();
		this.getTable().fireUpdate(false);
		this.getLyricEditor().update();
		
		this.unlock();
	}
    
    public void loadCursor(int style){
    	this.loadCursor(getShell(),style);
    }
    
    public void loadCursor(final Control control,final int style){
    	getDisplay().syncExec(new Runnable() {
			public void run() {
				if(!control.isDisposed()){
					control.setCursor(getDisplay().getSystemCursor(style));
				}
			}
		});
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