#ifndef _Included_org_herac_tuxguitar_player_impl_midiport_vst_jni_VST
#define _Included_org_herac_tuxguitar_player_impl_midiport_vst_jni_VST

#include <jni.h>
#include <audioeffectx.h>

typedef struct {
	void* library;
}JNIPlugin;

typedef struct {
	AEffect* effect;
	int editorOpen;
}JNIEffect;

VstIntPtr VSTCALLBACK VSTPluginCallback(AEffect*, VstInt32, VstInt32, VstIntPtr, void*, float);

#endif /* _Included_org_herac_tuxguitar_player_impl_midiport_vst_jni_VST */
