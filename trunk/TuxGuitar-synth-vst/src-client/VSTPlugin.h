#ifndef _Included_VSTPlugin
#define _Included_VSTPlugin

#include "VST.h"

void VSTPlugin_malloc(VSTPluginHandle **handle, const char *libraryPath);

void VSTPlugin_delete(VSTPluginHandle **handle);

#endif /* _Included_VSTPlugin */
