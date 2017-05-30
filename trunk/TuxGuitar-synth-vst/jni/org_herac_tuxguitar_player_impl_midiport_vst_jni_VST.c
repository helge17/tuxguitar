#include <stdlib.h>
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VST.h"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
	JNI_JVM = vm;
	return JNI_VERSION_1_4;
}

JavaVM* JNI_GetJVM( void )
{
	return JNI_JVM;
}

VstIntPtr VSTCALLBACK VSTPluginCallback(AEffect *effect, VstInt32 opcode, VstInt32 index, VstIntPtr value, void *ptr, float opt)
{
	jlong jresult = 0;
	
	if( JNI_GetJVM() != NULL ){	
		JNIEnv* jenv = NULL;
		JNI_GetJVM()->AttachCurrentThread((void **)&jenv, 0);
		if( jenv != NULL ){
			jclass cls = jenv->FindClass("org/herac/tuxguitar/player/impl/midiport/vst/jni/VSTCallback");
			if( cls != NULL ){
				jmethodID  mid = jenv->GetStaticMethodID( cls , "invoke", "(JJJJJF)J");
				if( mid != NULL ){
					jlong instance = 0;
					if( effect != NULL && effect->resvd1 != 0 ){
						memcpy(&instance, &effect->resvd1, sizeof(effect->resvd1));
					}
					jresult = jenv->CallStaticLongMethod( cls , mid , instance, (jlong) opcode, (jlong) index, (jlong) value, (jlong) ptr, (jfloat) opt);
				}
			}
		}
		JNI_GetJVM()->DetachCurrentThread();
	}
	
	VstIntPtr result = (VstIntPtr) NULL;
	
	memcpy(&result, &jresult, sizeof(result));
	
	return result;
}
