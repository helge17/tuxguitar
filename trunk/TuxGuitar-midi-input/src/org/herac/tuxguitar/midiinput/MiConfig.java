package org.herac.tuxguitar.midiinput;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;
import org.herac.tuxguitar.util.error.TGErrorManager;

class MiConfig
{
	private			TGConfigManager	f_Config;
	static final	String			KEY_MIDI_INPUT_PORT		= "midi.input.port";
	static final	String			KEY_MIDI_BASE_CHANNEL	= "midi.input.base.channel";
	static final	String			KEY_MIN_VELOCITY		= "midi.input.min.velocity";
	static final	String			KEY_MIN_DURATION		= "midi.input.min.duration";
	static final	String			KEY_MODE				= "midi.input.mode";
	static final	String			KEY_ECHO_TIMEOUT		= "midi.input.echo.timeout";
	static final	String			KEY_INPUT_TIMEOUT		= "midi.input.input.timeout";
	static final	String			KEY_CHORD_MODE			= "midi.input.chord.mode";
	// mancano le chiavi per le preferenze di registrazione
	static private	MiConfig		s_Instance;

	static final int	MIN_VELOCITY_THRESHOLD	=    0;	// minimum allowed value for f_MinVelocityThreshold
	static final int	MAX_VELOCITY_THRESHOLD	=   50;	// maximum allowed value for f_MinVelocityThreshold
	static final int	DEF_VELOCITY_THRESHOLD	=   40;	// default value for f_MinVelocityThreshold

	static final int	MIN_DURATION_THRESHOLD	=    0;	// minimum allowed value for f_MinDuration [msec]
	static final int	MAX_DURATION_THRESHOLD	=  100;	// maximum allowed value for f_MinDuration [msec]
	static final int	DEF_DURATION_THRESHOLD	=   25;	// default value for f_MinDuration [msec]

	static final int	MIN_ECHO_TIMEOUT		=  100;	// minimum allowed value for f_EchoTimeOut
	static final int	MAX_ECHO_TIMEOUT		= 1500;	// maximum allowed value for f_EchoTimeOut
	static final int	DEF_ECHO_TIMEOUT		=  500;	// default value for f_EchoTimeOut

	static final int	MIN_INPUT_TIMEOUT		=  100;	// minimum allowed value for f_InputTimeOut
	static final int	MAX_INPUT_TIMEOUT		= 1500;	// maximum allowed value for f_InputTimeOut
	static final int	DEF_INPUT_TIMEOUT		= 1000;	// default value for f_InputTimeOut

	static final int	CHORD_MODE_DIAGRAM		=    0;	// insert chord diagram only
	static final int	CHORD_MODE_ALL			=    1;	// insert chord diagram and modify staff

	static final int	MIN_COUNTDOWN_BARS		=    0;	// minimum allowed value for the # of count down bars before recording
	static final int	MAX_COUNTDOWN_BARS		=   16;	// maximum allowed value for the # of count down bars before recording
	static final int	DEF_COUNTDOWN_BARS		=    2;	// default value for the # of count down bars before recording

	static final int	MODE_FRETBOARD_ECHO		=    0;	// notes are simply echoed on TuxGuitar's fretboard, keyboard, etc.
	static final int	MODE_CHORDS_RECORDING	=    1;	// notes are used to insert a chord at current position
	static final int	MODE_SCALES_RECOGNITION	=    2;	// notes are used to identify a scale
	static final int	MODE_SONG_RECORDING		=    3;	// notes are recorded with their actual timing


	private	MiConfig()
	{
	super();
	}


	static MiConfig	instance()
	{
	if(s_Instance == null)
		s_Instance = new MiConfig();

	return s_Instance;
	}

	static void	init(TGContext context)
	{
		if( instance().f_Config == null ) {
			instance().f_Config = new TGConfigManager(context, "tuxguitar-midi-input");
		}
	}
	
	static TGConfigManager	getConfig()
	{
	return s_Instance.f_Config;
	}


