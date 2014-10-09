package org.herac.tuxguitar.app.editors.lyric;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessor;
import org.herac.tuxguitar.app.action.impl.edit.RedoAction;
import org.herac.tuxguitar.app.action.impl.edit.UndoAction;
import org.herac.tuxguitar.app.action.impl.track.GoNextTrackAction;
import org.herac.tuxguitar.app.action.impl.track.GoPreviousTrackAction;
import org.herac.tuxguitar.app.editors.TGUpdateEvent;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.keybindings.KeyBinding;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingAction;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingUtil;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGTrack;

public class LyricEditor implements TGEventListener {
	
	private static int EDITOR_WIDTH = 450;
	private static int EDITOR_HEIGHT = 200;
	
	protected static final KeyBindingAction KB_ACTIONS[] = new KeyBindingAction[]{
		new KeyBindingAction(UndoAction.NAME,new KeyBinding(122,KeyBindingUtil.CONTROL)),
		new KeyBindingAction(RedoAction.NAME,new KeyBinding(121,KeyBindingUtil.CONTROL)),
	};
	
	private TGTrack track;
	private Shell dialog;
	private LyricModifyListener listener;
	
	private Button previous;
	private Button next;
	private Label label;
	private Label fromLabel;
	private Spinner from;
	private Text text;
	private int caretPosition;
	
	private boolean updated;
	private int lastTrack;
	private int lastMeasuseCount;
	private String lastTrackName;
	
	public LyricEditor(){
		this.listener = new LyricModifyListener(this);
	}
	
	public void show() {
		this.dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM | SWT.RESIZE);
		this.dialog.setLayout(getDialogLayout());
		this.dialog.setSize(EDITOR_WIDTH,EDITOR_HEIGHT);
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				onDispose();
			}
		});
		
		this.track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
		this.loadComposites();
		this.loadProperties();
		this.loadIcons();
		this.updateItems();
		this.addListeners();
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER);
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
		TuxGuitar.getInstance().updateCache(true);
	}
	
	private GridLayout getDialogLayout(){
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		return layout;
	}
	
	private void loadComposites(){
		loadToolBar(this.dialog);
		loadLyricText(this.dialog);
	}
	
	private void loadToolBar(Composite parent){
		final Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout(5,false));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		this.previous = new Button(composite, SWT.ARROW | SWT.LEFT);
		this.next = new Button(composite, SWT.ARROW | SWT.RIGHT);
		
		this.label = new Label(composite,SWT.NONE);
		this.label.setText(this.track.getName());
		this.label.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		
		this.fromLabel = new Label(composite,SWT.NONE);
		this.fromLabel.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		
		this.from = new Spinner(composite,SWT.BORDER);
		this.from.setLayoutData(new GridData(50,SWT.DEFAULT));
		
		this.from.setMinimum(1);
		this.from.setMaximum(this.track.countMeasures());
		this.from.setSelection(this.track.getLyrics().getFrom());
		this.from.setEnabled(this.track.countMeasures() > 1);
		this.from.addModifyListener(this.listener);
		
		this.previous.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new TGActionProcessor(GoPreviousTrackAction.NAME).widgetSelected(e);
				composite.layout();
			}
		});
		this.next.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new TGActionProcessor(GoNextTrackAction.NAME).widgetSelected(e);
				composite.layout();
			}
		});
	}
	
	private void loadLyricText(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.text = new Text(composite,SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		this.text.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.text.setFocus();
		this.text.setText(this.track.getLyrics().getLyrics());
		this.text.addModifyListener(this.listener);
		this.text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				for( int i = 0 ; i < KB_ACTIONS.length ; i ++ ){
					if(event.keyCode == KB_ACTIONS[i].getKeyBinding().getKey() && event.stateMask == KB_ACTIONS[i].getKeyBinding().getMask()){
						new TGActionProcessor(KB_ACTIONS[i].getAction()).processEvent(event);
						return;
					}
				}
			}
		});
	}
	
	public void updateItems(){
		if(!isDisposed()){
			boolean enabled = !TuxGuitar.getInstance().getPlayer().isRunning();
			
			this.listener.setEnabled(false);
			if(this.updated){
				this.lastTrack = 0;
				this.lastTrackName = null;
				this.lastMeasuseCount = 0;
			}
			this.track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
			if( isTrackNameChanged() ){
				this.label.setText(this.track.getName());
			}
			if( isMeasureCountChanged() ){
				this.from.setMaximum(this.track.countMeasures());
			}
			if( isTrackChanged() ){
				this.from.setSelection(this.track.getLyrics().getFrom());
				this.text.setText(this.track.getLyrics().getLyrics());
				this.text.setSelection( (this.caretPosition >= 0 ? this.caretPosition : this.text.getCharCount()));
			}
			
			this.from.setEnabled( enabled && (this.track.countMeasures() > 1) );
			this.text.setEnabled( enabled );
			
			this.setCaretPosition(-1);
			
			this.listener.setEnabled( enabled );
			this.updated = false;
		}
	}
	
	private boolean isTrackChanged(){
		int current = this.track.getNumber();
		if(current != this.lastTrack){
			this.lastTrack = current;
			return true;
		}
		return false;
	}
	
	private boolean isTrackNameChanged(){
		String current = this.track.getName();
		if(this.lastTrackName == null || !current.equals( this.lastTrackName ) ){
			this.lastTrackName = current;
			return true;
		}
		return false;
	}
	
	private boolean isMeasureCountChanged(){
		int current = this.track.countMeasures();
		if(current != this.lastMeasuseCount){
			this.lastMeasuseCount = current;
			return true;
		}
		return false;
	}
	
	public void update(){
		this.updated = true;
	}
	
	public void setCaretPosition(int caretPosition) {
		this.caretPosition = caretPosition;
	}
	
	public TGTrack getTrack(){
		return this.track;
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			this.dialog.setText(TuxGuitar.getProperty("lyric.editor"));
			this.fromLabel.setText(TuxGuitar.getProperty("edit.from"));
		}
	}
	
	public void loadIcons(){
		if(!isDisposed()){
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
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

	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getProperty(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateItems();
		}else if( type == TGUpdateEvent.SONG_UPDATED || type == TGUpdateEvent.SONG_LOADED){
			this.update();
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIcons();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadProperties();
		}
		else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
}
