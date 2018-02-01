#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/tcp.h>
#include <time.h>
#include "VSTSocket.h"
#include "VSTLogger.h"

typedef struct {
	int socket;
    struct timespec time;
} VSTSocketHandleData;

void VSTSocketCreate(VSTSocketHandle **handle, int port)
{
	(*handle) = (VSTSocketHandle *) malloc( sizeof(VSTSocketHandle));
	(*handle)->data = malloc(sizeof(VSTSocketHandleData));
	(*handle)->connected = false;
	
	VSTSocketHandleData *data = ((VSTSocketHandleData *) (*handle)->data);
    
	//Create socket
	data->socket = socket(AF_INET , SOCK_STREAM , 0);
	if (data->socket == -1) {
		VSTLogger_log("Could not create socket\n");
		return;
	}
	
	VSTLogger_log("Socket created\n");
	
	struct sockaddr_in server;
	server.sin_addr.s_addr = inet_addr("127.0.0.1");
	server.sin_family = AF_INET;
	server.sin_port = htons( port );
	
	//Connect to remote server
	if (connect(data->socket , (struct sockaddr *) &server, sizeof(server)) < 0) {
		VSTLogger_log("Connect failed. Error\n");
		return;
	}
	
	clock_gettime(CLOCK_REALTIME, &(data->time));
    
    // Because we need real time messaging
	int tcpNoDelay = 1; 
	setsockopt(data->socket, IPPROTO_TCP, TCP_NODELAY, (char *) &tcpNoDelay, sizeof(int));
	
	(*handle)->connected = true;
	
	VSTLogger_log("Socket connected\n");
}

void VSTSocketDestroy(VSTSocketHandle **handle)
{
	if((*handle) != NULL && (*handle)->data != NULL ) {
		close(((VSTSocketHandleData *) (*handle)->data)->socket);
		
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
			int read = recv(data->socket, buffer, length, MSG_WAITALL);
			if( read == -1 ) {
				handle->connected = false;
				VSTLogger_log("Disconected\n");
			}
			else if( read == 0 ) {
				struct timespec now;
				clock_gettime(CLOCK_REALTIME, &now);
				
				if( now.tv_sec - data->time.tv_sec > 10 ) {
					handle->connected = false;
					VSTLogger_log("Time out\n");
				}
			}
			else {
				clock_gettime(CLOCK_REALTIME, &(data->time));
			}
		}
	}
}

void VSTSocketWrite(VSTSocketHandle *handle, void *buffer, int length)
{
	if( handle != NULL && handle->data != NULL ) {
		VSTSocketHandleData *data = ((VSTSocketHandleData *) handle->data);
		if( handle->connected ) {
			if( send(data->socket, buffer, length, MSG_WAITALL) < 0 ) {
				handle->connected = false;
				VSTLogger_log("Disconnected\n");
			}
		}
	}
}
