#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>
#include <mmsystem.h>
#include "org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem.h"

typedef struct handle{
	HMIDIOUT* out;
}midi_handle_t;

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem_malloc(JNIEnv* env, jobject obj)
{
	jlong ptr = 0;
	
	midi_handle_t *handle = (midi_handle_t *) malloc( sizeof(midi_handle_t) );
	handle->out = NULL;
	
	memcpy(&ptr, &handle, sizeof( handle ));
	
	return ptr;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem_free(JNIEnv* env, jobject obj, jlong ptr)
{
	midi_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL){
		free( handle );
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem_findPorts(JNIEnv* env, jobject obj, jlong ptr)
{
	midi_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL){
		MIDIOUTCAPSW moc;
		UINT count, i;
		count = midiOutGetNumDevs();
		for (i = 0; i < count; i++){
			if (midiOutGetDevCapsW(i, &moc, sizeof(MIDIOUTCAPSW)) == MMSYSERR_NOERROR){
				//Add a new MidiDevice to the java class
				jint device  = i;
				jstring name =  (*env)->NewString( env, (jchar*)moc.szPname , wcslen( moc.szPname ) );
				jclass cl = (*env)->GetObjectClass(env, obj);
				jmethodID mid = (*env)->GetMethodID(env, cl, "addPort", "(Ljava/lang/String;I)V");
				if (mid != 0){
					(*env)->CallVoidMethod(env, obj, mid, name, device);
				}
			}
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem_openPort(JNIEnv* env, jobject obj, jlong ptr, jint device)
{
	midi_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->out == NULL){
		handle->out = (HMIDIOUT *)malloc( sizeof(HMIDIOUT) );
		if ( midiOutOpen(handle->out, (UINT)device, 0, 0, CALLBACK_WINDOW) != MMSYSERR_NOERROR ){
			free( handle->out );
			handle->out = NULL;
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem_closePort(JNIEnv* env, jobject obj, jlong ptr)
{
	midi_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->out != NULL){
		midiOutClose(*handle->out);
		free( handle->out );
		handle->out = NULL;
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem_noteOn(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint note, jint velocity)
{
	midi_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->out != NULL){
		midiOutShortMsg(*handle->out, ( ( 0x90 | channel ) | ( note << 8) | ( velocity << 16) ) );
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem_noteOff(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint note, jint velocity)
{
	midi_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->out != NULL){
		midiOutShortMsg(*handle->out, ( ( 0x80 | channel ) | ( note << 8) | ( velocity << 16) ) );
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem_programChange(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint program)
{
	midi_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->out != NULL){
		midiOutShortMsg(*handle->out, ( ( 0xC0 | channel ) | ( program << 8) ) );
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem_controlChange(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint control, jint value)
{
	midi_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->out != NULL){
		midiOutShortMsg(*handle->out, ( ( 0xB0 | channel ) | ( control << 8) | ( value << 16) ) );
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_winmm_MidiSystem_pitchBend(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint value)
{
	midi_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->out != NULL){
		midiOutShortMsg(*handle->out, ( ( 0xE0 | channel ) | ( (value * 128 * 2) << 8 ) ) );
	}
}
