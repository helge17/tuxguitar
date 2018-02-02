#include <stdlib.h>
#include <windows.h>
#include <tchar.h>
#include "VSTEffectUI.h"
#include "VSTLogger.h"

#define WM_CREATE_VST_UI (WM_USER + 1)
#define WND_CLASS_NAME _T("tuxguitar-synth-vst-ui")
#define WND_DEFAULT_WIDTH 100
#define WND_DEFAULT_HEIGHT 100

typedef struct {
	bool editorOpen;
	bool editorProcessRunning;
} VSTEffectHandleUI;

LRESULT CALLBACK VSTEffectHandleUI_editorHwndProcess(HWND hwnd, UINT message, WPARAM wParam, LPARAM lParam);

void VSTEffectUI_malloc(VSTEffectHandle *effect)
{
	if( effect != NULL ) {
		
		VSTEffectHandleUI *handle = (VSTEffectHandleUI *) malloc( sizeof(VSTEffectHandleUI) );
		
		handle->editorOpen = false;
		handle->editorProcessRunning = false;
		
		effect->ui = handle;
	}
}

void VSTEffectUI_delete(VSTEffectHandle *effect)
{
	if( effect != NULL && effect->ui != NULL ){
		free ( effect->ui );
		
		effect->ui = NULL;
	}
}

void VSTEffectUI_openEditor(VSTEffectHandle *effect)
{
	if( effect != NULL && effect->ui != NULL ) {
		VSTEffectHandleUI *handle = (VSTEffectHandleUI *) effect->ui;
		
		if( handle->editorOpen != true ){
			handle->editorOpen = true;
		}
	}
}

void VSTEffectUI_closeEditor(VSTEffectHandle *effect)
{
	if( effect != NULL && effect->ui != NULL ) {
		VSTEffectHandleUI *handle = (VSTEffectHandleUI *) effect->ui;
		
		handle->editorOpen = false;
		
		while(handle->editorProcessRunning == true) {
			// wait for end...
		}
	}
}

void VSTEffectUI_isEditorOpen(VSTEffectHandle *effect, bool *value)
{
	(*value) = (effect != NULL && effect->ui != NULL ? ((VSTEffectHandleUI *) effect->ui)->editorOpen : false);
}

void VSTEffectUI_isEditorAvailable(VSTEffectHandle *effect, bool *value)
{
	bool editorAvailable = false;
	if( effect != NULL && effect->effect != NULL ) {
		editorAvailable = ((effect->effect->flags & effFlagsHasEditor) != 0);
	}
	(*value) = editorAvailable;
}

void VSTEffectUI_process(VSTEffectHandle *effect)
{
	if( effect != NULL && effect->ui != NULL && effect->effect != NULL ) {
		VSTEffectHandleUI *effect_ui = (VSTEffectHandleUI *) effect->ui;
		if( effect_ui->editorOpen && !effect_ui->editorProcessRunning ) {
			effect_ui->editorProcessRunning = true;
			
			HINSTANCE hInstance = (HINSTANCE) GetModuleHandle(NULL);
			WNDCLASS wc = {};
			BOOL wcRegistered = GetClassInfo(hInstance, WND_CLASS_NAME, &wc);
			if(!wcRegistered) {
				wc.lpfnWndProc = VSTEffectHandleUI_editorHwndProcess;
				wc.hInstance = hInstance;
				wc.lpszClassName = WND_CLASS_NAME;
				wc.hbrBackground = (HBRUSH)(COLOR_ACTIVECAPTION + 1);
				wcRegistered = RegisterClass(&wc);
			}
			
			if( wcRegistered) {
				DWORD style = (WS_OVERLAPPED | WS_CAPTION | WS_SYSMENU | WS_MINIMIZEBOX);
				HWND hwnd = CreateWindow(WND_CLASS_NAME, NULL, style, 0, 0, WND_DEFAULT_WIDTH, WND_DEFAULT_HEIGHT, NULL, 0, hInstance, 0);
				if( hwnd ) {
					ShowWindow(hwnd, SW_SHOW);
					SetWindowLongPtr(hwnd, GWLP_USERDATA, (LONG) effect);
					SendMessage(hwnd, WM_CREATE_VST_UI, 0, 0);
					
					MSG msg;
					int destroyed = 0;
					while (GetMessage(&msg, 0, 0, 0)) {
						DispatchMessage(&msg);
						
						if(!destroyed && !effect_ui->editorOpen) {
							destroyed = 1;
							DestroyWindow(hwnd);
						}
					}
				} else {
					VSTLogger_log("Could not create window.\n");
				}
			} else {
				VSTLogger_log("Could not register class.\n");
			}
			
			effect_ui->editorOpen = false;
			effect_ui->editorProcessRunning = false;
		} else {
			Sleep(100);
		}
	}
}

