package org.herac.tuxguitar.app.editors.tab.edit;

import java.util.Iterator;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Tablature;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.graphics.control.TGTrackSpacing;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGString;

public class EditorKit implements MouseListener,MouseMoveListener,MouseTrackListener,MenuListener{
	
	public static final int MOUSE_MODE_SELECTION = 1;
	public static final int MOUSE_MODE_EDITION = 2;
	
	private static final int FIRST_LINE_VALUES[] = new int[] {65,45,52,55};
	
	private int mouseMode;
	private boolean natural;
	private Tablature tablature;
	private MouseKit mouseKit;
	private Point position;
	private boolean menuOpen;
	private TGMeasureImpl selectedMeasure;
	
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
		return ( (getTablature().getViewLayout().getStyle() & TGLayout.DISPLAY_SCORE) != 0 );
	}
	
	public TGTrackImpl findSelectedTrack(int y){
		TGLayout layout = getTablature().getViewLayout();
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
	
	public void updateSelectedMeasure(int x, int y){
		TGMeasureImpl previousSelection = this.selectedMeasure;
		
		TGTrackImpl track = this.findSelectedTrack(y);
		if( track != null ) {
			this.selectedMeasure = this.findSelectedMeasure(track, x, y);
		}
		
		boolean selectionUpdated = false;
		
		if(!selectionUpdated && this.selectedMeasure == null && previousSelection != null ){
			selectionUpdated = true;
		}
		if(!selectionUpdated && this.selectedMeasure != null && previousSelection == null ){
			selectionUpdated = true;
		}
		if(!selectionUpdated && this.selectedMeasure != null ){
			selectionUpdated = !this.selectedMeasure.equals(previousSelection);
		}
		if( selectionUpdated ){
			TuxGuitar.instance().updateCache(false);
			System.out.println(this.selectedMeasure);
		}
	}
	
	public void resetSelectedMeasure(){
		this.selectedMeasure = null;
	}
	
	public void paintSelection(TGLayout layout, TGPainter painter) {
		if(!TuxGuitar.instance().getPlayer().isRunning()){
			TGMeasureImpl measure = this.selectedMeasure;
			if(measure != null && measure.getTs() != null){
				float scale = layout.getScale();
				int minValue = measure.getTrack().getString(measure.getTrack().stringCount()).getValue();
				int maxValue = measure.getTrack().getString(1).getValue() + 29;
				int lineSpacing = layout.getScoreLineSpacing();
				int width = (int)(10.0f * scale);
				int topHeight = measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
				int bottomHeight = (measure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_DOWN_LINES));
				int tempValue = 0;
				
				int x1 = 0;
				int x2 = 0;
				int y1 = (measure.getPosY() + measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
				int y2 = (y1 + (lineSpacing * 5));
				
				for(int b = 0 ; b < measure.countBeats() ; b++ ){
					TGBeatImpl beat = (TGBeatImpl)measure.getBeat(b);
					if(!beat.getVoice(getTablature().getCaret().getVoice()).isEmpty()){
						x1 = (measure.getHeaderImpl().getLeftSpacing(layout) + measure.getPosX() + beat.getPosX() + beat.getSpacing());
						x2 = x1 + width;
						
						painter.setForeground(layout.getResources().getLineColor());
						
						tempValue = FIRST_LINE_VALUES[measure.getClef() - 1];
						for(int y = (y1 - lineSpacing); y >= (y1 - topHeight); y -= lineSpacing){
							tempValue += (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue + 1) % 12])?2:1;
							tempValue += (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue + 1) % 12])?2:1;
							if( tempValue > maxValue ){
								break;
							}
							painter.initPath();
							painter.setAntialias(false);
							painter.moveTo(x1, y);
							painter.lineTo(x2, y);
							painter.closePath();
						}
						
						tempValue = FIRST_LINE_VALUES[measure.getClef() - 1] - 14;
						for(int y = y2; y <= (y2 + bottomHeight); y += lineSpacing){
							if(tempValue > 0){
								tempValue -= (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue - 1) % 12])?2:1;
								tempValue -= (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue - 1) % 12])?2:1;
								if( tempValue < minValue ){
									break;
								}
								painter.initPath();
								painter.setAntialias(false);
								painter.moveTo(x1, y);
								painter.lineTo(x2, y);
								painter.closePath();
							}
						}
					}
				}
			}
		}
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
