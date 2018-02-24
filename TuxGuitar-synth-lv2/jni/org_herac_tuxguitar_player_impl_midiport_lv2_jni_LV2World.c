#include <stdlib.h>
#include <string.h>
#include "org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2.h"
#include "org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2World.h"

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2World_malloc(JNIEnv *env, jobject obj)
{
	jlong ptr = 0;
	
	LV2World *handle = (LV2World *) malloc(sizeof(LV2World));
	
	handle->lilvWorld = lilv_world_new();
	
	lilv_world_load_all(handle->lilvWorld);
	
	memcpy(&ptr, &handle, sizeof( handle ));
	
	return ptr;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2World_free(JNIEnv *env, jobject obj, jlong ptr)
{
	LV2World *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ){
		if( handle->lilvWorld != NULL ){
			lilv_world_free(handle->lilvWorld);
			handle->lilvWorld = NULL;
		}
		free ( handle );
	}
}

JNIEXPORT jobject JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2World_getLilvPlugins(JNIEnv *env, jobject obj, jlong ptr)
{
	jobject jlist = NULL;
	
	LV2World *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->lilvWorld != NULL ) {
		jclass jlistCls = NULL;
		jclass jlongCls = NULL;
		jmethodID jlistInit = NULL;
		jmethodID jlistAddMid = NULL;
		jmethodID jlongInit = NULL;
		
		jlistCls = (*env)->FindClass(env, "java/util/ArrayList");
		if( jlistCls != NULL ) {
			jlistInit = (*env)->GetMethodID(env, jlistCls, "<init>", "()V");
			jlistAddMid = (*env)->GetMethodID(env, jlistCls, "add", "(Ljava/lang/Object;)Z");
			if( jlistInit != NULL && jlistAddMid != NULL) {
				jlist = (*env)->NewObject(env, jlistCls, jlistInit);
			}
		}
		jlongCls = (*env)->FindClass(env, "java/lang/Long");
		if( jlongCls != NULL ) {
			jlongInit = (*env)->GetMethodID(env, jlongCls, "<init>", "(J)V");
		}
		
		if( jlist != NULL && jlistAddMid != NULL && jlongCls != NULL && jlongInit != NULL ) {
			const LilvPlugins* lilvPlugins = lilv_world_get_all_plugins(handle->lilvWorld);
			if( lilvPlugins ) {
				LILV_FOREACH(plugins, itr, lilvPlugins) {
					const LilvPlugin *lilvPlugin = lilv_plugins_get(lilvPlugins, itr);
					
					jlong lilvPluginAddress = 0;
					memcpy(&lilvPluginAddress, &lilvPlugin, sizeof(lilvPlugin));
					jobject lilvPluginAddressObj = (*env)->NewObject(env, jlongCls, jlongInit, lilvPluginAddress);
					
					(*env)->CallBooleanMethod(env, jlist, jlistAddMid, lilvPluginAddressObj);
				}
			}
		}
	}
	return jlist;
}
