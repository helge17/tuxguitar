package org.herac.tuxguitar.midiinput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.error.TGErrorManager;


class MiStaffEvent
{
	static final byte	NONE		= 0;
	static final byte	BAR			= 1;
	static final byte	ON_BEAT		= 2;
	static final byte	OFF_BEAT	= 4;
	static final byte	TIE_BEAT	= 8;

	private byte		f_Type		= NONE;				// event type
	private List<MiStaffNote>	f_Notes		= new ArrayList<MiStaffNote>();	// note on list
	private TGBeat		f_Beat;							// TuxGuitar beat (optional)
	private long		f_BeginTime;					// begin time [ticks]


	public	MiStaffEvent(long inBeginTime)
	{
	f_BeginTime = inBeginTime;
	}


	boolean		isBar()			{ return((f_Type & BAR) == BAR); }
	boolean		isOnBeat()		{ return((f_Type & ON_BEAT) == ON_BEAT); }
	boolean		isOffBeat()		{ return((f_Type & OFF_BEAT) == OFF_BEAT); }
	boolean		isTieBeat()		{ return((f_Type & TIE_BEAT) == TIE_BEAT); }
	List<MiStaffNote>	    getNotes()		{ return(f_Notes); }
	long		getBeginTime()	{ return(f_BeginTime); }

	void		setBeat(TGBeat inBeat)	{ f_Beat = inBeat; }
	TGBeat		getBeat()				{ return(f_Beat); }


	public String	toString()
	{
	String	out = Long.toString(f_BeginTime);

	if(isBar())
		out += " BAR";

	out += System.getProperty("line.separator");

	if(!f_Notes.isEmpty())
		{
		for(Iterator<MiStaffNote> it = f_Notes.iterator(); it.hasNext();)
			{
			MiStaffNote	sn	= (MiStaffNote)it.next();

			out += (sn.isTied() ? "   T: " : "   N: ");
			out += sn;
			out += " ->" + (f_BeginTime + sn.getOverallDuration());
			out += System.getProperty("line.separator");
			}
		}

	return(out);
	}


	void	markAsBar()
	{
	f_Type |= BAR;
	}


	void	addNoteOn(MiNote inNote)
	{
	MiStaffNote	sn = new MiStaffNote(inNote);

	sn.setTied(false);
	f_Type |= ON_BEAT;
	f_Notes.add(sn);
	}


	void	addTiedNote(MiStaffNote inSN)
	{
	inSN.setTied(true);
	f_Type |= TIE_BEAT;
	f_Notes.add(inSN);
	}


	void	merge(MiStaffEvent inEvent)
	{
	try	{
		if(f_Beat != null)
			throw new Exception("f_Beat non � nullo!");

		if(f_BeginTime != inEvent.f_BeginTime)
			throw new Exception("f_BeginTime � diverso, old: " + f_BeginTime + " new: " + inEvent.f_BeginTime);
/*
		if(f_EndTime != inEvent.f_EndTime)
			throw new Exception("f_EndTime � diverso, old: " + f_EndTime + " new: " + inEvent.f_EndTime);
*/
		f_Type |= inEvent.f_Type;
		f_Notes.addAll(inEvent.f_Notes);
		}
	catch(Throwable throwable)
		{
		TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(throwable);
		}
	}


	private long	normalize(long inTime, long inResolution)
	{
	long	time = (inTime  / inResolution) * inResolution;

	if((inTime % inResolution) > inResolution / 2)
		time += inResolution;

	return(time);
	}


	void	normalizeBeat(int inNoteType)
	{
	long	resolution = MiStaffNote.noteToTicks(inNoteType);

	f_BeginTime = normalize(f_BeginTime, resolution);
	}


	void	normalizeDurations()
	{
	for(Iterator<MiStaffNote> it = f_Notes.iterator(); it.hasNext();)
		{
		MiStaffNote	sn	= (MiStaffNote)it.next();
		sn.normalizeDuration();
		}
	}
}
