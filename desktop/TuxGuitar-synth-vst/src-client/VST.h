#ifndef _Included_VST
#define _Included_VST

#include <aeffectx.h>

typedef struct {
	void* library;
} VSTPluginHandle;

typedef struct {
	AEffect* effect;
	void* ui;
	bool updated;
} VSTEffectHandle;

VstIntPtr VSTCALLBACK VSTPluginCallback(AEffect*, VstInt32, VstInt32, VstIntPtr, void*, float);

#endif /* _Included_VST */
