#include <pthread.h>
#include <stdbool.h>
#include <stdio.h>

#include "intset.h"

struct intset *s; // global variable, accessible in all functions in this file

// ----------------------------------------------------------------------------

/**
 * Local helper function for adding 25000 integers to the set.
 *
 * The 'arg' argument is assumed to point to an integer n. All
 * integers i such that n <= i < (n + 25000) are added to the set.
 */
static void *
add_values(void *arg)
{
  int *pn = arg;       // assume 'arg' points to an int
  int n = *pn;         // read the int from the pointer

  for (int i = n; i < n + 25000; i++) {
    intset_add(s, i);
  }

  return NULL;
}

// ----------------------------------------------------------------------------

/*
 * Local helper function. Checks that the set contains all values
 * 0 <= i < 100000, and that the size is exactly 100000.
 */
static void
check_result()
{
  int errors = 0;
  for (int i = 0; i < 100000; i++) {
    if (! intset_contains(s, i)) {
      errors++;
    }
  }
  if (errors != 0) {
    printf("ERROR: %d values missing\n", errors);
  }
	
  int sz = intset_size(s);
  if (sz != 100000) {
    printf("ERROR: size is %d, expected 100000\n", sz);
    errors++;
  }
	
  if (errors == 0) {
    printf(">>> PASS\n");
  } else {
    printf(">>> FAIL (%d errors)\n", errors);
  }
}

// ----------------------------------------------------------------------------

/*
 * Add numbers 0 <= n < 100000 sequentially, using one single thread.
 */
static void
single_threaded_test()
{
  int start1 = 0;
  int start2 = 25000;
  int start3 = 50000;
  int start4 = 75000;

  printf("single-threaded test:\n");

  s = intset_create();
	
  add_values(&start1);
  add_values(&start2);
  add_values(&start3);
  add_values(&start4);
  
  check_result();
}

// ----------------------------------------------------------------------------

/*
 * Add numbers 0 <= n < 100000 concurrently, in four threads.
 */
static void
multi_threaded_test()
{
  int start1 = 0;
  int start2 = 25000;
  int start3 = 50000;
  int start4 = 75000;

  pthread_t t1;
  pthread_t t2;
  pthread_t t3;
  pthread_t t4;
	
  printf("multi-threaded test:\n");
	
  s = intset_create();
	
  pthread_create(&t1, NULL, &add_values, &start1);
  pthread_create(&t2, NULL, &add_values, &start2);
  pthread_create(&t3, NULL, &add_values, &start3);
  pthread_create(&t4, NULL, &add_values, &start4);
	
  pthread_join(t1, NULL);
  pthread_join(t2, NULL);
  pthread_join(t3, NULL);
  pthread_join(t4, NULL);
	
  check_result();
}

// ----------------------------------------------------------------------------

int
main()
{
  single_threaded_test();
  multi_threaded_test();
}
