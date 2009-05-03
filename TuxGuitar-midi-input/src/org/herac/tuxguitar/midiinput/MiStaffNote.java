package org.herac.tuxguitar.midiinput;

import org.herac.tuxguitar.song.models.TGDuration;

class MiStaffNote
{
	static final int	SIXTY_FOURTH		= (int)TGDuration.QUARTER_TIME / 16;
	static final int	SIXTY_FOURTH_d		= SIXTY_FOURTH + SIXTY_FOURTH / 2;
	static final int	SIXTY_FOURTH_dd		= SIXTY_FOURTH_d + SIXTY_FOURTH / 4;
	static final int	THIRTY_SECOND		= (int)TGDuration.QUARTER_TIME / 8;
	static final int	THIRTY_SECOND_d		= THIRTY_SECOND + THIRTY_SECOND / 2;
	static final int	THIRTY_SECOND_dd	= THIRTY_SECOND_d + THIRTY_SECOND / 4;
	static final int	SIXTEENTH			= (int)TGDuration.QUARTER_TIME / 4;
	static final int	SIXTEENTH_d			= SIXTEENTH + SIXTEENTH / 2;
	static final int	SIXTEENTH_dd		= SIXTEENTH_d + SIXTEENTH / 4;
	static final int	EIGHTH				= (int)TGDuration.QUARTER_TIME / 2;
	static final int	EIGHTH_d			= EIGHTH + EIGHTH / 2;
	static final int	EIGHTH_dd			= EIGHTH_d + EIGHTH / 4;
	static final int	QUARTER				= (int)TGDuration.QUARTER_TIME;
	static final int	QUARTER_d			= QUARTER + QUARTER / 2;
	static final int	QUARTER_dd			= QUARTER_d + QUARTER / 4;
	static final int	HALF				= (int)TGDuration.QUARTER_TIME * 2;
	static final int	HALF_d				= HALF + HALF / 2;
	static final int	HALF_dd				= HALF_d + HALF / 4;
	static final int	WHOLE				= (int)TGDuration.QUARTER_TIME *  4;

	private byte	f_String;		// 1-based string index
	private byte	f_Fret;			// 0-based fret index
	private byte	f_Pitch;		// MIDI note pitch
	private byte	f_Velocity;		// MIDI note velocity
	private long	f_Duration;		// nominal duration [ticks]
	private byte	f_DotCount;		// number of dots
	private boolean	f_IsTied;		// true if this is a tied note


	public	MiStaffNote(MiNote inNote)
	{
	f_String	= inNote.getString();
	f_Fret		= inNote.getFret();
	f_Pitch		= inNote.getPitch();
	f_Velocity	= inNote.getVelocity();
	f_Duration	= inNote.getDuration();
	f_DotCount	= 0;
	f_IsTied	= false;
	}


	public	MiStaffNote(MiStaffNote inNote)
	{
	f_String	= inNote.f_String;
	f_Fret		= inNote.f_Fret;
	f_Pitch		= inNote.f_Pitch;
	f_Velocity	= inNote.f_Velocity;
	f_Duration	= inNote.f_Duration;
	f_DotCount	= inNote.f_DotCount;
	f_IsTied	= inNote.f_IsTied;
	}


	byte	getString()		{ return f_String; }
	byte	getFret()		{ return f_Fret; }
	byte	getPitch()		{ return f_Pitch; }
	byte	getVelocity()	{ return f_Velocity; }

	void	setTied(boolean	inTied)			{ f_IsTied = inTied; }
	boolean	isTied()						{ return f_IsTied; }

	void	setDuration	(long inDuration)	{ f_Duration = inDuration; f_DotCount = 0; }
	long	getNominalDuration()			{ return f_Duration; }
	byte	getDotCount()					{ return f_DotCount; }


	long	getOverallDuration()
	{
	long	time = f_Duration;
	
	if(f_DotCount == 1)
		time += time / 2;
	else if(f_DotCount == 2)
		time += ((time / 4) * 3);

	return(time);
	}


	public String	toString()
	{
	String	out =
		"(" + f_String + "," + f_Fret + ") " +
		"("	+ f_Pitch  + "," + f_Velocity + ") " +
		"d " + getOverallDuration();

	if(f_DotCount == 1)
		out += " (" + f_Duration + "*)";
	else if(f_DotCount == 2)
		out += " (" + f_Duration + "**)";

	return(out);
	}


	private long	nd3(long inBottom, long inTop, long inTime)
	{
	long	mid = inBottom + (inTop - inBottom) / 2;

	return(inTime <= mid ? inBottom : inTop);
	}


