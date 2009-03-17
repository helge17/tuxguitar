package org.herac.tuxguitar.midiinput;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.song.managers.TGSongManager;

import org.herac.tuxguitar.gui.actions.transport.TransportPlayAction;
import org.herac.tuxguitar.gui.actions.transport.TransportStopAction;
import org.herac.tuxguitar.gui.editors.TablatureEditor;
import org.herac.tuxguitar.gui.editors.tab.Caret;

//import org.herac.tuxguitar.gui.editors.TablatureEditor;
//import org.herac.tuxguitar.gui.editors.tab.Caret;

import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGTrack;

class MiRecorder
{
static	private	MiRecorder	s_Instance;
		private	boolean		f_IsRecording;
		private	boolean		f_SavedMetronomeStatus;
		private	TGTrack		f_TempTrack;				// temporary track
		private	int			f_Tempo;					// recording tempo [bpm]
		private	long		f_StartPosition;			// recording start position [ticks?]
		private	MiBuffer	f_Buffer = new MiBuffer();	// input notes buffer


	private MiRecorder()
	{
	}


	static public MiRecorder instance()
	{
	if(s_Instance == null)
		s_Instance = new MiRecorder();

	return s_Instance;
	}


	public boolean	isRecording()	{ return(f_IsRecording); }


	public void		addNote(byte inString, byte inFret, byte inPitch, byte inVelocity, long inTimeStamp)
	{
	f_Buffer.addEvent(inString, inFret, inPitch, inVelocity, inTimeStamp);
	}


	public void		start()
	{
	f_SavedMetronomeStatus = TuxGuitar.instance().getPlayer().isMetronomeEnabled();
	TuxGuitar.instance().getPlayer().setMetronomeEnabled(true);

	TablatureEditor	editor	= TuxGuitar.instance().getTablatureEditor();
	Caret			caret	= editor.getTablature().getCaret();

	f_Tempo			= caret.getMeasure().getTempo().getValue();
	f_StartPosition = caret.getMeasure().getStart()/* - caret.getDuration().getTime()*/;

	TGSongManager	tgSongMgr	= TuxGuitar.instance().getSongManager();

	f_TempTrack = tgSongMgr.createTrack();

	f_TempTrack.setName("Traccia temporanea input MIDI");

	// allocate measures
	int		requestedMeasuresCount = 10,
			currMeasuresCount = tgSongMgr.getSong().countMeasureHeaders();

	for(int m = currMeasuresCount + 1; m < requestedMeasuresCount; m++)
		tgSongMgr.addNewMeasure(m);

	TuxGuitar.instance().fireUpdate();
	TuxGuitar.instance().getMixer().update();

	TuxGuitar.instance().getAction(TransportPlayAction.NAME).process(null);

	// come si sincronizza il timestamp iniziale con il playback?
	f_Buffer.startRecording(MiPort.getNotesPortTimeStamp());
	f_IsRecording = true;
	}


	public void		stop()
	{
	TGSongManager	tgSongMgr = TuxGuitar.instance().getSongManager();

	f_Buffer.stopRecording(MiPort.getNotesPortTimeStamp());
	f_IsRecording = false;

	TuxGuitar.instance().getAction(TransportStopAction.NAME).process(null);
	TuxGuitar.instance().getPlayer().setMetronomeEnabled(f_SavedMetronomeStatus);
	
	// qui deve cancellare la traccia di servizio...
	tgSongMgr.removeTrack(f_TempTrack);

/* da ripristinare...
	if(f_Buffer.finalize(
		(byte)MiConfig.instance().getMinVelocity(),
		(long)MiConfig.instance().getMinDuration() * 1000) > 0)
		{
		f_Buffer.toTrack(f_Tempo, TGDuration.SIXTY_FOURTH, f_StartPosition);
		}
*/
	f_Buffer.toTrack(f_Tempo, TGDuration.EIGHTH, f_StartPosition);	// *** TEST ***

	TuxGuitar.instance().fireUpdate();
	TuxGuitar.instance().getMixer().update();
	}
}