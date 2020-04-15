/*	cam-control.c				    *
 ****************************************************
 * C program that opens a socket that receives      *
 * messages from INET and orders the camera to move *
 ***************************************************/

#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <unistd.h>
#include "v4l2_control.h"

int main()
{
	int fd, ret;

	step_t step = LARGE;
	/*Location of the camera device*/
	const char *device = "/dev/video0";

	/*Open a socket to receive datagrams*/
	struct sockaddr_in local_addr;
	struct sockaddr_in client_addr;
	socklen_t size_addr;
	char buff[100];
	int nbytes;

	int sock_fd = socket(AF_INET, SOCK_DGRAM, 0);

	if (sock_fd == -1)
	{
		perror("Socket");
		exit(-1);
	}

	local_addr.sin_family = AF_INET;
	local_addr.sin_port = htons(3001);
	local_addr.sin_addr.s_addr = INADDR_ANY;

	int err = bind(sock_fd, (struct sockaddr *)&local_addr, sizeof(local_addr));
	if (err == -1)
	{
		perror("bind");
		exit(-1);
	}

	printf("\nSocket created and binded\nReady to receive messages\n\n");
	/*End of socket setup*/
	
	/*Open camera into a file descriptor*/
	fd = v4l2_open(device);

	if (fd < 0)
	{
		fprintf(stderr, "V4L2 Open '%s': %s\n", device, strerror(errno));
		return fd;
	}

	while(1)
	{
		size_addr = sizeof(client_addr);
		nbytes = recvfrom(sock_fd, buff, 6, 0, (struct sockaddr *)&client_addr, &size_addr);		
		printf("BUFF: %s\n", (char *)buff);
		if(nbytes == 0){
			break;
		}
		ret = 0;
		/*If message received was left*/
		if (!strncmp((char *)buff, "left", 4))
		{
			/*If message received was left*/
			ret = v4l2_pan(fd, LEFT, step);
		}
		else if (!strncmp((char *)buff, "right", 5))
		{
			/*If message received was right*/
			ret = v4l2_pan(fd, RIGHT, step);
		}
		else if (!strncmp((char *)buff, "up", 2))
		{
			/*If message received was up*/
			ret = v4l2_tilt(fd, UP, step);
		}
		else if (!strncmp((char *)buff, "down", 4))
		{
			/*If message received was down*/
			ret = v4l2_tilt(fd, DOWN, step);
		}
		
		/*If neither of these we're received*/
		if (ret != 0){
			fprintf(stderr, "V4L2 error: %s\n", strerror(errno));
		}
	}
	/*Close camera and socket fd*/
	v4l2_close(fd);
	close(sock_fd);
	return 0;
}
