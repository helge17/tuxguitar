package app.tuxguitar.jack.synthesizer.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.gm.GMChannelRoute;
import app.tuxguitar.gm.GMChannelRouter;
import app.tuxguitar.gm.GMChannelRouterConfigurator;
import app.tuxguitar.jack.synthesizer.JackChannelParameter;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;

public class JackChannelSettingsDialog implements TGChannelSettingsDialog{

	public static final short MAX_CHANNELS = 16;
	public static final short DEFAULT_INSTRUMENT_CHANNEL_1 = 0;
	public static final short DEFAULT_INSTRUMENT_CHANNEL_2 = 1;
	public static final short DEFAULT_PERCUSSION_CHANNEL = 9;

	public static final String CHANNELS_DATA = "channels";

	private TGContext context;
	private TGSong song;
	private TGChannel channel;
	private GMChannelRouter router;
	private UIWindow dialog;
	private UIDropDownSelect<Integer> gmChannel1Combo;
	private UIDropDownSelect<Integer> gmChannel2Combo;
	private UICheckBox exclusiveButton;
	private UICheckBox stripPCButton;
	private JackMidiPlayerListener jackMidiPlayerListener;

	public JackChannelSettingsDialog(TGContext context, TGChannel channel, TGSong song){
		this.context = context;
		this.song = song;
		this.channel = channel;
		this.router = new GMChannelRouter();
		this.jackMidiPlayerListener = new JackMidiPlayerListener(this.context, this);
	}

