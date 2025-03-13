package app.tuxguitar.app.action.impl.track;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.editors.tab.Caret;
import app.tuxguitar.app.system.config.TGConfig;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class SelectTrackAction extends TGActionBase {

	public static final String NAME = "action.track.select";

	public SelectTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		final AWTEvent awtEvent = context.getAttribute(AWTEvent.class.getName());

		final Caret caret = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
		final JButton button = (JButton) awtEvent.getSource();
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
	}
}
