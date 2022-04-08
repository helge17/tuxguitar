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
		LV2Plugin *handle = NULL;
		LV2Plugin_malloc(&handle, world, lilvPlugin);

		memcpy(&ptr, &handle, sizeof( handle ));
	}
	return ptr;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_free(JNIEnv *env, jobject obj, jlong ptr)
{
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ) {
		LV2Plugin_free(&handle);
	}
}

JNIEXPORT jstring JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_getUri(JNIEnv *env, jobject obj, jlong ptr)
{
	jstring jvalue = NULL;
	
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ) {
		const char* value = NULL;
		LV2Plugin_getUri(handle, &value);
		if( value != NULL ) {
			jvalue = env->NewStringUTF(value);
		}
	}
	
	return jvalue;
}

JNIEXPORT jstring JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_getName(JNIEnv *env, jobject obj, jlong ptr)
{
	jstring jvalue = NULL;
	
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ){
		const char* value = NULL;
		LV2Plugin_getName(handle, &value);
		if( value != NULL ) {
			jvalue = env->NewStringUTF(value);
		}
	}
	
	return jvalue;
}

JNIEXPORT jint JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_getAudioInputPortCount(JNIEnv *env, jobject obj, jlong ptr)
{
	jint value = 0;
	
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ) {
		LV2Plugin_getAudioInputPortCount(handle, (LV2Int32 *) &value);
	}
	return value;
}

JNIEXPORT jint JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_getAudioOutputPortCount(JNIEnv *env, jobject obj, jlong ptr)
{
	jint value = 0;
	
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ) {
		LV2Plugin_getAudioOutputPortCount(handle, (LV2Int32 *) &value);
	}
	return value;
}

JNIEXPORT jint JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_getMidiInputPortCount(JNIEnv *env, jobject obj, jlong ptr)
{
	jint value = 0;
	
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ) {
		LV2Plugin_getMidiInputPortCount(handle, (LV2Int32 *) &value);
	}
	return value;
}

JNIEXPORT jobject JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_getControlPortInfo(JNIEnv *env, jobject obj, jlong ptr, jint index)
{
	jobject value = 0;
	
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->ports != NULL ) {
		if( index >= 0 && index < handle->portCount && handle->ports[index]->type == TYPE_CONTROL ) {
			jclass jPortRangeCls = NULL;
			jmethodID jPortRangeInit = NULL;
			
			jPortRangeCls = env->FindClass("org/herac/tuxguitar/player/impl/midiport/lv2/jni/LV2ControlPortInfo");
			if( jPortRangeCls != NULL ) {
				jPortRangeInit = env->GetMethodID(jPortRangeCls, "<init>", "(Ljava/lang/String;ZFFF)V");
				if( jPortRangeInit != NULL ) {
					bool toggled = false;
					LilvNode* name = NULL;
					LilvNode* defaultValue = NULL;
					LilvNode* minimumValue = NULL;
					LilvNode* maximumValue = NULL;
					LilvNode* lv2_toggled = lilv_new_uri(handle->world->lilvWorld, LV2_CORE__toggled);
					
					lilv_port_get_range(handle->lilvPlugin, handle->ports[index]->lilvPort, &defaultValue, &minimumValue, &maximumValue);
					name = lilv_port_get_name(handle->lilvPlugin, handle->ports[index]->lilvPort);
					toggled = lilv_port_has_property(handle->lilvPlugin, handle->ports[index]->lilvPort, lv2_toggled);
					
					value = env->NewObject(
						jPortRangeCls,
						jPortRangeInit,
						env->NewStringUTF(lilv_node_as_string(name)),
						(toggled ? JNI_TRUE : JNI_FALSE),
						(jfloat) lilv_node_as_float(defaultValue),
						(jfloat) lilv_node_as_float(minimumValue),
						(jfloat) lilv_node_as_float(maximumValue));
					
					lilv_node_free(name);
					lilv_node_free(defaultValue);
					lilv_node_free(lv2_toggled);
				}
			}
		}
	}
	return value;
}

JNIEXPORT jobject JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin_getControlPortIndices(JNIEnv *env, jobject obj, jlong ptr)
{
	jobject jlist = NULL;
	
	LV2Plugin *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->ports != NULL ) {
		jclass jlistCls = NULL;
		jclass jintCls = NULL;
		jmethodID jlistInit = NULL;
		jmethodID jlistAddMid = NULL;
		jmethodID jintInit = NULL;
		
		jlistCls = env->FindClass("java/util/ArrayList");
		if( jlistCls != NULL ) {
			jlistInit = env->GetMethodID(jlistCls, "<init>", "()V");
			jlistAddMid = env->GetMethodID(jlistCls, "add", "(Ljava/lang/Object;)Z");
			if( jlistInit != NULL && jlistAddMid != NULL) {
				jlist = env->NewObject(jlistCls, jlistInit);
			}
		}
		jintCls = env->FindClass("java/lang/Integer");
		if( jintCls != NULL ) {
			jintInit = env->GetMethodID(jintCls, "<init>", "(I)V");
		}
		
		if( jlist != NULL && jlistAddMid != NULL && jintCls != NULL && jintInit != NULL ) {
			for (uint32_t i = 0; i < handle->portCount; i ++) {
				if( handle->ports[i]->type == TYPE_CONTROL ) {
					env->CallBooleanMethod(jlist, jlistAddMid, env->NewObject(jintCls, jintInit, (jint) i));
				}
			}
		}
	}
	return jlist;
}
