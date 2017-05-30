#include <windows.h>
#include <audioeffectx.h>
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPluginLoader.h"

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