	private long	nd2(long inDuration)
	{
		 if(inDuration < SIXTY_FOURTH)		return(SIXTY_FOURTH);
	else if(inDuration < SIXTY_FOURTH_d)	return(nd3(SIXTY_FOURTH,	SIXTY_FOURTH_d,		inDuration));
	else if(inDuration < SIXTY_FOURTH_dd)	return(nd3(SIXTY_FOURTH_d,	SIXTY_FOURTH_dd,	inDuration));

	else if(inDuration < THIRTY_SECOND)		return(nd3(SIXTY_FOURTH_dd,	THIRTY_SECOND,		inDuration));
	else if(inDuration < THIRTY_SECOND_d)	return(nd3(THIRTY_SECOND,	THIRTY_SECOND_d,	inDuration));
	else if(inDuration < THIRTY_SECOND_dd)	return(nd3(THIRTY_SECOND_d,	THIRTY_SECOND_dd,	inDuration));

	else if(inDuration < SIXTEENTH)			return(nd3(THIRTY_SECOND_dd,SIXTEENTH,			inDuration));
	else if(inDuration < SIXTEENTH_d)		return(nd3(SIXTEENTH, 		SIXTEENTH_d,		inDuration));
	else if(inDuration < SIXTEENTH_dd)		return(nd3(SIXTEENTH_d, 	SIXTEENTH_dd,		inDuration));

	else if(inDuration < EIGHTH)			return(nd3(SIXTEENTH_dd,	EIGHTH,				inDuration));
	else if(inDuration < EIGHTH_d)			return(nd3(EIGHTH, 			EIGHTH_d,			inDuration));
	else if(inDuration < EIGHTH_dd)			return(nd3(EIGHTH_d,		EIGHTH_dd,			inDuration));

	else if(inDuration < QUARTER)			return(nd3(EIGHTH_dd,		QUARTER,			inDuration));
	else if(inDuration < QUARTER_d)			return(nd3(QUARTER,			QUARTER_d,			inDuration));
	else if(inDuration < QUARTER_dd)		return(nd3(QUARTER_d,		QUARTER_dd,			inDuration));

	else if(inDuration < HALF)				return(nd3(QUARTER_dd,		HALF,				inDuration));
	else if(inDuration < HALF_d)			return(nd3(HALF,			HALF_d,				inDuration));
	else if(inDuration < HALF_dd)			return(nd3(HALF_d,			HALF_dd,			inDuration));

	else if(inDuration < WHOLE)				return(nd3(HALF_dd,			WHOLE,				inDuration));
	else									return(WHOLE);
	}


	void	normalizeDuration()
	{
	switch((int)nd2(getOverallDuration()))
		{
		case SIXTY_FOURTH:		f_Duration = SIXTY_FOURTH;	f_DotCount = 0;	break;
		case SIXTY_FOURTH_d:	f_Duration = SIXTY_FOURTH;	f_DotCount = 1;	break;
		case SIXTY_FOURTH_dd:	f_Duration = SIXTY_FOURTH;	f_DotCount = 2;	break;
		case THIRTY_SECOND:		f_Duration = THIRTY_SECOND;	f_DotCount = 0;	break;
		case THIRTY_SECOND_d:	f_Duration = THIRTY_SECOND;	f_DotCount = 1;	break;
		case THIRTY_SECOND_dd:	f_Duration = THIRTY_SECOND;	f_DotCount = 2;	break;
		case SIXTEENTH:			f_Duration = SIXTEENTH;		f_DotCount = 0;	break;
		case SIXTEENTH_d:		f_Duration = SIXTEENTH;		f_DotCount = 1;	break;
		case SIXTEENTH_dd:		f_Duration = SIXTEENTH;		f_DotCount = 2;	break;
		case EIGHTH:			f_Duration = EIGHTH;		f_DotCount = 0;	break;
		case EIGHTH_d:			f_Duration = EIGHTH;		f_DotCount = 1;	break;
		case EIGHTH_dd:			f_Duration = EIGHTH;		f_DotCount = 2;	break;
		case QUARTER:			f_Duration = QUARTER;		f_DotCount = 0;	break;
		case QUARTER_d:			f_Duration = QUARTER;		f_DotCount = 1;	break;
		case QUARTER_dd:		f_Duration = QUARTER;		f_DotCount = 2;	break;
		case HALF:				f_Duration = HALF;			f_DotCount = 0;	break;
		case HALF_d:			f_Duration = HALF;			f_DotCount = 1;	break;
		case HALF_dd:			f_Duration = HALF;			f_DotCount = 2;	break;
		case WHOLE:				f_Duration = WHOLE;			f_DotCount = 0;	break;
		}
	}


	static	long	noteToTicks(int inNoteType)
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


	static int	durationToNoteType(long inDuration)
	{
	// converts duration [ticks] into note type
		
	if(inDuration >= noteToTicks(TGDuration.WHOLE))					return(TGDuration.WHOLE);
	else if(inDuration >= noteToTicks(TGDuration.HALF))				return(TGDuration.HALF);
	else if(inDuration >= noteToTicks(TGDuration.QUARTER))			return(TGDuration.QUARTER);
	else if(inDuration >= noteToTicks(TGDuration.EIGHTH))			return(TGDuration.EIGHTH);
	else if(inDuration >= noteToTicks(TGDuration.SIXTEENTH))		return(TGDuration.SIXTEENTH);
	else if(inDuration >= noteToTicks(TGDuration.THIRTY_SECOND))	return(TGDuration.THIRTY_SECOND);
	else															return(TGDuration.SIXTY_FOURTH);
	}
}
