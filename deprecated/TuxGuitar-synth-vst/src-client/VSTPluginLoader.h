#ifndef _Included_VSTPluginLoader
#define _Included_VSTPluginLoader

#include "VST.h"

void VSTPluginLoad( void **plugin , const char *file );

void VSTPluginFree( void **plugin );

void VSTPluginMain( void **plugin , AEffect **effect , audioMasterCallback callback );

#endif /* _Included_VSTPluginLoader */
