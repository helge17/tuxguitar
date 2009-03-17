package org.herac.tuxguitar.midiinput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.managers.TGTrackManager;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;


class MiBuffer
{
	private ArrayList	f_Notes			= new ArrayList();	// time-ordered list of notes
	private ArrayList	f_NoteOffMap	= new ArrayList();	// time-ordered map of NOTE_OFF events
	private	long		f_StartTime		= -1;				// initial MIDI time stamp [microseconds]
	private	long		f_StopTime		= -1;				// final MIDI time stamp [microseconds]


	public void startRecording(long inTimeStamp)
	{
	f_StartTime	= inTimeStamp;
	f_StopTime	= -1;

	f_Notes.clear();
	f_NoteOffMap.clear();
	}


	public void stopRecording(long inTimeStamp)
	{
	f_StopTime	= inTimeStamp;
	}


	public void addEvent(byte inString, byte inFret, byte inPitch, byte inVelocity, long inTimeStamp)
	{
	if(inVelocity == 0)
		f_NoteOffMap.add(new MiNote(inString, inFret, inPitch, inVelocity, inTimeStamp));
	else
		f_Notes.add(new MiNote(inString, inFret, inPitch, inVelocity, inTimeStamp));
	}


	public void	dump(String inTitle)
	{
	Iterator	it = f_Notes.iterator();

	System.out.println();
	System.out.println("dumping " + inTitle + "...");
	//System.out.println("Started at: " + f_StartTime);
	//System.out.println("Stopped at: " + f_StopTime);
	System.out.println();

	while(it.hasNext())
		{
		MiNote	n = (MiNote)it.next();

		System.out.println(
				"str: "		+ n.getString() +
				", fret: "	+ n.getFret() +
				", pitch: "	+ n.getPitch() +
				", vel: "	+ n.getVelocity() +
				", on: "	+ n.getTimeOn() +
				", off: "	+ n.getTimeOff() +
				", dur: "	+ n.getDuration());
		}
	}


	public int	finalize(byte inMinVelocity, long inMinDuration)
	{
	Iterator	onIt;

	// determine notes duration
	onIt = f_Notes.iterator();
	while(onIt.hasNext())
		{
		MiNote	on = (MiNote)onIt.next();

		Iterator	offIt = f_NoteOffMap.iterator();
		boolean		found = false;

		while(offIt.hasNext() && !found)
			{
			MiNote	off = (MiNote)offIt.next();

			if(	on.getString()	== off.getString() &&
				on.getFret()	== off.getFret())
				{
				on.setTimeOff(off.getTimeOn());
				offIt.remove();
				found = true;
				}
			}

		// if a note is still playing, set its end time
		if(!found)
			on.setTimeOff(f_StopTime);
		}

	// remove notes with insufficient velocity or duration
	onIt = f_Notes.iterator();
	while(onIt.hasNext())
		{
		MiNote	on = (MiNote)onIt.next();
/*
		System.out.println(
				"str: "		+ on.getString() +
				", fret: "	+ on.getFret() +
				", pitch: "	+ on.getPitch() +
				", vel: "	+ on.getVelocity() +
				", dur: "	+ on.getDuration());
*/
		if(	on.getVelocity() < inMinVelocity ||
			on.getDuration() < inMinDuration)
			{
/*
			System.out.println(
					"removed "	+
					"str: "		+ on.getString() +
					", fret: "	+ on.getFret() +
					", pitch: "	+ on.getPitch() +
					", vel: "	+ on.getVelocity() +
					", dur: "	+ on.getDuration());
*/
			onIt.remove();
			}
		}

	return(f_Notes.size());
	}


	public TGChord	toChord(int inStringsCount)
	{
	TGSongManager	tgSongMgr	= TuxGuitar.instance().getSongManager();
	TGChord			tgChord		= tgSongMgr.getFactory().newChord(inStringsCount);
	Iterator		it			= f_Notes.iterator();

	while(it.hasNext())
		{
		MiNote	note = (MiNote)it.next();

		tgChord.addFretValue(note.getString() - 1, note.getFret());
		}

	return(tgChord);
	}


	public TGBeat	toBeat()
	{
	TGSongManager	tgSongMgr	= TuxGuitar.instance().getSongManager();
	TGBeat			tgBeat		= tgSongMgr.getFactory().newBeat();
	Iterator		it			= f_Notes.iterator();

	while(it.hasNext())
		{
		MiNote	note	= (MiNote)it.next();
		TGNote	tgNote	= tgSongMgr.getFactory().newNote();

		tgNote.setString(note.getString());
		tgNote.setValue(note.getFret());
		tgBeat.getVoice(0).addNote(tgNote);
		}

	return(tgBeat);
	}


