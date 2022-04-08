#ifndef _Included_LV2Worker
#define _Included_LV2Worker

#include <pthread.h>

typedef struct LV2WorkerQueueImpl LV2WorkerQueue;

struct LV2WorkerImpl {
    bool running;
    pthread_t thread;
    pthread_mutex_t lock;
    LV2Instance* instance;
    LV2WorkerQueue* requestQueue;
    LV2WorkerQueue* responseQueue;
    LV2_Worker_Interface* iface;
};

struct LV2WorkerQueueImpl {
    uint32_t size;
    void* data;
	
    LV2WorkerQueue* next;
};

void LV2Worker_malloc(LV2Worker **handle);

void LV2Worker_free(LV2Worker **handle);

void LV2Worker_freeQueue(LV2WorkerQueue **queue);

void LV2Worker_start(LV2Worker *handle, LV2Instance *instance);

void LV2Worker_stop(LV2Worker *handle);

void LV2Worker_enqueue(LV2Worker *handle, LV2WorkerQueue **queue, uint32_t size, const void *data);

void LV2Worker_processResponses(LV2Worker *handle);

void* LV2Worker_processThread(void* ptr);

LV2_Worker_Status LV2Worker_schedule(LV2_Worker_Schedule_Handle workerHandle, uint32_t size, const void* data);

LV2_Worker_Status LV2Worker_respond(LV2_Worker_Respond_Handle workerHandle, uint32_t size, const void* data);

#endif
