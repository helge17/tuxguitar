#ifndef _Included_VSTSocket
#define _Included_VSTSocket

typedef struct {
	void *data;
	bool connected;
} VSTSocketHandle;

void VSTSocketCreate(VSTSocketHandle **handle, int port);

void VSTSocketDestroy(VSTSocketHandle **handle);

void VSTSocketRead(VSTSocketHandle *handle, void *buffer, int length);

void VSTSocketWrite(VSTSocketHandle *handle, void *buffer, int length);

#endif /* _Included_VSTSocket */
