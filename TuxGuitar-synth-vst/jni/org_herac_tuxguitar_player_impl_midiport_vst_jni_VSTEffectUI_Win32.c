#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <tchar.h>
#include <audioeffectx.h>
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI.h"
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VST.h"

#define WM_CREATE_VST_UI (WM_USER + 1)
#define WND_CLASS_NAME _T("tuxguitar-synth-vst-ui")
#define WND_DEFAULT_WIDTH 100
#define WND_DEFAULT_HEIGHT 100

typedef struct {
	JNIEffect* effect;
	jboolean editorOpen;
	jboolean editorProcessRunning;
} JNIEffectUI;

DWORD WINAPI JNIEffectUI_editorThreadProcess(LPVOID lParam);

LRESULT CALLBACK JNIEffectUI_editorHwndProcess(HWND hwnd, UINT message, WPARAM wParam, LPARAM lParam);

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    malloc
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_malloc(JNIEnv *env, jobject obj, jlong ptr)
{
	jlong jptr = 0;
	
	JNIEffect *effect = NULL;
	memcpy(&effect, &ptr, sizeof(effect));
	if( effect != NULL ){
		
		JNIEffectUI *handle = (JNIEffectUI *) malloc( sizeof(JNIEffectUI) );
		
		handle->effect = effect;
		handle->editorOpen = JNI_FALSE;
		handle->editorProcessRunning = JNI_FALSE;
		
		memcpy(&jptr, &handle, sizeof( handle ));
	}
	
	return jptr;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    delete
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_delete(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffectUI *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL){
		if( handle->effect != NULL){
			handle->effect = NULL;
		}
		free ( handle );
		(handle) = NULL;
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    openEditor
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_openEditor(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffectUI *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->editorOpen != JNI_TRUE ){
		handle->editorOpen = JNI_TRUE;
		
		DWORD threadId;
		HANDLE thread = CreateThread(NULL, 0, JNIEffectUI_editorThreadProcess, handle, 0, &threadId);
		if(!thread) {
			handle->editorOpen = JNI_FALSE;
		}
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    closeEditor
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_closeEditor(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffectUI *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->effect != NULL ) {
		handle->editorOpen = JNI_FALSE;
	}
	
	while(handle->editorProcessRunning == JNI_TRUE) {
		// wait for end...
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    isEditorOpen
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_isEditorOpen(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffectUI *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->effect != NULL ){
		return handle->editorOpen;
	}
	return JNI_FALSE;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    isEditorAvailable
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_isEditorAvailable(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffectUI *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->effect != NULL && handle->effect->effect != NULL ){
		return ((handle->effect->effect->flags & effFlagsHasEditor) != 0);
	}
	return JNI_FALSE;
}

DWORD WINAPI JNIEffectUI_editorThreadProcess(LPVOID lParam)
{
	JNIEffectUI *handle = NULL;
	memcpy(&handle, &lParam, sizeof(handle));
	if( handle != NULL && handle->effect != NULL && handle->effect->effect != NULL ) {
		handle->editorProcessRunning = JNI_TRUE;
		
		HINSTANCE hInstance = (HINSTANCE) GetModuleHandle(NULL);
		WNDCLASS wc = {};
		BOOL wcRegistered = GetClassInfo(hInstance, WND_CLASS_NAME, &wc);
		if(!wcRegistered) {
			wc.lpfnWndProc = JNIEffectUI_editorHwndProcess;
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
				SetWindowLongPtr(hwnd, GWLP_USERDATA, handle);
				SendMessage(hwnd, WM_CREATE_VST_UI, 0, 0);
				
				MSG msg;
				int destroyed = 0;
				while (GetMessage(&msg, 0, 0, 0)) {
					DispatchMessage(&msg);
					
					if(!destroyed && handle->editorOpen == JNI_FALSE ) {
						destroyed = 1;
						DestroyWindow(hwnd);
					}
				}
			} else {
				printf("Could not create window.");
			}
		} else {
			printf("Could not register class.");
		}
		
		handle->editorOpen = JNI_FALSE;
		handle->editorProcessRunning = JNI_FALSE;
	}
	return 0;
}

LRESULT CALLBACK JNIEffectUI_editorHwndProcess(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
	switch(msg) {
		case WM_CREATE: {
			SetTimer (hwnd, 1, 20, 0);
		} break;
		case WM_CREATE_VST_UI: {
			LONG_PTR ptr = GetWindowLongPtr(hwnd, GWLP_USERDATA);
			if( ptr ) {
				JNIEffectUI *handle = NULL;
				memcpy(&handle, &ptr, sizeof(handle));
				if( handle != NULL && handle->effect != NULL && handle->effect->effect != NULL ) {
					char effect_name[256];
					handle->effect->effect->dispatcher(handle->effect->effect, effGetEffectName, 0, 0, effect_name, 0);
					strcat(effect_name, " [TuxGuitar]");
					SetWindowText (hwnd, effect_name);
					
					handle->effect->effect->dispatcher (handle->effect->effect, effEditOpen, 0, 0, hwnd, 0);

					ERect* eRect = 0;
					handle->effect->effect->dispatcher (handle->effect->effect, effEditGetRect, 0, 0, &eRect, 0);
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
				JNIEffectUI *handle = NULL;
				memcpy(&handle, &ptr, sizeof(handle));
				if( handle != NULL && handle->effect != NULL && handle->effect->effect != NULL ) {
					handle->effect->effect->dispatcher (handle->effect->effect, effEditIdle, 0, 0, 0, 0);
				}
			}
		} break;
		case WM_DESTROY:
		{
			KillTimer (hwnd, 1);

			LONG_PTR ptr = GetWindowLongPtr(hwnd, GWLP_USERDATA);
			if( ptr ) {
				JNIEffectUI *handle = NULL;
				memcpy(&handle, &ptr, sizeof(handle));
				if( handle != NULL && handle->effect != NULL && handle->effect->effect != NULL ) {
					handle->effect->effect->dispatcher (handle->effect->effect, effEditClose, 0, 0, 0, 0);
				}
			}
			
			PostQuitMessage(hwnd);
		} break;
	}
    return DefWindowProc(hwnd, msg, wParam, lParam);
}
