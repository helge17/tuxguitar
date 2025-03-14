package app.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.impl.layout.TGSetChordDiagramEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGSetChordNameEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGSetScoreEnabledAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.util.TGContext;

public class TGViewMenu extends TGMenuBase {

	public TGViewMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_view, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		TGContext context = findContext();
		TGLayout layout = TGSongViewController.getInstance(context).getLayout();
		int style = layout.getStyle();

		this.initializeItem(menu, R.id.action_view_layout_show_score, this.createActionProcessor(TGSetScoreEnabledAction.NAME), true, (style & TGLayout.DISPLAY_SCORE) != 0);
		this.initializeItem(menu, R.id.action_view_layout_show_chord_name, this.createActionProcessor(TGSetChordNameEnabledAction.NAME), true, (style & TGLayout.DISPLAY_CHORD_NAME) != 0);
		this.initializeItem(menu, R.id.action_view_layout_show_chord_diagram, this.createActionProcessor(TGSetChordDiagramEnabledAction.NAME), true, (style & TGLayout.DISPLAY_CHORD_DIAGRAM) != 0);
	}
}
