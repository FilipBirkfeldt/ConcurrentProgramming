//  -------------------------------------------------------------------------
//
//  fail:
//
//  Macros for terminating the program upon error.
//
//  -------------------------------------------------------------------------

/**
 * Macro for terminating the program with an error message.
 */
#define fail(msg, ...)                                          \
  while (true) {                                                \
    fprintf(stderr, msg, __VA_ARGS__);                          \
    fprintf(stderr,                                             \
            "(function %s, file %s, line %d)\n",                \
            __func__, __FILE__, __LINE__);                      \
    exit(EXIT_FAILURE);                                         \
  }                                                             \


/**
 * Macro for checking a condition, and terminating the program if the
 * condition is true. Assumes errno to be set.
 */
#define fail_if(cond, context)                                  \
  while (cond) {                                                \
    perror(context);                                            \
    fprintf(stderr,                                             \
            "(function %s, file %s, line %d)\n",                \
            __func__, __FILE__, __LINE__);                      \
    exit(EXIT_FAILURE);                                         \
  }