LRESULT CALLBACK VSTEffectHandleUI_editorHwndProcess(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
	switch(msg) {
		case WM_CREATE: {
			SetTimer (hwnd, 1, 20, 0);
		} break;
		case WM_CREATE_VST_UI: {
			LONG_PTR ptr = GetWindowLongPtr(hwnd, GWLP_USERDATA);
			if( ptr ) {
				VSTEffectHandle *effect = NULL;
				memcpy(&effect, &ptr, sizeof(effect));
				if( effect != NULL && effect->effect != NULL ) {
					char effect_name[256];
					effect->effect->dispatcher(effect->effect, effGetEffectName, 0, 0, effect_name, 0);
					strcat(effect_name, " [TuxGuitar]");
					SetWindowText (hwnd, effect_name);
					
					effect->effect->dispatcher (effect->effect, effEditOpen, 0, 0, hwnd, 0);

					ERect* eRect = 0;
					effect->effect->dispatcher (effect->effect, effEditGetRect, 0, 0, &eRect, 0);
					if (eRect)
					{
						int x = 0;
						int y = 0;
						int width = eRect->right - eRect->left;
						int height = eRect->bottom - eRect->top;
						if (width < WND_DEFAULT_WIDTH) {
							width = WND_DEFAULT_WIDTH;
						}
						if (height < WND_DEFAULT_HEIGHT) {
							height = WND_DEFAULT_HEIGHT;
						}
						
						RECT wRect;
						SetRect (&wRect, 0, 0, width, height);
						AdjustWindowRect (&wRect, GetWindowLong (hwnd, GWL_STYLE), FALSE);
						
						width = wRect.right - wRect.left;
						height = wRect.bottom - wRect.top;
						x = ((GetSystemMetrics(SM_CXSCREEN) - width) / 2);
						y = ((GetSystemMetrics(SM_CYSCREEN) - height) / 2);
						SetWindowPos (hwnd, HWND_TOPMOST, x, y, width, height, 0);
					}
				}
			}
		} break;
		case WM_TIMER:
		{
			LONG_PTR ptr = GetWindowLongPtr(hwnd, GWLP_USERDATA);
			if( ptr ) {
				VSTEffectHandle *effect = NULL;
				memcpy(&effect, &ptr, sizeof(effect));
				if( effect != NULL && effect->effect != NULL ) {
					effect->effect->dispatcher (effect->effect, effEditIdle, 0, 0, 0, 0);
				}
			}
		} break;
		case WM_DESTROY:
		{
			KillTimer (hwnd, 1);

			LONG_PTR ptr = GetWindowLongPtr(hwnd, GWLP_USERDATA);
			if( ptr ) {
				VSTEffectHandle *effect = NULL;
				memcpy(&effect, &ptr, sizeof(effect));
				if( effect != NULL && effect->effect != NULL ) {
					effect->effect->dispatcher (effect->effect, effEditClose, 0, 0, 0, 0);
				}
			}
			
			PostQuitMessage(0);
		} break;
	}
    return DefWindowProc(hwnd, msg, wParam, lParam);
}
