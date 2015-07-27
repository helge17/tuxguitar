package org.herac.tuxguitar.jack.synthesizer.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.gm.GMChannelRouterConfigurator;
import org.herac.tuxguitar.jack.synthesizer.JackChannelParameter;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class JackChannelSettingsDialog implements TGChannelSettingsDialog{
	
	public static final short MAX_CHANNELS = 16;
	public static final short DEFAULT_INSTRUMENT_CHANNEL_1 = 0;
	public static final short DEFAULT_INSTRUMENT_CHANNEL_2 = 1;
	public static final short DEFAULT_PERCUSSION_CHANNEL = 9;
	
	private TGContext context;
	private TGSong song;
	private TGChannel channel;
	private GMChannelRouter router;
	private Shell dialog;
	private Combo gmChannel1Combo;
	private Combo gmChannel2Combo;
	private Button exclusiveButton;
	private JackMidiPlayerListener jackMidiPlayerListener;
	
	public JackChannelSettingsDialog(TGContext context, TGChannel channel, TGSong song){
		this.context = context;
		this.song = song;
		this.channel = channel;
		this.router = new GMChannelRouter();
		this.jackMidiPlayerListener = new JackMidiPlayerListener(this.context, this);
	}
	
	public void show(final Shell parent) {
		this.configureRouter(true);
		
		this.dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM);
		this.dialog.setLayout(new GridLayout(1,false));
		this.dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.dialog.setText(TuxGuitar.getProperty("jack.settings.channel.dialog"));
		
		//-------------------- GM Channels -------------------------------
		Group group = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("jack.settings.channel.gm.tip"));
		
		Label gmChannel1Label = new Label(group, SWT.NULL);
		gmChannel1Label.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		gmChannel1Label.setText(TuxGuitar.getProperty("jack.settings.channel.gm.channel.label-1") + ":");
		
		this.gmChannel1Combo = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		this.gmChannel1Combo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		this.gmChannel1Combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChannel();
			}
		});
		
		Label gmChannel2Label = new Label(group, SWT.NULL);
		gmChannel2Label.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		gmChannel2Label.setText(TuxGuitar.getProperty("jack.settings.channel.gm.channel.label-2") + ":");
		
		this.gmChannel2Combo = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		this.gmChannel2Combo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		this.gmChannel2Combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChannel();
			}
		});
		
		//-------------------- Jack Options-------------------------------
		Group optionsGroup = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);
		optionsGroup.setLayout(new GridLayout());
		optionsGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		optionsGroup.setText(TuxGuitar.getProperty("options"));
		
		this.exclusiveButton = new Button(optionsGroup, SWT.CHECK);
		this.exclusiveButton.setText(TuxGuitar.getProperty("jack.settings.channel.exclusive.port"));
		this.exclusiveButton.setSelection(isExclusive());
		this.exclusiveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateExclusive();
			}
		});
		
		//-------------------- Jack Options-------------------------------
		
		this.updateDefaultExclusiveChannels();
		this.updateChannelCombos();
		this.updateControls();
		
		this.addMidiPlayerListener();
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				removeMidiPlayerListener();
			}
		});
		
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
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
			channels.add(new Integer(i));
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
	private void reloadChannelCombo(Combo combo, List<Integer> channels, int selected, String valueKey){
		if(!(combo.getData() instanceof List) || isDifferentList(channels, (List<Integer>) combo.getData())){
			combo.removeAll();
			combo.setData(channels);
			for( int i = 0 ; i < channels.size() ; i ++ ){
				combo.add(TuxGuitar.getProperty(valueKey, new String[]{channels.get(i).toString()}));
			}
		}
		for( int i = 0 ; i < channels.size() ; i ++ ){
			Integer channel = (Integer)channels.get(i);
			if( channel.intValue() == selected ){
				combo.select( i );
			}
		}
	}
	
	public void updateExclusive(){
		boolean exclusive = this.exclusiveButton.getSelection();
		
		this.setChannelParameter(this.channel, JackChannelParameter.PARAMETER_EXCLUSIVE_PORT, Boolean.toString(exclusive));
		this.removeChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_1);
		this.removeChannelParameter(this.channel, JackChannelParameter.PARAMETER_GM_CHANNEL_2);
		
		this.configureRouter(true);
		this.updateDefaultExclusiveChannels();
		this.updateChannelCombos();
		this.updatePlayerChannels();
	}
	
	@SuppressWarnings("unchecked")
	public void updateChannel(){
		int channel1 = -1;
		int channel2 = -1;
		int channel1Selection = this.gmChannel1Combo.getSelectionIndex();
		
		Object channel1Data = this.gmChannel1Combo.getData();
		if( channel1Selection >= 0 && channel1Data instanceof List && ((List<Integer>)channel1Data).size() > channel1Selection ){
			channel1 = ((Integer)((List<Integer>)channel1Data).get(channel1Selection)).intValue();
		}
		
		int channel2Selection = this.gmChannel2Combo.getSelectionIndex();
		Object channel2Data = this.gmChannel2Combo.getData();
		if( channel2Selection >= 0 && channel2Data instanceof List && ((List<Integer>)channel2Data).size() > channel2Selection ){
			channel2 = ((Integer)((List<Integer>)channel2Data).get(channel2Selection)).intValue();
		}
		
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
			TGChannelParameter parameter = (TGChannelParameter)it.next();
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
