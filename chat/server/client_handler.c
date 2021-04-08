#include <pthread.h>
#include <sched.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "client_handler.h"
#include "connection.h"
#include "fail.h"
#include "msg_store.h"

// ----------------------------------------------------------------------------

#define PACKET_LOGIN                   ('L')
#define PACKET_CREATE_TOPIC            ('C')
#define PACKET_SELECT_TOPIC            ('S')
#define PACKET_POST_MESSAGE            ('P')
#define PACKET_RESTART                 ('R')
#define PACKET_LOGOUT                  ('O')

#define PACKET_PUBLISH_TOPIC           "T %d %s %s\n"
#define PACKET_PUBLISH_MESSAGE_TEXT    "M %d %d %s %s\n"
#define PACKET_PUBLISH_MESSAGE_COUNT   "C %d %d\n"
#define PACKET_LOGGED_OUT              "O\n"

#define CONNECTION_BUFFER_SIZE    (1024)

// ----------------------------------------------------------------------------
// Local function: thread for sending messages

static void *
client_writer_thread_main(void *arg)
{
  struct connection *conn    = (struct connection *) arg;
  struct msg_store *store    = connection_get_msg_store(conn);
  struct client_state *state = connection_get_client_state(conn);

  while (true) {
    char *username;
    char *text;
    int topic_id;
    int nbr_messages;
    int message_id;

    switch (msg_store_await_message_or_topic(store, state)) {

      case TOPIC_STATE_LOGOUT_REQUESTED:
        connection_send(conn, PACKET_LOGGED_OUT);
        // fall through

      case TOPIC_STATE_DISCONNECTED:
        pthread_exit(NULL);
        break;

      default:
        if (msg_store_check_for_new_topic(store, state,
                                          &topic_id, &username, &text))
        {
          connection_send(conn, PACKET_PUBLISH_TOPIC,
                          topic_id, username, text);
        } else if (msg_store_check_for_new_message(store, state,
                                                   &topic_id, &message_id,
                                                   &username, &text))
        {
          connection_send(conn, PACKET_PUBLISH_MESSAGE_TEXT,
                          topic_id, message_id, username, text);
        } else if (msg_store_check_for_updated_message_count(store, state,
                                                             &topic_id, &nbr_messages))
        {
          connection_send(conn, PACKET_PUBLISH_MESSAGE_COUNT,
                          topic_id, nbr_messages);
        }
    }
  }
}

// ----------------------------------------------------------------------------
// Local function: thread for receiving messages

static void *
client_reader_thread_main(void *arg)
{
  struct connection *conn    = (struct connection *) arg;
  struct msg_store *store    = connection_get_msg_store(conn);
  struct client_state *state = connection_get_client_state(conn);

  char buffer[CONNECTION_BUFFER_SIZE];  // buffer for received data
  char *username = NULL;
  pthread_t writer_thread;
  bool writer_thread_started = false;
  bool active = true;

  while (active) {
    bool still_connected = connection_receive(conn, buffer, sizeof(buffer));
    if (! still_connected) {
      printf("[%p] '%s' disconnected unexpectedly\n", state, username);
      msg_store_select_topic(store, state, TOPIC_STATE_DISCONNECTED);
      break;
    }

    char type = buffer[0];
    char *text = buffer + 1;
    switch (type)
    {
      case PACKET_LOGIN:
        printf("[%p] '%s' logged in\n", state, text);
        if (username != NULL) {
          fail("[%p] already logged in as '%s'\n", state, username);
        }
        username = strdup(text);

        // now start the writer thread
        int status = pthread_create(&writer_thread,
                                    NULL,
                                    &client_writer_thread_main,
                                    conn);
        fail_if(status != 0, "pthread_create");

        // set server thread policy to SCHED_RR, low priority
        struct sched_param params;
        params.sched_priority = sched_get_priority_min(SCHED_RR);
        pthread_setschedparam(writer_thread, SCHED_RR, &params);

        writer_thread_started = true;
        break;

      case PACKET_LOGOUT:
        printf("[%p] '%s' logged out\n", state, username);
        msg_store_select_topic(store, state, TOPIC_STATE_LOGOUT_REQUESTED);
        active = false;
        break;

      case PACKET_POST_MESSAGE:
        msg_store_add_message(store, state, username, text);
        break;

      case PACKET_CREATE_TOPIC:
        msg_store_add_topic(store, username, text);
        break;

      case PACKET_SELECT_TOPIC:
        msg_store_select_topic(store, state, atoi(text));
        break;

      case PACKET_RESTART:
        printf("\n"
               "------------------------------------------------------\n"
               "SERVER RESTARTING FOR UNIT TEST %s\n"
               "------------------------------------------------------\n\n",
               text);
        exit(EXIT_SUCCESS);
        break;

      default:
        fail("[%p] unexpected message type '%c'\n", state, type);
    }
  }

  // Clean-up: the client is now in one of the states
  // TOPIC_STATE_LOGOUT_REQUESTED or TOPIC_STATE_DISCONNECTED.
  // Wait for the writer thread to terminate, then release the shared data.
  if (writer_thread_started) {
    int status = pthread_join(writer_thread, NULL);
    fail_if(status != 0, "pthread_join");
    free(username);
  }
  connection_dispose(conn);
  return NULL;
}

// ============================================================================

void
client_handler_launch(struct connection *conn)
{
  // Start the reader thread here. The writer thread is started in
  // client_reader_thread_main() above, upon receiving PACKET_LOGIN.

  pthread_t reader_thread;
  int status = pthread_create(&reader_thread,
                              NULL,
                              &client_reader_thread_main,
                              conn);
  fail_if(status != 0, "pthread_create");

  // set server thread policy to SCHED_RR, low priority
  struct sched_param params;
  params.sched_priority = sched_get_priority_min(SCHED_RR);
  pthread_setschedparam(reader_thread, SCHED_RR, &params);
}
