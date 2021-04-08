//  -------------------------------------------------------------------------
//
//  server:
//
//  Functions for a network server, allowing clients to connect.
//
//  -------------------------------------------------------------------------

/**
 * Start server.  The server is currently configured to be reachable
 * only from the same machine.
 */
void
server_start();

/**
 * Blocks until a client connects, then returns a connection.
 */
struct connection *
server_await_connection();
