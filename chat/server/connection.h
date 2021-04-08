//  -------------------------------------------------------------------------
//
//  connection:
//
//  Functions for creating connections, used for sending and receiving
//  messages. Also keeps track of some useful data structures, such as
//  a msg_store and client_state.
//
//  -------------------------------------------------------------------------

struct connection;
struct msg_store;                          // defined in msg_store.c
struct client_state;                       // defined in msg_store.h

/**
 * Create a connection for the given socket, and the given message
 * store.
 */
struct connection *
connection_create(int socket, struct msg_store *store);

/**
 * Retrieves the msg_store associated with the connection _conn_.
 */
struct msg_store *
connection_get_msg_store(struct connection *conn);

/**
 * Get the client_state associated with the connection _conn_.
 */
struct client_state *
connection_get_client_state(struct connection *conn);

/**
 * Blocks until a message is available on connection _conn_, then stores
 * the message in the buffer pointed to by _buffer_.  Returns true if a
 * message could be received, or false if the server disconnects.
 */
bool
connection_receive(struct connection *conn,
                   char *buffer,
                   int buffer_size);

/**
 * Sends a message on connection _conn_, using a printf-style format.
 */
void
connection_send(struct connection *conn,
                char *format,
                ...);

/**
 * Closes connection _conn_ for communication, and disposes of all
 * data allocated for the connection.
 */
void
connection_dispose(struct connection *conn);
