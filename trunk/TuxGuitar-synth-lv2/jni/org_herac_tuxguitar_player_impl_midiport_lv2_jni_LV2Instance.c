#include <stdlib.h>
#include <string.h>
#include "org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2.h"
#include "org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance.h"

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance_malloc(JNIEnv *env, jobject obj, jlong pluginPtr, jint bufferSize)
{
	jlong ptr = 0;
	
	LV2Plugin *plugin = NULL;
	memcpy(&plugin, &pluginPtr, sizeof(plugin));
	
	if( plugin != NULL ) {
		LV2Instance *handle = (LV2Instance *) malloc(sizeof(LV2Instance));
		
		handle->plugin = plugin;
		handle->lilvInstance = lilv_plugin_instantiate(handle->plugin->lilvPlugin, 44100.00, NULL);
		handle->portCount = lilv_plugin_get_num_ports(handle->plugin->lilvPlugin);
		handle->ports = (LV2Port **) malloc(sizeof(LV2Port) * handle->portCount);
		handle->bufferSize = (uint32_t) bufferSize;
		
		LilvNode* lv2_ControlPort = lilv_new_uri(handle->plugin->world->lilvWorld, LV2_CORE__ControlPort);
		LilvNode* lv2_AudioPort   = lilv_new_uri(handle->plugin->world->lilvWorld, LV2_CORE__AudioPort);
		LilvNode* lv2_InputPort   = lilv_new_uri(handle->plugin->world->lilvWorld, LV2_CORE__InputPort);
		LilvNode* lv2_OutputPort  = lilv_new_uri(handle->plugin->world->lilvWorld, LV2_CORE__OutputPort);
		
		for (uint32_t i = 0; i < handle->portCount; i ++) {
			handle->ports[i] = (LV2Port *) malloc(sizeof(LV2Port));
			handle->ports[i]->lilvPort = lilv_plugin_get_port_by_index(handle->plugin->lilvPlugin, i);
			handle->ports[i]->type = TYPE_UNKNOWN;
			handle->ports[i]->connection = NULL;
			
			if( lilv_port_is_a(handle->plugin->lilvPlugin, handle->ports[i]->lilvPort, lv2_ControlPort)) {
				handle->ports[i]->type = TYPE_CONTROL;
				handle->ports[i]->connection = malloc(sizeof(float));
				
				LilvNode* defaultValue = NULL;
				lilv_port_get_range(handle->plugin->lilvPlugin, handle->ports[i]->lilvPort, &defaultValue, NULL, NULL);
				*((float *) handle->ports[i]->connection) = lilv_node_as_float(defaultValue);
				lilv_node_free(defaultValue);
			}
			else if( lilv_port_is_a(handle->plugin->lilvPlugin, handle->ports[i]->lilvPort, lv2_AudioPort)) {
				if( lilv_port_is_a(handle->plugin->lilvPlugin, handle->ports[i]->lilvPort, lv2_InputPort)) {
					handle->ports[i]->type = TYPE_AUDIO_IN;
					handle->ports[i]->connection = malloc(sizeof(float) * handle->bufferSize);
					for(int s = 0 ; s < handle->bufferSize; s ++) {
						((float *) handle->ports[i]->connection)[s] = 0.0f;
					}
				}
				else if( lilv_port_is_a(handle->plugin->lilvPlugin, handle->ports[i]->lilvPort, lv2_OutputPort)) {
					handle->ports[i]->type = TYPE_AUDIO_OUT;
					handle->ports[i]->connection = malloc(sizeof(float) * handle->bufferSize);
					for(int s = 0 ; s < handle->bufferSize; s ++) {
						((float *) handle->ports[i]->connection)[s] = 0.0f;
					}
				}
			}
			lilv_instance_connect_port(handle->lilvInstance, i, handle->ports[i]->connection);
		}
		
		lilv_node_free(lv2_ControlPort);
		lilv_node_free(lv2_AudioPort);
		lilv_node_free(lv2_InputPort);
		lilv_node_free(lv2_OutputPort);
		
		lilv_instance_activate(handle->lilvInstance);
		
		memcpy(&ptr, &handle, sizeof( handle ));
	}
	return ptr;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance_free(JNIEnv *env, jobject obj, jlong ptr)
{
	LV2Instance *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL ) {
		lilv_instance_deactivate(handle->lilvInstance);
		lilv_instance_free(handle->lilvInstance);
		
		for (uint32_t i = 0; i < handle->portCount; i ++) {
			free ( handle->ports[i]->connection );
			free ( handle->ports[i] );
		}
		
		free ( handle->ports );
		free ( handle );
	}
}

