package app.tuxguitar.android.action.impl.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.transport.TGTransport;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGGoFirstMeasureAction extends TGActionBase {

	public static final String NAME = "action.measure.go-first";

	public TGGoFirstMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		MidiPlayer midiPlayer = MidiPlayer.getInstance(getContext());
		if( midiPlayer.isRunning() ){
			TGTransport.getInstance(getContext()).gotoFirst();
		}
		else{
			TGCaret caret = getEditor().getCaret();
			TGTrack track = caret.getTrack();
			TGMeasure measure = getSongManager(context).getTrackManager().getFirstMeasure(track);
			if(track != null && measure != null){
				caret.update(track.getNumber(), measure.getStart(), caret.getSelectedString().getNumber());
			}
		}
	}
}
