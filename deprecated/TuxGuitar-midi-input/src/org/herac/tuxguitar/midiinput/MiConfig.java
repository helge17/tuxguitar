package org.herac.tuxguitar.midiinput;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;
import org.herac.tuxguitar.util.error.TGErrorManager;

class MiConfig
{
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

	private			TGContext	f_Context;
	private			TGConfigManager	f_Config;
	
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
		instance().f_Context = context;
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


	void	showDialog(UIWindow parent)
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

		final UIFactory uiFactory = TGApplication.getInstance(this.f_Context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("midiinput.config.title"));

		// MIDI
		UITableLayout groupMidiLayout = new UITableLayout();
		UILegendPanel groupMidi = uiFactory.createLegendPanel(dialog);
		groupMidi.setLayout(groupMidiLayout);
		groupMidi.setText(TuxGuitar.getProperty("midiinput.config.label.group.midi"));
		dialogLayout.set(groupMidi, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------MIDI INPUT PORT------------------
		UILabel	lblPort = uiFactory.createLabel(groupMidi);
		lblPort.setText(TuxGuitar.getProperty("midiinput.config.label.port") + ":");
		groupMidiLayout.set(lblPort, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<String>	cmbPort = uiFactory.createDropDownSelect(groupMidi);
		for(int i = 0 ; i < portsNames.size(); i++)
			{
			String	portName = (String)portsNames.get(i);

			cmbPort.addItem(new UISelectItem<String>(portName, portName));
			}
		cmbPort.setSelectedValue(currPortName);
		groupMidiLayout.set(cmbPort, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//------------------MIDI BASE CHANNEL------------------
		UILabel	lblChannel = uiFactory.createLabel(groupMidi);
		lblChannel.setText(TuxGuitar.getProperty("midiinput.config.label.basechannel") + ":");
		groupMidiLayout.set(lblChannel, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<Integer>	cmbChannel = uiFactory.createDropDownSelect(groupMidi);
		for(int i = 0 ; i < 16; i++) {
			cmbChannel.addItem(new UISelectItem<Integer>(Integer.toString(i + 1), i));
		}
		cmbChannel.setSelectedValue(currBaseChannel);
		groupMidiLayout.set(cmbChannel, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		// General input
		UITableLayout groupInputLayout = new UITableLayout();
		UILegendPanel groupInput = uiFactory.createLegendPanel(dialog);
		groupInput.setLayout(groupInputLayout);
		groupInput.setText(TuxGuitar.getProperty("midiinput.config.label.group.input"));
		dialogLayout.set(groupInput, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------MIN VELOCITY THRESHOLD------------------
		UILabel	lblVelocity = uiFactory.createLabel(groupInput);
		lblVelocity.setText(TuxGuitar.getProperty("midiinput.config.label.minvelocity") + ":");
		groupInputLayout.set(lblVelocity, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UISpinner	spnMinVelocity = uiFactory.createSpinner(groupInput);
		spnMinVelocity.setMinimum(MIN_VELOCITY_THRESHOLD);
		spnMinVelocity.setMaximum(MAX_VELOCITY_THRESHOLD);
		spnMinVelocity.setValue(currMinVelocity);
		groupInputLayout.set(spnMinVelocity, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//------------------MIN VELOCITY DURATION------------------
		UILabel	lblDuration = uiFactory.createLabel(groupInput);
		lblDuration.setText(TuxGuitar.getProperty("midiinput.config.label.minduration") + ":");
		groupInputLayout.set(lblDuration, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UISpinner	spnMinDuration = uiFactory.createSpinner(groupInput);
		spnMinDuration.setMinimum(MIN_DURATION_THRESHOLD);
		spnMinDuration.setMaximum(MAX_DURATION_THRESHOLD);
		spnMinDuration.setValue(currMinDuration);
		groupInputLayout.set(spnMinDuration, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		// Echo/Chords/Scales
		UITableLayout groupEchoLayout = new UITableLayout();
		UILegendPanel groupEcho = uiFactory.createLegendPanel(dialog);
		groupEcho.setLayout(groupEchoLayout);
		groupEcho.setText(TuxGuitar.getProperty("midiinput.config.label.group.echo"));
		dialogLayout.set(groupEcho, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//------------------ECHO TIME OUT------------------
		UILabel	lblEchoTimeOut = uiFactory.createLabel(groupEcho);
		lblEchoTimeOut.setText(TuxGuitar.getProperty("midiinput.config.label.echotimeout") + ":");
		groupEchoLayout.set(lblEchoTimeOut, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UISpinner	spnEchoTimeOut = uiFactory.createSpinner(groupEcho);
		spnEchoTimeOut.setMinimum(MIN_ECHO_TIMEOUT);
		spnEchoTimeOut.setMaximum(MAX_ECHO_TIMEOUT);

		spnEchoTimeOut.setValue(currEchoTimeOut);
		groupEchoLayout.set(spnEchoTimeOut, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//------------------INPUT TIME OUT------------------
		UILabel	lblInputTimeOut = uiFactory.createLabel(groupEcho);
		lblInputTimeOut.setText(TuxGuitar.getProperty("midiinput.config.label.inputtimeout") + ":");
		groupEchoLayout.set(lblInputTimeOut, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UISpinner	spnInputTimeOut = uiFactory.createSpinner(groupEcho);
		spnInputTimeOut.setMinimum(MIN_INPUT_TIMEOUT);
		spnInputTimeOut.setMaximum(MAX_INPUT_TIMEOUT);

		spnInputTimeOut.setValue(currInputTimeOut);
		groupEchoLayout.set(spnInputTimeOut, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//------------------CHORD MODE------------------
		UILabel	lblMode = uiFactory.createLabel(groupEcho);
		lblMode.setText(TuxGuitar.getProperty("midiinput.config.label.chordmode") + ":");
		groupEchoLayout.set(lblMode, 3, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<Integer>	cmbChordMode = uiFactory.createDropDownSelect(groupEcho);
		
		cmbChordMode.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("midiinput.chordmode.diagram"), 0));
		cmbChordMode.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("midiinput.chordmode.all"), 1));
		cmbChordMode.setSelectedValue(MiConfig.instance().getChordMode());
		
		groupEchoLayout.set(cmbChordMode, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
	///* RECORDING
		// Recording
//		UITableLayout groupRecLayout = new UITableLayout();
//		UILegendPanel groupRec = uiFactory.createLegendPanel(dialog);
//		groupRec.setLayout(groupRecLayout);
//		groupRec.setText(TuxGuitar.getProperty("midiinput.config.label.group.rec"));
//		dialogLayout.set(groupRec, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------REC COUNTDOWN BARS------------------
//		UILabel	lblCountdownBars = uiFactory.createLabel(groupRec);
//		lblCountdownBars.setText(TuxGuitar.getProperty("midiinput.config.label.countdown") + ":");
//		groupRecLayout.set(lblCountdownBars, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
//		
//		final UISpinner	spnCountdownBars = uiFactory.createSpinner(groupRec);
//		spnCountdownBars.setMinimum(MIN_COUNTDOWN_BARS);
//		spnCountdownBars.setMaximum(MAX_COUNTDOWN_BARS);
//		spnCountdownBars.setValue(0);
//		groupRecLayout.set(spnCountdownBars, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		//------------------USE METRONOME------------------
//		final UICheckBox	chkMetronome = uiFactory.createCheckBox(groupRec);
//		chkMetronome.setText(TuxGuitar.getProperty("midiinput.config.label.metronome"));
//		chkMetronome.setSelected(true);
//		groupRecLayout.set(chkMetronome, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
//		
		//------------------USE PLAYBACK------------------
//		final UICheckBox	chkPlayback = uiFactory.createCheckBox(groupRec);
//		chkPlayback.setText(TuxGuitar.getProperty("midiinput.config.label.playback"));
//		chkPlayback.setSelected(true);
//		groupRecLayout.set(chkPlayback, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
//		
	 //*/
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() 
			{
			public void onSelect(UISelectionEvent event) 
				{
				String portName = cmbPort.getSelectedValue();

				if( portName != null )
					{
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

				Integer baseChannel = cmbChannel.getSelectedValue();

				if(	baseChannel != null && baseChannel >= 0 && baseChannel < 16)
					{
					s_Instance.f_Config.setValue(KEY_MIDI_BASE_CHANNEL, baseChannel);
					s_Instance.f_Config.save();

					MiProvider.instance().setBaseChannel(baseChannel);
					}

				int		minVelocity = spnMinVelocity.getValue();

				if(	minVelocity >= MIN_VELOCITY_THRESHOLD &&
					minVelocity <= MAX_VELOCITY_THRESHOLD)
					{
					s_Instance.f_Config.setValue(KEY_MIN_VELOCITY, minVelocity);
					s_Instance.f_Config.save();

					MiProvider.instance().setMinVelocity((byte)minVelocity);
					}

				int		minDuration = spnMinDuration.getValue();

				if(	minDuration >= MIN_DURATION_THRESHOLD &&
					minDuration <= MAX_DURATION_THRESHOLD)
					{
					s_Instance.f_Config.setValue(KEY_MIN_DURATION, minDuration);
					s_Instance.f_Config.save();

					MiProvider.instance().setMinDuration(minDuration);
					}

				int		echoTimeOut = spnEchoTimeOut.getValue();

				if(	echoTimeOut >= MIN_ECHO_TIMEOUT &&
					echoTimeOut <= MAX_ECHO_TIMEOUT)
					{
					s_Instance.f_Config.setValue(KEY_ECHO_TIMEOUT, echoTimeOut);
					s_Instance.f_Config.save();

					MiProvider.instance().setEchoTimeOut(echoTimeOut);
					}

				int		inputTimeOut = spnInputTimeOut.getValue();

				if(	inputTimeOut >= MIN_INPUT_TIMEOUT &&
					inputTimeOut <= MAX_INPUT_TIMEOUT)
					{
					s_Instance.f_Config.setValue(KEY_INPUT_TIMEOUT, inputTimeOut);
					s_Instance.f_Config.save();

					MiProvider.instance().setInputTimeOut(inputTimeOut);
					}

				Integer		chordMode = cmbChordMode.getSelectedValue();
				if(	chordMode != null)
					{
					s_Instance.f_Config.setValue(KEY_CHORD_MODE, chordMode);
					s_Instance.f_Config.save();

					MiProvider.instance().setChordMode(chordMode);
					}

				dialog.dispose();
				}
			});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		

		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	catch(Exception e)
		{
		TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(e);
		}
	}
}
