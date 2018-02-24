#include <stdlib.h>
#include <string.h>
#include "org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2.h"
#include "org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin.h"

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_malloc(JNIEnv *env, jobject obj, jlong worldPtr, jlong lilvPluginPtr)
{
	jlong ptr = 0;
	
	LV2World *world = NULL;
	memcpy(&world, &worldPtr, sizeof(world));
	
	LilvPlugin* lilvPlugin = NULL;
	memcpy(&lilvPlugin, &lilvPluginPtr, sizeof(lilvPlugin));
	
	if( world != NULL && lilvPlugin != NULL ) {
		LV2Plugin *handle = (LV2Plugin *) malloc(sizeof(LV2Plugin));
		
		handle->world = world;
		handle->lilvPlugin = lilvPlugin;
		
		memcpy(&ptr, &handle, sizeof( handle ));
	}
	return ptr;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_free(JNIEnv *env, jobject obj, jlong ptr)
{
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ){
		handle->world = NULL;
		handle->lilvPlugin = NULL;
		
		free ( handle );
	}
}

JNIEXPORT jstring JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_getUri(JNIEnv *env, jobject obj, jlong ptr)
{
	jstring jvalue = NULL;
	
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->lilvPlugin != NULL ){
		const LilvNode* lilvNode = lilv_plugin_get_uri(handle->lilvPlugin);
		if( lilvNode != NULL ) {
			jvalue = (*env)->NewStringUTF(env, lilv_node_as_string(lilvNode));
		}
	}
	
	return jvalue;
}

JNIEXPORT jstring JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_getName(JNIEnv *env, jobject obj, jlong ptr)
{
	jstring jvalue = NULL;
	
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->lilvPlugin != NULL ){
		LilvNode* lilvNode = lilv_plugin_get_name(handle->lilvPlugin);
		if( lilvNode != NULL ) {
			jvalue = (*env)->NewStringUTF(env, lilv_node_as_string(lilvNode));
			lilv_node_free(lilvNode);
		}
	}
	
	return jvalue;
}
