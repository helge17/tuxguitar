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

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_malloc(JNIEnv* env, jobject obj)
{
	jlong ptr = 0;
	
	fluid_handle_t *handle = (fluid_handle_t *) malloc( sizeof(fluid_handle_t) );
	
	handle->settings = new_fluid_settings();
	handle->synth = new_fluid_synth(handle->settings);
	handle->driver = NULL;
	handle->soundfont_id = 0;
	
	fluid_synth_set_gain( handle->synth, 0.5 );
	fluid_synth_set_chorus_on( handle->synth, 0 );
	fluid_synth_set_reverb_on( handle->synth, 0 );
	fluid_synth_set_interp_method( handle->synth, -1, FLUID_INTERP_NONE );
	
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

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_loadDriver(JNIEnv* env, jobject obj, jlong ptr, jstring name)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		if( handle->driver != NULL ){
			delete_fluid_audio_driver(handle->driver);
		}
		if( name != NULL ){
			const jbyte *driver = (*env)->GetStringUTFChars(env, name, NULL);
			fluid_settings_setstr(handle->settings, "audio.driver", (char*)driver);
			(*env)->ReleaseStringUTFChars(env, name, driver);
		}
		handle->driver = new_fluid_audio_driver(handle->settings,handle->synth);
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

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_noteOn(JNIEnv* env, jobject ojb, jlong ptr, jint channel, jint note, jint velocity)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_noteon(handle->synth, channel, note, velocity);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_noteOff(JNIEnv* env, jobject ojb, jlong ptr, jint channel, jint note, jint velocity)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_noteoff(handle->synth, channel, note);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_programChange(JNIEnv* env, jobject ojb, jlong ptr, jint channel, jint program)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_program_change(handle->synth, channel, program);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_controlChange(JNIEnv* env, jobject ojb, jlong ptr, jint channel, jint control, jint value)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_cc(handle->synth, channel, control, value);
		if( control == 0x06 ){
			fluid_synth_pitch_wheel_sens(handle->synth, channel, value);
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_pitchBend(JNIEnv* env, jobject ojb, jlong ptr, jint channel, jint value)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		fluid_synth_pitch_bend(handle->synth, channel,  ((value * 128)));
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_fluidsynth_MidiSynth_findDrivers(JNIEnv* env, jobject obj, jlong ptr)
{
	fluid_handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->synth != NULL){
		
		typedef struct{
			JNIEnv* env;
			jobject obj;
		}fluid_settings_foreach_option_data;
		
		void fluid_settings_foreach_option_callback(void *data, char *name, char *option)
		{
			fluid_settings_foreach_option_data* handle = (fluid_settings_foreach_option_data *)data;
			
			jstring driver = (*handle->env)->NewStringUTF(handle->env, option);
			
			//Add a new driver option to the java class
			jclass cl = (*handle->env)->GetObjectClass(handle->env, handle->obj);
			jmethodID mid = (*handle->env)->GetMethodID(handle->env, cl, "addDriver", "(Ljava/lang/String;)V");
			if (mid != 0){
				(*env)->CallVoidMethod(handle->env, handle->obj, mid,driver);
			}
		}
		fluid_settings_foreach_option_data* data = (fluid_settings_foreach_option_data *)malloc(sizeof(fluid_settings_foreach_option_data*));
		data->env = env;
		data->obj = obj;
		
		fluid_settings_foreach_option(handle->settings,"audio.driver", data, fluid_settings_foreach_option_callback );
		free ( data );
	}
}
