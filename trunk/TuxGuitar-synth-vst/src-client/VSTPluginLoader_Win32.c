#include <windows.h>
#include "VSTPluginLoader.h"

void VSTPluginLoad(  void **plugin , const char *file )
{
    (*plugin) = LoadLibrary( file );
}

void VSTPluginFree( void **plugin )
{
	if( (*plugin) != NULL ){
		FreeLibrary( (HMODULE)(*plugin) );
		(*plugin) = NULL;
	}
}

void VSTPluginMain( void **plugin , AEffect **effect , audioMasterCallback callback )
{
	AEffect* (*VSTPluginMain) (audioMasterCallback) = NULL;
	
	VSTPluginMain = (AEffect* (*)(audioMasterCallback)) GetProcAddress((HMODULE) (*plugin), "VSTPluginMain");
	if(VSTPluginMain == NULL) {
		VSTPluginMain = (AEffect* (*)(audioMasterCallback)) GetProcAddress((HMODULE) (*plugin), "main");
	}
	
	
	if(VSTPluginMain != NULL) {
		(*effect) = VSTPluginMain( (audioMasterCallback) callback );
	}else{
		(*effect) = NULL;
	}
}
