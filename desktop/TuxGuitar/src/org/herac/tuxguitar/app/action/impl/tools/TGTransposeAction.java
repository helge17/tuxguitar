package org.herac.tuxguitar.app.action.impl.tools;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGTransposeAction extends TGActionBase{
	
	public static final String NAME = "action.tools.transpose-notes";
	
	public static final String ATTRIBUTE_TRANSPOSITION = "transposition";
	public static final String ATTRIBUTE_TRY_KEEP_STRING = "tryKeepString";
	public static final String ATTRIBUTE_APPLY_TO_CHORDS = "applyToChords";
	public static final String ATTRIBUTE_APPLY_TO_ALL_TRACKS = "applyToAllTracks";
	public static final String ATTRIBUTE_APPLY_TO_ALL_MEASURES = "applyToAllMeasures";
	
	public TGTransposeAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGTrack contextTrack = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGMeasure contextMeasure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		
		Integer transposition = context.getAttribute(ATTRIBUTE_TRANSPOSITION);
		Boolean tryKeepString = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_TRY_KEEP_STRING));
		Boolean applyToChords = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_APPLY_TO_CHORDS));
		Boolean applyToAllTracks = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_APPLY_TO_ALL_TRACKS));
		Boolean applyToAllMeasures = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_APPLY_TO_ALL_MEASURES));
		
		if( applyToAllMeasures ){
			if( applyToAllTracks ){
				for( int i = 0 ; i < song.countTracks() ; i ++ ){
					transposeTrack(songManager, song, song.getTrack( i ) , transposition , tryKeepString , applyToChords);
				}
			} else {
				transposeTrack(songManager, song, contextTrack, transposition , tryKeepString , applyToChords);
			}
		}else{
			if( applyToAllTracks ){
				for( int i = 0 ; i < song.countTracks() ; i ++ ){
					TGTrack track = song.getTrack( i );
					TGMeasure measure = songManager.getTrackManager().getMeasure(track, contextMeasure.getNumber() );
					if( measure != null ){
						transposeMeasure(songManager, song, measure, transposition , tryKeepString , applyToChords);
					}
				}
			} else {
				transposeMeasure(songManager, song, contextMeasure, transposition , tryKeepString , applyToChords);
			}
		}
	}
	
	public void transposeMeasure(TGSongManager songManager, TGSong song, TGMeasure measure, int transposition , boolean tryKeepString , boolean applyToChords ) {
		if( transposition != 0 && !songManager.isPercussionChannel(song, measure.getTrack().getChannelId()) ){
			songManager.getMeasureManager().transposeNotes( measure , transposition , tryKeepString , applyToChords , -1 );
		}
	}
	
	public void transposeTrack(TGSongManager songManager, TGSong song, TGTrack track, int transposition , boolean tryKeepString , boolean applyToChords ) {
		if( transposition != 0 && !songManager.isPercussionChannel(song, track.getChannelId()) ){
			songManager.getTrackManager().transposeNotes( track , transposition , tryKeepString , applyToChords, -1 );
		}
	}
}
