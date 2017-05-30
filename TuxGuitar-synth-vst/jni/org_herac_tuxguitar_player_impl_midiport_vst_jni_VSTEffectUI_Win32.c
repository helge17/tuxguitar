#include <stdio.h>
#include <stdlib.h>
#include <audioeffectx.h>
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI.h"
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VST.h"

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    malloc
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_malloc(JNIEnv *env, jobject obj, jlong ptr)
{
	return ptr;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    delete
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_delete(JNIEnv *env, jobject obj, jlong ptr)
{
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    openEditor
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_openEditor(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		handle->effect->dispatcher (handle->effect, effEditOpen, 0, 0, NULL, 0);
		handle->editorOpen = JNI_TRUE;
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    closeEditor
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_closeEditor(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		handle->effect->dispatcher (handle->effect, effEditClose, 0, 0, NULL, 0);
		handle->editorOpen = JNI_FALSE;
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI
 * Method:    isEditorOpen
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI_isEditorOpen(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL && handle->editorOpen == JNI_TRUE){
		return JNI_TRUE;
	}
	return JNI_FALSE;
}
