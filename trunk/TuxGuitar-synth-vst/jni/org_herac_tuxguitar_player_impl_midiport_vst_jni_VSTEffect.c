#include <stdio.h>
#include <stdlib.h>
#include <audioeffectx.h>
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect.h"
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VST.h"

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    malloc
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_malloc(JNIEnv *env, jobject obj, jlong ptr)
{
	jlong jptr = 0;
	
	AEffect *effect = NULL;
	memcpy(&effect, &ptr, sizeof(effect));
	if(effect != NULL && effect->magic == kEffectMagic){
		
		JNIEffect *handle = (JNIEffect *) malloc( sizeof(JNIEffect) );
		
		handle->effect = effect;
		handle->editorOpen = JNI_FALSE;
		handle->editorHandle = NULL;
		
		memcpy(&jptr, &handle, sizeof( handle ));
	}
	
	return jptr;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    delete
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_delete(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL){
		// effect should be destroyed at "closeEffect"
		if(handle->effect != NULL){
			handle->effect = NULL;
		}
		free ( handle );
		(handle) = NULL;
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    openEffect
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_openEffect(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		handle->effect->resvd1 = NULL;
		memcpy(&handle->effect->resvd1, &handle, sizeof(handle->effect->resvd1));
		handle->effect->dispatcher (handle->effect, effOpen, 0, 0, 0, 0);
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    closeEffect
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_closeEffect(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		handle->effect->resvd1 = NULL;
		handle->effect->dispatcher (handle->effect, effClose, 0, 0, 0, 0);
		handle->effect = NULL;
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    getNumParams
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_getNumParams(JNIEnv *env, jobject obj, jlong ptr)
{
	jint value = 0;
	
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		value = (jint)handle->effect->numParams;
	}
	
	return value;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    getNumInputs
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_getNumInputs(JNIEnv *env, jobject obj, jlong ptr)
{
	jint value = 0;
	
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		value = (jint)handle->effect->numInputs;
	}
	
	return value;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    getNumOutputs
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_getNumOutputs(JNIEnv *env, jobject obj, jlong ptr)
{
	jint value = 0;
	
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		value = (jint)handle->effect->numOutputs;
	}
	
	return value;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    setBlockSize
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_setBlockSize(JNIEnv *env, jobject obj, jlong ptr, jint value)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		handle->effect->dispatcher (handle->effect, effSetBlockSize, 0, value, 0, 0);
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    setSampleRate
 * Signature: (JF)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_setSampleRate(JNIEnv *env, jobject obj, jlong ptr, jfloat value)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		handle->effect->dispatcher (handle->effect, effSetSampleRate, 0, 0, 0, value);
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    setParameter
 * Signature: (JIF)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_setParameter(JNIEnv *env, jobject obj, jlong ptr, jint index, jfloat value)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		handle->effect->setParameter(handle->effect, index, value);
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    getParameter
 * Signature: (JI)F
 */
JNIEXPORT jfloat JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_getParameter(JNIEnv *env, jobject obj, jlong ptr, jint index)
{
	jfloat value = 0;
	
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		value = handle->effect->getParameter(handle->effect, index);
	}
	
	return value;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    getParameterName
 * Signature: (JI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_getParameterName(JNIEnv *env, jobject obj, jlong ptr, jint index)
{
	jstring jvalue = NULL;
	
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		char *value = (char *)malloc( sizeof(char) * 255 ); //kVstMaxParamStrLen is very small
		handle->effect->dispatcher (handle->effect, effGetParamName, index, 0, value, 0);
		jvalue = env->NewStringUTF( value );
		free( value );
	}
	
	return jvalue;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    getParameterLabel
 * Signature: (JI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_getParameterLabel(JNIEnv *env, jobject obj, jlong ptr, jint index)
{
	jstring jvalue = NULL;
	
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		char *value = (char *)malloc( sizeof(char) * 255 ); //kVstMaxParamStrLen is very small
		handle->effect->dispatcher (handle->effect, effGetParamLabel, index, 0, value, 0);
		jvalue = env->NewStringUTF( value );
		free( value );
	}
	
	return jvalue;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    sendShortMessage
 * Signature: (JBBBB)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_sendMessages(JNIEnv *env, jobject obj, jlong ptr, jobjectArray jmessages)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		int count = env->GetArrayLength( jmessages  );
		VstEvents *midi_events = (VstEvents *) malloc( (sizeof(VstEvents) - 2) + (sizeof(VstEvent *) * count) );
		midi_events->numEvents = env->GetArrayLength( jmessages  );
		midi_events->reserved = NULL;
		
		for( int i = 0 ; i < midi_events->numEvents ; i ++ ){
			jbyte *data = env->GetByteArrayElements ( (jbyteArray) env->GetObjectArrayElement(jmessages, i) , NULL);
			midi_events->events[i] = (VstEvent *) malloc( sizeof( VstMidiEvent ) );
			((VstMidiEvent *)midi_events->events[i])->type = kVstMidiType;
			((VstMidiEvent *)midi_events->events[i])->byteSize = sizeof(VstMidiEvent);
			((VstMidiEvent *)midi_events->events[i])->deltaFrames = 0;
			((VstMidiEvent *)midi_events->events[i])->flags = 0;
			((VstMidiEvent *)midi_events->events[i])->noteLength = 0;
			((VstMidiEvent *)midi_events->events[i])->noteOffset = 0;
			((VstMidiEvent *)midi_events->events[i])->detune = 0;
			((VstMidiEvent *)midi_events->events[i])->noteOffVelocity = 0;
			((VstMidiEvent *)midi_events->events[i])->reserved1 = 0;
			((VstMidiEvent *)midi_events->events[i])->reserved2 = 0;
			((VstMidiEvent *)midi_events->events[i])->midiData[0] = ( data[0] | (unsigned char )data[1]);
			((VstMidiEvent *)midi_events->events[i])->midiData[1] = data[2];
			((VstMidiEvent *)midi_events->events[i])->midiData[2] = data[3];
			((VstMidiEvent *)midi_events->events[i])->midiData[3] = 0;
		}
		
		handle->effect->dispatcher (handle->effect, effProcessEvents, 0, 0, midi_events, 0);
		
		delete midi_events;
	}
}
 
/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    sendProcessReplacing
 * Signature: (J[[F[[FI)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_sendProcessReplacing(JNIEnv *env, jobject obj, jlong ptr, jobjectArray inputs, jobjectArray outputs, jint blocksize)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		int ilen = env->GetArrayLength( inputs  );
		int olen = env->GetArrayLength( outputs );
		
		float** pInputs =  (float **)malloc( (sizeof(float *) * ilen) );
		float** pOutputs = (float **)malloc( (sizeof(float *) * olen) );
		
		for(int i = 0; i < ilen; i++) {
			pInputs[i] = (float*) env->GetPrimitiveArrayCritical((jarray) env->GetObjectArrayElement(inputs, i), NULL);
		}
		for(int i = 0; i < olen; i++) {
			pOutputs[i] = (float*) env->GetPrimitiveArrayCritical((jarray) env->GetObjectArrayElement(outputs, i), NULL);
		}
		
		handle->effect->processReplacing(handle->effect, pInputs, pOutputs, blocksize);
		
		for(int i = 0; i < ilen; i++) {
			env->ReleasePrimitiveArrayCritical((jarray) env->GetObjectArrayElement(inputs, i), pInputs[i], 0);
		}
		for(int i = 0; i < olen; i++) {
			env->ReleasePrimitiveArrayCritical((jarray) env->GetObjectArrayElement(outputs, i), pOutputs[i], 0);
		}
		
		delete [] pInputs;
		delete [] pOutputs;
		
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    openEditor
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_openEditor(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		handle->effect->dispatcher (handle->effect, effEditOpen, 0, 0, NULL, 0);
		handle->editorOpen = JNI_TRUE;
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    closeEditor
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_closeEditor(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL){
		handle->effect->dispatcher (handle->effect, effEditClose, 0, 0, NULL, 0);
		handle->editorOpen = JNI_FALSE;
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect
 * Method:    isEditorOpen
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect_isEditorOpen(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIEffect *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->effect != NULL && handle->editorOpen == JNI_TRUE){
		return JNI_TRUE;
	}
	return JNI_FALSE;
}
