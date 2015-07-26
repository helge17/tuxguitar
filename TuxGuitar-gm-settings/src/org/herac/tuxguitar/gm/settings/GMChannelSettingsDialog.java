package org.herac.tuxguitar.gm.settings;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGSong;

public class GMChannelSettingsDialog implements TGChannelSettingsDialog{
	
	private TGSong song;
	private TGChannel channel;
	private GMChannelRouter router;
	private Combo gmChannel1Combo;
	private Combo gmChannel2Combo;
	
	public GMChannelSettingsDialog(TGChannel channel, TGSong song){
		this.song = song;
		this.channel = channel;
		this.router = new GMChannelRouter();
	}
	
	public void show(final Shell parent) {
		this.configureRouter();
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM);
		dialog.setLayout(new GridLayout(1,false));
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		dialog.setText(TuxGuitar.getProperty("gm.settings.dialog.title"));
		
		// ----------------------------------------------------------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("gm.settings.dialog.tip"));
		
		Label gmChannel1Label = new Label(group, SWT.NULL);
		gmChannel1Label.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		gmChannel1Label.setText(TuxGuitar.getProperty("gm.settings.channel.label-1") + ":");
		
		this.gmChannel1Combo = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		this.gmChannel1Combo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		this.gmChannel1Combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChannel();
			}
		});
		
		Label gmChannel2Label = new Label(group, SWT.NULL);
		gmChannel2Label.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		gmChannel2Label.setText(TuxGuitar.getProperty("gm.settings.channel.label-2") + ":");
		
		this.gmChannel2Combo = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		this.gmChannel2Combo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		this.gmChannel2Combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChannel();
			}
		});
		
		updateChannelCombos();
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	protected GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private void configureRouter(){
		GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(this.router);
		gmChannelRouterConfigurator.configureRouter(this.song.getChannels());
	}
	
	private void updateChannelCombos(){
		GMChannelRoute route = this.router.getRoute(this.channel.getChannelId());
		
		List<Integer> channels = this.router.getFreeChannels(route);
		
		this.reloadChannelCombo(this.gmChannel1Combo, channels, route.getChannel1(), "gm.settings.channel.value-1");
		this.reloadChannelCombo(this.gmChannel2Combo, channels, route.getChannel2(), "gm.settings.channel.value-2");
		
		boolean playerRunning = TuxGuitar.getInstance().getPlayer().isRunning();
		
		this.gmChannel1Combo.setEnabled(!playerRunning && !this.channel.isPercussionChannel() && this.gmChannel1Combo.getItemCount() > 0);
		this.gmChannel2Combo.setEnabled(!playerRunning && !this.channel.isPercussionChannel() && this.gmChannel2Combo.getItemCount() > 0);
	}
	
	@SuppressWarnings("unchecked")
	private void reloadChannelCombo(Combo combo, List<Integer> channels, int selected, String valueKey){
		if(!(combo.getData() instanceof List) || isDifferentList(channels, (List<Integer>)combo.getData())){
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
	
	@SuppressWarnings("unchecked")
	public void updateChannel(){
		int channel1 = -1;
		int channel2 = -1;
		int channel1Selection = this.gmChannel1Combo.getSelectionIndex();
		
		Object channel1Data = this.gmChannel1Combo.getData();
		if( channel1Selection >= 0 && channel1Data instanceof List && ((List<Integer>)channel1Data).size() > channel1Selection ){
			channel1 = (((List<Integer>)channel1Data).get(channel1Selection)).intValue();
		}
		
		int channel2Selection = this.gmChannel2Combo.getSelectionIndex();
		Object channel2Data = this.gmChannel2Combo.getData();
		if( channel2Selection >= 0 && channel2Data instanceof List && ((List<Integer>)channel2Data).size() > channel2Selection ){
			channel2 = (((List<Integer>)channel2Data).get(channel2Selection)).intValue();
		}
		
		setChannelParameter(this.channel, GMChannelRoute.PARAMETER_GM_CHANNEL_1, Integer.toString(channel1));
		setChannelParameter(this.channel, GMChannelRoute.PARAMETER_GM_CHANNEL_2, Integer.toString(channel2));
		
		configureRouter();
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
}