	String	getMidiInputPortName()	{ return s_Instance.f_Config.getStringValue(KEY_MIDI_INPUT_PORT); }
	int		getMidiBaseChannel()	{ return s_Instance.f_Config.getIntegerValue(KEY_MIDI_BASE_CHANNEL, 0); }
	int		getMode()				{ return s_Instance.f_Config.getIntegerValue(KEY_MODE, MODE_FRETBOARD_ECHO); }
	int		getMinVelocity()		{ return s_Instance.f_Config.getIntegerValue(KEY_MIN_VELOCITY, DEF_VELOCITY_THRESHOLD); }
	int		getMinDuration()		{ return s_Instance.f_Config.getIntegerValue(KEY_MIN_DURATION, DEF_DURATION_THRESHOLD); }
	int		getEchoTimeOut()		{ return s_Instance.f_Config.getIntegerValue(KEY_ECHO_TIMEOUT, DEF_INPUT_TIMEOUT); }
	int		getInputTimeOut()		{ return s_Instance.f_Config.getIntegerValue(KEY_INPUT_TIMEOUT, DEF_INPUT_TIMEOUT); }
	int		getChordMode()			{ return s_Instance.f_Config.getIntegerValue(KEY_CHORD_MODE, CHORD_MODE_DIAGRAM); }


