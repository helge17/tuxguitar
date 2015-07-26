package org.herac.tuxguitar.midiinput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGNote;


class MiBuffer
{
	private List<MiNote>	f_Notes			= new ArrayList<MiNote>();	// time-ordered list of notes
	private List<MiNote>	f_NoteOffMap	= new ArrayList<MiNote>();	// time-ordered map of NOTE_OFF events
	private	long		f_StartTime		= -1;				// first MIDI time stamp [microseconds]
	private	long		f_StopTime		= -1;				// last MIDI time stamp [microseconds]

	private	boolean		s_TESTING = false;


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


	static void	dump(List<MiNote> inList, String inTitle)
	{
	Iterator<MiNote>	it = inList.iterator();

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
	if(s_TESTING)
		{
		int	tempo = MiRecorder.instance().getTempo();

		f_StartTime	= MiStaff.ticksToTimestamp(tempo, 0);
		f_StopTime	= MiStaff.ticksToTimestamp(tempo, 15698);
/*
		// a very long note
		addEventDebug(tempo,     0, 4, 50, 81);
		addEventDebug(tempo, 15698, 4, 50, 0);
*/
		// a short piece
		addEventDebug(tempo,   873, 1, 66, 96);
		addEventDebug(tempo,  1054, 1, 66, 0);
		addEventDebug(tempo,  1056, 1, 67, 81);
		addEventDebug(tempo,  1307, 1, 67, 0);
		addEventDebug(tempo,  1310, 1, 68, 67);
		addEventDebug(tempo,  1369, 1, 68, 0);
		addEventDebug(tempo,  2549, 1, 69, 78);
		addEventDebug(tempo,  2841, 1, 69, 0);

		addEventDebug(tempo,   852, 2, 62, 106);
		addEventDebug(tempo,  1301, 2, 62, 0);
		addEventDebug(tempo,  1304, 2, 61, 32);
		addEventDebug(tempo,  1389, 2, 61, 0);
		addEventDebug(tempo,  2545, 2, 62, 81);
		addEventDebug(tempo,  2765, 2, 62, 0);
		addEventDebug(tempo,  3418, 2, 65, 78);
		addEventDebug(tempo,  4172, 2, 65, 0);
		addEventDebug(tempo,  4281, 2, 67, 56);
		addEventDebug(tempo,  4433, 2, 67, 0);
		addEventDebug(tempo,  6027, 2, 59, 79);
		addEventDebug(tempo,  6627, 2, 59, 0);
		addEventDebug(tempo,  7657, 2, 60, 114);
		addEventDebug(tempo,  7879, 2, 60, 0);
		addEventDebug(tempo,  7881, 2, 59, 81);
		addEventDebug(tempo,  8434, 2, 59, 0);
		addEventDebug(tempo,  9780, 2, 62, 103);
		addEventDebug(tempo, 10072, 2, 62, 0);
		addEventDebug(tempo, 10074, 2, 63, 40);
		addEventDebug(tempo, 10146, 2, 63, 0);
		addEventDebug(tempo, 10266, 2, 66, 107);
		addEventDebug(tempo, 11002, 2, 66, 0);
		addEventDebug(tempo, 11535, 2, 64, 76);
		addEventDebug(tempo, 13661, 2, 64, 0);

		addEventDebug(tempo,   473, 3, 57, 51);
		addEventDebug(tempo,  1241, 3, 57, 0);
		addEventDebug(tempo,  1242, 3, 56, 9);
		addEventDebug(tempo,  1424, 3, 56, 0);
		addEventDebug(tempo,  2540, 3, 58, 68);
		addEventDebug(tempo,  2772, 3, 58, 0);
		addEventDebug(tempo,  3412, 3, 60, 73);
		addEventDebug(tempo,  4227, 3, 60, 0);
		addEventDebug(tempo,  6024, 3, 60, 58);
		addEventDebug(tempo,  6427, 3, 60, 0);
		addEventDebug(tempo,  6430, 3, 59, 17);
		addEventDebug(tempo,  6591, 3, 59, 0);
		addEventDebug(tempo,  6594, 3, 55, 1);
		addEventDebug(tempo,  6612, 3, 55, 0);
		addEventDebug(tempo,  7660, 3, 57, 103);
		addEventDebug(tempo,  8096, 3, 57, 0);
		addEventDebug(tempo,  8099, 3, 57, 107);
		addEventDebug(tempo,  8200, 3, 57, 0);
		addEventDebug(tempo,  8203, 3, 56, 79);
		addEventDebug(tempo,  8230, 3, 56, 0);
		addEventDebug(tempo,  8232, 3, 55, 77);
		addEventDebug(tempo,  8491, 3, 55, 0);
		addEventDebug(tempo,  9407, 3, 57, 87);
		addEventDebug(tempo, 10048, 3, 57, 0);
		addEventDebug(tempo, 10255, 3, 61, 74);
		addEventDebug(tempo, 10874, 3, 61, 0);
		addEventDebug(tempo, 10877, 3, 60, 23);
		addEventDebug(tempo, 10973, 3, 60, 0);
		addEventDebug(tempo, 10975, 3, 55, 17);
		addEventDebug(tempo, 11084, 3, 55, 0);
		addEventDebug(tempo, 11531, 3, 62, 70);
		addEventDebug(tempo, 13649, 3, 62, 0);

		addEventDebug(tempo,     0, 4, 50, 81);
		addEventDebug(tempo,  1405, 4, 50, 0);
		addEventDebug(tempo,  1750, 4, 53, 98);
		addEventDebug(tempo,  2763, 4, 53, 0);
		addEventDebug(tempo,  3884, 4, 50, 51);
		addEventDebug(tempo,  4678, 4, 50, 0);
		addEventDebug(tempo,  5597, 4, 55, 77);
		addEventDebug(tempo,  6392, 4, 55, 0);
		addEventDebug(tempo,  8975, 4, 50, 71);
		addEventDebug(tempo, 11023, 4, 50, 0);
		addEventDebug(tempo, 11081, 4, 50, 46);
		addEventDebug(tempo, 11311, 4, 50, 0);
		addEventDebug(tempo, 11524, 4, 55, 76);
		addEventDebug(tempo, 14489, 4, 55, 0);

		addEventDebug(tempo,  4783, 5, 45, 90);
		addEventDebug(tempo,  8521, 5, 45, 0);

		addEventDebug(tempo,  3430, 6, 46, 94);
		addEventDebug(tempo,  4506, 6, 46, 0);
		addEventDebug(tempo,  4509, 6, 40, 17);
		addEventDebug(tempo,  6680, 6, 40, 0);
		addEventDebug(tempo,  6868, 6, 41, 99);
		addEventDebug(tempo,  8180, 6, 41, 0);
		addEventDebug(tempo,  8182, 6, 40, 83);
		addEventDebug(tempo,  8486, 6, 40, 0);
		addEventDebug(tempo,  8587, 6, 43, 95);
		addEventDebug(tempo, 10010, 6, 43, 0);
		addEventDebug(tempo, 10275, 6, 46, 99);
		addEventDebug(tempo, 10302, 6, 46, 0);
		addEventDebug(tempo, 10304, 6, 47, 99);
		addEventDebug(tempo, 10943, 6, 47, 0);
		addEventDebug(tempo, 10946, 6, 40, 51);
		addEventDebug(tempo, 11350, 6, 40, 0);
		addEventDebug(tempo, 11492, 6, 45, 114);
		addEventDebug(tempo, 15698, 6, 45, 0);

		}

	Iterator<MiNote>	onIt;

	// determine notes duration
	onIt = f_Notes.iterator();
	while(onIt.hasNext())
		{
		MiNote	on = (MiNote)onIt.next();

		Iterator<MiNote>	offIt = f_NoteOffMap.iterator();
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
	TGSongManager	tgSongMgr	= TuxGuitar.getInstance().getSongManager();
	TGChord			tgChord		= tgSongMgr.getFactory().newChord(inStringsCount);
	Iterator<MiNote> it			= f_Notes.iterator();

	while(it.hasNext())
		{
		MiNote	note = (MiNote)it.next();

		tgChord.addFretValue(note.getString() - 1, note.getFret());
		}

	return(tgChord);
	}


	public TGBeat	toBeat()
	{
	TGSongManager	tgSongMgr	= TuxGuitar.getInstance().getSongManager();
	TGBeat			tgBeat		= tgSongMgr.getFactory().newBeat();
	Iterator<MiNote> it			= f_Notes.iterator();

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


	public TreeSet<Byte>	toPitchesSet()
	{
	TreeSet<Byte>		pitches	= new TreeSet<Byte>();
	Iterator<MiNote>	it		= f_Notes.iterator();

	while(it.hasNext())
		{
		MiNote	note = (MiNote)it.next();

		pitches.add(new Byte(note.getPitch()));
		}

	return(pitches);
	}


	private	void	addEventDebug(int inTempo, long inTick, int inString, int inPitch, int inVelocity)
	{
	// to facilitate tests using output from Midi2Mtx
	addEvent(
		(byte)inString,
		(byte)MiProvider.getFret(inPitch, inString),
		(byte)inPitch,
		(byte)inVelocity,
		MiStaff.ticksToTimestamp(inTempo, inTick));
	}


	public void		toTrack(int inTempo, long inStartPosition, String inTrackName)
	{
	/*MiStaff	staff = */new MiStaff(f_Notes, inTempo, f_StartTime, f_StopTime, inStartPosition, inTrackName);
	}
}
