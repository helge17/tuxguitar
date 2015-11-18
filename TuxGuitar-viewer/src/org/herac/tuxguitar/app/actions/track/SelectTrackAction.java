/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.track;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.system.config.TGConfig;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SelectTrackAction extends Action{
	public static final String NAME = "action.track.select";
	
	public SelectTrackAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK );
	}
	
	protected int execute(AWTEvent e){
		final Caret caret = getEditor().getTablature().getCaret();
		final JButton button = (JButton) e.getSource();
		final JPopupMenu menu = new JPopupMenu();
		Iterator<?> it = TuxGuitar.instance().getTablatureEditor().getTablature().getSong().getTracks();
		while( it.hasNext() ){
			final TGTrack track = (TGTrack) it.next();
			JMenuItem item = new JRadioButtonMenuItem( track.getName() , (track.getNumber() == caret.getTrack().getNumber()) );
			item.setFont( TGConfig.FONT_WIDGETS );
			item.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					caret.update( track.getNumber() );
					TuxGuitar.instance().updateCache( true );
				}
			});
			menu.add( item );
		}
		menu.show(button, 0, button.getHeight() );
		
		return 0;
	}
}
