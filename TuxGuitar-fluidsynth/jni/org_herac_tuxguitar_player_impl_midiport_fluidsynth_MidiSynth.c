#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fluidsynth.h>
#include "org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth.h"

typedef struct{
	fluid_settings_t* settings;
	fluid_synth_t* synth;
	fluid_audio_driver_t* driver;
	int soundfont_id;
}fluid_handle_t;

typedef struct{
	JNIEnv* env;
	jobject options;
}fluid_settings_foreach_option_data;

void fluid_settings_foreach_option_callback(void *data, char *name, char *option)
{
	fluid_settings_foreach_option_data* handle = (fluid_settings_foreach_option_data *)data;
	
	jstring joption = (*handle->env)->NewStringUTF(handle->env, option);
	jclass cl = (*handle->env)->GetObjectClass(handle->env, handle->options);
	jmethodID mid = (*handle->env)->GetMethodID(handle->env, cl, "add", "(Ljava/lang/Object;)Z");
	if (mid != 0){
		(*handle->env)->CallBooleanMethod(handle->env, handle->options, mid, joption);
	}
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_malloc(JNIEnv* env, jobject obj)
{
	jlong ptr = 0;
	
	fluid_handle_t *handle = (fluid_handle_t *) malloc( sizeof(fluid_handle_t) );
	
	handle->settings = new_fluid_settings();
	handle->synth = NULL;
	handle->driver = NULL;
	handle->soundfont_id = 0;
	
	memcpy(&ptr, &handle, sizeof( handle ));
	
	return ptr;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_free(JNIEnv* env, jobject obj, jlong ptr)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL){
		if( handle->driver != NULL ){
			delete_fluid_audio_driver(handle->driver);
		}
		if( handle->synth != NULL ){
			delete_fluid_synth(handle->synth);
		}
		if( handle->settings != NULL ){
			delete_fluid_settings(handle->settings);
		}
		free ( handle );
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_open(JNIEnv* env, jobject obj, jlong ptr)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL){
		if( handle->driver != NULL ){
			delete_fluid_audio_driver(handle->driver);
		}
		if( handle->synth != NULL ){
			delete_fluid_synth(handle->synth);
		}
		handle->synth = new_fluid_synth(handle->settings);
		handle->driver = new_fluid_audio_driver(handle->settings,handle->synth);
		
		fluid_synth_set_interp_method( handle->synth, -1, FLUID_INTERP_NONE );
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_close(JNIEnv* env, jobject obj, jlong ptr)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL){
		if( handle->driver != NULL ){
			delete_fluid_audio_driver(handle->driver);
		}
		if( handle->synth != NULL ){
			delete_fluid_synth(handle->synth);
		}
		handle->synth = NULL;
		handle->driver = NULL;
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_loadFont(JNIEnv* env, jobject obj, jlong ptr, jstring path)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL && handle->soundfont_id <= 0){
		const jbyte *font = (*env)->GetStringUTFChars(env, path, NULL);
		handle->soundfont_id = fluid_synth_sfload(handle->synth, font, 1);
		(*env)->ReleaseStringUTFChars(env, path, font);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_unloadFont(JNIEnv* env, jobject obj, jlong ptr)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL && handle->soundfont_id > 0){
		fluid_synth_sfunload(handle->synth, handle->soundfont_id, 1);
		handle->soundfont_id = 0;
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_systemReset(JNIEnv* env, jobject obj, jlong ptr)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_system_reset(handle->synth);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_noteOn(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint note, jint velocity)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_noteon(handle->synth, channel, note, velocity);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_noteOff(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint note, jint velocity)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_noteoff(handle->synth, channel, note);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_programChange(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint program)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_program_change(handle->synth, channel, program);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_controlChange(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint control, jint value)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_cc(handle->synth, channel, control, value);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_pitchBend(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint value)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_pitch_bend(handle->synth, channel,  ((value * 128)));
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_setDoubleProperty(JNIEnv* env, jobject obj, jlong ptr, jstring key, jdouble value)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
		fluid_settings_setnum(handle->settings, (char *)jkey, (float)value );
		(*env)->ReleaseStringUTFChars(env, key, jkey);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_setIntegerProperty(JNIEnv* env, jobject obj, jlong ptr, jstring key, jint value)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
		fluid_settings_setint(handle->settings, (char *)jkey, (int)value );
		(*env)->ReleaseStringUTFChars(env, key, jkey);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_setStringProperty(JNIEnv* env, jobject obj, jlong ptr, jstring key, jstring value)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
		const jbyte *jvalue = (*env)->GetStringUTFChars(env, value, NULL);
		fluid_settings_setstr(handle->settings, (char *)jkey, (char *)jvalue );
		(*env)->ReleaseStringUTFChars(env, key, jkey);
		(*env)->ReleaseStringUTFChars(env, value, jvalue);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_getDoubleProperty(JNIEnv* env, jobject obj, jlong ptr, jstring key, jobject ref)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		jclass cl = (*env)->GetObjectClass(env, ref);
		jmethodID mid = (*env)->GetMethodID(env, cl, "setValue", "(D)V");
		if (mid != 0){
			double value = 0;
			const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
			
			fluid_settings_getnum(handle->settings,(char *)jkey, &value );
			
			(*env)->ReleaseStringUTFChars(env, key, jkey);
			(*env)->CallVoidMethod( env, ref , mid , (jdouble)value );
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_getIntegerProperty(JNIEnv* env, jobject obj, jlong ptr, jstring key, jobject ref)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		jclass cl = (*env)->GetObjectClass(env, ref);
		jmethodID mid = (*env)->GetMethodID(env, cl, "setValue", "(I)V");
		if (mid != 0){
			int value = 0;
			const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
			
			fluid_settings_getint(handle->settings,(char *)jkey, &value );
			
			(*env)->ReleaseStringUTFChars(env, key, jkey);
			(*env)->CallVoidMethod( env, ref , mid , (jint)value );
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_getStringProperty(JNIEnv* env, jobject obj, jlong ptr, jstring key, jobject ref)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		jclass cl = (*env)->GetObjectClass(env, ref);
		jmethodID mid = (*env)->GetMethodID(env, cl, "setValue", "(Ljava/lang/String;)V");
		if (mid != 0){
			jstring jvalue = NULL;
			char *value = NULL;
			const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
			
			fluid_settings_getstr(handle->settings,(char *)jkey, &value );
			jvalue = (*env)->NewStringUTF(env, value);
			
			(*env)->ReleaseStringUTFChars(env, key, jkey);
			(*env)->CallVoidMethod( env, ref , mid , jvalue );
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_getDoublePropertyDefault(JNIEnv* env, jobject obj, jlong ptr, jstring key, jobject ref)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		jclass cl = (*env)->GetObjectClass(env, ref);
		jmethodID mid = (*env)->GetMethodID(env, cl, "setValue", "(D)V");
		if (mid != 0){
			const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
			double value = fluid_settings_getnum_default(handle->settings,(char *)jkey);
			
			(*env)->ReleaseStringUTFChars(env, key, jkey);
			(*env)->CallVoidMethod( env, ref , mid , (jdouble)value );
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_getIntegerPropertyDefault(JNIEnv* env, jobject obj, jlong ptr, jstring key, jobject ref)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		jclass cl = (*env)->GetObjectClass(env, ref);
		jmethodID mid = (*env)->GetMethodID(env, cl, "setValue", "(I)V");
		if (mid != 0){
			const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
			int value = fluid_settings_getint_default(handle->settings,(char *)jkey);
			
			(*env)->ReleaseStringUTFChars(env, key, jkey);
			(*env)->CallVoidMethod( env, ref , mid , (jint)value );
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_getStringPropertyDefault(JNIEnv* env, jobject obj, jlong ptr, jstring key, jobject ref)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		jclass cl = (*env)->GetObjectClass(env, ref);
		jmethodID mid = (*env)->GetMethodID(env, cl, "setValue", "(Ljava/lang/String;)V");
		if (mid != 0){
			const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
			char *value = fluid_settings_getstr_default(handle->settings,(char *)jkey);
			jstring jvalue = (*env)->NewStringUTF(env, value);
			
			(*env)->ReleaseStringUTFChars(env, key, jkey);
			(*env)->CallVoidMethod( env, ref , mid , jvalue );
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_getDoublePropertyRange(JNIEnv* env, jobject obj, jlong ptr, jstring key, jobject minimumRef, jobject maximumRef)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		jclass clMin = (*env)->GetObjectClass(env, minimumRef);
		jclass clMax = (*env)->GetObjectClass(env, maximumRef);
		jmethodID midMin = (*env)->GetMethodID(env, clMin, "setValue", "(D)V");
		jmethodID midMax = (*env)->GetMethodID(env, clMax, "setValue", "(D)V");
		if (midMin != 0 && midMax != 0){
			double minimum = 0;
			double maximum = 0;
			const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
			
			fluid_settings_getnum_range(handle->settings,(char *)jkey, &minimum , &maximum );
			
			(*env)->ReleaseStringUTFChars(env, key, jkey);
			(*env)->CallVoidMethod( env, minimumRef , midMin , (jdouble)minimum );
			(*env)->CallVoidMethod( env, maximumRef , midMax , (jdouble)maximum );
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_getIntegerPropertyRange(JNIEnv* env, jobject obj, jlong ptr, jstring key, jobject minimumRef, jobject maximumRef)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		jclass clMin = (*env)->GetObjectClass(env, minimumRef);
		jclass clMax = (*env)->GetObjectClass(env, maximumRef);
		jmethodID midMin = (*env)->GetMethodID(env, clMin, "setValue", "(I)V");
		jmethodID midMax = (*env)->GetMethodID(env, clMax, "setValue", "(I)V");
		if (midMin != 0 && midMax != 0){
			int minimum = 0;
			int maximum = 0;
			const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
			
			fluid_settings_getint_range(handle->settings,(char *)jkey, &minimum , &maximum );
			
			(*env)->ReleaseStringUTFChars(env, key, jkey);
			(*env)->CallVoidMethod( env, minimumRef , midMin , (jint)minimum );
			(*env)->CallVoidMethod( env, maximumRef , midMax , (jint)maximum );
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_getPropertyOptions(JNIEnv* env, jobject obj, jlong ptr, jstring key, jobject options)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL){
		const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
		fluid_settings_foreach_option_data* data = (fluid_settings_foreach_option_data *)malloc( sizeof(fluid_settings_foreach_option_data));
		data->env = env;
		data->options = options;
		
		fluid_settings_foreach_option(handle->settings, (char *)jkey, data, fluid_settings_foreach_option_callback );
		(*env)->ReleaseStringUTFChars(env, key, jkey);
		
		free ( data );
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_isRealtimeProperty(JNIEnv* env, jobject obj, jlong ptr, jstring key, jobject ref)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->settings != NULL && key != NULL ){
		jclass cl = (*env)->GetObjectClass(env, ref);
		jmethodID mid = (*env)->GetMethodID(env, cl, "setValue", "(Z)V");
		if (mid != 0){
			const jbyte *jkey = (*env)->GetStringUTFChars(env, key, NULL);
			int value = fluid_settings_is_realtime(handle->settings,(char *)jkey);
			
			(*env)->ReleaseStringUTFChars(env, key, jkey);
			(*env)->CallVoidMethod( env, ref , mid , (value != 0 ? JNI_TRUE : JNI_FALSE) );
		}
	}
}
