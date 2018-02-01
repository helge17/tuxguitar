#ifndef _Included_VST
#define _Included_VST

#include <aeffectx.h>

typedef struct {
	void* library;
}JNIPlugin;

typedef struct {
	AEffect* effect;
	void* ui;
}JNIEffect;

VstIntPtr VSTCALLBACK VSTPluginCallback(AEffect*, VstInt32, VstInt32, VstIntPtr, void*, float);

#endif /* _Included_VST */
