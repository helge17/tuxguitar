#include <stdlib.h>
#include <winsock2.h>
#include <time.h>
#include "VSTSocket.h"
#include "VSTLogger.h"

typedef struct {
    WSADATA wsa;
    SOCKET socket;
    time_t time;
} VSTSocketHandleData;

void VSTSocketCreate(VSTSocketHandle **handle, int port)
{
	(*handle) = (VSTSocketHandle *) malloc( sizeof(VSTSocketHandle));
	(*handle)->data = malloc(sizeof(VSTSocketHandleData));
	(*handle)->connected = false;
	
	VSTSocketHandleData *data = ((VSTSocketHandleData *) (*handle)->data);
	
	VSTLogger_log("Initialising Winsock...\n");
	if( WSAStartup(MAKEWORD(2,2), &(data->wsa) ) != 0) {
		VSTLogger_log("Failed. Error Code : %d",WSAGetLastError());
		return;
	}
	
	VSTLogger_log("Initialised.\n");
	
	data->socket = socket(AF_INET , SOCK_STREAM , 0);
	if( data->socket == INVALID_SOCKET) {
		VSTLogger_log("Could not create socket : %d" , WSAGetLastError());
		return;
	}
 
	VSTLogger_log("Socket created.\n");
    
	struct sockaddr_in server;
	server.sin_addr.s_addr = inet_addr("127.0.0.1");
	server.sin_family = AF_INET;
	server.sin_port = htons( port );
	
	//Connect to remote server
	if (connect(data->socket , (struct sockaddr *)&server , sizeof(server)) < 0) {
		VSTLogger_log("connect failed. Error\n");
		return;
	}
	
	data->time = time(NULL);
	
	// Because we need real time messaging
	int tcpNoDelay = 1; 
	setsockopt(data->socket, IPPROTO_TCP, TCP_NODELAY, (char *) &tcpNoDelay, sizeof(int));
	
	(*handle)->connected = true;
	
	VSTLogger_log("Socket Connected\n");
}

void VSTSocketDestroy(VSTSocketHandle **handle)
{
	if((*handle) != NULL && (*handle)->data != NULL ) {
		closesocket(((VSTSocketHandleData *) (*handle)->data)->socket);
		WSACleanup();
		
		free ((*handle)->data);
		free ((*handle));
		
		(*handle) = NULL;
	}
}

void VSTSocketRead(VSTSocketHandle *handle, void *buffer, int length)
{
	if( handle != NULL && handle->data != NULL ) {
		VSTSocketHandleData *data = ((VSTSocketHandleData *) handle->data);
		if( handle->connected ) {			
			int read = recv(data->socket, (char *) buffer, length, MSG_WAITALL);
			if( read == SOCKET_ERROR ) {
				handle->connected = false;
				VSTLogger_log("Disconected: \n");
			}
			else if( read == 0 ) {
				time_t now = time(NULL);
				if( now - data->time > 10 ) {
					handle->connected = false;
					VSTLogger_log("Time out: \n");
				}
			}
			else {
				data->time = time(NULL);
			}
		}
	}
}

void VSTSocketWrite(VSTSocketHandle *handle, void *buffer, int length)
{
	if( handle != NULL && handle->data != NULL ) {
		VSTSocketHandleData *data = ((VSTSocketHandleData *) handle->data);
		if( handle->connected ) {
			if( send(data->socket, (char *) buffer, length, MSG_WAITALL) < 0 ) {
				handle->connected = false;
				VSTLogger_log("Disconnected: \n");
			}
		}
	}
}
