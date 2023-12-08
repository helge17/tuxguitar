#ifndef _Included_LV2Socket
#define _Included_LV2Socket

typedef struct {
	void *data;
	bool connected;
} LV2Socket;

void LV2Socket_create(LV2Socket **handle, int port);

void LV2Socket_destroy(LV2Socket **handle);

void LV2Socket_read(LV2Socket *handle, void *buffer, int length);

void LV2Socket_write(LV2Socket *handle, void *buffer, int length);

#endif /* _Included_LV2Socket */
