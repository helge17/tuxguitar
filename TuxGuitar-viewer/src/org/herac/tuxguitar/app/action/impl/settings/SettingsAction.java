package org.herac.tuxguitar.app.action.impl.settings;

import java.awt.AWTEvent;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.layout.SetChordDiagramEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.SetChordNameEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.SetScoreEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.SetTablatureEnabledAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportMixerAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportSetupAction;
import org.herac.tuxguitar.app.system.config.TGConfig;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.util.TGContext;

public class SettingsAction extends TGActionBase {
	
	public static final String NAME = "action.settings.settings";
	
	public SettingsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		AWTEvent awtEvent = context.getAttribute(AWTEvent.class.getName());
		
		JButton button = (JButton) awtEvent.getSource();
		JPopupMenu menu = new JPopupMenu();
		
		this.addViewMenu( menu );
		this.addSoundMenu( menu );
		
		menu.show(button, 0, button.getHeight());
	}
	
	protected void addViewMenu( JPopupMenu parent ){
		int style = TuxGuitar.instance().getTablatureEditor().getTablature().getViewLayout().getStyle();
		
		JMenu showMenu = new JMenu("View");
		showMenu.setFont( TGConfig.FONT_WIDGETS );
		
		JMenuItem showScore = new JCheckBoxMenuItem("Show Score", ( style & TGLayout.DISPLAY_SCORE) != 0 );
		showScore.addActionListener(new TGActionProcessorListener(getContext(), SetScoreEnabledAction.NAME));
		showScore.setFont( TGConfig.FONT_WIDGETS );
		showMenu.add( showScore );
		
		JMenuItem showTablature = new JCheckBoxMenuItem("Show Tablature", ( style & TGLayout.DISPLAY_TABLATURE) != 0 );
		showTablature.addActionListener(new TGActionProcessorListener(getContext(), SetTablatureEnabledAction.NAME));
		showTablature.setFont( TGConfig.FONT_WIDGETS );
		showMenu.add( showTablature );
		
		JMenuItem showChordName = new JCheckBoxMenuItem("Show Chord Names", ( style & TGLayout.DISPLAY_CHORD_NAME) != 0 );
		showChordName.addActionListener(new TGActionProcessorListener(getContext(), SetChordNameEnabledAction.NAME));
		showChordName.setFont( TGConfig.FONT_WIDGETS );
		showMenu.add( showChordName );
		
		JMenuItem showChordDiagram = new JCheckBoxMenuItem("Show Chord Diagrams", ( style & TGLayout.DISPLAY_CHORD_DIAGRAM) != 0 );
		showChordDiagram.addActionListener(new TGActionProcessorListener(getContext(), SetChordDiagramEnabledAction.NAME));
		showChordDiagram.setFont( TGConfig.FONT_WIDGETS );
		showMenu.add( showChordDiagram );
		
		parent.add( showMenu );
	}
	
	protected void addSoundMenu( JPopupMenu parent ){
		JMenu soundMenu = new JMenu("Sound");
		soundMenu.setFont( TGConfig.FONT_WIDGETS );
		
		JMenuItem soundMixer = new JMenuItem("Open Mixer");
		soundMixer.addActionListener(new TGActionProcessorListener(getContext(), TransportMixerAction.NAME));
		soundMixer.setFont( TGConfig.FONT_WIDGETS );
		soundMenu.add( soundMixer );
		
		JMenuItem soundSetup = new JMenuItem("Sound Settings");
		soundSetup.addActionListener(new TGActionProcessorListener(getContext(), TransportSetupAction.NAME));
		soundSetup.setFont( TGConfig.FONT_WIDGETS );
		soundMenu.add( soundSetup );
		
		parent.add( soundMenu );
	}
}