	public TreeSet	toPitchesSet()
	{
	TreeSet		pitches	= new TreeSet();
	Iterator	it		= f_Notes.iterator();

	while(it.hasNext())
		{
		MiNote	note = (MiNote)it.next();

		pitches.add(new Byte(note.getPitch()));
		}

	return(pitches);
	}


	private	long	timestampToTicks(int inTempo, long inTimeStamp)
	{
	//double	microsecsPerQuarter = ((60.0 / inTempo) * 1000000),
	//		microsecsPerTick	= microsecsPerQuarter / TGDuration.QUARTER_TIME;

	long	ticks = (inTimeStamp * inTempo * TGDuration.QUARTER_TIME) / 60000000L;

	return(/*(long)microsecsPerTick*/ticks);
	}


	private	long	noteToTicks(int inNoteType)
	{
	switch(inNoteType)
		{
		case TGDuration.WHOLE:			return(TGDuration.QUARTER_TIME *  4);
		case TGDuration.HALF:			return(TGDuration.QUARTER_TIME *  2);
		case TGDuration.QUARTER:		return(TGDuration.QUARTER_TIME);
		case TGDuration.EIGHTH:			return(TGDuration.QUARTER_TIME /  2);
		case TGDuration.SIXTEENTH:		return(TGDuration.QUARTER_TIME /  4);
		case TGDuration.THIRTY_SECOND:	return(TGDuration.QUARTER_TIME /  8);
		case TGDuration.SIXTY_FOURTH:	return(TGDuration.QUARTER_TIME / 16);
		default:						return(1L);
		}
	}


	private	int		durationToNoteType(long inDuration)
	{
	// converts duration [ticks] into note type
		
	if(inDuration >= noteToTicks(TGDuration.WHOLE))
		return(TGDuration.WHOLE);
	else if(inDuration >= noteToTicks(TGDuration.HALF))
		return(TGDuration.HALF);
	else if(inDuration >= noteToTicks(TGDuration.QUARTER))
		return(TGDuration.QUARTER);
	else if(inDuration >= noteToTicks(TGDuration.EIGHTH))
		return(TGDuration.EIGHTH);
	else if(inDuration >= noteToTicks(TGDuration.SIXTEENTH))
		return(TGDuration.SIXTEENTH);
	else if(inDuration >= noteToTicks(TGDuration.THIRTY_SECOND))
		return(TGDuration.THIRTY_SECOND);
	else
		return(TGDuration.SIXTY_FOURTH);
	}


	private void	normalize(int inTempo, int inNoteType, long inStartPosition)
	{
	// *** TEST ***
	f_Notes.clear();
	f_StartTime	= 265910000;
	f_StopTime	= 274549000;

	MiNote	n;

	n = new MiNote((byte)3, (byte)2, (byte)57, (byte)127, 265910000);	n.setTimeOff(267326000);	f_Notes.add(n);
	n = new MiNote((byte)2, (byte)3, (byte)62, (byte)108, 267419000);	n.setTimeOff(267587000);	f_Notes.add(n);
	n = new MiNote((byte)2, (byte)2, (byte)61, (byte) 90, 267651000);	n.setTimeOff(267860000);	f_Notes.add(n);
	n = new MiNote((byte)3, (byte)2, (byte)57, (byte)127, 267942000);	n.setTimeOff(269615000);	f_Notes.add(n);
	n = new MiNote((byte)2, (byte)5, (byte)64, (byte)105, 269921000);	n.setTimeOff(270137000);	f_Notes.add(n);
	n = new MiNote((byte)2, (byte)6, (byte)65, (byte) 95, 270139000);	n.setTimeOff(270338000);	f_Notes.add(n);
	n = new MiNote((byte)2, (byte)8, (byte)67, (byte)126, 270403000);	n.setTimeOff(271197000);	f_Notes.add(n);
	n = new MiNote((byte)2, (byte)2, (byte)61, (byte)112, 271443000);	n.setTimeOff(271653000);	f_Notes.add(n);
	n = new MiNote((byte)2, (byte)3, (byte)62, (byte) 99, 271671000);	n.setTimeOff(271912000);	f_Notes.add(n);
	n = new MiNote((byte)3, (byte)2, (byte)57, (byte) 95, 271944000);	n.setTimeOff(274549000);	f_Notes.add(n);
	// *** TEST ***

	long		timeResolution	= noteToTicks(inNoteType);
	Iterator	it				= f_Notes.iterator();
	
	while(it.hasNext())
		{
		MiNote	note	= (MiNote)it.next();
		long	timeOn	= note.getTimeOn(),
				timeOff	= note.getTimeOff();

		// absolute to relative time stamps
		timeOn	-= f_StartTime;
		timeOff	-= f_StartTime;

		// time stamps to ticks
		timeOn	= inStartPosition + timestampToTicks(inTempo, timeOn);
		timeOff	= inStartPosition + timestampToTicks(inTempo, timeOff);

		// relative to absolute ticks
		timeOn	+= inStartPosition;
		timeOff	+= inStartPosition;

		// force to imposed resolution
		long	attack	= (timeOn  / timeResolution) * timeResolution,
				release	= (timeOff / timeResolution) * timeResolution;

		if((timeOn % timeResolution) > timeResolution / 2)
			attack += timeResolution;

		if((timeOff % timeResolution) > timeResolution / 2)
			release += timeResolution;

		// update values
		note.setTimeOn(attack);
		note.setTimeOff(release);
		}
	}


