#ifndef _Included_LV2Lock
#define _Included_LV2Lock

#include "LV2.h"
#include <pthread.h>

struct LV2LockImpl {
    pthread_mutex_t lock;
};

void LV2Lock_malloc(LV2Lock **handle);

void LV2Lock_free(LV2Lock **handle);

bool LV2Lock_lock(LV2Lock *handle);

bool LV2Lock_trylock(LV2Lock *handle);

void LV2Lock_unlock(LV2Lock *handle);

#endif /* _Included_LV2Lock */
