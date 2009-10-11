/*
 * Created on 20-mar-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.transport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.transport.TransportMetronomeAction;
import org.herac.tuxguitar.gui.actions.transport.TransportModeAction;
import org.herac.tuxguitar.gui.editors.TGRedrawListener;
import org.herac.tuxguitar.gui.editors.TGUpdateListener;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.gui.util.MidiTickUtil;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TGTransport implements TGRedrawListener, TGUpdateListener, IconLoader,LanguageLoader{
	private static final int PLAY_MODE_DELAY = 250;
	
	public static final int STATUS_STOPPED = 1;
	public static final int STATUS_PAUSED = 2;
	public static final int STATUS_RUNNING = 3;
	
	protected Shell dialog;
	protected Label label;
	protected ProgressBar tickProgress;
	protected Button metronome;
	protected Button mode;
	protected ToolBar toolBar;
	protected ToolItem first;
	protected ToolItem last;
	protected ToolItem previous;
	protected ToolItem next;
	protected ToolItem stop;
	protected ToolItem play;
	protected boolean editingTickScale;
	protected long redrawTime;
	protected int status;
	
	public TGTransport() {
		super();
	}
	
	public void show() {
		this.dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM);
		this.dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		this.dialog.setLayout(new GridLayout());
		this.dialog.setText(TuxGuitar.getProperty("transport"));
		this.initComposites();
		this.initToolBar();
		this.redraw();
		
		this.addListeners();
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
				TuxGuitar.instance().updateCache(true);
			}
		});
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	public void addListeners(){
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
		TuxGuitar.instance().getEditorManager().addRedrawListener(this);
		TuxGuitar.instance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.instance().getIconManager().removeLoader(this);
		TuxGuitar.instance().getLanguageManager().removeLoader(this);
		TuxGuitar.instance().getEditorManager().removeRedrawListener(this);
		TuxGuitar.instance().getEditorManager().removeUpdateListener(this);
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
		this.metronome.addSelectionListener(TuxGuitar.instance().getAction(TransportMetronomeAction.NAME));
		
		this.mode = new Button(composite,SWT.PUSH);
		this.mode.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.mode.addSelectionListener(TuxGuitar.instance().getAction(TransportModeAction.NAME));
		
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
				gotoMeasure(getSongManager().getMeasureHeaderAt(TGTransport.this.tickProgress.getSelection()),true);
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
			this.redraw();
		}
	}
	
	private void initToolBar(){
		if(this.toolBar != null){
			this.toolBar.dispose();
		}
		this.toolBar = new ToolBar(this.dialog,SWT.FLAT);
		
		this.first = new ToolItem(this.toolBar,SWT.PUSH);
		this.first.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gotoFirst();
			}
		});
		
		this.previous = new ToolItem(this.toolBar,SWT.PUSH);
		this.previous.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gotoPrevious();
			}
		});
		
		this.stop = new ToolItem(this.toolBar,SWT.PUSH);
		this.stop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stop();
			}
		});
		
		this.play = new ToolItem(this.toolBar,SWT.PUSH);
		this.play.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				play();
			}
		});
		
		this.next = new ToolItem(this.toolBar,SWT.PUSH);
		this.next.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gotoNext();
			}
		});
		
		this.last = new ToolItem(this.toolBar,SWT.PUSH);
		this.last.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gotoLast();
			}
		});
		
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
			
			if(TuxGuitar.instance().getPlayer().isRunning()){
				setStatus(STATUS_RUNNING);
			}else if(TuxGuitar.instance().getPlayer().isPaused()){
				setStatus(STATUS_PAUSED);
			}else{
				setStatus(STATUS_STOPPED);
			}
			
			if(force || lastStatus != getStatus()){
				if(getStatus() == STATUS_RUNNING){
					this.first.setImage(TuxGuitar.instance().getIconManager().getTransportFirst2());
					this.last.setImage(TuxGuitar.instance().getIconManager().getTransportLast2());
					this.previous.setImage(TuxGuitar.instance().getIconManager().getTransportPrevious2());
					this.next.setImage(TuxGuitar.instance().getIconManager().getTransportNext2());
					this.stop.setImage(TuxGuitar.instance().getIconManager().getTransportStop2());
					this.play.setImage(TuxGuitar.instance().getIconManager().getTransportPause());
				}else if(getStatus() == STATUS_PAUSED){
					this.first.setImage(TuxGuitar.instance().getIconManager().getTransportFirst2());
					this.last.setImage(TuxGuitar.instance().getIconManager().getTransportLast2());
					this.previous.setImage(TuxGuitar.instance().getIconManager().getTransportPrevious2());
					this.next.setImage(TuxGuitar.instance().getIconManager().getTransportNext2());
					this.stop.setImage(TuxGuitar.instance().getIconManager().getTransportStop2());
					this.play.setImage(TuxGuitar.instance().getIconManager().getTransportPlay2());
				}else if(getStatus() == STATUS_STOPPED){
					this.first.setImage(TuxGuitar.instance().getIconManager().getTransportFirst1());
					this.last.setImage(TuxGuitar.instance().getIconManager().getTransportLast1());
					this.previous.setImage(TuxGuitar.instance().getIconManager().getTransportPrevious1());
					this.next.setImage(TuxGuitar.instance().getIconManager().getTransportNext1());
					this.stop.setImage(TuxGuitar.instance().getIconManager().getTransportStop1());
					this.play.setImage(TuxGuitar.instance().getIconManager().getTransportPlay1());
				}
				this.loadPlayText();
			}
			TGMeasureHeader first = getSongManager().getFirstMeasureHeader();
			TGMeasureHeader last = getSongManager().getLastMeasureHeader();
			this.tickProgress.setMinimum((int)first.getStart());
			this.tickProgress.setMaximum((int)(last.getStart() + last.getLength()) -1);
			this.metronome.setSelection(TuxGuitar.instance().getPlayer().isMetronomeEnabled());
			
			this.redraw();
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
			this.dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
			this.dialog.layout(true);
			this.dialog.pack(true);
		}
	}
	
	private void loadOptionIcons(){
		this.metronome.setImage(TuxGuitar.instance().getIconManager().getTransportMetronome());
		this.mode.setImage(TuxGuitar.instance().getIconManager().getTransportMode());
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
		return TuxGuitar.instance().getSongManager();
	}
	
	public void gotoFirst(){
		gotoMeasure(getSongManager().getFirstMeasureHeader(),true);
	}
	
	public void gotoLast(){
		gotoMeasure(getSongManager().getLastMeasureHeader(),true) ;
	}
	
	public void gotoNext(){
		MidiPlayer player = TuxGuitar.instance().getPlayer();
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(MidiTickUtil.getStart(player.getTickPosition()));
		if(header != null){
			gotoMeasure(getSongManager().getNextMeasureHeader(header),true);
		}
	}
	
	public void gotoPrevious(){
		MidiPlayer player = TuxGuitar.instance().getPlayer();
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(MidiTickUtil.getStart(player.getTickPosition()));
		if(header != null){
			gotoMeasure(getSongManager().getPrevMeasureHeader(header),true);
		}
	}
	
	public void gotoMeasure(TGMeasureHeader header){
		gotoMeasure(header,false);
	}
	
	public void gotoMeasure(TGMeasureHeader header,boolean moveCaret){
		if(header != null){
			TGMeasure playingMeasure = null;
			if( TuxGuitar.instance().getPlayer().isRunning() ){
				TuxGuitar.instance().getEditorCache().updatePlayMode();
				playingMeasure = TuxGuitar.instance().getEditorCache().getPlayMeasure();
			}
			if( playingMeasure == null || playingMeasure.getHeader().getNumber() != header.getNumber() ){
				TuxGuitar.instance().getPlayer().setTickPosition(MidiTickUtil.getTick(header.getStart()));
				if(moveCaret){
					TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().goToTickPosition();
					TuxGuitar.instance().updateCache(true);
				}
				redraw();
			}
		}
	}
	
	public void gotoPlayerPosition(){
		TuxGuitar.instance().lock();
		
		MidiPlayer player = TuxGuitar.instance().getPlayer();
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(MidiTickUtil.getStart(player.getTickPosition()));
		if(header != null){
			player.setTickPosition(MidiTickUtil.getTick(header.getStart()));
		}
		TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().goToTickPosition();
		TuxGuitar.instance().unlock();
		
		TuxGuitar.instance().updateCache(true);
	}
	
	public void play(){
		MidiPlayer player = TuxGuitar.instance().getPlayer();
		if(!player.isRunning()){
			try{
				player.getMode().reset();
				player.play();
			}catch(MidiPlayerException exception){
				MessageDialog.errorMessage(exception);
			}
		}else{
			player.pause();
		}
	}
	
	public void stop(){
		MidiPlayer player = TuxGuitar.instance().getPlayer();
		if(!player.isRunning()){
			player.reset();
			this.gotoPlayerPosition();
		}else{
			player.reset();
		}
	}
	
	public void redraw(){
		if(!TuxGuitar.instance().isLocked()){
			if(!isDisposed()){
				new SyncThread(new Runnable() {
					public void run() {
						if(!isDisposed() && !TuxGuitar.instance().isLocked()){
							if(isEditingTickScale()){
								TGTransport.this.label.setText(Long.toString(TGTransport.this.tickProgress.getSelection()));
							}
							else if(!TuxGuitar.instance().getPlayer().isRunning()){
								long tickPosition = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getPosition();
								
								TGTransport.this.label.setText(Long.toString(tickPosition));
								TGTransport.this.tickProgress.setSelection((int)tickPosition);
							}
						}
					}
				}).start();
			}
		}
	}
	
	public void redrawPlayingMode(){
		if(!TuxGuitar.instance().isLocked()){
			//TuxGuitar.instance().lock();
			if(!isDisposed()){
				if(!isEditingTickScale() && TuxGuitar.instance().getPlayer().isRunning()){
					long time = System.currentTimeMillis();
					if(time > this.redrawTime + PLAY_MODE_DELAY){
						long position = (TuxGuitar.instance().getEditorCache().getPlayStart() + (TuxGuitar.instance().getPlayer().getTickPosition() - TuxGuitar.instance().getEditorCache().getPlayTick()));
						this.label.setText(Long.toString(position));
						this.tickProgress.setSelection((int)position);
						this.redrawTime = time;
					}
				}
			}
			//TuxGuitar.instance().unlock();
		}
	}
	
	public void doRedraw(int type) {
		if( type == TGRedrawListener.PLAYING_THREAD || type == TGRedrawListener.PLAYING_NEW_BEAT ){
			this.redrawPlayingMode();
		}
	}
	
	public void doUpdate(int type) {
		if( type == TGUpdateListener.SELECTION ){
			this.updateItems();
		}
	}
}
