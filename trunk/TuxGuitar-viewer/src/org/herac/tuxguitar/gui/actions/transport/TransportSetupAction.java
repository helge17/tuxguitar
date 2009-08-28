/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.transport;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.ActionDialog;
import org.herac.tuxguitar.gui.system.config.TGConfig;
import org.herac.tuxguitar.player.base.MidiOutputPort;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransportSetupAction extends ActionDialog{
	public static final String NAME = "action.transport.setup";
	
	public TransportSetupAction() {
		super( NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE );
	}
	
	protected void openDialog(){
		final List ports = TuxGuitar.instance().getPlayer().listOutputPorts();
		final String[] listData = new String[ ports.size() ];
		
		int selectedIndex = -1;
		for( int i = 0 ; i < ports.size() ; i ++ ){
			MidiOutputPort port = (MidiOutputPort)ports.get( i );
			listData[ i ] = port.getName();
			if(TuxGuitar.instance().getPlayer().isOutputPortOpen( port.getKey() )){
				selectedIndex = i;
			}
		}
		
		final JFrame dialog = createDialog();
		
		final JList list = new JList();
		list.setFont( TGConfig.FONT_WIDGETS );
		list.setListData(listData);
		if(selectedIndex >= 0){
			list.setSelectedIndex(selectedIndex);
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(null, "MIDI Port", TitledBorder.LEADING, TitledBorder.TOP , TGConfig.FONT_WIDGETS));
		panel.add(new JScrollPane(list));
		
		//-----------------------------------------
		JButton buttonOk = new JButton("Ok");
		buttonOk.setFont( TGConfig.FONT_WIDGETS );
		buttonOk.setPreferredSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = list.getSelectedIndex();
				final MidiOutputPort port = (MidiOutputPort)ports.get( index );
				// Load the new MIDI Port
				new Thread(new Runnable() {
					public void run() {
						TuxGuitar.instance().getPlayer().reset();
						TuxGuitar.instance().getPlayer().loadOutputPort(port);
						TuxGuitar.instance().updateCache( true );
					}
				}).start();
				
				dialog.dispose();
			}
		});
		
		JButton buttonCancel = new JButton("Cancel");
		buttonCancel.setFont( TGConfig.FONT_WIDGETS );
		buttonCancel.setPreferredSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				TuxGuitar.instance().updateCache( true );
			}
		});
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		buttons.add(Box.createHorizontalGlue());
		buttons.add(buttonOk);
		buttons.add(buttonCancel);
		
		//-----------------------------------------
		JPanel container = new JPanel();
		container.setLayout(new GridBagLayout());
		container.add( panel, getConstraints(0,0,1f,1f,GridBagConstraints.BOTH));
		container.add( buttons, getConstraints(0,1,1f,0f,GridBagConstraints.BOTH));
		
		dialog.setTitle("MIDI Setup");
		dialog.getContentPane().setLayout(new GridBagLayout());
		dialog.getContentPane().add(container, getConstraints(0,0,1f,1f,GridBagConstraints.BOTH));
		dialog.setMinimumSize(new Dimension(350,0));
		dialog.pack();
		dialog.setLocationRelativeTo(TuxGuitar.instance().getShell());
		dialog.setResizable(false);
		dialog.setVisible(true);
	}
}