JNIEXPORT jint JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance_getInputPortCount(JNIEnv *env, jobject obj, jlong ptr)
{
	jint value = 0;
	
	LV2Instance *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->ports != NULL ) {
		for (uint32_t i = 0; i < handle->portCount; i ++) {
			if( handle->ports[i]->type == TYPE_AUDIO_IN ) {
				value ++;
			}
		}
	}
	return value;
}

JNIEXPORT jint JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance_getOutputPortCount(JNIEnv *env, jobject obj, jlong ptr)
{
	jint value = 0;
	
	LV2Instance *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->ports != NULL ) {
		for (uint32_t i = 0; i < handle->portCount; i ++) {
			if( handle->ports[i]->type == TYPE_AUDIO_OUT ) {
				value ++;
			}
		}
	}
	return value;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance_setControlPortValue(JNIEnv *env, jobject obj, jlong ptr, jint index, jfloat value)
{
	LV2Instance *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->ports != NULL ) {
		if( index >= 0 && index < handle->portCount && handle->ports[index]->type == TYPE_CONTROL && handle->ports[index]->connection != NULL ) {
			*((float *) handle->ports[index]->connection) = value;
		}
	}
}

JNIEXPORT jfloat JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance_getControlPortValue(JNIEnv *env, jobject obj, jlong ptr, jint index)
{
	jint value = 0;
	
	LV2Instance *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->ports != NULL ) {
		if( index >= 0 && index < handle->portCount && handle->ports[index]->type == TYPE_CONTROL && handle->ports[index]->connection != NULL ) {
			value = *((float *) handle->ports[index]->connection);
		}
	}
	return value;
}