	void	showDialog(Shell parent)
	{
	try {
		final List<String> portsNames			= MiPortProvider.listPortsNames();
		final String	currPortName		= getMidiInputPortName();
		final int		currBaseChannel		= getMidiBaseChannel();
		final int		currMinVelocity		= getMinVelocity();
		final int		currMinDuration		= getMinDuration();
		final int		currEchoTimeOut		= getEchoTimeOut();
		final int		currInputTimeOut	= getInputTimeOut();
		//final int		currChordMode		= getChordMode();

		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("midiinput.config.title"));

		// MIDI
		Group	groupMidi = new Group(dialog, SWT.SHADOW_ETCHED_IN);
		groupMidi.setLayout(new GridLayout(2, false));
		groupMidi.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		groupMidi.setText(TuxGuitar.getProperty("midiinput.config.label.group.midi"));

		//------------------MIDI INPUT PORT------------------
		Label	lblPort = new Label(groupMidi, SWT.LEFT);
		lblPort.setText(TuxGuitar.getProperty("midiinput.config.label.port") + ":");

		final Combo	cmbPort = new Combo(groupMidi, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbPort.setLayoutData(new GridData(130, SWT.DEFAULT));

		for(int i = 0 ; i < portsNames.size(); i++)
			{
			String	portName = (String)portsNames.get(i);

			cmbPort.add(portName);

			if(portName.equals(currPortName))
				cmbPort.select(i);
			}

		//------------------MIDI BASE CHANNEL------------------
		Label	lblChannel = new Label(groupMidi, SWT.LEFT);
		lblChannel.setText(TuxGuitar.getProperty("midiinput.config.label.basechannel") + ":");

		final Combo	cmbChannel = new Combo(groupMidi, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbChannel.setLayoutData(new GridData(130, SWT.DEFAULT));

		for(int i = 1 ; i <= 16; i++)
			cmbChannel.add(Integer.toString(i));

		cmbChannel.select(currBaseChannel);

		// General input
		Group	groupInput = new Group(dialog, SWT.SHADOW_ETCHED_IN);
		groupInput.setLayout(new GridLayout(2, false));
		groupInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		groupInput.setText(TuxGuitar.getProperty("midiinput.config.label.group.input"));

		//------------------MIN VELOCITY THRESHOLD------------------
		Label	lblVelocity = new Label(groupInput, SWT.LEFT);
		lblVelocity.setText(TuxGuitar.getProperty("midiinput.config.label.minvelocity") + ":");

		final Spinner	spnMinVelocity = new Spinner(groupInput, SWT.BORDER);
		spnMinVelocity.setLayoutData(new GridData(130, SWT.DEFAULT));
		spnMinVelocity.setMinimum(MIN_VELOCITY_THRESHOLD);
		spnMinVelocity.setMaximum(MAX_VELOCITY_THRESHOLD);

		spnMinVelocity.setSelection(currMinVelocity);

		//------------------MIN VELOCITY DURATION------------------
		Label	lblDuration = new Label(groupInput, SWT.LEFT);
		lblDuration.setText(TuxGuitar.getProperty("midiinput.config.label.minduration") + ":");

		final Spinner	spnMinDuration = new Spinner(groupInput, SWT.BORDER);
		spnMinDuration.setLayoutData(new GridData(130, SWT.DEFAULT));
		spnMinDuration.setMinimum(MIN_DURATION_THRESHOLD);
		spnMinDuration.setMaximum(MAX_DURATION_THRESHOLD);

		spnMinDuration.setSelection(currMinDuration);

		// Echo/Chords/Scales
		Group	groupEcho = new Group(dialog, SWT.SHADOW_ETCHED_IN);
		groupEcho.setLayout(new GridLayout(2, false));
		groupEcho.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		groupEcho.setText(TuxGuitar.getProperty("midiinput.config.label.group.echo"));

		//------------------ECHO TIME OUT------------------
		Label	lblEchoTimeOut = new Label(groupEcho, SWT.LEFT);
		lblEchoTimeOut.setText(TuxGuitar.getProperty("midiinput.config.label.echotimeout") + ":");

		final Spinner	spnEchoTimeOut = new Spinner(groupEcho, SWT.BORDER);
		spnEchoTimeOut.setLayoutData(new GridData(130, SWT.DEFAULT));
		spnEchoTimeOut.setMinimum(MIN_ECHO_TIMEOUT);
		spnEchoTimeOut.setMaximum(MAX_ECHO_TIMEOUT);

		spnEchoTimeOut.setSelection(currEchoTimeOut);

		//------------------INPUT TIME OUT------------------
		Label	lblInputTimeOut = new Label(groupEcho, SWT.LEFT);
		lblInputTimeOut.setText(TuxGuitar.getProperty("midiinput.config.label.inputtimeout") + ":");

		final Spinner	spnInputTimeOut = new Spinner(groupEcho, SWT.BORDER);
		spnInputTimeOut.setLayoutData(new GridData(130, SWT.DEFAULT));
		spnInputTimeOut.setMinimum(MIN_INPUT_TIMEOUT);
		spnInputTimeOut.setMaximum(MAX_INPUT_TIMEOUT);

		spnInputTimeOut.setSelection(currInputTimeOut);

		//------------------CHORD MODE------------------
		Label	lblMode = new Label(groupEcho, SWT.LEFT);
		lblMode.setText(TuxGuitar.getProperty("midiinput.config.label.chordmode") + ":");

		final Combo	cmbChordMode = new Combo(groupEcho, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbChordMode.setLayoutData(new GridData(130, SWT.DEFAULT));

		cmbChordMode.add(TuxGuitar.getProperty("midiinput.chordmode.diagram"));
		cmbChordMode.add(TuxGuitar.getProperty("midiinput.chordmode.all"));

		cmbChordMode.select(MiConfig.instance().getChordMode());

	///* RECORDING
		// Recording
		Group	groupRec = new Group(dialog, SWT.SHADOW_ETCHED_IN);
		groupRec.setLayout(new GridLayout(2, false));
		groupRec.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		groupRec.setText(TuxGuitar.getProperty("midiinput.config.label.group.rec"));

		//------------------REC COUNTDOWN BARS------------------
		Label	lblCountdownBars = new Label(groupRec, SWT.LEFT);
		lblCountdownBars.setText(TuxGuitar.getProperty("midiinput.config.label.countdown") + ":");

		final Spinner	spnCountdownBars = new Spinner(groupRec, SWT.BORDER);
		spnCountdownBars.setLayoutData(new GridData(130, SWT.DEFAULT));
		spnCountdownBars.setMinimum(MIN_COUNTDOWN_BARS);
		spnCountdownBars.setMaximum(MAX_COUNTDOWN_BARS);

		//spnCountdownBars.setSelection(currInputTimeOut);

		//------------------USE METRONOME------------------
		final Button	chkMetronome = new Button(groupRec, SWT.CHECK);
		chkMetronome.setText(TuxGuitar.getProperty("midiinput.config.label.metronome"));
		chkMetronome.setSelection(true);

		//------------------USE PLAYBACK------------------
		final Button	chkPlayback = new Button(groupRec, SWT.CHECK);
		chkPlayback.setText(TuxGuitar.getProperty("midiinput.config.label.playback"));
		chkPlayback.setSelection(true);
	 //*/
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2, false));
		buttons.setLayoutData(new GridData(SWT.END, SWT.FILL, true, true));

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;

		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(data);
		buttonOK.addSelectionListener(new SelectionAdapter()
			{
			public void widgetSelected(SelectionEvent arg0)
				{
				int		portSelection = cmbPort.getSelectionIndex();

				if(portSelection >= 0 && portSelection < portsNames.size())
					{
					String	portName = (String)portsNames.get(portSelection);

					s_Instance.f_Config.setValue(KEY_MIDI_INPUT_PORT, portName);
					s_Instance.f_Config.save();

					try {
						MiPort.setNotesPort(portName);
						}
					catch(MiException mie)
						{
						TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(mie);
						}
					}

				int		baseChannel = cmbChannel.getSelectionIndex();

				if(	baseChannel >= 0 &&
					baseChannel < 16)
					{
					s_Instance.f_Config.setValue(KEY_MIDI_BASE_CHANNEL, baseChannel);
					s_Instance.f_Config.save();

					MiProvider.instance().setBaseChannel(baseChannel);
					}

				int		minVelocity = spnMinVelocity.getSelection();

				if(	minVelocity >= MIN_VELOCITY_THRESHOLD &&
					minVelocity <= MAX_VELOCITY_THRESHOLD)
					{
					s_Instance.f_Config.setValue(KEY_MIN_VELOCITY, minVelocity);
					s_Instance.f_Config.save();

					MiProvider.instance().setMinVelocity((byte)minVelocity);
					}

				int		minDuration = spnMinDuration.getSelection();

				if(	minDuration >= MIN_DURATION_THRESHOLD &&
					minDuration <= MAX_DURATION_THRESHOLD)
					{
					s_Instance.f_Config.setValue(KEY_MIN_DURATION, minDuration);
					s_Instance.f_Config.save();

					MiProvider.instance().setMinDuration(minDuration);
					}

				int		echoTimeOut = spnEchoTimeOut.getSelection();

				if(	echoTimeOut >= MIN_ECHO_TIMEOUT &&
					echoTimeOut <= MAX_ECHO_TIMEOUT)
					{
					s_Instance.f_Config.setValue(KEY_ECHO_TIMEOUT, echoTimeOut);
					s_Instance.f_Config.save();

					MiProvider.instance().setEchoTimeOut(echoTimeOut);
					}

				int		inputTimeOut = spnInputTimeOut.getSelection();

				if(	inputTimeOut >= MIN_INPUT_TIMEOUT &&
					inputTimeOut <= MAX_INPUT_TIMEOUT)
					{
					s_Instance.f_Config.setValue(KEY_INPUT_TIMEOUT, inputTimeOut);
					s_Instance.f_Config.save();

					MiProvider.instance().setInputTimeOut(inputTimeOut);
					}

				int		chordMode = cmbChordMode.getSelectionIndex();

				s_Instance.f_Config.setValue(KEY_CHORD_MODE, chordMode);
				s_Instance.f_Config.save();

				MiProvider.instance().setChordMode(chordMode);

				dialog.dispose();
				}
			});

		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(data);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});

		dialog.setDefaultButton(buttonOK);

		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		}
	catch(Exception e)
		{
		TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(e);
		}
	}
}
