//  -------------------------------------------------------------------------
//
//  msg_store:
//
//  Functions for storing and retrieving topics and messages. Every
//  message belongs to a particular topic.
//
//  Each client subscribes to one topic at a time. New topics are
//  always published to all clients. New messages are published to all
//  clients that subscribe to the topic the message belongs to.
//
//  -------------------------------------------------------------------------

struct msg_store;
struct client_state;                       // defined below

/**
 * Create a new message store.
 */
struct msg_store *
msg_store_create();

/**
 * Adds a new topic to the message store.  The topic will be published
 * to all connected clients.  Returns the ID of the newly created
 * topic.
 */
int
msg_store_add_topic(struct msg_store *store,
                    char *username,
                    char *text);

/**
 * Adds a new message to the topic currently subscribed to by
 * _client_.  The message will be published to all clients that
 * subscribe to this topic.
 */
void
msg_store_add_message(struct msg_store *store,
                      struct client_state *client,
                      char *username,
                      char *text);

/**
 * Retrieves the next topic that needs to be published to this client.
 * The result is stored in variables pointed to by parameters
 * _topic_id_, _username_, and _text_.
 * Returns true if there was such a topic, false otherwise.
 */
bool
msg_store_check_for_new_topic(struct msg_store *store,
                              struct client_state *client,
                              int *topic_id,
                              char **username,
                              char **text);

/**
 * Retrieves the next message (within the current topic) that needs to
 * be published to this client.  The result is stored in variables
 * pointed to by parameters _topic_id_, _message_id_, _username_, and
 * _text_.  Returns true if there was such a message, false otherwise.
 */
bool
msg_store_check_for_new_message(struct msg_store *store,
                                struct client_state *client,
                                int *topic_id,
                                int *message_id,
                                char **username,
                                char **text);

/**
 * Checks for a topic that has new messages (that is, an increased message
 * count), and then retrieves the updated message count. The result is stored
 * in variables pointed to by parameters _topic_id_ and _message_count_.
 * Returns true if there was such an updated message count, false otherwise.
 */
bool
msg_store_check_for_updated_message_count(struct msg_store *store,
                                          struct client_state *client,
                                          int *topic_id,
                                          int *message_count);

/**
 * Waits until either a new message or a new topic is available for _client_.
 *
 * Returns the client's reading state
 * (current topic ID, or one of the TOPIC_STATE constants below).
 */
int
msg_store_await_message_or_topic(struct msg_store *store,
                                 struct client_state *client);

/**
 * Initialize client_state for a particular client. Called when a user logs in.
 * The client is initially set to not subscribe to any topic.
 */
void
msg_store_init_client(struct msg_store *store,
                      struct client_state *client);

/**
 * Dispose of client_state for a particular client.
 * Called when a user has logged out.
 */
void
msg_store_dispose_client(struct msg_store *store,
                         struct client_state *client);

/**
 * Set _client_ to subscribe to topid _topic_id_.
 */
void
msg_store_select_topic(struct msg_store *store,
                       struct client_state *client,
                       int topic_id);

// ----------------------------------------------------------------------------

/**
 * Struct for keeping track of what has been reported to a particular client.
 */
struct client_state {
  int current_topic_id;          // topic selected by this client
  int nbr_read;                  // number of messages read (in current topic)
  struct list *message_counts;   // number of reported messages for each topic
};

/** Special values for 'current_topic_id', to denote client state */
enum {
  TOPIC_STATE_NO_TOPIC         = -1,    // not subscribing to any topic
  TOPIC_STATE_LOGOUT_REQUESTED = -2,    // client has requested to log out
  TOPIC_STATE_DISCONNECTED     = -3     // client is disconnected
};
