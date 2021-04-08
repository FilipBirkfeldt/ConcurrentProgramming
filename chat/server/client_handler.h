//  -------------------------------------------------------------------------
//
//  client_handler:
//
//  Functions for launching threads running on behalf of clients.
//
//  -------------------------------------------------------------------------

struct connection;          // defined further in connection.c

/**
 * Launch two threads on behalf of the connection. One thread receives
 * incoming messages, and the other thread sends outgoing messages.
 */
void
client_handler_launch(struct connection *conn);
