package org.herac.tuxguitar.gui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.actions.composition.ChangeInfoAction;
import org.herac.tuxguitar.gui.actions.track.GoToTrackAction;
import org.herac.tuxguitar.gui.actions.track.TrackPropertiesAction;
import org.herac.tuxguitar.gui.editors.TGRedrawListener;
import org.herac.tuxguitar.gui.editors.TGUpdateListener;
import org.herac.tuxguitar.gui.editors.TablatureEditor;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGTableViewer implements TGRedrawListener, TGUpdateListener, LanguageLoader{
	
	public static final Color[] BACKGROUNDS = new Color[]{
		new Color(TuxGuitar.instance().getDisplay(),255,255,255),
		new Color(TuxGuitar.instance().getDisplay(),238,238,238),
		new Color(TuxGuitar.instance().getDisplay(),192,192,192)
	};
	
	private Composite composite;
	private ScrollBar hSroll;
	private TGTable table;
	private int selectedTrack;
	private int selectedMeasure;
	private int trackCount = 0;
	private boolean autoSizeEnabled;
	private boolean update;
	private boolean followScroll;
	
	public TGTableViewer() {
		TuxGuitar.instance().getLanguageManager().addLoader(this);
		TuxGuitar.instance().getEditorManager().addRedrawListener(this);
		TuxGuitar.instance().getEditorManager().addUpdateListener(this);
	}
	
	public void init(Composite parent){
		this.composite = new Composite(parent,SWT.H_SCROLL);
		this.addLayout();
		this.addTable();
		this.addHScroll();
		this.loadConfig();
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
		this.hSroll = getComposite().getHorizontalBar();
		this.hSroll.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				redrawLocked();
			}
		});
	}
	
	private void addTable(){
		MouseListener listener = mouseFocusListener();
		this.table = new TGTable(getComposite());
		this.table.getColumnNumber().getControl().addMouseListener(listener);
		this.table.getColumnName().getControl().addMouseListener(listener);
		this.table.getColumnInstrument().getControl().addMouseListener(listener);
		this.table.getColumnCanvas().getControl().addMouseListener(listener);
		this.table.getColumnCanvas().getControl().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				TuxGuitar.instance().getAction(ChangeInfoAction.NAME).process(e);
			}
		});
		this.fireUpdate(true);
		this.loadProperties();
	}
	
	public void loadProperties() {
		this.table.getColumnNumber().setTitle(TuxGuitar.getProperty("track.number"));
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
		this.followScroll = true;
	}
	
	public void updateHScroll(){
		int width = (getEditor().getTablature().getCaret().getTrack().countMeasures() * this.table.getRowHeight());
		this.hSroll.setIncrement(this.table.getScrollIncrement());
		this.hSroll.setMaximum(width);
		this.hSroll.setThumb(Math.min(width ,this.table.getColumnCanvas().getControl().getClientArea().width));
	}
	
	public TGTable getTable(){
		return this.table;
	}
	
	public int getHScrollSelection(){
		return this.hSroll.getSelection();
	}
	
	public TablatureEditor getEditor(){
		return TuxGuitar.instance().getTablatureEditor();
	}
	
	private String getInstrument(TGTrack track){
		if(track.isPercussionTrack()){
			return TuxGuitar.getProperty("track.name.default-percussion-name");
		}
		MidiInstrument[] list = TuxGuitar.instance().getPlayer().getInstruments();
		int index = track.getChannel().getInstrument();
		if(list != null && index >= 0 && index < list.length){
			return list[index].getName();
		}
		return new String();
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
					
					//Name
					row.getName().setText(track.getName());
					row.getName().setData(track);
					
					//Instrument
					row.getInstrument().setText(getInstrument(track));
					row.getInstrument().setData(track);
					
					row.setMouseListenerLabel(new MouseAdapter() {
						
						public void mouseUp(MouseEvent e) {
							row.getPainter().setFocus();
						}
						
						public void mouseDown(MouseEvent e) {
							if(track.getNumber() != getEditor().getTablature().getCaret().getTrack().getNumber()){
								TuxGuitar.instance().getAction(GoToTrackAction.NAME).process(e);
							}
						}
						
						public void mouseDoubleClick(final MouseEvent e) {
							new Thread(new Runnable() {
								public void run() {
									ActionLock.waitFor();
									TuxGuitar.instance().getAction(TrackPropertiesAction.NAME).process(e);
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
		//return ( this.table.getMinHeight() + getComposite().getHorizontalBar().getSize().y );
	}
	
	private void redrawRows(int selectedTrack){
		int rows = this.table.getRowCount();
		for(int i = 0; i < rows; i ++){
			TGTableRow row = this.table.getRow(i); 
			row.getPainter().redraw();
			if(this.selectedTrack != selectedTrack){
				row.setBackground( ((selectedTrack - 1) == i)?BACKGROUNDS[2]:BACKGROUNDS[ i % 2] );
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
			
			if(this.followScroll){
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
		int hScrollSelection = this.hSroll.getSelection();
		int hScrollThumb = this.hSroll.getThumb();
		
		int measureSize = this.table.getRowHeight();
		int measurePosition = ( (selectedMeasure * measureSize) - measureSize );
		
		if( (measurePosition - hScrollSelection) < 0 || (measurePosition + measureSize - hScrollSelection ) > hScrollThumb){
			this.hSroll.setSelection(measurePosition);
		}
	}
	
	public void loadConfig(){
		this.autoSizeEnabled = TuxGuitar.instance().getConfig().getBooleanConfigValue(TGConfigKeys.TABLE_AUTO_SIZE);
		this.trackCount = 0;
	}
	
	public static void disposeColors(){
		for(int i = 0;i < BACKGROUNDS.length;i++){
			BACKGROUNDS[i].dispose();
		}
	}
	
	public Composite getComposite(){
		return this.composite;
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

	public void doRedraw(int type) {
		if( type == TGRedrawListener.NORMAL ){
			this.redraw();
		}else if( type == TGRedrawListener.PLAYING_NEW_BEAT ){
			this.redrawPlayingMode();
		}
	}
	
	public void doUpdate(int type) {
		if( type == TGUpdateListener.SELECTION ){
			this.updateItems();
		}else if( type == TGUpdateListener.SONG_UPDATED ){
			this.fireUpdate( false );
		}else if( type == TGUpdateListener.SONG_LOADED ){
			this.fireUpdate( true );
		}
	}
}
