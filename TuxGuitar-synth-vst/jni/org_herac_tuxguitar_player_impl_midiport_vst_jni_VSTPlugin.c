#include <stdlib.h>
#include <audioeffectx.h>
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPluginLoader.h"
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPlugin.h"
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VST.h"

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPlugin
 * Method:    malloc
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPlugin_malloc(JNIEnv *env, jobject obj, jstring str)
{
	jlong ptr = 0;
	
	void *library = NULL;
	const char *libraryPath = (env)->GetStringUTFChars( str , NULL );
	
	JNI_GetJVM()->DetachCurrentThread();
	
	VSTPluginLoad( &library, libraryPath );
	if (library != NULL) {
		JNIPlugin *handle = (JNIPlugin *) malloc( sizeof(JNIPlugin) );
		
		handle->library = library;
		
		memcpy(&ptr, &handle, sizeof( handle ));
	}
	
	(env)->ReleaseStringUTFChars( str , libraryPath );
	
	return ptr;
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPlugin
 * Method:    delete
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPlugin_delete(JNIEnv *env, jobject obj, jlong ptr)
{
	JNIPlugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL){
		if( handle->library != NULL ){
			VSTPluginFree( &(handle->library) );
		}
		free ( handle );
		(handle) = NULL;
	}
}

/*
 * Class:     org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPlugin
 * Method:    initEffect
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPlugin_initEffect(JNIEnv *env, jobject obj, jlong ptr)
{
	jlong jptr = 0;
	
	JNIPlugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->library != NULL){
		AEffect* effect = NULL;
		
		VSTPluginMain( &(handle->library) , &effect , VSTPluginCallback );
		
		if(effect != NULL && effect->magic == kEffectMagic) {
			memcpy(&jptr, &effect, sizeof( effect ));
		}
	}
	
	return jptr;
}
