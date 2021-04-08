#include <ctype.h>
#include <signal.h>
#include <stdarg.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "connection.h"
#include "fail.h"
#include "msg_store.h"

struct connection {
  FILE *infile;                       // for parsing input
  FILE *outfile;                      // for formatted output
  struct msg_store *msg_store;        // message store for this connection
  struct client_state client;         // read state for client
};

// ----------------------------------------------------------------------------

struct connection *
connection_create(int socket, struct msg_store *store)
{
  struct connection * conn = malloc(sizeof(struct connection));
  fail_if(conn == NULL, "malloc");

  conn->infile  = fdopen(dup(socket), "r");
  conn->outfile = fdopen(socket, "w");
  conn->msg_store = store;

  msg_store_init_client(store, &conn->client);

  // ignore SIGPIPE errors (can happen when connections are closed)
  signal(SIGPIPE, SIG_IGN);

  return conn;
}

// ----------------------------------------------------------------------------

struct msg_store *
connection_get_msg_store(struct connection *conn)
{
  return conn->msg_store;
}

// ----------------------------------------------------------------------------

struct client_state *
connection_get_client_state(struct connection *conn)
{
  return &conn->client;
}

// ----------------------------------------------------------------------------

bool
connection_receive(struct connection *conn,
                   char *buffer,
                   int buffer_size)
{
  char *s = fgets(buffer, buffer_size, conn->infile);
  if (s == NULL) {
    return false;
  }
  int pos = strlen(s) - 1;
  while (pos > 0 && isspace((unsigned char) buffer[pos])) {
    buffer[pos] = '\0';
    pos--;
  }
  return true;
}

// ----------------------------------------------------------------------------

void
connection_send(struct connection *conn,
                char *format,
                ...)
{
  va_list args;
  va_start(args, format);
  vfprintf(conn->outfile, format, args);
  fflush(conn->outfile);
  va_end(args);
}

// ----------------------------------------------------------------------------

void
connection_dispose(struct connection *conn)
{
  msg_store_dispose_client(conn->msg_store, &conn->client);
  fclose(conn->infile);
  fclose(conn->outfile);
  free(conn);
}
