#include <stdlib.h>
#include "VSTPluginLoader.h"
#include "VSTPlugin.h"
#include "VST.h"

void VSTPlugin_malloc(JNIPlugin **handle, const char *libraryPath)
{
	void *library = NULL;
	
	VSTPluginLoad( &library, libraryPath );
	if (library != NULL) {
		(*handle) = (JNIPlugin *) malloc( sizeof(JNIPlugin) );
		(*handle)->library = library;
	}
}

void VSTPlugin_delete(JNIPlugin **handle)
{
	if( (*handle) != NULL ){
		if( (*handle)->library != NULL ){
			VSTPluginFree( &((*handle)->library) );
		}
		free ( (*handle) );
		
		(*handle) = NULL;
	}
}
