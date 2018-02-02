#include <stdlib.h>
#include <unistd.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xatom.h>
#include "VSTEffectUI.h"

typedef struct {
	Display *dpy;
	bool editorOpen;
	bool editorProcessRunning;
} JNIEffectUI;

void VSTEffectUI_malloc(JNIEffect *effect)
{
	if( effect != NULL ){
		JNIEffectUI *handle = (JNIEffectUI *) malloc( sizeof(JNIEffectUI) );
		
		handle->dpy = XOpenDisplay(NULL);
		handle->editorOpen = false;
		handle->editorProcessRunning = false;
		
		effect->ui = handle;
	}
}

void VSTEffectUI_delete(JNIEffect *effect)
{
	if( effect != NULL && effect->ui != NULL ) {
		JNIEffectUI *handle = (JNIEffectUI *) effect->ui;
		if( handle->dpy != NULL){
			XCloseDisplay(handle->dpy);
			handle->dpy = NULL;
		}
		free ( handle );
		
		effect->ui = NULL;
	}
}

void VSTEffectUI_openEditor(JNIEffect *effect)
{
	if( effect != NULL && effect->ui != NULL ) {
		JNIEffectUI *handle = (JNIEffectUI *) effect->ui;
		if(!handle->editorOpen ) {
			handle->editorOpen = true;
		}
	}
}

void VSTEffectUI_closeEditor(JNIEffect *effect)
{
	if( effect != NULL && effect->ui != NULL ) {
		JNIEffectUI *handle = (JNIEffectUI *) effect->ui;
		
		handle->editorOpen = false;
		
		while(handle->editorProcessRunning == true) {
			// wait for end...
		}
	}
}

void VSTEffectUI_isEditorOpen(JNIEffect *effect, bool *value)
{
	(*value) = (effect != NULL && effect->ui != NULL ? ((JNIEffectUI *) effect->ui)->editorOpen : false);
}

void VSTEffectUI_isEditorAvailable(JNIEffect *effect, bool *value)
{
	bool editorAvailable = false;
	if( effect != NULL && effect->effect != NULL ) {
		editorAvailable = ((effect->effect->flags & effFlagsHasEditor) != 0);
	}
	(*value) = editorAvailable;
}

void VSTEffectUI_process(JNIEffect *effect) 
{
	if( effect != NULL && effect->effect != NULL ) {
		JNIEffectUI *ui = (JNIEffectUI *) effect->ui;
		if( ui->editorOpen && !ui->editorProcessRunning ) {
			ui->editorProcessRunning = true;
			
			Window win = XCreateSimpleWindow(ui->dpy, DefaultRootWindow(ui->dpy), 0, 0, 300, 300, 0, 0, 0);
			
			// ------------------------------------------------------------ //
			Atom wmDeleteMessage = XInternAtom(ui->dpy, "WM_DELETE_WINDOW", false);
			XSetWMProtocols(ui->dpy, win, &wmDeleteMessage, 1);
			
			Atom windowTypeProp = XInternAtom(ui->dpy, "_NET_WM_WINDOW_TYPE", False);
			Atom windowTypeValue = XInternAtom(ui->dpy, "_NET_WM_WINDOW_TYPE_DIALOG", False);
			XChangeProperty(ui->dpy, win, windowTypeProp, XA_ATOM, 32, PropModeReplace, (unsigned char *)&windowTypeValue, 1);

			Atom stateProp = XInternAtom(ui->dpy, "_NET_WM_STATE", False);
			Atom stateValue = XInternAtom(ui->dpy, "_NET_WM_STATE_ABOVE", False);
			XChangeProperty(ui->dpy, win, stateProp, XA_ATOM, 32, PropModeReplace, (unsigned char *) &stateValue, 1);

			// ------------------------------------------------------------ //
			char effect_name[256];
			effect->effect->dispatcher(effect->effect, effGetEffectName, 0, 0, effect_name, 0);
			strcat(effect_name, " [TuxGuitar]");
			XStoreName(ui->dpy, win, effect_name);

			// ------------------------------------------------------------ //
			ERect* eRect = 0;
			effect->effect->dispatcher (effect->effect, effEditGetRect, 0, 0, &eRect, 0);
			if (eRect) {
				int width = eRect->right - eRect->left;
				int height = eRect->bottom - eRect->top;
				
				XSizeHints hHints;
				hHints.min_width  = width;
				hHints.min_height  = height;
				hHints.max_width = width;
				hHints.max_height = height;
				hHints.flags = USSize | PSize | PMinSize | PMaxSize;
				XSetWMSizeHints(ui->dpy, win, &hHints, (Atom) USSize | PSize | PMinSize | PMaxSize );
				XSetNormalHints(ui->dpy, win, &hHints );
				XResizeWindow(ui->dpy, win, width, height);
			}

			// ------------------------------------------------------------ //
			XMapWindow(ui->dpy, win);
			XFlush(ui->dpy);
			effect->effect->dispatcher (effect->effect, effEditOpen, 0, (VstIntPtr) ui->dpy, (void*) win, 0);
			
			// ------------------------------------------------------------ //
			XEvent event;
			while(ui->editorOpen) {
				if (XPending(ui->dpy)) {
					XNextEvent(ui->dpy, &event);
					if (event.type == ClientMessage) {
						if ((Atom)event.xclient.data.l[0] == wmDeleteMessage) {
							ui->editorOpen = false;
						}
					}
				}
			}
			
			// ------------------------------------------------------------ //
			effect->effect->dispatcher (effect->effect, effEditClose, 0, 0, NULL, 0);
			
			XDestroyWindow(ui->dpy, win);
			XFlush(ui->dpy);
			
			ui->editorProcessRunning = false;
		} else {
			usleep(100000);
		}
	}
}
