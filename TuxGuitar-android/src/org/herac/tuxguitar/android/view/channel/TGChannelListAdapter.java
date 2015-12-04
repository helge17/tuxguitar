package org.herac.tuxguitar.android.view.channel;

import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.util.TGProcess;
import org.herac.tuxguitar.android.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.song.models.TGChannel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class TGChannelListAdapter extends BaseAdapter {

	private List<TGChannel> channels;
	private TGChannelListView channelList;
	private TGProcess notifyDataSetChangedLater;
	private boolean eventInProgress;
	
	public TGChannelListAdapter(TGChannelListView channelList) {
		this.channelList = channelList;
		this.eventInProgress = false;
		this.createSyncProcesses();
	}
	
	public void setChannels(List<TGChannel> channels) {
		this.channels = channels;
	}
	
	@Override
	public int getCount() {
		return (this.channels != null ? this.channels.size() : 0);
	}
	
	@Override
	public Object getItem(int position) {
		return (this.channels != null && this.channels.size() > position ? this.channels.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public LayoutInflater getLayoutInflater() {
		return (LayoutInflater) this.channelList.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TGChannel channel = (TGChannel) this.getItem(position);
		
		View view = (convertView != null ? convertView : getLayoutInflater().inflate(R.layout.view_channel_list_item, parent, false));
		view.setTag(channel);
		view.setOnClickListener(this.channelList.getActionHandler().createEditChannelAction(channel));
		view.setOnLongClickListener(this.channelList.getActionHandler().createChannelItemMenuAction(channel));
		
		TextView textViewName = (TextView) view.findViewById(R.id.channel_item_name);
		textViewName.setText(channel.getName());
		
		SeekBar seekBarVolume = (SeekBar) view.findViewById(R.id.channel_item_volume_value);
		seekBarVolume.setTag(channel);
		seekBarVolume.setProgress(channel.getVolume());
		seekBarVolume.setOnSeekBarChangeListener(this.createVolumeChangeListener());
		
		return view;
	}
	
	public void updateVolume(TGChannel channel, short volume) {
		if( volume != channel.getVolume() && (volume >= 0 && volume <= 127)) {
			this.channelList.getActionHandler().createUpdateVolumeAction(channel, volume).process();
		}
	}
	
	private OnSeekBarChangeListener createVolumeChangeListener() {
		return new OnSeekBarChangeListener() {
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				TGChannelListAdapter.this.eventInProgress = true;
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				TGChannelListAdapter.this.eventInProgress = false;
			}
			
			@Override
			public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {
				if( progress >= 0 && progress <= 127 ) {
					TGChannelListAdapter.this.updateVolume((TGChannel) seekBar.getTag(), Integer.valueOf(progress).shortValue());
				}
			}
		};
	}
	
	public void notifyDataSetChanged() {
		if(!this.eventInProgress ) {
			super.notifyDataSetChanged();
		} else {
			this.notifyDataSetChangedLater.process();
		}
	}
	
	public void createSyncProcesses() {
		this.notifyDataSetChangedLater = new TGSyncProcessLocked(this.channelList.findContext(), new Runnable() {
			public void run() {
				notifyDataSetChanged();
			}
		});
	}
}
