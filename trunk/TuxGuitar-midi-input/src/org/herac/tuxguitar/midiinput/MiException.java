package org.herac.tuxguitar.midiinput;

public class MiException
	extends Exception
{
	private static final long serialVersionUID = 1L;

	public MiException(String message)
	{
	super(message);
	}

	public MiException(String message, Throwable cause)
	{
	super(message, cause);
	}
}
