/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.settings;

import java.awt.AWTEvent;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.layout.SetChordDiagramEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetChordNameEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetScoreEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetTablatureEnabledAction;
import org.herac.tuxguitar.gui.actions.transport.TransportMixerAction;
import org.herac.tuxguitar.gui.actions.transport.TransportSetupAction;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.system.config.TGConfig;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SettingsAction extends Action{
	public static final String NAME = "action.settings.settings";
	
	public SettingsAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK );
	}
	
	protected int execute(AWTEvent e){
		JButton button = (JButton) e.getSource();
		JPopupMenu menu = new JPopupMenu();
		
		this.addViewMenu( menu );
		this.addSoundMenu( menu );
		
		menu.show(button, 0, button.getHeight() );
		
		return 0;
	}
	
	protected void addViewMenu( JPopupMenu parent ){
		int style = getEditor().getTablature().getViewLayout().getStyle();
		
		JMenu showMenu = new JMenu("View");
		showMenu.setFont( TGConfig.FONT_WIDGETS );
		
		JMenuItem showScore = new JCheckBoxMenuItem("Show Score", ( style & ViewLayout.DISPLAY_SCORE) != 0 );
		showScore.addActionListener( TuxGuitar.instance().getAction( SetScoreEnabledAction.NAME ) );
		showScore.setFont( TGConfig.FONT_WIDGETS );
		showMenu.add( showScore );
		
		JMenuItem showTablature = new JCheckBoxMenuItem("Show Tablature", ( style & ViewLayout.DISPLAY_TABLATURE) != 0 );
		showTablature.addActionListener( TuxGuitar.instance().getAction( SetTablatureEnabledAction.NAME ) );
		showTablature.setFont( TGConfig.FONT_WIDGETS );
		showMenu.add( showTablature );
		
		JMenuItem showChordName = new JCheckBoxMenuItem("Show Chord Names", ( style & ViewLayout.DISPLAY_CHORD_NAME) != 0 );
		showChordName.addActionListener( TuxGuitar.instance().getAction( SetChordNameEnabledAction.NAME ) );
		showChordName.setFont( TGConfig.FONT_WIDGETS );
		showMenu.add( showChordName );
		
		JMenuItem showChordDiagram = new JCheckBoxMenuItem("Show Chord Diagrams", ( style & ViewLayout.DISPLAY_CHORD_DIAGRAM) != 0 );
		showChordDiagram.addActionListener( TuxGuitar.instance().getAction( SetChordDiagramEnabledAction.NAME ) );
		showChordDiagram.setFont( TGConfig.FONT_WIDGETS );
		showMenu.add( showChordDiagram );
		
		parent.add( showMenu );
	}
	
	protected void addSoundMenu( JPopupMenu parent ){
		JMenu soundMenu = new JMenu("Sound");
		soundMenu.setFont( TGConfig.FONT_WIDGETS );
		
		JMenuItem soundMixer = new JMenuItem("Open Mixer");
		soundMixer.addActionListener( TuxGuitar.instance().getAction( TransportMixerAction.NAME ) );
		soundMixer.setFont( TGConfig.FONT_WIDGETS );
		soundMenu.add( soundMixer );
		
		JMenuItem soundSetup = new JMenuItem("Sound Settings");
		soundSetup.addActionListener( TuxGuitar.instance().getAction( TransportSetupAction.NAME ) );
		soundSetup.setFont( TGConfig.FONT_WIDGETS );
		soundMenu.add( soundSetup );
		
		parent.add( soundMenu );
	}
}
