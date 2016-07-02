package org.herac.tuxguitar.app.view.dialog.lyric;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.track.TGGoNextTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoPreviousTrackAction;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingAction;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.util.TGProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGLyric;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyPressedListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIKeyConvination;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UITextArea;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGLyricEditor implements TGEventListener {
	
	public static final String ATTRIBUTE_BYPASS_EVENTS_FROM = "bypass-events-from";
	
	private static final float EDITOR_WIDTH = 450f;
	private static final float EDITOR_HEIGHT = 200f;
	
	private TGContext context;
	private TGTrack track;
	private UIWindow dialog;
	private TGLyricModifyListener listener;
	
	private UIButton previous;
	private UIButton next;
	private UILabel label;
	private UILabel fromLabel;
	private UISpinner from;
	private UITextArea text;
	
	private boolean updated;
	private int lastTrack;
	private int lastMeasuseCount;
	private String lastTrackName;
	
	private TGProcess loadPropertiesProcess;
	private TGProcess loadIconsProcess;
	private TGProcess updateItemsProcess;
	
	private List<KeyBindingAction> keyBindings;
	
	public TGLyricEditor(TGContext context){
		this.context = context;
		this.listener = new TGLyricModifyListener(this);
		this.keyBindings = new ArrayList<KeyBindingAction>();
		this.createSyncProcesses();
	}
	
	public TGLyric createLyrics() {
		if(!this.isDisposed()) {
			TGLyric tgLyric = TGDocumentManager.getInstance(this.context).getSongManager().getFactory().newLyric();
			tgLyric.setFrom(this.from.getValue());
			tgLyric.setLyrics(this.text.getText());
			
			return tgLyric;
		}
		return null;
	}
	
	public void show() {
		UIFactory uiFactory = this.getUIFactory();
		
		this.dialog = uiFactory.createWindow(TGWindow.getInstance(this.context).getWindow(), false, true);
		this.dialog.setLayout(new UITableLayout(0f));
		this.dialog.setBounds(new UIRectangle(0, 0, EDITOR_WIDTH, EDITOR_HEIGHT));
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				TGLyricEditor.this.onDispose();
			}
		});
		
		this.track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
		this.loadComposites();
		this.loadProperties();
		this.loadIcons();
		this.updateItems();
		this.addListeners();
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_LAYOUT);
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getIconManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
	}
	
	public void onDispose(){
		this.track = null;
		this.label = null;
		this.text = null;
		this.dialog = null;
		this.removeListeners();
	}
	
	private void loadComposites(){
		loadToolBar();
		loadLyricText();
	}
	
	private void loadToolBar(){
		UIFactory uiFactory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) this.dialog.getLayout();
		
		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = uiFactory.createPanel(this.dialog, false);
		panel.setLayout(panelLayout);
		parentLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, null, null, 0f);
		
		this.previous = uiFactory.createButton(panel);
		panelLayout.set(this.previous, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		this.next = uiFactory.createButton(panel);
		panelLayout.set(this.next, 1, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		this.label = uiFactory.createLabel(panel);
		this.label.setText(this.track.getName());
		panelLayout.set(this.label, 1, 3, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);
		
		this.fromLabel = uiFactory.createLabel(panel);
		panelLayout.set(this.fromLabel, 1, 4, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, false);
		
		this.from = uiFactory.createSpinner(panel);
		this.from.setMinimum(1);
		this.from.setMaximum(this.track.countMeasures());
		this.from.setValue(this.track.getLyrics().getFrom());
		this.from.setEnabled(this.track.countMeasures() > 1);
		this.from.addSelectionListener(this.listener);
		panelLayout.set(this.from, 1, 5, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false, 1, 1, 60f, null, null);
		
		this.previous.addSelectionListener(new TGActionProcessorListener(TGLyricEditor.this.context, TGGoPreviousTrackAction.NAME));
		this.next.addSelectionListener(new TGActionProcessorListener(TGLyricEditor.this.context, TGGoNextTrackAction.NAME));
	}
	
	private void loadLyricText(){
		UIFactory uiFactory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) this.dialog.getLayout();
		
		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = uiFactory.createPanel(this.dialog, false);
		panel.setLayout(panelLayout);
		parentLayout.set(panel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
		
		this.text = uiFactory.createTextArea(panel, true, false);
		this.text.setFocus();
		this.text.setText(this.track.getLyrics().getLyrics());
		this.text.addModifyListener(this.listener);
		this.text.addKeyPressedListener(new UIKeyPressedListener() {
			public void onKeyPressed(UIKeyEvent event) {
				for(KeyBindingAction keyBinding : TGLyricEditor.this.keyBindings) {
					if( event.getKeyConvination().equals(keyBinding.getConvination()) ){
						new TGActionProcessorListener(TGLyricEditor.this.context, keyBinding.getAction()).processEvent(event);
						return;
					}
				}
			}
		});
		panelLayout.set(this.text, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		panelLayout.set(this.text, UITableLayout.PACKED_WIDTH, 0f);
		panelLayout.set(this.text, UITableLayout.PACKED_HEIGHT, 0f);
	}
	
	public void updateItems(){
		if(!isDisposed()){
			boolean enabled = !TuxGuitar.getInstance().getPlayer().isRunning();
			boolean doLayout = false;
			
			this.listener.setEnabled(false);
			if( this.updated ){
				this.lastTrack = 0;
				this.lastTrackName = null;
				this.lastMeasuseCount = 0;
			}
			this.track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
			if( isTrackNameChanged() ){
				doLayout = true;
				this.label.setText(this.track.getName());
			}
			if( isMeasureCountChanged() ){
				doLayout = true;
				this.from.setMaximum(this.track.countMeasures());
			}
			if( isTrackChanged() || isTrackLyricChanged() ){
				doLayout = true;
				this.from.setValue(this.track.getLyrics().getFrom());
				this.text.setText(this.track.getLyrics().getLyrics());
			}
			
			this.from.setEnabled( enabled && (this.track.countMeasures() > 1) );
			this.text.setEnabled( enabled );
			
			this.listener.setEnabled( enabled );
			this.updated = false;
			
			if( doLayout ) {
				this.dialog.layout();
			}
		}
	}
	
	private boolean isTrackChanged(){
		int current = this.track.getNumber();
		if( current != this.lastTrack ){
			this.lastTrack = current;
			return true;
		}
		return false;
	}
	
	private boolean isTrackLyricChanged(){
		if( this.track.getLyrics().getFrom() != this.from.getValue() ) {
			return true;
		}
		if(!this.track.getLyrics().getLyrics().equals(this.text.getText()) ) {
			return true;
		}
		return false;
	}
	
	private boolean isTrackNameChanged(){
		String current = this.track.getName();
		if( this.lastTrackName == null || !current.equals( this.lastTrackName ) ){
			this.lastTrackName = current;
			return true;
		}
		return false;
	}
	
	private boolean isMeasureCountChanged(){
		int current = this.track.countMeasures();
		if( current != this.lastMeasuseCount){
			this.lastMeasuseCount = current;
			return true;
		}
		return false;
	}
	
	public void update() {
		this.updated = true;
	}
	
	public TGTrack getTrack(){
		return this.track;
	}
	
	public TGContext getContext() {
		return this.context;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	public void loadKeyBinding(String actionId) {
		UIKeyConvination keyConvination = KeyBindingActionManager.getInstance(this.context).getKeyBindingForAction(actionId);
		if( keyConvination != null ) {
			this.keyBindings.add(new KeyBindingAction(actionId, keyConvination));
		}
	}
	
	public void loadKeyBindings() {
		if(!isDisposed()){
			this.keyBindings.clear();
			this.loadKeyBinding(TGUndoAction.NAME);
			this.loadKeyBinding(TGRedoAction.NAME);
		}
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			this.loadKeyBindings();
			this.dialog.setText(TuxGuitar.getProperty("lyric.editor"));
			this.fromLabel.setText(TuxGuitar.getProperty("edit.from"));
			this.dialog.layout();
		}
	}
	
	public void loadIcons(){
		if(!isDisposed()){
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			this.previous.setImage(TuxGuitar.getInstance().getIconManager().getArrowLeft());
			this.next.setImage(TuxGuitar.getInstance().getIconManager().getArrowRight());
		}
	}
	
	public boolean isDisposed() {
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public void dispose(){
		if(!isDisposed()){
			this.dialog.dispose();
		}
	}

	public boolean isByPassEvent(TGEvent event) {
		TGAbstractContext context = event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);
		if( context != null ) {
			Object owner = context.getAttribute(ATTRIBUTE_BYPASS_EVENTS_FROM);
			if( owner != null && owner.equals(this) ) {
				return true;
			}
		}
		return false;
	}
	
	public void createSyncProcesses() {
		this.loadPropertiesProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});
		
		this.loadIconsProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});
		
		this.updateItemsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateItems();
			}
		});
	}
	
	public void processUpdateEvent(TGEvent event) {
		if(!this.isByPassEvent(event)) {
			int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
			if( type == TGUpdateEvent.SELECTION ){
				this.updateItemsProcess.process();
			}else if( type == TGUpdateEvent.SONG_UPDATED || type == TGUpdateEvent.SONG_LOADED){
				this.update();
			}
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
		else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
	
	public static TGLyricEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGLyricEditor.class.getName(), new TGSingletonFactory<TGLyricEditor>() {
			public TGLyricEditor createInstance(TGContext context) {
				return new TGLyricEditor(context);
			}
		});
	}
}