JNIEXPORT jobject JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance_getControlPortInfo(JNIEnv *env, jobject obj, jlong ptr, jint index)
{
	jobject value = 0;
	
	LV2Instance *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->ports != NULL ) {
		if( index >= 0 && index < handle->portCount && handle->ports[index]->type == TYPE_CONTROL ) {
			jclass jPortRangeCls = NULL;
			jmethodID jPortRangeInit = NULL;
			
			jPortRangeCls = (*env)->FindClass(env, "org/herac/tuxguitar/player/impl/midiport/lv2/jni/LV2ControlPortInfo");
			if( jPortRangeCls != NULL ) {
				jPortRangeInit = (*env)->GetMethodID(env, jPortRangeCls, "<init>", "(Ljava/lang/String;ZFFF)V");
				if( jPortRangeInit != NULL ) {
					bool toggled = false;
					LilvNode* name = NULL;
					LilvNode* defaultValue = NULL;
					LilvNode* minimumValue = NULL;
					LilvNode* maximumValue = NULL;
					LilvNode* lv2_toggled = lilv_new_uri(handle->plugin->world->lilvWorld, LV2_CORE__toggled);
					
					lilv_port_get_range(handle->plugin->lilvPlugin, handle->ports[index]->lilvPort, &defaultValue, &minimumValue, &maximumValue);
					name = lilv_port_get_name(handle->plugin->lilvPlugin, handle->ports[index]->lilvPort);
					toggled = lilv_port_has_property(handle->plugin->lilvPlugin, handle->ports[index]->lilvPort, lv2_toggled);
					
					value = (*env)->NewObject(
						env,
						jPortRangeCls,
						jPortRangeInit,
						(*env)->NewStringUTF(env, lilv_node_as_string(name)),
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

JNIEXPORT jobject JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance_getControlPortIndices(JNIEnv *env, jobject obj, jlong ptr)
{
	jobject jlist = NULL;
	
	LV2Instance *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->ports != NULL ) {
		jclass jlistCls = NULL;
		jclass jintCls = NULL;
		jmethodID jlistInit = NULL;
		jmethodID jlistAddMid = NULL;
		jmethodID jintInit = NULL;
		
		jlistCls = (*env)->FindClass(env, "java/util/ArrayList");
		if( jlistCls != NULL ) {
			jlistInit = (*env)->GetMethodID(env, jlistCls, "<init>", "()V");
			jlistAddMid = (*env)->GetMethodID(env, jlistCls, "add", "(Ljava/lang/Object;)Z");
			if( jlistInit != NULL && jlistAddMid != NULL) {
				jlist = (*env)->NewObject(env, jlistCls, jlistInit);
			}
		}
		jintCls = (*env)->FindClass(env, "java/lang/Integer");
		if( jintCls != NULL ) {
			jintInit = (*env)->GetMethodID(env, jintCls, "<init>", "(I)V");
		}
		
		if( jlist != NULL && jlistAddMid != NULL && jintCls != NULL && jintInit != NULL ) {
			for (uint32_t i = 0; i < handle->portCount; i ++) {
				if( handle->ports[i]->type == TYPE_CONTROL ) {
					(*env)->CallBooleanMethod(env, jlist, jlistAddMid, (*env)->NewObject(env, jintCls, jintInit, (jint) i));
				}
			}
		}
	}
	return jlist;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2Instance_processAudio(JNIEnv *env, jobject obj, jlong ptr, jobjectArray inputs, jobjectArray outputs)
{
	LV2Instance *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if( handle != NULL && handle->ports != NULL ) {
		// copy inputs to lv2 buffer
		int iIndex = 0;
		int iLen = (*env)->GetArrayLength(env, inputs );
		for (uint32_t i = 0; i < handle->portCount; i ++) {
			if( handle->ports[i]->type == TYPE_AUDIO_IN ) {
				if( iIndex < iLen ) {
					float* sourceBuffer = (float*) (*env)->GetPrimitiveArrayCritical(env, (jarray) (*env)->GetObjectArrayElement(env, inputs, iIndex), NULL);
					float* targetBuffer = (float*) handle->ports[i]->connection;
					for(int s = 0 ; s < handle->bufferSize; s ++) {
						targetBuffer[s] = sourceBuffer[s];
					}
					(*env)->ReleasePrimitiveArrayCritical(env, (jarray) (*env)->GetObjectArrayElement(env, inputs, iIndex), sourceBuffer, 0);
				}
				iIndex ++;
			}
		}
		
		// process lv2 buffers 
		lilv_instance_run(handle->lilvInstance, handle->bufferSize);
		
		// copy outputs from lv2 buffer
		int oLen = (*env)->GetArrayLength(env, outputs);
		int oIndex = 0;
		for (uint32_t i = 0; i < handle->portCount; i ++) {
			if( handle->ports[i]->type == TYPE_AUDIO_OUT ) {
				if( oIndex < oLen ) {
					float* sourceBuffer = (float*) handle->ports[i]->connection;
					float* targetBuffer = (float*) (*env)->GetPrimitiveArrayCritical(env, (jarray) (*env)->GetObjectArrayElement(env, outputs, oIndex), NULL);
					for(int s = 0 ; s < handle->bufferSize; s ++) {
						targetBuffer[s] = sourceBuffer[s];
					}
					(*env)->ReleasePrimitiveArrayCritical(env, (jarray) (*env)->GetObjectArrayElement(env, outputs, oIndex), targetBuffer, 0);
				}
				oIndex ++;
			}
		}
	}
}
