package org.herac.tuxguitar.app.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionLock;
import org.herac.tuxguitar.app.action.impl.composition.ChangeInfoAction;
import org.herac.tuxguitar.app.action.impl.track.GoToTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TrackPropertiesAction;
import org.herac.tuxguitar.app.editors.TGRedrawEvent;
import org.herac.tuxguitar.app.editors.TGUpdateEvent;
import org.herac.tuxguitar.app.editors.TablatureEditor;
import org.herac.tuxguitar.app.items.menu.TrackMenu;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGTableViewer implements TGEventListener {
	
	private Composite composite;
	private ScrollBar hScroll;
	private Color[] backgrounds;
	private Color[] foregrounds;
	private TGTable table;
	private int selectedTrack;
	private int selectedMeasure;
	private int trackCount = 0;
	private boolean autoSizeEnabled;
	private boolean update;
	private boolean followScroll;
	private boolean resetTexts;
	
	public TGTableViewer() {
		TuxGuitar.instance().getLanguageManager().addLoader(this);
		TuxGuitar.instance().getEditorManager().addRedrawListener(this);
		TuxGuitar.instance().getEditorManager().addUpdateListener(this);
	}
	
	public void init(Composite parent){
		this.composite = new Composite(parent,SWT.H_SCROLL);
		this.addColors();
		this.addLayout();
		this.addTable();
		this.addHScroll();
		this.loadConfig();
	}
	
	private void addColors(){
		Display display = this.getComposite().getDisplay();

		this.backgrounds = new Color[]{
			new Color(display,255,255,255),
			new Color(display,238,238,238),
			new Color(display,192,192,192),
		};
		this.foregrounds = new Color[]{
			new Color( display, 0, 0, 0 ),
			new Color( display, 0, 0, 0 ),
			new Color( display, 0, 0, 0 ),
		};
	}
	
	private void addLayout(){
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		getComposite().setLayout(layout);
	}
	
	private void addHScroll(){
		this.hScroll = getComposite().getHorizontalBar();
		this.hScroll.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				redrawLocked();
			}
		});
	}
	
	private void addTable(){
		MouseListener listener = mouseFocusListener();
		this.table = new TGTable(getComposite());
		this.table.getColumnNumber().getControl().addMouseListener(listener);
		this.table.getColumnSoloMute().getControl().addMouseListener(listener);
		this.table.getColumnName().getControl().addMouseListener(listener);
		this.table.getColumnInstrument().getControl().addMouseListener(listener);
		this.table.getColumnCanvas().getControl().addMouseListener(listener);
		this.table.getColumnCanvas().getControl().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
					public void run() throws TGException {
						TuxGuitar.instance().getActionManager().execute(ChangeInfoAction.NAME);
					}
				});
			}
		});
		this.fireUpdate(true);
		this.loadProperties();
	}
	
	public void loadProperties() {
		this.table.getColumnNumber().setTitle(TuxGuitar.getProperty("track.number"));
		this.table.getColumnSoloMute().setTitle(TuxGuitar.getProperty("track.short-solo-mute"));
		this.table.getColumnName().setTitle(TuxGuitar.getProperty("track.name"));
		this.table.getColumnInstrument().setTitle(TuxGuitar.getProperty("track.instrument"));
	}
	
	public void fireUpdate(boolean newSong){
		this.update = true;
		if(newSong){
			this.trackCount = 0;
		}
	}
	
	public void updateItems(){
		this.resetTexts = true;
		this.followScroll = true;
	}
	
	public void updateHScroll(){
		int width = (getEditor().getTablature().getCaret().getTrack().countMeasures() * this.table.getRowHeight());
		this.hScroll.setIncrement(this.table.getScrollIncrement());
		this.hScroll.setMaximum(width);
		this.hScroll.setThumb(Math.min(width ,this.table.getColumnCanvas().getControl().getClientArea().width));
	}
	
	public TGTable getTable(){
		return this.table;
	}
	
	public int getHScrollSelection(){
		return this.hScroll.getSelection();
	}
	
	public TablatureEditor getEditor(){
		return TuxGuitar.instance().getTablatureEditor();
	}
	
	private String getInstrument(TGTrack track){
		TGChannel channel = TuxGuitar.instance().getSongManager().getChannel(track.getChannelId());
		if( channel != null ){
			return ( channel.getName() != null ? channel.getName() : new String() );
		}
		return new String();
	}
	
	private String getSoloMute(TGTrack track){
		if( track.isSolo() ){
			return TuxGuitar.getProperty("track.short-solo-mute.s");
		}
		if( track.isMute() ){
			return TuxGuitar.getProperty("track.short-solo-mute.m");
		}
		return TuxGuitar.getProperty("track.short-solo-mute.none");
	}
	
	private void updateTable(){
		if(this.update){
			int count = TuxGuitar.instance().getSongManager().getSong().countTracks();
			this.table.removeRowsAfter(count);
			for(int i = this.table.getRowCount(); i < count; i ++){
				this.table.newRow();
			}
			for(int i = 0; i < count; i ++){
				final TGTrack track = TuxGuitar.instance().getSongManager().getSong().getTrack(i);
				final TGTableRow row = this.table.getRow(i);
				if(row != null){
					//Number
					row.getNumber().setText(Integer.toString(track.getNumber()));
					row.getNumber().setData(track);
					row.getNumber().setMenu(createTrackMenu());
					
					//Solo-Mute
					row.getSoloMute().setText(getSoloMute(track));
					row.getSoloMute().setData(track);
					row.getSoloMute().setMenu(createTrackMenu());
					
					//Name
					row.getName().setText(track.getName());
					row.getName().setData(track);
					row.getName().setMenu(createTrackMenu());
					
					//Instrument
					row.getInstrument().setText(getInstrument(track));
					row.getInstrument().setData(track);
					row.getInstrument().setMenu(createTrackMenu());
					
					row.setMouseListenerLabel(new MouseAdapter() {
						
						public void mouseUp(MouseEvent e) {
							row.getPainter().setFocus();
						}
						
						public void mouseDown(MouseEvent e) {
							if( track.getNumber() != getEditor().getTablature().getCaret().getTrack().getNumber() ){
								TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
									public void run() throws TGException {
										TGActionManager tgActionManager = TuxGuitar.instance().getActionManager();
										TGActionContext tgActionContext = tgActionManager.createActionContext();
										tgActionContext.setAttribute(GoToTrackAction.PROPERTY_TRACK, track);
										tgActionManager.execute(GoToTrackAction.NAME, tgActionContext);
									}
								});
							}
						}
						
						public void mouseDoubleClick(final MouseEvent e) {
							new Thread(new Runnable() {
								public void run() {
									TGActionLock.waitFor();
									TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
										public void run() throws TGException {
											TuxGuitar.instance().getActionManager().execute(TrackPropertiesAction.NAME);
										}
									});
								}
							}).start();
						}
					});
					row.setMouseListenerCanvas(new MouseAdapter() {
						
						public void mouseUp(MouseEvent e) {
							row.getPainter().setFocus();
						}
						
						public void mouseDown(MouseEvent e) {
							int index = ((e.x + getHScrollSelection())/ getTable().getRowHeight());
							if(index >= 0 && index < track.countMeasures()){
								TGMeasureImpl measure = (TGMeasureImpl)track.getMeasure(index);
								TGBeat beat = TuxGuitar.instance().getSongManager().getMeasureManager().getFirstBeat(measure.getBeats());
								if(beat != null){
									getEditor().getTablature().getCaret().moveTo((TGTrackImpl)track,measure,beat,1);
									TuxGuitar.instance().updateCache(true);
								}
							}
						}
					});
					row.setPaintListenerCanvas(new TGTableCanvasPainter(this,track));
				}
			}
			this.table.update();
			this.selectedTrack = 0;
			this.selectedMeasure = 0;
			
			if(this.autoSizeEnabled && this.trackCount != count){
				TuxGuitar.instance().setTableHeight( getHeight() );
				this.trackCount = count;
			}
			
		}
		this.update = false;
	}
	
	private int getHeight(){
		Rectangle r1 = this.composite.getBounds();
		Rectangle r2 = this.composite.getClientArea();
		return ( this.table.getMinHeight() + (r1.height - r2.height) );
	}
	
	private void resetTextsValues(){
		int rows = this.table.getRowCount();
		for(int i = 0; i < rows; i ++){
			TGTableRow row = this.table.getRow(i);
			
			row.getNumber().setText(Integer.toString(((TGTrack)row.getNumber().getData()).getNumber()));
			row.getSoloMute().setText(getSoloMute((TGTrack)row.getSoloMute().getData()));
			row.getName().setText(((TGTrack)row.getName().getData()).getName());
			row.getInstrument().setText(getInstrument((TGTrack)row.getInstrument().getData()));
		}
	}
	
	private void redrawRows(int selectedTrack){
		int rows = this.table.getRowCount();
		for(int i = 0; i < rows; i ++){
			TGTableRow row = this.table.getRow(i); 
			row.getPainter().redraw();
			if(this.selectedTrack != selectedTrack){
				row.setBackground( this.backgrounds[ ((selectedTrack - 1) == i)? 2: ( i % 2 ) ] );
				row.setForeground( this.foregrounds[ ((selectedTrack - 1) == i)? 2: ( i % 2 ) ] );
			}
		}
	}
	
	public void redrawLocked(){
		if( !TuxGuitar.instance().isLocked() ){
			TuxGuitar.instance().lock();
			redraw();
			TuxGuitar.instance().unlock();
		}
	}
	
	public void redraw(){
		if(!isDisposed() && !TuxGuitar.instance().isLocked()){
			this.updateTable();
			this.table.getColumnCanvas().setTitle(TuxGuitar.instance().getSongManager().getSong().getName());
			int selectedTrack = getEditor().getTablature().getCaret().getTrack().getNumber();
			this.redrawRows(selectedTrack);
			this.selectedTrack = selectedTrack;
			this.selectedMeasure = 0;
			this.updateHScroll();
			
			if( this.resetTexts ){
				this.resetTextsValues();
				this.resetTexts = false;
			}
			if( this.followScroll ){
				this.followHorizontalScroll(getEditor().getTablature().getCaret().getMeasure().getNumber());
				this.followScroll = false;
			}
			getComposite().redraw();
		}
	}
	
	public void redrawPlayingMode(){
		if(!isDisposed() && !TuxGuitar.instance().isLocked()){
			TGMeasure measure =  TuxGuitar.instance().getEditorCache().getPlayMeasure();
			if(measure != null && measure.getTrack() != null){
				this.updateTable();
				int selectedTrack = measure.getTrack().getNumber();
				int selectedMeasure = measure.getNumber();
				if(this.selectedTrack != selectedTrack || this.selectedMeasure != selectedMeasure){
					this.redrawRows(selectedTrack);
					this.followHorizontalScroll(selectedMeasure);
				}
				this.selectedTrack = selectedTrack;
				this.selectedMeasure = selectedMeasure;
			}
		}
	}
	
	private void followHorizontalScroll(int selectedMeasure){
		int hScrollSelection = this.hScroll.getSelection();
		int hScrollThumb = this.hScroll.getThumb();
		
		int measureSize = this.table.getRowHeight();
		int measurePosition = ( (selectedMeasure * measureSize) - measureSize );
		
		if( (measurePosition - hScrollSelection) < 0 || (measurePosition + measureSize - hScrollSelection ) > hScrollThumb){
			this.hScroll.setSelection(measurePosition);
		}
	}
	
	public void loadConfig(){
		this.autoSizeEnabled = TuxGuitar.instance().getConfig().getBooleanValue(TGConfigKeys.TABLE_AUTO_SIZE);
		this.trackCount = 0;
	}
	
	public Composite getComposite(){
		return this.composite;
	}
	
	public Menu createTrackMenu(){
		final TrackMenu trackMenu = new TrackMenu(getComposite().getShell(),SWT.POP_UP);
		trackMenu.showItems();
		trackMenu.update();
		
		final TGEventListener trackMenuUpdateListener = new TGEventListener() {
			public void processEvent(TGEvent event) {
				int type = ((Integer)event.getProperty(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
				if(!trackMenu.isDisposed() && type == TGUpdateEvent.SELECTION ){
					trackMenu.update();
				}
			}
		};
		final TGEventListener trackMenuLanguageLoader = new TGEventListener() {
			public void processEvent(TGEvent event) {
				if(!trackMenu.isDisposed()){
					trackMenu.loadProperties();
				}
			}
		};
		final TGEventListener trackMenuIconLoader = new TGEventListener() {
			public void processEvent(TGEvent event) {
				if(!trackMenu.isDisposed()){
					trackMenu.loadIcons();
				}
			}
		};
		
		TuxGuitar.instance().getEditorManager().addUpdateListener(trackMenuUpdateListener);
		TuxGuitar.instance().getLanguageManager().addLoader(trackMenuLanguageLoader);
		TuxGuitar.instance().getIconManager().addLoader(trackMenuIconLoader);
		
		trackMenu.getMenu().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TuxGuitar.instance().getEditorManager().removeUpdateListener(trackMenuUpdateListener);
				TuxGuitar.instance().getLanguageManager().removeLoader(trackMenuLanguageLoader);
				TuxGuitar.instance().getIconManager().removeLoader(trackMenuIconLoader);
			}
		});
		
		return trackMenu.getMenu();
	}
	
	public void disposeColors(){
		for(int i = 0; i < this.backgrounds.length; i++){
			this.backgrounds[i].dispose();
		}
		for(int i = 0; i < this.foregrounds.length; i++){
			this.foregrounds[i].dispose();
		}
	}
	
	public void dispose(){
		if(!isDisposed()){
			getComposite().dispose();
			disposeColors();
		}
	}
	
	public boolean isDisposed(){
		return (getComposite() == null || getComposite().isDisposed());
	}
	
	protected int getSelectedTrack(){
		return this.selectedTrack;
	}
	
	private MouseListener mouseFocusListener(){
		return new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				TGTable table = getTable();
				if(table != null){
					TGTableRow row = table.getRow( ( getSelectedTrack() - 1 ) );
					if(row != null){
						row.getPainter().setFocus();
					}
				}
			}
		};
	}

	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getProperty(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redraw();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayingMode();
		}
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getProperty(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateItems();
		}else if( type == TGUpdateEvent.SONG_UPDATED ){
			this.fireUpdate( false );
		}else if( type == TGUpdateEvent.SONG_LOADED ){
			this.fireUpdate( true );
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadProperties();
		}
		else if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processRedrawEvent(event);
		}
		else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
}
