#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/tcp.h>
#include <time.h>
#include "LV2Socket.h"
#include "LV2Logger.h"

typedef struct {
	int socket;
    struct timespec time;
} LV2SocketData;

void LV2Socket_create(LV2Socket **handle, int port)
{
	(*handle) = (LV2Socket *) malloc( sizeof(LV2Socket));
	(*handle)->data = malloc(sizeof(LV2SocketData));
	(*handle)->connected = false;
	
	LV2SocketData *data = ((LV2SocketData *) (*handle)->data);
    
	//Create socket
	data->socket = socket(AF_INET , SOCK_STREAM , 0);
	if (data->socket == -1) {
		LV2Logger_log("LV2Client -> could not create socket\n");
		return;
	}
	
	LV2Logger_log("LV2Client -> socket created\n");
	
	struct sockaddr_in server;
	server.sin_addr.s_addr = inet_addr("127.0.0.1");
	server.sin_family = AF_INET;
	server.sin_port = htons( port );
	
	//Connect to remote server
	if (connect(data->socket , (struct sockaddr *) &server, sizeof(server)) < 0) {
		LV2Logger_log("LV2Client -> connect failed. Error\n");
		return;
	}
	
	clock_gettime(CLOCK_REALTIME, &(data->time));
    
    // Because we need real time messaging
	int tcpNoDelay = 1; 
	setsockopt(data->socket, IPPROTO_TCP, TCP_NODELAY, (char *) &tcpNoDelay, sizeof(int));
	
	(*handle)->connected = true;
	
	LV2Logger_log("LV2Client -> socket connected\n");
}

void LV2Socket_destroy(LV2Socket **handle)
{
	if((*handle) != NULL && (*handle)->data != NULL ) {
		close(((LV2SocketData *) (*handle)->data)->socket);
		
		free ((*handle)->data);
		free ((*handle));
		
		(*handle) = NULL;
	}
}

void LV2Socket_read(LV2Socket *handle, void *buffer, int length)
{
	if( handle != NULL && handle->data != NULL ) {
		LV2SocketData *data = ((LV2SocketData *) handle->data);
		if( handle->connected ) {
			int read = recv(data->socket, buffer, length, MSG_WAITALL);
			if( read == -1 ) {
				handle->connected = false;
				LV2Logger_log("LV2Client -> disconected\n");
			}
			else if( read == 0 ) {
				struct timespec now;
				clock_gettime(CLOCK_REALTIME, &now);
				
				if( now.tv_sec - data->time.tv_sec > 10 ) {
					handle->connected = false;
					LV2Logger_log("LV2Client -> time out\n");
				}
			}
			else {
				clock_gettime(CLOCK_REALTIME, &(data->time));
			}
		}
	}
}

void LV2Socket_write(LV2Socket *handle, void *buffer, int length)
{
	if( handle != NULL && handle->data != NULL ) {
		LV2SocketData *data = ((LV2SocketData *) handle->data);
		if( handle->connected ) {
			if( send(data->socket, buffer, length, MSG_WAITALL) < 0 ) {
				handle->connected = false;
				LV2Logger_log("LV2Client -> disconnected\n");
			}
		}
	}
}