	public void open(UIWindow parent) {
		this.configureRouter(true);

		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();

		this.dialog = uiFactory.createWindow(parent, false, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("jack.settings.channel.dialog"));

		//-------------------- GM Channels -------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(this.dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("jack.settings.channel.gm.tip"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel gmChannel1Label = uiFactory.createLabel(group);
		gmChannel1Label.setText(TuxGuitar.getProperty("jack.settings.channel.gm.channel.label-1") + ":");
		groupLayout.set(gmChannel1Label, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		this.gmChannel1Combo = uiFactory.createDropDownSelect(group);
		this.gmChannel1Combo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateChannel();
			}
		});
		groupLayout.set(this.gmChannel1Combo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		UILabel gmChannel2Label = uiFactory.createLabel(group);
		gmChannel2Label.setText(TuxGuitar.getProperty("jack.settings.channel.gm.channel.label-2") + ":");
		groupLayout.set(gmChannel2Label, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		this.gmChannel2Combo = uiFactory.createDropDownSelect(group);
		this.gmChannel2Combo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateChannel();
			}
		});
		groupLayout.set(this.gmChannel2Combo, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		//-------------------- Jack Options-------------------------------
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel optionsGroup = uiFactory.createLegendPanel(this.dialog);
		optionsGroup.setLayout(optionsLayout);
		optionsGroup.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(optionsGroup, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.exclusiveButton = uiFactory.createCheckBox(optionsGroup);
		this.exclusiveButton.setText(TuxGuitar.getProperty("jack.settings.channel.exclusive.port"));
		this.exclusiveButton.setSelected(isExclusive());
		this.exclusiveButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateExclusive();
			}
		});
		optionsLayout.set(this.exclusiveButton, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		this.stripPCButton = uiFactory.createCheckBox(optionsGroup);
		this.stripPCButton.setText(TuxGuitar.getProperty("jack.settings.channel.strip-pc"));
		this.stripPCButton.setSelected(stripPC());
		this.stripPCButton.setEnabled(isExclusive());
		this.stripPCButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				updateStripPC();
			}
		});
		optionsLayout.set(this.stripPCButton, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		//-------------------- Jack Options-------------------------------

		this.updateChannelCombos();
		this.updateControls();

		this.addMidiPlayerListener();
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeMidiPlayerListener();
			}
		});

		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void close() {
		if( this.isOpen()) {
			this.dialog.dispose();
		}
	}

	public boolean isOpen(){
		return (this.dialog != null && !this.dialog.isDisposed());
	}

	public void addMidiPlayerListener(){
		MidiPlayer.getInstance(this.context).addListener(this.jackMidiPlayerListener);
	}

	public void removeMidiPlayerListener(){
		MidiPlayer.getInstance(this.context).removeListener(this.jackMidiPlayerListener);
	}

	public void updateControls(){
		if( this.dialog != null && !this.dialog.isDisposed() ){
			boolean playerRunning = TuxGuitar.getInstance().getPlayer().isRunning();

			this.gmChannel1Combo.setEnabled(!playerRunning && !this.channel.isPercussionChannel() && this.gmChannel1Combo.getItemCount() > 0);
			this.gmChannel2Combo.setEnabled(!playerRunning && !this.channel.isPercussionChannel() && this.gmChannel2Combo.getItemCount() > 0);
			this.exclusiveButton.setEnabled(!playerRunning);
		}
	}

	private void configureRouter( boolean updateChannel ){
		if(!this.isExclusive()){
			GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(this.router);
			gmChannelRouterConfigurator.configureRouter(findGmChannels());
			if( updateChannel ){
				this.updateChannelFromRouter();
			}
		}
	}

	private void updateChannelCombos(){
		if(this.isExclusive() ){
			this.reloadExclusiveChannelCombos();
		} else {
			this.reloadGMChannelCombos();
		}
		this.updateControls();
	}

	private void reloadExclusiveChannelCombos(){
		List<Integer> channels = new ArrayList<Integer>();
		for(int i = 0 ; i < MAX_CHANNELS ; i ++){
			channels.add(Integer.valueOf(i));
		}

		int channel1 = getIntegerChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_1, -1);
		int channel2 = getIntegerChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_2, -1);

		this.reloadChannelCombo(this.gmChannel1Combo, channels, channel1, "jack.settings.channel.gm.channel.value-1");
		this.reloadChannelCombo(this.gmChannel2Combo, channels, channel2, "jack.settings.channel.gm.channel.value-2");
	}

	private void reloadGMChannelCombos(){
		GMChannelRoute route = this.router.getRoute(this.channel.getChannelId());

		List<Integer> channels = this.router.getFreeChannels(route);

		this.reloadChannelCombo(this.gmChannel1Combo, channels, route.getChannel1(), "jack.settings.channel.gm.channel.value-1");
		this.reloadChannelCombo(this.gmChannel2Combo, channels, route.getChannel2(), "jack.settings.channel.gm.channel.value-2");
	}

	@SuppressWarnings("unchecked")
	private void reloadChannelCombo(UIDropDownSelect<Integer> combo, List<Integer> channels, int selected, String valueKey){
		if(!(combo.getData(CHANNELS_DATA) instanceof List) || isDifferentList(channels, (List<Integer>) combo.getData(CHANNELS_DATA))){
			combo.removeItems();
			combo.setData(CHANNELS_DATA, channels);
			for(Integer channel : channels){
				combo.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty(valueKey, new String[]{channel.toString()}), channel));
			}
		}
		combo.setSelectedValue(selected);
	}

	public void updateExclusive(){
		boolean exclusive = this.exclusiveButton.isSelected();

		this.setChannelParameter(this.channel, JackChannelParameter.PARAMETER_EXCLUSIVE_PORT, Boolean.toString(exclusive));
		this.removeChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_1);
		this.removeChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_2);

		if (!exclusive) {
			this.stripPCButton.setSelected(false);
			this.updateStripPC();
		}
		this.stripPCButton.setEnabled(exclusive);

		this.configureRouter(true);
		this.updateDefaultExclusiveChannels();
		this.updateChannelCombos();
		this.updatePlayerChannels();
	}

	public void updateStripPC(){
		boolean strip = this.stripPCButton.isSelected();

		if (strip) {
			this.setChannelParameter(this.channel, JackChannelParameter.PARAMETER_STRIP_PC, Boolean.toString(true));
		} else {
			this.removeChannelParameter(this.channel, JackChannelParameter.PARAMETER_STRIP_PC);
		}
	}

	public void updateChannel(){
		Integer channel1Selection = this.gmChannel1Combo.getSelectedValue();
		Integer channel2Selection = this.gmChannel2Combo.getSelectedValue();

		int channel1 = (channel1Selection != null ? channel1Selection : -1);
		int channel2 = (channel2Selection != null ? channel2Selection : -1);

		setChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_1, Integer.toString(channel1));
		setChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_2, Integer.toString(channel2));

		this.configureRouter(false);
		this.updatePlayerChannels();
	}

	public void updateChannelFromRouter(){
		GMChannelRoute route = this.router.getRoute(this.channel.getChannelId());
		if( findChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_1) == null ){
			setChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_1, Integer.toString(route.getChannel1()));
		}
		if( findChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_2) == null ){
			setChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_2, Integer.toString(route.getChannel2()));
		}
	}

	public void updateDefaultExclusiveChannels() {
		if( this.isExclusive() ){
			int channel1 = ( this.channel.isPercussionChannel() ? DEFAULT_PERCUSSION_CHANNEL : DEFAULT_INSTRUMENT_CHANNEL_1);
			int channel2 = ( this.channel.isPercussionChannel() ? DEFAULT_PERCUSSION_CHANNEL : DEFAULT_INSTRUMENT_CHANNEL_2);

			setChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_1, Integer.toString(channel1));
			setChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_2, Integer.toString(channel2));
		}
	}

	public void updatePlayerChannels() {
		try {
			if(!MidiPlayer.getInstance(this.context).isRunning()){
				MidiPlayer.getInstance(this.context).updateChannels();
			}
		} catch(MidiPlayerException e){
			TGErrorManager.getInstance(this.context).handleError(e);
		}
	}

	private void setChannelParameter( TGChannel tgChannel, String key, String value ){
		TGChannelParameter tgChannelParameter = findOrCreateChannelParameter(tgChannel, key);
		tgChannelParameter.setValue(value);
	}

	private TGChannelParameter findChannelParameter( TGChannel tgChannel, String key ){
		Iterator<TGChannelParameter> it = tgChannel.getParameters();
		while( it.hasNext() ){
			TGChannelParameter parameter = it.next();
			if( parameter.getKey().equals( key ) ){
				return parameter;
			}
		}
		return null;
	}

	private TGChannelParameter findOrCreateChannelParameter( TGChannel tgChannel, String key ){
		TGChannelParameter tgChannelParameter = findChannelParameter(tgChannel, key);
		if( tgChannelParameter == null ){
			tgChannelParameter = TuxGuitar.getInstance().getSongManager().getFactory().newChannelParameter();
			tgChannelParameter.setKey(key);
			tgChannel.addParameter(tgChannelParameter);
		}
		return tgChannelParameter;
	}

	private int getIntegerChannelParameter( TGChannel tgChannel, String key , int nullValue){
		TGChannelParameter tgChannelParameter = findChannelParameter(tgChannel, key);
		if( tgChannelParameter != null && tgChannelParameter.getValue() != null ){
			return Integer.parseInt( tgChannelParameter.getValue() );
		}
		return nullValue;
	}

	private void removeChannelParameter( TGChannel tgChannel, String key ){
		int index = -1;
		int count = tgChannel.countParameters();
		for(int i = 0 ; i < count; i ++){
			TGChannelParameter parameter = tgChannel.getParameter(i);
			if( parameter.getKey().equals( key ) ){
				index = i;
			}
		}
		if( index >= 0 ){
			tgChannel.removeParameter(index);
		}
	}

	private boolean isDifferentList(List<?> list1, List<?> list2){
		if( list1.size() != list2.size() ){
			return true;
		}
		for( int i = 0 ; i < list1.size() ; i ++ ){
			if(!list1.get(i).equals(list2.get(i)) ){
				return true;
			}
		}
		return false;
	}

	private boolean isExclusive(TGChannel tgChannel) {
		TGChannelParameter tgChannelParameter = findChannelParameter(tgChannel, JackChannelParameter.PARAMETER_EXCLUSIVE_PORT);
		if( tgChannelParameter != null ){
			return Boolean.TRUE.toString().equals( tgChannelParameter.getValue() );
		}
		return false;
	}

	private boolean isExclusive() {
		return isExclusive(this.channel);
	}

	private boolean stripPC() {
		TGChannelParameter tgChannelParameter = findChannelParameter(this.channel, JackChannelParameter.PARAMETER_STRIP_PC);
		if( tgChannelParameter != null ){
			return isExclusive() && Boolean.TRUE.toString().equals( tgChannelParameter.getValue() );
		}
		return false;
	}

	private Iterator<TGChannel> findGmChannels(){
		List<TGChannel> tgChannels = new ArrayList<TGChannel>();

		int count = this.song.countChannels();
		for(int i = 0 ; i < count ; i ++) {
			TGChannel tgChannel = this.song.getChannel( i );
			if(!this.isExclusive( tgChannel ) ){
				tgChannels.add( tgChannel );
			}
		}

		return tgChannels.iterator();
	}
}
