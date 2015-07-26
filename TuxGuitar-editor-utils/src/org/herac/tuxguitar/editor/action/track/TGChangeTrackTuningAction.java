package org.herac.tuxguitar.editor.action.track;

import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeTrackTuningAction extends TGActionBase {
	
	public static final String NAME = "action.track.change-tuning";
	
	public static final String ATTRIBUTE_STRINGS = "strings";
	public static final String ATTRIBUTE_TRANSPOSE_STRINGS = "transposeStrings";
	public static final String ATTRIBUTE_TRANSPOSE_TRY_KEEP_STRINGS = "transposeTryKeepString";
	public static final String ATTRIBUTE_TRANSPOSE_APPLY_TO_CHORDS = "transposeApplyToChords";
	
	public TGChangeTrackTuningAction(TGContext context) {
		super(context, NAME);
	}
	
	@SuppressWarnings("unchecked")
	protected void processAction(TGActionContext context){
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		List<TGString> strings = ((List<TGString>) context.getAttribute(ATTRIBUTE_STRINGS));
		if( track != null && strings != null ){
			int[] transpositions = createTranspositions(track, strings);
			
			TGSongManager tgSongManager = getSongManager(context);
			tgSongManager.getTrackManager().changeInstrumentStrings(track, strings);
			
			Boolean transposeStrings = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_TRANSPOSE_STRINGS));
			if( Boolean.TRUE.equals(transposeStrings) ){
				boolean transposeTryKeepString = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_TRANSPOSE_TRY_KEEP_STRINGS));
				boolean transposeApplyToChords = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_TRANSPOSE_APPLY_TO_CHORDS));
				
				tgSongManager.getTrackManager().transposeNotes(track, transpositions, transposeTryKeepString, transposeApplyToChords );
			}
		}
	}
	
	public int[] createTranspositions(TGTrack track, List<?> newStrings ){
		int[] transpositions = new int[ newStrings.size() ];
		
		TGString newString = null;
		TGString oldString = null;
		for( int index = 0; index < transpositions.length ; index ++ ){
			for( int i = 0; i < track.stringCount() ; i ++ ){
				TGString string = track.getString( i + 1 );
				if( string.getNumber() == (index + 1) ){
					oldString = string;
					break;
				}
			}
			for( int i = 0; i < newStrings.size() ; i ++ ){
				TGString string = (TGString)newStrings.get( i );
				if( string.getNumber() == (index + 1) ){
					newString = string;
					break;
				}
			}
			if( oldString != null && newString != null ){
				transpositions[ index ] = (oldString.getValue() - newString.getValue());
			}else{
				transpositions[ index ] = 0;
			}
			
			newString = null;
			oldString = null;
		}
		
		return transpositions;
	}
}
