package org.herac.tuxguitar.app.toolbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.settings.SettingsAction;
import org.herac.tuxguitar.app.action.impl.track.SelectTrackAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportPlayAction;
import org.herac.tuxguitar.app.editors.tab.Tablature;
import org.herac.tuxguitar.app.system.config.TGConfig;
import org.herac.tuxguitar.app.util.TGResourceUtils;
import org.herac.tuxguitar.util.TGContext;

public class TGToolBar {
	
	private TGContext context;
	private JPanel buttons;
	private AbstractButton buttonPlay;
	private AbstractButton buttonTrack;
	private AbstractButton buttonSetup;
	
	public TGToolBar(TGContext context) {
		this.context = context;
	}
	
	public void init() {
		this.buttonPlay = getImageButton(new JToggleButton(), "transport_play", ".png");
		this.buttonPlay.addActionListener(new TGActionProcessorListener(this.context, TransportPlayAction.NAME));
		
		this.buttonTrack = getLinkButton (getImageButton(new JButton(), null,null) );
		this.buttonTrack.addActionListener(new TGActionProcessorListener(this.context, SelectTrackAction.NAME));
		
		this.buttonSetup = getImageButton(new JButton(), "setup", ".png");
		this.buttonSetup.addActionListener(new TGActionProcessorListener(this.context, SettingsAction.NAME));
	}
	
	public Component getPanel(){
		if( this.buttons == null ){
			this.init();
			
			this.buttons = new JPanel(){
				
				private static final long serialVersionUID = 9136810316642761074L;

				public void update( Graphics g ){
					this.paint( g );
				}
			};
			this.buttons.setLayout( new BoxLayout( this.buttons, BoxLayout.LINE_AXIS ) );
			this.buttons.add( this.buttonPlay  );
			this.buttons.add( Box.createHorizontalGlue() );
			this.buttons.add( this.buttonTrack  );
			this.buttons.add( Box.createHorizontalGlue() );
			this.buttons.add( this.buttonSetup  );
			this.buttons.setBackground( TGConfig.COLOR_WIDGET_BACKGROUND );
		}
		return this.buttons;
	}
	
	public void updateItems(){
		Tablature tablature = TuxGuitar.instance().getTablatureEditor().getTablature();
		this.buttonTrack.setForeground( Color.BLACK );
		if(this.buttonTrack.getText() == null || !this.buttonTrack.getText().equals( tablature.getCaret().getTrack().getName() )){
			this.buttonTrack.setText( tablature.getCaret().getTrack().getName());
			this.buttonTrack.getParent().invalidate();
			this.buttonTrack.getParent().validate();
			this.buttonTrack.getParent().doLayout();
		}
		this.buttonPlay.setSelected( TuxGuitar.instance().getPlayer().isRunning() );
	}
	
	private AbstractButton getImageButton( AbstractButton button, String iconPrefix, String iconSuffix ){
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setVerticalTextPosition(JButton.CENTER);
		button.setBorderPainted( false );
		button.setContentAreaFilled( false );
		button.setFocusPainted( false );
		button.setMargin( new Insets(0,0,0,0) );
		button.setIcon( TGResourceUtils.loadIcon( iconPrefix + iconSuffix ) );
		button.setPressedIcon( TGResourceUtils.loadIcon( iconPrefix + "_pressed" + iconSuffix ) );
		button.setRolloverIcon( TGResourceUtils.loadIcon( iconPrefix + "_over" + iconSuffix ) );
		button.setSelectedIcon( TGResourceUtils.loadIcon( iconPrefix + "_selected" + iconSuffix ) );
		button.setRolloverSelectedIcon( TGResourceUtils.loadIcon( iconPrefix + "_selected_over" + iconSuffix ) );
		return button;
	}
	
	private AbstractButton getLinkButton( final AbstractButton button  ){
		button.setFont( TGConfig.FONT_WIDGETS );
		button.setForeground( Color.BLACK );
		button.addMouseListener( new MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				button.setForeground( Color.BLACK );
			}
			public void mouseEntered(MouseEvent e) {
				button.setForeground( Color.LIGHT_GRAY );
			}
		});
		return button;
	}
}
