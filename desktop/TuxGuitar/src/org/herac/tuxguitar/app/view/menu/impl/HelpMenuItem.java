package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.help.TGHelpGoHomeAction;
import org.herac.tuxguitar.app.action.impl.help.TGOpenAboutDialogAction;
import org.herac.tuxguitar.app.action.impl.help.TGOpenDocumentationDialogAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class HelpMenuItem extends TGMenuItem{

	private UIMenuSubMenuItem helpMenuItem;
	private UIMenuActionItem doc;
	private UIMenuActionItem about;
	private UIMenuActionItem goHome;

	public HelpMenuItem(UIMenu parent) {
		this.helpMenuItem = parent.createSubMenuItem();
	}

	public void showItems(){
		//--DOC--
		this.doc = this.helpMenuItem.getMenu().createActionItem();
		this.doc.addSelectionListener(this.createActionProcessor(TGOpenDocumentationDialogAction.NAME));

		//--ABOUT--
		this.about = this.helpMenuItem.getMenu().createActionItem();
		this.about.addSelectionListener(this.createActionProcessor(TGOpenAboutDialogAction.NAME));

		// -- GO TO HOME PAGE --
		this.goHome = this.helpMenuItem.getMenu().createActionItem();
		this.goHome.addSelectionListener(this.createActionProcessor(TGHelpGoHomeAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void loadProperties(){
		this.setMenuItemTextAndAccelerator(this.helpMenuItem, "help", null);
		this.setMenuItemTextAndAccelerator(this.doc, "help.doc", TGOpenDocumentationDialogAction.NAME);
		this.setMenuItemTextAndAccelerator(this.about, "help.about", TGOpenAboutDialogAction.NAME);
		this.setMenuItemTextAndAccelerator(this.goHome, "help.goHome", TGHelpGoHomeAction.NAME);
	}

	public void loadIcons(){
		this.doc.setImage(TuxGuitar.getInstance().getIconManager().getHelpDoc());
		this.about.setImage(TuxGuitar.getInstance().getIconManager().getHelpAbout());
		this.goHome.setImage(TuxGuitar.getInstance().getIconManager().getGoHome());
	}

	public void update(){
		//Nothing to do
	}
}
