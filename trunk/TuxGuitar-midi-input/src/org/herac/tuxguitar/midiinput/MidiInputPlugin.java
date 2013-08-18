package org.herac.tuxguitar.midiinput;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.plugins.base.TGToolItemPlugin;
import org.herac.tuxguitar.app.util.MessageDialog;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MidiInputPlugin
	extends TGToolItemPlugin
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
	public String getModuleId()
	{
	return MiPlugin.MODULE_ID;
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