	public void		toTrack(int inTempo, int inNoteType, long inStartPosition)
	{
		dump("before normalization");
	normalize(inTempo, inNoteType, inStartPosition);
		dump("after normalization");
		System.out.println();

	TGSongManager	tgSongMgr	= TuxGuitar.instance().getSongManager();
	TGTrackManager	tgTrackMgr	= tgSongMgr.getTrackManager();
	TGTrack			tgTrack		= tgSongMgr.createTrack();

	tgTrack.setName("Nuovo input MIDI");

	boolean		firstLoop = true;

	while(!f_Notes.isEmpty())
		{
		Iterator	it = f_Notes.iterator();

		while(it.hasNext())
			{
			MiNote			note	= (MiNote)it.next();
			TGMeasureHeader	mh		= tgSongMgr.getMeasureHeaderAt(note.getTimeOn());

			// add measures as needed
			while(mh == null)
				{
				tgSongMgr.addNewMeasure(tgSongMgr.getSong().countMeasureHeaders() + 1);
				mh = tgSongMgr.getMeasureHeaderAt(note.getTimeOn());
				}

			TGMeasure	tgMeasure	= tgTrackMgr.getMeasureAt(tgTrack, note.getTimeOn());

			if(tgMeasure != null)
				{
				TGBeat		tgBeat		= null;
				TGNote		tgNote		= tgSongMgr.getFactory().newNote();
				TGDuration	tgDuration	= tgSongMgr.getFactory().newDuration();

				tgNote.setString	(note.getString());
				tgNote.setValue		(note.getFret());
				tgNote.setVelocity	(note.getVelocity());

				// I would prefer using dotted notes instead of legato,
				// but TuxGuitar does not allow for more than one dot per note...
				tgNote.setTiedNote	(!firstLoop);

				// look for an existing beat to reuse
				tgBeat = tgSongMgr.getMeasureManager().getBeat(tgMeasure, note.getTimeOn());

				// create beat if needed
				if(tgBeat == null)
					{
					tgBeat = tgSongMgr.getFactory().newBeat();
						tgBeat.setStart(note.getTimeOn()
								// this is bad, but TuxGuitar position seems wrong, start should not be set at first note end
								- TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getDuration().getTime());
					tgMeasure.addBeat(tgBeat);
					}

				// determine note type, update note-on time, update list
				int		noteType = durationToNoteType(note.getDuration());

				note.setTimeOn(note.getTimeOn() + noteToTicks(noteType));

				if(note.getTimeOn() >= note.getTimeOff())
					it.remove();

				tgDuration.setValue(noteType);

	System.out.println(
			"measure: "	+ tgMeasure.getNumber() +
			", beat: "	+ tgBeat.getStart() +
			", string: "+ tgNote.getString() +
			", fret: "	+ tgNote.getValue() +
			", vel: "	+ tgNote.getVelocity() +
			", tied: "	+ tgNote.isTiedNote() +
			", durValue: "	+ tgDuration.getValue() +
			", durTime: "	+ tgDuration.getTime());

				// here we probably should choose the voice
				// it would be nice to have one voice for each string...
				tgBeat.getVoice(0).setDuration(tgDuration);
				tgBeat.getVoice(0).addNote(tgNote);

			//	tgSongMgr.getMeasureManager().addNote(tgBeat, tgNote, tgDuration, 0);
			//	tgSongMgr.getMeasureManager().moveVoices(tgMeasure, tgBeat.getStart(), tgBeat.getVoice(/*caret.getVoice()*/0).getDuration().getTime(), /*caret.getVoice()*/0, tgBeat.getVoice(/*caret.getVoice()*/0).getDuration());
				}
			}

		firstLoop = false;
		}

	//tgSongMgr.autoCompleteSilences();
	}
}
