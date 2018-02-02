#include <stdlib.h>
#include "VSTPluginLoader.h"
#include "VSTPlugin.h"
#include "VST.h"

void VSTPlugin_malloc(VSTPluginHandle **handle, const char *libraryPath)
{
	void *library = NULL;
	
	VSTPluginLoad( &library, libraryPath );
	if (library != NULL) {
		(*handle) = (VSTPluginHandle *) malloc( sizeof(VSTPluginHandle) );
		(*handle)->library = library;
	}
}

void VSTPlugin_delete(VSTPluginHandle **handle)
{
	if( (*handle) != NULL ){
		if( (*handle)->library != NULL ){
			VSTPluginFree( &((*handle)->library) );
		}
		free ( (*handle) );
		
		(*handle) = NULL;
	}
}
