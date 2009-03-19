package org.herac.tuxguitar.midiinput;

import org.eclipse.swt.widgets.Shell;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.gui.system.plugins.base.TGToolItemPlugin;
import org.herac.tuxguitar.gui.system.plugins.TGPluginSetup;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;

public class MidiInputPlugin
	extends TGToolItemPlugin
	implements TGPluginSetup
{
	public void setEnabled(boolean enabled)
		throws TGPluginException
	{
	if(enabled)
		{
		// try to setup the environment according to the user's preferences

		String	notesDeviceName = MiConfig.instance().getMidiInputPortName();

		if(notesDeviceName != null)
			{
			try {
				MiPort.setNotesPort(notesDeviceName);
				}
			catch(MiException mie)
				{
				MessageDialog.errorMessage(mie);
				}
			}

		MiProvider.instance().setBaseChannel	(MiConfig.instance().getMidiBaseChannel());
		MiProvider.instance().setMode			(MiConfig.instance().getMode());
		MiProvider.instance().setMinVelocity	((byte)MiConfig.instance().getMinVelocity());
		MiProvider.instance().setMinDuration	(MiConfig.instance().getMinDuration());
		MiProvider.instance().setEchoTimeOut	(MiConfig.instance().getEchoTimeOut());
		MiProvider.instance().setInputTimeOut	(MiConfig.instance().getInputTimeOut());
		}
	else
		{
		// try to cleanup the environment

		try {
			MiPort.setNotesPort(null);
			MiPort.setControlPort(null);
			}
		catch(MiException mie)
			{
			MessageDialog.errorMessage(mie);
			}
		}

	super.setEnabled(enabled);
	}

	/*
	 *	TGPlugin implementation
	 */

	public String getAuthor()
	{
	return "Amedeo Farello <afarello@tiscalinet.it>";
	}


	public String getDescription()
	{
	return "Supports MIDI equipped guitars";
	}


	public String getName()
	{
	return "MIDI input plugin";
	}


	public String getVersion()
	{
	return "1.0";
	}

	/*
	 *	TGPluginSetup implementation
	 */

	public void setupDialog(Shell parent)
	{
	MiConfig.instance().showDialog(parent);
	}

	/*
	 *	TGToolItemPlugin implementation
	 */

	protected void doAction()
	{
	MiPanel.instance().showDialog(TuxGuitar.instance().getShell());
	}


	protected String getItemName()
	{
	return "MIDI input";
	}
}
