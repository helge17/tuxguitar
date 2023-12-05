#include <dlfcn.h>
#include "VSTPluginLoader.h"

void VSTPluginLoad(  void **plugin , const char *file )
{
	(*plugin) = dlopen( file , RTLD_LAZY );
	if ( (*plugin) != NULL) {
		dlerror();
	}
}

void VSTPluginFree( void **plugin )
{
	if( (*plugin) != NULL ){
		dlclose( (*plugin) );
		(*plugin) = NULL;
	}
}

void VSTPluginMain( void **plugin , AEffect **effect , audioMasterCallback callback )
{
	AEffect* (*VSTPluginMain) (audioMasterCallback) = NULL;
	
	VSTPluginMain = (AEffect* (*)(audioMasterCallback)) dlsym( (*plugin) , "VSTPluginMain");
	if(VSTPluginMain == NULL) {
		VSTPluginMain = (AEffect* (*)(audioMasterCallback))dlsym( (*plugin) , "main");
	}
	
	if(VSTPluginMain != NULL) {
		(*effect) = VSTPluginMain( (audioMasterCallback) callback );
	}else{
		(*effect) = NULL;
	}
}
