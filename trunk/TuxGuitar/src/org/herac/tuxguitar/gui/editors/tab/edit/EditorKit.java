package org.herac.tuxguitar.gui.editors.tab.edit;

import java.util.Iterator;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.tab.TGBeatImpl;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.editors.tab.TGTrackSpacing;
import org.herac.tuxguitar.gui.editors.tab.Tablature;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGString;

public class EditorKit implements MouseListener,MouseMoveListener,MouseTrackListener,MenuListener{
	
	public static final int MOUSE_MODE_SELECTION = 1;
	public static final int MOUSE_MODE_EDITION = 2;
	
	private int mouseMode;
	private boolean natural;
	private Tablature tablature;
	private MouseKit mouseKit;
	private Point position;
	private boolean menuOpen;
	
	public EditorKit(Tablature tablature){
		this.tablature = tablature;
		this.mouseKit = new MouseKit(this);
		this.position = new Point(0,0);
		this.menuOpen = false;
		this.tablature.addMouseListener(this);
		this.tablature.addMouseMoveListener(this);
		this.tablature.addMouseTrackListener(this);
		this.setDefaults();
	}
	
	private void setDefaults(){
		this.setMouseMode(TuxGuitar.instance().getConfig().getIntConfigValue(TGConfigKeys.EDITOR_MOUSE_MODE,MOUSE_MODE_EDITION));
		this.setNatural(TuxGuitar.instance().getConfig().getBooleanConfigValue(TGConfigKeys.EDITOR_NATURAL_KEY_MODE,true));
	}
	
	public int getMouseMode() {
		return this.mouseMode;
	}
	
	public void setMouseMode(int mouseMode) {
		this.mouseMode = mouseMode;
	}
	
	public boolean isNatural() {
		return this.natural;
	}
	
	public void setNatural(boolean natural) {
		this.natural = natural;
	}
	
	public Tablature getTablature() {
		return this.tablature;
	}	
	
	public void tryBack(){
		this.mouseKit.tryBack();
	}
	
	public boolean select() {
		int x = this.position.x;
		int y = this.position.y;
		if(x >= 0 && y >= 0){
			TGTrackImpl track = findSelectedTrack(y);
			if (track != null) {
				TGMeasureImpl measure = findSelectedMeasure(track, x, y);
				if (measure != null) {
					TGBeat beat = findSelectedBeat(measure, x);
					TGString tgString = findSelectedString(measure, y);
					if (beat != null) {
						int string = (tgString != null)?tgString.getNumber():getTablature().getCaret().getSelectedString().getNumber();
						getTablature().getCaret().moveTo(track, measure, beat, string);
					}
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isScoreEnabled(){
		return ( (getTablature().getViewLayout().getStyle() & ViewLayout.DISPLAY_SCORE) != 0 );
	}
	
	public TGTrackImpl findSelectedTrack(int y){
		ViewLayout layout = getTablature().getViewLayout();
		int number = layout.getTrackNumberAt(y);
		if(number >= 0){
			return (TGTrackImpl)layout.getSongManager().getTrack(number);
		}
		return null;
	}
	
	public TGMeasureImpl findSelectedMeasure(TGTrackImpl track,int x,int y){
		TGMeasureImpl measure = null;
		int minorDistance = 0;
		
		Iterator it = track.getMeasures();
		while(it.hasNext()){
			TGMeasureImpl m = (TGMeasureImpl)it.next();
			if(!m.isOutOfBounds() && m.getTs() != null){
				boolean isAtX = (x >= m.getPosX() && x <= m.getPosX() + m.getWidth(getTablature().getViewLayout()) + m.getSpacing());
				if(isAtX){
					int measureHeight = m.getTs().getSize();
					int distanceY = Math.min(Math.abs(y - (m.getPosY())),Math.abs(y - ( m.getPosY() + measureHeight - 10)));
					if(measure == null || distanceY < minorDistance){
						measure = m;
						minorDistance = distanceY;
					}
				}
			}
		}
		return measure;
	}
	
	public TGBeatImpl findSelectedBeat(TGMeasureImpl measure, int x){
		int voice = getTablature().getCaret().getVoice();
		int posX = measure.getHeaderImpl().getLeftSpacing(getTablature().getViewLayout()) + measure.getPosX();
		int bestDiff = -1;
		TGBeatImpl bestBeat = null;
		Iterator it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeatImpl beat = (TGBeatImpl)it.next();
			if(!beat.getVoice(voice).isEmpty()){
				int diff = Math.abs(x - (posX + (beat.getPosX() + beat.getSpacing())));
				if(bestDiff == -1 || diff < bestDiff){
					bestBeat = beat;
					bestDiff = diff;
				}
			}
		}
		if( bestBeat == null ){
			bestBeat = (TGBeatImpl)getTablature().getViewLayout().getSongManager().getMeasureManager().getFirstBeat(measure.getBeats());
		}
		return bestBeat;
	}
	
	public TGString findSelectedString(TGMeasureImpl measure,int y) {
		TGString string = null;
		int stringSpacing = getTablature().getViewLayout().getStringSpacing();
		int minorDistance = 0;
		int firstStringY = measure.getPosY() + measure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE);
		
		Iterator it = measure.getTrack().getStrings().iterator();
		while(it.hasNext()){
			TGString currString = (TGString)it.next();
			int distanceX = Math.abs(y - (firstStringY + ((currString.getNumber() * stringSpacing) - stringSpacing)));
			if(string == null || distanceX < minorDistance){
				string = currString;
				minorDistance = distanceX;
			}
		}
		
		return string;
	}
	
	public void mouseDown(MouseEvent e) {
		this.position.x = e.x;
		this.position.y = e.y;
	}
	
	public void mouseUp(MouseEvent e) {
		this.position.x = e.x;
		this.position.y = e.y;
		this.tablature.setFocus();
		if(select()){
			TuxGuitar.instance().updateCache(true);
			if(!this.menuOpen && e.button == 1 && !TuxGuitar.instance().getPlayer().isRunning() && isScoreEnabled() && getMouseMode() == MOUSE_MODE_EDITION){
				this.mouseKit.mouseUp(e);
			}
		}
	}
	
	public void mouseMove(MouseEvent e) {
		if(!this.menuOpen && !TuxGuitar.instance().getPlayer().isRunning()){
			if(isScoreEnabled() && getMouseMode() == MOUSE_MODE_EDITION){
				this.mouseKit.mouseMove(e);
			}
		}
	}
	
	public void mouseExit(MouseEvent e) {
		if(!this.menuOpen && !TuxGuitar.instance().getPlayer().isRunning()){
			if(isScoreEnabled() && getMouseMode() == MOUSE_MODE_EDITION){
				this.mouseKit.mouseExit();
			}
		}
	}
	
	public void menuShown(MenuEvent e) {
		this.menuOpen = true;
		this.select();
		TuxGuitar.instance().updateCache(true);
	}
	
	public void menuHidden(MenuEvent e){
		this.menuOpen = false;
		TuxGuitar.instance().updateCache(true);
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		//not implemented
	}
	
	public void mouseEnter(MouseEvent e) {
		//not implemented
	}
	
	public void mouseHover(MouseEvent e) {
		//not implemented
	}
}
