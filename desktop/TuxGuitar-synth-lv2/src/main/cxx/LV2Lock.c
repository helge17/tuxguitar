#include <stdlib.h>
#include "LV2Lock.h"

void LV2Lock_malloc(LV2Lock **handle)
{
	(*handle) = (LV2Lock *) malloc(sizeof(LV2Lock));

	pthread_mutexattr_t attr;
	pthread_mutexattr_init(&attr);
	pthread_mutexattr_settype (&attr, PTHREAD_MUTEX_RECURSIVE_NP);
	pthread_mutex_init(&((*handle)->lock), &attr);
}

void LV2Lock_free(LV2Lock **handle)
{
	if( (*handle) != NULL ){
		pthread_mutex_destroy(&((*handle)->lock));

		free ( (*handle) );
		
		(*handle) = NULL;
	}
}

bool LV2Lock_lock(LV2Lock *handle)
{
	return (pthread_mutex_lock( &handle->lock ) == 0);
}

bool LV2Lock_trylock(LV2Lock *handle)
{
	return (pthread_mutex_trylock( &handle->lock ) == 0);
}

void LV2Lock_unlock(LV2Lock *handle)
{
	pthread_mutex_unlock( &handle->lock );
}