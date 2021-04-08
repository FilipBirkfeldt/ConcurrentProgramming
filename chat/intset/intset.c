#include <pthread.h>
#include <limits.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

#include "intset.h"
//har vi sparat? 

/*
 * Set implemented using closed hashing.
 */

struct intset {
  int size;
  int allocated;
  int *data;
  pthread_mutex_t mutex;
};

// special value indicating a free array element
#define EMPTY_SLOT       (INT_MIN)

// ----------------------------------------------------------------------------

struct intset *
intset_create()
{
  struct intset *s = malloc(sizeof(struct intset));

  // if memory allocation fails, terminate with an error
  if(s == NULL) {
    perror("malloc");
    exit(1);
  }


  pthread_mutex_init(&s->mutex, NULL);
  s->size = 0;
  s->allocated = 10;
  s->data = malloc(sizeof(int) * s->allocated);
  for (int i = 0; i < s->allocated; i++) {
    s->data[i] = EMPTY_SLOT;
  }
  
  return s;
}

// ----------------------------------------------------------------------------

// local function: return hash index for a
static int
index(struct intset *s, int a)
{
  return abs(a % s->allocated);
}

// ----------------------------------------------------------------------------

// local function: find index where 'a' should be
// if a is in the set, it is in element [idx]
// if a is not in the set, element [idx] is where it should be
static int
find(struct intset *s, int a)
{
  int idx = index(s, a);
  for (int i = 0; i < s->allocated; i++) {
    if (s->data[idx] == a || s->data[idx] == EMPTY_SLOT) {
      return idx;
    }
    idx = (idx + 1) % s->allocated;
  }

  // shouldn't happen: if we got here, it means that the array is completely
  // full, which it should never become (we would rehash before that)

  return -1;
}

// ----------------------------------------------------------------------------

bool
intset_add(struct intset *s, int a)
{
  pthread_mutex_lock(&s->mutex);
  // rehash if more than 70% is used
  if (s->size >= s->allocated * 7 / 10) {
    int old_allocated = s->allocated;
    int *old_data     = s->data;

    // double array size
    s->allocated *= 2;
    s->data = malloc(sizeof(int) * s->allocated);
    for (int i = 0; i < s->allocated; i++) {
      s->data[i] = EMPTY_SLOT;
    }

    // copy values to new array
    for (int i = 0; i < old_allocated; i++) {
      int a = old_data[i];
      if (a != EMPTY_SLOT) {
        int idx = index(s, a);
        int attempts = 0;
	    while (s->data[idx] != EMPTY_SLOT && attempts < s->allocated) {
	      idx = (idx + 1) % s->allocated;
          attempts++;
	    }
	    s->data[idx] = a;
      }
    }

    free(old_data);
  }

  int idx = find(s, a);
  if (s->data[idx] == a) {
    return false;
  }

  s->data[idx] = a;
  s->size++;

  pthread_mutex_unlock(&s->mutex);

  return true;
}

// ----------------------------------------------------------------------------

bool
intset_contains(struct intset *s, int a)
{
  pthread_mutex_lock(&s->mutex);
  // use private helper function above
  int idx = find(s, a);
  bool found = (s->data[idx] == a);

  pthread_mutex_unlock(&s->mutex);

  return found;
}

// ----------------------------------------------------------------------------

int
intset_size(struct intset *s)
{
  pthread_mutex_lock(&s->mutex);

  int sz = s->size;

  pthread_mutex_unlock(&s->mutex);

  return sz;
}
