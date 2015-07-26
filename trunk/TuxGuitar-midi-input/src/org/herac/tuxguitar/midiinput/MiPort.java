package org.herac.tuxguitar.midiinput;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import org.herac.tuxguitar.app.TuxGuitar;

public class MiPort
	implements Receiver
{
	private MidiDevice	f_Device;
	private Transmitter	f_Transmitter;
	
	private static MiPort	s_NotesPort;
	private static MiPort	s_ControlPort;


	private MiPort(MidiDevice inDevice)
	{
	f_Device = inDevice;
	}


	public String getName()
	{
	return f_Device.getDeviceInfo().getName();
	}


	protected void connectTransmitter(Transmitter inTransmitter)
	{
	f_Transmitter = inTransmitter;

	if(f_Transmitter != null)
		f_Transmitter.setReceiver(this);
	}


	protected synchronized void openPort()
		throws MiException
	{
	try {
		if(!f_Device.isOpen()) {
			try {
				f_Device.open();
			} catch(MidiUnavailableException e){
				throw new MiException(e.getMessage(), e);
			}
//			final MidiDevice device = f_Device;
//			TGSynchronizer.instance().execute(new Runnable() {
//				public void run() throws TGException {
//					try {
//						device.open();
//					} catch(MidiUnavailableException e){
//						throw new TGException(e);
//					}
//				}
//			});
		}

		if(f_Transmitter == null) {
			try {
				connectTransmitter(f_Device.getTransmitter());
			} catch(MidiUnavailableException e){
				throw new MiException(e.getMessage(), e);
			}
//			final MidiDevice device = f_Device;
//			TGSynchronizer.instance().execute(new Runnable() {
//				public void run() throws TGException {
//					try {
//						connectTransmitter(device.getTransmitter());
//					} catch(MidiUnavailableException e){
//						throw new TGException(e);
//					}
//				}
//			});
		}
	}
	catch(Throwable t) {
		throw new MiException(TuxGuitar.getProperty("midiinput.error.midi.port.open"), t);
		}
	}


	protected synchronized void closePort()
		throws MiException
	{
	try {
		if(f_Transmitter != null) {
			f_Transmitter.close();
			connectTransmitter(null);
		}
//			final Transmitter transmitter = f_Transmitter;
//			TGSynchronizer.instance().execute(new Runnable() {
//				public void run() throws TGException {
//					transmitter.close();
//					connectTransmitter(null);
//					}
//				});
//			}

		if(f_Device.isOpen()) {
			f_Device.close();
//			final MidiDevice device = f_Device;
//			TGSynchronizer.instance().execute(new Runnable() {
//				public void run() throws TGException {
//					device.close();
//					}
//				});
		}
	}
	catch(Throwable t) {
		throw new MiException(TuxGuitar.getProperty("midiinput.error.midi.port.close"), t);
		}
	}

	/*
	 *	Notes port management
	 */

	public static void setNotesPort(String inDeviceName)
		throws MiException
	{
	if(s_NotesPort != null)
		s_NotesPort.closePort();

	MidiDevice	device = MiPortProvider.getDevice(inDeviceName);

	if(device != null)
		{
		s_NotesPort = new MiPort(device);
		s_NotesPort.openPort();
		}
	}


	public static long getNotesPortTimeStamp()
	{
	if(s_NotesPort != null)
		return(s_NotesPort.f_Device.getMicrosecondPosition());
	else
		return(-1);
	}

	/*
	 *	Control port management
	 */

	public static void setControlPort(String inDeviceName)
		throws MiException
	{
	if(s_ControlPort != null)
		s_ControlPort.closePort();

	MidiDevice	device = MiPortProvider.getDevice(inDeviceName);

	if(device != null)
		{
		s_ControlPort = new MiPort(device);
		s_ControlPort.openPort();
		}
	}

	/*
	 *	javax.sound.midi.Receiver implementation
	 */
	
	public void close()
	{	
	}


	public void send(MidiMessage inMessage, long inTimeStamp)
	{
	if(inMessage instanceof ShortMessage)
		{
		ShortMessage	mm = (ShortMessage)inMessage;
		
		switch(mm.getCommand())
			{
			case ShortMessage.NOTE_ON:
			case ShortMessage.NOTE_OFF:
				MiProvider.instance().noteReceived(mm, inTimeStamp);
				break;

			case ShortMessage.CONTROL_CHANGE:
				//System.err.println("Control change");
				break;
			}
		}
	}
}
