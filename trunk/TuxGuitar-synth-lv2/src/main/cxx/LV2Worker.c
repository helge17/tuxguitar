#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "LV2.h"
#include "LV2Worker.h"
#include "LV2Instance.h"
#include "LV2Logger.h"

void LV2Worker_malloc(LV2Worker **handle)
{
	(*handle) = (LV2Worker *) malloc(sizeof(LV2Worker));
	(*handle)->running = false;
	(*handle)->iface = NULL;
	(*handle)->instance = NULL;
	(*handle)->requestQueue = NULL;
	(*handle)->responseQueue = NULL;

	pthread_mutex_init(&(*handle)->lock, NULL);
}

void LV2Worker_free(LV2Worker **handle)
{
	if( (*handle) != NULL ) {
		LV2Worker_stop((*handle));

		free ((*handle));

		(*handle) = NULL;
	}
}

void LV2Worker_freeQueue(LV2WorkerQueue** queue)
{
	if( (*queue) != NULL) {
		LV2WorkerQueue* prev = NULL;
		LV2WorkerQueue* next = (*queue);
		while( next != NULL ) {
			prev = next;
			next = next->next;

			free( prev->data );
			free( prev );
		}
		(*queue) = NULL;
	}
}

void LV2Worker_start(LV2Worker *handle, LV2Instance *instance)
{
	if( handle != NULL ) {
		LilvNode* lv2WorkerInterfaceURI = lilv_new_uri(instance->plugin->world->lilvWorld, LV2_WORKER__interface);
		if( lilv_plugin_has_extension_data(instance->plugin->lilvPlugin, lv2WorkerInterfaceURI)) {
			handle->instance = instance;
			handle->iface = (LV2_Worker_Interface *) lilv_instance_get_extension_data(instance->lilvInstance, LV2_WORKER__interface);
			if( handle->iface != NULL ) {
				handle->running = true;
				
				if( pthread_create(&(handle->thread), NULL, LV2Worker_processThread, handle)) {
					handle->running = false;
				}
			}
		}
		lilv_node_free(lv2WorkerInterfaceURI);
	}
}

void LV2Worker_stop(LV2Worker *handle)
{
	if( handle != NULL ) {
		if( handle->running ) {
			handle->running = false;

			pthread_join(handle->thread, NULL);
		}
	}
}

void LV2Worker_enqueue(LV2Worker *handle, LV2WorkerQueue **queue, uint32_t size, const void *data)
{
	if( handle != NULL ) {
		LV2WorkerQueue* prev = NULL;
		LV2WorkerQueue* next = (*queue);
		while( next != NULL ) {
			prev = next;
			next = next->next;
		}

		next = (LV2WorkerQueue*) malloc(sizeof(LV2WorkerQueue));
		next->size = size;
		next->data = malloc(size);
		next->next = NULL;

		memcpy(next->data, data, size);

		if( prev != NULL ) {
			prev->next = next;
		} else {
			(*queue) = next;
		}
	}
}

LV2_Worker_Status LV2Worker_schedule(LV2_Worker_Schedule_Handle workerHandle, uint32_t size, const void *data)
{
	LV2Worker *handle = (LV2Worker *) workerHandle;
	if( handle != NULL ) {
		if( pthread_mutex_lock( &handle->lock ) == 0 ) {
			LV2Worker_enqueue(handle, &(handle->requestQueue), size, data);
			
			pthread_mutex_unlock( &handle->lock );
		}
	}
	return LV2_WORKER_SUCCESS;
}

LV2_Worker_Status LV2Worker_respond(LV2_Worker_Respond_Handle workerHandle, uint32_t size, const void* data)
{
	LV2Worker *handle = (LV2Worker *) workerHandle;
	if( handle != NULL ) {
		if( pthread_mutex_lock( &handle->lock ) == 0 ) {
			LV2Worker_enqueue(handle, &(handle->responseQueue), size, data);
			
			pthread_mutex_unlock( &handle->lock );
		}
	}
	return LV2_WORKER_SUCCESS;
}

void LV2Worker_processResponses(LV2Worker *handle)
{
	if( handle != NULL && handle->running ) {
		LV2WorkerQueue* queue = NULL;
		LV2_Worker_Interface* iface = NULL;
		LV2_Handle lv2Handle = NULL;
		if( pthread_mutex_lock( &handle->lock ) == 0 ) {
			if( handle->running ) {
				queue = handle->responseQueue;
				iface = handle->iface;
				lv2Handle = handle->instance->lilvInstance->lv2_handle;

				handle->responseQueue = NULL;
			}
			pthread_mutex_unlock( &handle->lock );
		}
		
		if( iface != NULL && lv2Handle != NULL ) {
			if( queue != NULL ) {
				LV2WorkerQueue* next = queue;
				while( next != NULL ) {
					iface->work_response(lv2Handle, next->size, next->data);
					
					next = next->next;
				}
				LV2Worker_freeQueue(&queue);
			}
			if (iface->end_run != NULL) {
				iface->end_run(lv2Handle);
			}
		}
	}
}

void* LV2Worker_processThread(void* ptr)
{
	LV2Worker *handle = (LV2Worker *) ptr;
	if( handle != NULL ) {
		while(handle->running) {
			LV2WorkerQueue* queue = NULL;
			if( pthread_mutex_trylock( &handle->lock ) == 0 ) {
				if( handle->running ) {
					queue = handle->requestQueue;
					
					handle->requestQueue = NULL;
				}
				pthread_mutex_unlock( &handle->lock );
			}

			LV2WorkerQueue* next = queue;
			while( next != NULL ) {
				handle->iface->work(
					handle->instance->lilvInstance->lv2_handle, 
					LV2Worker_respond, 
					handle, 
					next->size, 
					next->data);

				next = next->next;
			}
			LV2Worker_freeQueue(&queue);

			usleep(100000);
		}
	}
	return NULL;
}

