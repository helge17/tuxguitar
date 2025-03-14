package app.tuxguitar.android.view.keyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.action.impl.caret.TGGoDownAction;
import app.tuxguitar.android.action.impl.caret.TGGoLeftAction;
import app.tuxguitar.android.action.impl.caret.TGGoRightAction;
import app.tuxguitar.android.action.impl.caret.TGGoUpAction;
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import app.tuxguitar.android.action.impl.view.TGShowSmartMenuAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.application.TGApplicationUtil;
import app.tuxguitar.android.menu.controller.TGMenuController;
import app.tuxguitar.android.menu.controller.impl.contextual.TGDurationMenu;
import app.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import app.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import app.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import app.tuxguitar.editor.action.note.TGSetNoteFretNumberAction;
import app.tuxguitar.util.TGContext;

public class TGTabKeyboard extends FrameLayout {

	public TGTabKeyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onFinishInflate() {
		this.attachView();
		this.addListeners();
		super.onFinishInflate();
	}

	public void attachView() {
		TGTabKeyboardController.getInstance(TGApplicationUtil.findContext(this)).setView(this);
	}

	public void addListeners() {
		TGContext context = this.findContext();
		findViewById(R.id.tab_kb_button_number_0).setOnClickListener(new TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(0)));
		findViewById(R.id.tab_kb_button_number_1).setOnClickListener(new TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(1)));
		findViewById(R.id.tab_kb_button_number_2).setOnClickListener(new TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(2)));
		findViewById(R.id.tab_kb_button_number_3).setOnClickListener(new TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(3)));
		findViewById(R.id.tab_kb_button_number_4).setOnClickListener(new TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(4)));
		findViewById(R.id.tab_kb_button_number_5).setOnClickListener(new TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(5)));
		findViewById(R.id.tab_kb_button_number_6).setOnClickListener(new TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(6)));
		findViewById(R.id.tab_kb_button_number_7).setOnClickListener(new TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(7)));
		findViewById(R.id.tab_kb_button_number_8).setOnClickListener(new TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(8)));
		findViewById(R.id.tab_kb_button_number_9).setOnClickListener(new TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(9)));

		findViewById(R.id.tab_kb_button_insert).setOnClickListener(new TGActionProcessorListener(context, TGInsertRestBeatAction.NAME));
		findViewById(R.id.tab_kb_button_delete).setOnClickListener(new TGActionProcessorListener(context, TGDeleteNoteOrRestAction.NAME));

		findViewById(R.id.tab_kb_button_up).setOnClickListener(new TGActionProcessorListener(context, TGGoUpAction.NAME));
		findViewById(R.id.tab_kb_button_down).setOnClickListener(new TGActionProcessorListener(context, TGGoDownAction.NAME));
		findViewById(R.id.tab_kb_button_left).setOnClickListener(new TGActionProcessorListener(context, TGGoLeftAction.NAME));
		findViewById(R.id.tab_kb_button_right).setOnClickListener(new TGActionProcessorListener(context, TGGoRightAction.NAME));

		findViewById(R.id.tab_kb_button_increment_duration).setOnClickListener(new TGActionProcessorListener(context, TGIncrementDurationAction.NAME));
		findViewById(R.id.tab_kb_button_decrement_duration).setOnClickListener(new TGActionProcessorListener(context, TGDecrementDurationAction.NAME));
		findViewById(R.id.tab_kb_button_set_duration).setOnClickListener(createContextMenuActionListener(new TGDurationMenu(this.findActivity())));

		findViewById(R.id.tab_kb_button_select).setOnClickListener(new TGActionProcessorListener(context, TGShowSmartMenuAction.NAME));
	}

	public TGActionProcessorListener createContextMenuActionListener(TGMenuController controller) {
		TGActionProcessorListener tgActionProcessor = new TGActionProcessorListener(this.findContext(), TGOpenMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, this.findActivity());
		return tgActionProcessor;
	}

	public void toggleVisibility() {
		this.clearAnimation();
		if( this.getVisibility() == VISIBLE ) {
			this.animate().setDuration(300).translationY(this.getHeight()).setListener(new AnimatorListenerAdapter() {
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					TGTabKeyboard.this.clearAnimation();
					TGTabKeyboard.this.setVisibility(GONE);
				}
			});
		} else {
			this.setVisibility(VISIBLE);
			this.animate().setDuration(300).translationY(0f).setListener(new AnimatorListenerAdapter() {
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					TGTabKeyboard.this.clearAnimation();
				}
			});
		}
	}

	private TGActivity findActivity() {
		return (TGActivity) getContext();
	}

	private TGContext findContext() {
		return TGApplicationUtil.findContext(this);
	}
}
