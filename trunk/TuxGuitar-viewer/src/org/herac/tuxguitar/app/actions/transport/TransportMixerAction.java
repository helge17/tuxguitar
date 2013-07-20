/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.transport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.ActionDialog;
import org.herac.tuxguitar.app.system.config.TGConfig;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TransportMixerAction extends ActionDialog {
	public static final String NAME = "action.transport.mixer";
	
	public static final int MUTE = 0x01;
	public static final int SOLO = 0x02;
	public static final int VOLUME = 0x04;
	public static final int BALANCE = 0x08;
	public static final int CHANGE_ALL = (MUTE | SOLO | VOLUME | BALANCE );
	
	private boolean locked;
	
	public TransportMixerAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE );
	}
	
	public void setLocked( boolean locked){
		this.locked = locked;
	}
	
	public boolean isLocked(){
		return this.locked;
	}
	
	protected void openDialog(){
		final JFrame dialog = createDialog();
		
		final TrackPanel[] trackPanels = new TrackPanel[getSongManager().getSong().countTracks()];
		for( int i = 0 ; i < trackPanels.length ; i ++ ){
			trackPanels[i] = new TrackPanel(getSongManager().getSong().getTrack(i));
			trackPanels[i].init();
			
			ChangeVolumeListener volumeListener = new ChangeVolumeListener(trackPanels[i],trackPanels);
			ChangeBalanceListener balanceListener = new ChangeBalanceListener(trackPanels[i],trackPanels);
			
			trackPanels[i].getTrackVolume().addChangeListener(volumeListener);
			
			trackPanels[i].getTrackBalance().addChangeListener(balanceListener);
			
			trackPanels[i].getTrackSolo().addActionListener(new ChangeSoloListener(trackPanels[i],trackPanels));
			trackPanels[i].getTrackMute().addActionListener(new ChangeMuteListener(trackPanels[i],trackPanels));
		}
		
		this.setLocked(true);
		this.fireUpdate(trackPanels, CHANGE_ALL);
		this.setLocked(false);
		
		//---Player things--------------------------------------
		final JSlider playerVolume = new JSlider(JSlider.VERTICAL);
		playerVolume.setMinimum(1);
		playerVolume.setMaximum(10);
		playerVolume.setPaintTicks(false);
		playerVolume.setPaintLabels(false);
		playerVolume.setValue( TuxGuitar.instance().getPlayer().getVolume() );
		playerVolume.setAlignmentX( JSlider.CENTER_ALIGNMENT );
		
		JPanel playerVolumePanel = new JPanel();
		playerVolumePanel.setLayout(new BoxLayout(playerVolumePanel,BoxLayout.X_AXIS));
		playerVolumePanel.add( Box.createHorizontalGlue());
		playerVolumePanel.add( playerVolume );
		playerVolumePanel.add( Box.createHorizontalGlue());
		
		JLabel playerVolumeLabel = new JLabel("Gain:");
		playerVolumeLabel.setFont( TGConfig.FONT_WIDGETS );
		
		final JLabel playerVolumeValue = new JLabel(Integer.toString(TuxGuitar.instance().getPlayer().getVolume()));
		playerVolumeValue.setFont( TGConfig.FONT_WIDGETS );
		playerVolumeValue.setBackground(Color.WHITE);
		
		JPanel playerVolumeValuePanel = new JPanel();
		playerVolumeValuePanel.setLayout(new GridBagLayout());
		playerVolumeValuePanel.setBorder(BorderFactory.createEtchedBorder());
		playerVolumeValuePanel.add(playerVolumeLabel, getConstraints(0,0,0f,0f,GridBagConstraints.BOTH,1,1,4));
		playerVolumeValuePanel.add(playerVolumeValue , getConstraints(1,0,1f,0f,GridBagConstraints.BOTH,1,1,4));
		
		JPanel playerPanel = new JPanel();
		playerPanel.setLayout(new BorderLayout());
		playerPanel.setBorder(BorderFactory.createEtchedBorder());
		playerPanel.add( playerVolumePanel , BorderLayout.CENTER);
		playerPanel.add( playerVolumeValuePanel , BorderLayout.SOUTH);
		
		playerVolume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = playerVolume.getValue();
				playerVolumeValue.setText( Integer.toString( value ) );
				TuxGuitar.instance().getPlayer().setVolume( value );
			}
		});
		
		//-----------------------------------------
		JPanel container = new JPanel();
		container.setLayout(new GridLayout());
		for( int i = 0 ; i < trackPanels.length ; i ++ ){
			container.add( trackPanels[i] );
		}
		container.add(playerPanel);
		container.setBorder(BorderFactory.createEtchedBorder());
		
		dialog.addWindowListener( new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				TuxGuitar.instance().updateCache( true );
			}
		} );
		dialog.setTitle("Mixer");
		dialog.getContentPane().setLayout(new GridLayout());
		dialog.getContentPane().add(container);
		dialog.pack();
		dialog.setLocationRelativeTo(TuxGuitar.instance().getShell());
		dialog.setResizable(false);
		dialog.setVisible(true);
	}
	
	public void fireChannelChanges(TrackPanel[] trackPanels, TGChannel channel, int type){
		Iterator it = getSongManager().getSong().getTracks();
		while(it.hasNext()){
			TGTrack track = (TGTrack)it.next();
			if(track.getChannel().getChannel() == channel.getChannel()){
				channel.copy(track.getChannel());
			}
		}
		if (TuxGuitar.instance().getPlayer().isRunning()) {
			TuxGuitar.instance().getPlayer().updateControllers();
		}
		fireUpdate(trackPanels, type);
	}
	
	protected void fireUpdate(TrackPanel[] trackPanels, int type){
		for(int i = 0 ; i < trackPanels.length ; i ++){
			fireUpdate(trackPanels[i], type);
		}
	}
	
	private void fireUpdate(TrackPanel track, int type){
		TGChannel channel = track.getTrack().getChannel();
		
		if((type & SOLO) != 0 || (type & MUTE) != 0){
			track.getTrackSolo().setSelected( track.getTrack().isSolo() );
			track.getTrackMute().setSelected( track.getTrack().isMute() );
		}
		if((type & VOLUME) != 0){
			track.getTrackVolume().setValue( channel.getVolume() );
			track.getTrackVolumeValue().setText( Integer.toString( channel.getVolume() ) );
		}
		if((type & BALANCE) != 0){
			track.getTrackBalance().setValue( channel.getBalance() );
		}
	}
	
	private class TrackPanel extends JPanel{
		
		private static final long serialVersionUID = 3290312295563628800L;

		private TGTrack track;
		
		private JCheckBox trackSolo;
		private JCheckBox trackMute;
		
		private JSlider trackBalance;
		private JSlider trackVolume;
		private JLabel trackVolumeValue;
		
		public TrackPanel(TGTrack track){
			this.track = track;
		}
		
		public void init(){
			this.trackSolo = new JCheckBox("Solo");
			this.trackSolo.setFont( TGConfig.FONT_WIDGETS );
			
			this.trackMute = new JCheckBox("Mute");
			this.trackMute.setFont( TGConfig.FONT_WIDGETS );
			
			this.trackBalance = new JSlider(JSlider.HORIZONTAL);
			this.trackBalance.setMinimum(0);
			this.trackBalance.setMaximum(127);
			this.trackBalance.setExtent(1);
			this.trackBalance.setPaintTicks(false);
			this.trackBalance.setPaintLabels(false);
			
			JPanel northPanel = new JPanel();
			northPanel.setLayout(new GridBagLayout());
			northPanel.add( this.trackSolo , getConstraints(0,0,1f,0f,GridBagConstraints.BOTH));
			northPanel.add( this.trackMute , getConstraints(0,1,1f,0f,GridBagConstraints.BOTH));
			northPanel.add( this.trackBalance , getConstraints(0,2,1f,0f,GridBagConstraints.BOTH));
			
			this.trackVolume = new JSlider(JSlider.VERTICAL);
			this.trackVolume.setMinimum(0);
			this.trackVolume.setMaximum(127);
			this.trackVolume.setExtent(1);
			this.trackVolume.setPaintTicks(false);
			this.trackVolume.setPaintLabels(false);
			
			JPanel trackVolumePanel = new JPanel();
			trackVolumePanel.setLayout(new BoxLayout(trackVolumePanel,BoxLayout.X_AXIS));
			trackVolumePanel.add( Box.createHorizontalGlue());
			trackVolumePanel.add( this.trackVolume );
			trackVolumePanel.add( Box.createHorizontalGlue());
			
			JLabel trackVolumeLabel = new JLabel("Volume:");
			trackVolumeLabel.setFont( TGConfig.FONT_WIDGETS );
			
			this.trackVolumeValue = new JLabel();
			this.trackVolumeValue.setFont( TGConfig.FONT_WIDGETS );
			this.trackVolumeValue.setBackground(Color.WHITE);
			
			JPanel trackVolumeValuePanel = new JPanel();
			trackVolumeValuePanel.setLayout(new GridBagLayout());
			trackVolumeValuePanel.setBorder(BorderFactory.createEtchedBorder());
			trackVolumeValuePanel.add(trackVolumeLabel, getConstraints(0,0,0f,0f,GridBagConstraints.BOTH,1,1,4));
			trackVolumeValuePanel.add(this.trackVolumeValue , getConstraints(1,0,1f,0f,GridBagConstraints.BOTH,1,1,4));
			
			this.setLayout(new BorderLayout());
			this.setBorder(BorderFactory.createEtchedBorder());
			this.setPreferredSize(new Dimension(100,300));
			this.add(northPanel, BorderLayout.NORTH);
			this.add(trackVolumePanel, BorderLayout.CENTER);
			this.add(trackVolumeValuePanel, BorderLayout.SOUTH);
		}
		
		public TGTrack getTrack(){
			return this.track;
		}
		
		public JCheckBox getTrackSolo() {
			return this.trackSolo;
		}

		public JCheckBox getTrackMute() {
			return this.trackMute;
		}

		public JSlider getTrackBalance() {
			return this.trackBalance;
		}

		public JSlider getTrackVolume() {
			return this.trackVolume;
		}

		public JLabel getTrackVolumeValue() {
			return this.trackVolumeValue;
		}
	}
	
	private class ChangeVolumeListener implements ChangeListener{
		
		private TrackPanel track;
		private TrackPanel[] tracks;
		
		public ChangeVolumeListener(TrackPanel track, TrackPanel[] tracks){
			this.track = track;
			this.tracks = tracks;
		}
		
		public void stateChanged(ChangeEvent e) {
			if(!isLocked()){
				setLocked(true);
				
				int value = this.track.getTrackVolume().getValue();
				TGChannel channel = this.track.getTrack().getChannel();
				if(value != channel.getVolume()){
					channel.setVolume((short)value );
					fireChannelChanges(this.tracks, channel, VOLUME);
				}
				
				setLocked(false);
			}
		}
	}
	
	private class ChangeBalanceListener implements ChangeListener{
		
		private TrackPanel track;
		private TrackPanel[] tracks;
		
		public ChangeBalanceListener(TrackPanel track, TrackPanel[] tracks){
			this.track = track;
			this.tracks = tracks;
		}
		
		public void stateChanged(ChangeEvent e) {
			if(!isLocked()){
				setLocked(true);
				
				int value = this.track.getTrackBalance().getValue();
				TGChannel channel = this.track.getTrack().getChannel();
				if(value != channel.getBalance()){
					channel.setBalance((short)value );
					fireChannelChanges(this.tracks, channel, BALANCE);
				}
				
				setLocked(false);
			}
		}
	}
	
	private class ChangeSoloListener implements ActionListener{
		
		private TrackPanel track;
		private TrackPanel[] tracks;
		
		public ChangeSoloListener(TrackPanel track, TrackPanel[] tracks){
			this.track = track;
			this.tracks = tracks;
		}
		
		public void actionPerformed(ActionEvent e) {
			if(!isLocked()){
				setLocked(true);
				
				TGTrack track = this.track.getTrack();
				track.setSolo(this.track.getTrackSolo().isSelected());
				if(track.isSolo()){
					track.setMute(false);
				}
				fireChannelChanges(this.tracks, track.getChannel(), SOLO);
				
				setLocked(false);
			}
		}
	}
	
	private class ChangeMuteListener implements ActionListener{
		
		private TrackPanel track;
		private TrackPanel[] tracks;
		
		public ChangeMuteListener(TrackPanel track, TrackPanel[] tracks){
			this.track = track;
			this.tracks = tracks;
		}
		
		public void actionPerformed(ActionEvent e) {
			if(!isLocked()){
				setLocked(true);
				
				TGTrack track = this.track.getTrack();
				track.setMute(this.track.getTrackMute().isSelected());
				if(track.isMute()){
					track.setSolo(false);
				}
				fireChannelChanges(this.tracks, track.getChannel(), MUTE);
				
				setLocked(false);
			}
		}
	}
}