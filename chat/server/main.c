#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#include "client_handler.h"
#include "connection.h"
#include "fail.h"
#include "server.h"

/**
 * This is the code to run the server. This is run in a special, supervised
 * child process, so we can restart it when needed.
 */
static void
run_server()
{
  server_start();

  while (true) {
    struct connection *connection_to_client = server_await_connection();
    client_handler_launch(connection_to_client);
  }
}

// ----------------------------------------------------------------------------

/**
 * Supervise a child process. If the child process terminates due to an error,
 * an error message is printed and the program is terminated.
 */
static void
supervise(pid_t child_process)
{
  int wstatus;

  pid_t pid = wait(&wstatus);
  fail_if(pid < 0, "wait");

  if (WIFEXITED(wstatus)) {
    int exit_status = WEXITSTATUS(wstatus);
    if(exit_status == EXIT_SUCCESS) {
      // normal exit -- return and restart
      return;
    } else {
      // server exited due to error -- pass it on

      printf("\n"
             "------------------------------------------------------------\n"
             "SERVER TERMINATED: internal error\n"
             "------------------------------------------------------------\n\n");
      exit(exit_status);
    }
  }

  printf("\n"
         "------------------------------------------------------------\n"
         "SERVER TERMINATED: ");

  if (WIFSIGNALED(wstatus)) {
    printf("%s", strsignal(WTERMSIG(wstatus)));
  } else if (WIFSTOPPED(wstatus)) {
    printf("%s", strsignal(WSTOPSIG(wstatus)));
  }

  printf("\n"
         "------------------------------------------------------------\n\n");
  exit(EXIT_FAILURE);
}

// ----------------------------------------------------------------------------

int
main()
{
  while (true) {
    pid_t child_process = fork();
    fail_if(child_process < 0, "fork");

    // SIGINT (ctrl-C) will automatically be sent to both parent and child.
    // Ignore ctrl-C in the parent process; instead wait for child to terminate.
    if (child_process == 0) {
      // child
      signal(SIGINT, SIG_DFL);
      run_server();
    } else {
      // parent
      signal(SIGINT, SIG_IGN);
      supervise(child_process);
    }
  }
}
