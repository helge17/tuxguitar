package org.herac.tuxguitar.android.view.keyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.caret.TGGoDownAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoUpAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import org.herac.tuxguitar.android.action.impl.view.TGShowSmartMenuAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.android.menu.controller.TGMenuController;
import org.herac.tuxguitar.android.menu.controller.impl.contextual.TGDurationMenu;
import org.herac.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import org.herac.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import org.herac.tuxguitar.editor.action.note.TGSetNoteFretNumberAction;
import org.herac.tuxguitar.util.TGContext;

public class TGTabKeyboard extends FrameLayout {

	public TGTabKeyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onFinishInflate() {
		this.attachView();
		this.addListeners();
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
