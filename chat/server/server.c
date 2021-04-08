#include <netinet/in.h>
#include <netinet/tcp.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>

#include "connection.h"
#include "fail.h"
#include "msg_store.h"
#include "server.h"

#define SERVER_PORT   (9000)

static int server_socket;
static struct msg_store * store;

// ----------------------------------------------------------------------------

void
server_start()
{
  struct sockaddr_in server_address;
  memset(&server_address, 0, sizeof(struct sockaddr_in));
  server_address.sin_family      = AF_INET;
  server_address.sin_port        = htons(SERVER_PORT);
  server_address.sin_addr.s_addr = htonl(INADDR_LOOPBACK);

  server_socket = socket(AF_INET, SOCK_STREAM, 0);
  fail_if(server_socket < 0, "socket");

  int option = 1;
  setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, &option, sizeof(option));
  setsockopt(server_socket, IPPROTO_TCP, TCP_NODELAY, &option, sizeof(option));

  int status;
  status = bind(server_socket,
                (const struct sockaddr *) &server_address,
                sizeof(server_address));
  fail_if(status < 0, "bind");

  // If more than 1000 clients initiate connection at the exact same time,
  // connections will be refused.
  status = listen(server_socket, 1000);
  fail_if(status < 0, "listen");

  store = msg_store_create();
}

// ----------------------------------------------------------------------------

struct connection *
server_await_connection()
{
  struct sockaddr_in client_address;
  socklen_t sz = sizeof(client_address);

  int client_socket = accept(server_socket,
                             (struct sockaddr *) &client_address,
                             &sz);
  fail_if(client_socket < 0, "accept");

  static int one = 1;
  int status = setsockopt(client_socket,
                          IPPROTO_TCP,
                          TCP_NODELAY,
                          (char *) &one,
                          sizeof(int));
  fail_if(status < 0, "setsockopt");

  return connection_create(client_socket, store);
}
