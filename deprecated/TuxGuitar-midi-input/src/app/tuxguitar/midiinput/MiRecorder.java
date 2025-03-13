package app.tuxguitar.midiinput;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.transport.TGTransportPlayAction;
import app.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGSynchronizer;

class MiRecorder
{
static	private	MiRecorder	s_Instance;
		private	boolean		f_IsRecording;
		private	boolean		f_SavedMetronomeStatus;
		private	TGTrack		f_TempTrack;				// temporary track
		private	int			f_Tempo;					// recording tempo [bpm]
		private	long		f_StartPosition;			// recording start position [ticks]
		private	MiBuffer	f_Buffer = new MiBuffer();	// input notes buffer

		private	boolean		s_TESTING = true;


	private MiRecorder()
	{
	}


	int	getTempo()	// just for DEBUG
	{
		return(f_Tempo);
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
	TGContext	tgContext = TuxGuitar.getInstance().getContext();
	TGSongManager	tgSongMgr = TuxGuitar.getInstance().getSongManager();
	TGDocumentManager	tgDocMgr = TuxGuitar.getInstance().getDocumentManager();

	if(s_TESTING)
		{
		TGTempo	tempo = tgSongMgr.getFactory().newTempo();

		tempo.setValue(80);
		tgSongMgr.changeTempos(tgDocMgr.getSong(), TGDuration.QUARTER_TIME, tempo, true);
		}

	f_SavedMetronomeStatus = TuxGuitar.getInstance().getPlayer().isMetronomeEnabled();
	TuxGuitar.getInstance().getPlayer().setMetronomeEnabled(true);

	TablatureEditor	editor	= TuxGuitar.getInstance().getTablatureEditor();
	Caret			caret	= editor.getTablature().getCaret();

	f_Tempo			= caret.getMeasure().getTempo().getValue();
	f_StartPosition = caret.getMeasure().getStart();

	f_TempTrack = tgSongMgr.addTrack(tgDocMgr.getSong());

	f_TempTrack.setName("Traccia temporanea input MIDI");
/*
	// allocate measures
	int		requestedMeasuresCount = 10,
			currMeasuresCount = tgSongMgr.getSong().countMeasureHeaders();

	for(int m = currMeasuresCount + 1; m < requestedMeasuresCount; m++)
		tgSongMgr.addNewMeasure(m);

	TuxGuitar.instance().fireUpdate();
	TuxGuitar.instance().getMixer().update();
*/
	TGSynchronizer.getInstance(tgContext).executeLater(new Runnable() {
		public void run() throws TGException {
			TuxGuitar.getInstance().getActionManager().execute(TGTransportPlayAction.NAME);
		}
	});

	// come si sincronizza il timestamp iniziale con il playback?
	f_Buffer.startRecording(MiPort.getNotesPortTimeStamp());
	f_IsRecording = true;
	}


	public void		stop()
	{
	TGContext	tgContext = TuxGuitar.getInstance().getContext();
	TGDocumentManager	tgDocMgr = TuxGuitar.getInstance().getDocumentManager();
	TGSongManager	tgSongMgr = TuxGuitar.getInstance().getSongManager();

	f_Buffer.stopRecording(MiPort.getNotesPortTimeStamp());
	f_IsRecording = false;

	TGSynchronizer.getInstance(tgContext).executeLater(new Runnable() {
		public void run() throws TGException {
			TuxGuitar.getInstance().getActionManager().execute(TGTransportStopAction.NAME);
		}
	});
	TuxGuitar.getInstance().getPlayer().setMetronomeEnabled(f_SavedMetronomeStatus);

	// qui deve cancellare la traccia di servizio...
	tgSongMgr.removeTrack(tgDocMgr.getSong(), f_TempTrack);

	if(f_Buffer.finalize(
		(byte)MiConfig.instance().getMinVelocity(),
		(long)MiConfig.instance().getMinDuration() * 1000) > 0)
		{
		f_Buffer.toTrack(f_Tempo, f_StartPosition, "Nuovo input MIDI");
		}

	TuxGuitar.getInstance().getEditorManager().updateSong();
	}
}