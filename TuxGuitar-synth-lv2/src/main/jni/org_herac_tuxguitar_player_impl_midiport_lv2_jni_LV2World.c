#include <stdlib.h>
#include <string.h>
#include "org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2.h"
#include "org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2World.h"

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2World_malloc(JNIEnv *env, jobject obj)
{
	jlong ptr = 0;

	LV2World *handle = NULL;
	LV2World_malloc(&handle);
	
	memcpy(&ptr, &handle, sizeof( handle ));
	
	return ptr;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2World_free(JNIEnv *env, jobject obj, jlong ptr)
{
	LV2World *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ) {
		LV2World_free(&handle);
	}
}

JNIEXPORT jobject JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2World_getAllPlugins(JNIEnv *env, jobject obj, jlong ptr)
{
	jobject jlist = NULL;
	
	LV2World *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ) {
		jclass jlistCls = NULL;
		jclass jpluginCls = NULL;
		jmethodID jlistInit = NULL;
		jmethodID jlistAddMid = NULL;
		jmethodID jpluginInit = NULL;
		
		jlistCls = env->FindClass("java/util/ArrayList");
		if( jlistCls != NULL ) {
			jlistInit = env->GetMethodID(jlistCls, "<init>", "()V");
			jlistAddMid = env->GetMethodID(jlistCls, "add", "(Ljava/lang/Object;)Z");
			if( jlistInit != NULL && jlistAddMid != NULL) {
				jlist = env->NewObject(jlistCls, jlistInit);
			}
		}
		
		jpluginCls = env->FindClass("org/herac/tuxguitar/player/impl/midiport/lv2/jni/LV2Plugin");
		if( jpluginCls != NULL ) {
			jpluginInit = env->GetMethodID(jpluginCls, "<init>", "(J)V");
		}
		
		if( jlist != NULL && jlistAddMid != NULL && jpluginCls != NULL && jpluginInit != NULL ) {
			LV2Int32 count = 0;
			LV2Plugin **plugins = NULL;
			LV2World_getAllPlugins(handle, &plugins, &count);
			for (uint32_t i = 0; i < count; i++) {
				LV2Plugin *plugin = plugins[i];
				
				jlong pluginAddress = 0;
				memcpy(&pluginAddress, &plugin, sizeof(plugin));
				jobject pluginAddressObj = env->NewObject(jpluginCls, jpluginInit, pluginAddress);
				
				env->CallBooleanMethod(jlist, jlistAddMid, pluginAddressObj);
			}
		}
	}
	return jlist;
}
