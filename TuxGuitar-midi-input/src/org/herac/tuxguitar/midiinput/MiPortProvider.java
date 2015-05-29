package org.herac.tuxguitar.midiinput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import org.herac.tuxguitar.app.TuxGuitar;

public class MiPortProvider
{	
	public static List<String> listPortsNames() throws MiException
	{
	try {
		List<String>		portsNames	= new ArrayList<String>();
		MidiDevice.Info[]	infos		= MidiSystem.getMidiDeviceInfo();
		
		for(int i = 0; i < infos.length; i++)
			{
			try {
				Iterator<String>	it		= portsNames.iterator();
				boolean		exists	= false;
				
				while(it.hasNext())
					{
					if(((String)it.next()).equals(infos[i].getName()))
						{
						exists = true;
						break;
						}
					}

				if(!exists)
					{
					MidiDevice	device = MidiSystem.getMidiDevice(infos[i]);

					if(	device.getMaxTransmitters() == 0 ||
						device instanceof Sequencer)
						continue;

					portsNames.add(infos[i].getName());
					}

				}
			catch (MidiUnavailableException mue)
				{
				throw new MiException(TuxGuitar.getProperty("midiinput.error.midi.unavailable"), mue);
				}
			}
		
		return portsNames;
		}
	catch(Throwable t)
		{
		throw new MiException(TuxGuitar.getProperty("midiinput.error.unknown"), t);
		}
	}


	public static MidiDevice getDevice(String inDeviceName) throws MiException
	{
	MidiDevice.Info[]	infos = MidiSystem.getMidiDeviceInfo();

	for(int i = 0; i < infos.length; i++)
		{
		if(infos[i].getName().equals(inDeviceName))
			{
			try {
				MidiDevice device = MidiSystem.getMidiDevice(infos[i]);

				if(	device.getMaxTransmitters() == 0 ||
					device instanceof Sequencer)
					continue;

				return(device);
				}
			catch(MidiUnavailableException mue)
				{
				throw new MiException(TuxGuitar.getProperty("midiinput.error.midi.unavailable"), mue);
				}
			}
		}

	return(null);
	}
}
