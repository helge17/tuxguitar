package org.herac.tuxguitar.android.view.tablature;

import java.util.Iterator;

import org.herac.tuxguitar.android.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.graphics.control.TGTrackSpacing;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGString;

public class TGSongViewAxisSelector {

	private TGSongViewController controller;
	
	public TGSongViewAxisSelector(TGSongViewController controller) {
		this.controller = controller;
	}
	
	public boolean select(float x, float y) {
		if( x >= 0 && y >= 0 ){
			TGTrackImpl track = findSelectedTrack(y);
			if (track != null) {
				TGMeasureImpl measure = findSelectedMeasure(track, x, y);
				if (measure != null) {
					TGBeat beat = findSelectedBeat(measure, x);
					if (beat != null) {
						TGString string = findSelectedString(measure, y);
						if( string == null ){
							string = this.controller.getCaret().getSelectedString();
						}
						
						this.callMoveTo(track, measure, beat, string);
						
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private TGTrackImpl findSelectedTrack(float y){
		TGLayout layout = this.controller.getLayout();
		int number = layout.getTrackNumberAt(y);
		if(number >= 0){
			return (TGTrackImpl)layout.getSongManager().getTrack(this.controller.getSong(), number);
		}
		return null;
	}
	
	private TGMeasureImpl findSelectedMeasure(TGTrackImpl track, float x, float y){
		TGMeasureImpl measure = null;
		float minorDistance = 0;
		
		Iterator<?> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasureImpl m = (TGMeasureImpl)it.next();
			if(!m.isOutOfBounds() && m.getTs() != null){
				boolean isAtX = (x >= m.getPosX() && x <= m.getPosX() + m.getWidth(this.controller.getLayout()) + m.getSpacing());
				if(isAtX){
					float measureHeight = m.getTs().getSize();
					float distanceY = Math.min(Math.abs(y - (m.getPosY())),Math.abs(y - ( m.getPosY() + measureHeight - 10)));
					if(measure == null || distanceY < minorDistance){
						measure = m;
						minorDistance = distanceY;
					}
				}
			}
		}
		return measure;
	}
	
	private TGBeatImpl findSelectedBeat(TGMeasureImpl measure, float x){
		TGLayout layout = this.controller.getLayout();
		int voice = this.controller.getCaret().getVoice();
		float posX = measure.getHeaderImpl().getLeftSpacing(layout) + measure.getPosX();
		float bestDiff = -1;
		TGBeatImpl bestBeat = null;
		Iterator<?> it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeatImpl beat = (TGBeatImpl)it.next();
			if(!beat.getVoice(voice).isEmpty()){
				float diff = Math.abs(x - (posX + (beat.getPosX() + beat.getSpacing(layout))));
				if(bestDiff == -1 || diff < bestDiff){
					bestBeat = beat;
					bestDiff = diff;
				}
			}
		}
		if( bestBeat == null ){
			bestBeat = (TGBeatImpl) layout.getSongManager().getMeasureManager().getFirstBeat(measure.getBeats());
		}
		return bestBeat;
	}
	
	private TGString findSelectedString(TGMeasureImpl measure, float y) {
		TGString string = null;
		float stringSpacing = this.controller.getLayout().getStringSpacing();
		float minorDistance = 0;
		float firstStringY = measure.getPosY() + measure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE);
		
		Iterator<?> it = measure.getTrack().getStrings().iterator();
		while(it.hasNext()){
			TGString currString = (TGString)it.next();
			float distanceX = Math.abs(y - (firstStringY + ((currString.getNumber() * stringSpacing) - stringSpacing)));
			if(string == null || distanceX < minorDistance){
				string = currString;
				minorDistance = distanceX;
			}
		}
		
		return string;
	}
	
	private void callMoveTo(TGTrackImpl track, TGMeasureImpl measure, TGBeat beat, TGString string) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.controller.getContext(), TGMoveToAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.processOnNewThread();
	}
}
