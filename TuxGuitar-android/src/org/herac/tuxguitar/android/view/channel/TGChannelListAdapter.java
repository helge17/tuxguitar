package org.herac.tuxguitar.android.view.channel;

import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class TGChannelListAdapter extends BaseAdapter {

	private TGChannelListView channelList;
	private boolean eventInProgress;
	
	public TGChannelListAdapter(TGChannelListView channelList) {
		this.channelList = channelList;
		this.eventInProgress = false;
	}

	@Override
	public int getCount() {
		TGDocumentManager documentManager = TGDocumentManager.getInstance(this.channelList.findContext());
		if( documentManager.getSong() != null ) {
			return documentManager.getSong().countChannels();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		TGDocumentManager documentManager = TGDocumentManager.getInstance(this.channelList.findContext());
		if( documentManager.getSong() != null ) {
			return documentManager.getSong().getChannel(position);
		}
		return null;
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
			this.notifyDataSetChangedLater();
		}
	}
	
	public void notifyDataSetChangedLater() {
		TGSynchronizer.getInstance(this.channelList.findContext()).executeLater(new Runnable() {
			public void run() throws TGException {
				notifyDataSetChanged();
			}
		});
	}
}
