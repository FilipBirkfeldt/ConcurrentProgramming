#include <stdbool.h>
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

#include "fail.h"
#include "list.h"

#define INITIAL_ARRAYLIST_SIZE   (10)

// ----------------------------------------------------------------------------

struct list {
  void **buf;             // points to array of void* pointers
  int allocated;          // number of allocated (possibly unused) elements
  int used;               // number of actually used elements
};

// ----------------------------------------------------------------------------

struct list *
list_create()
{
  struct list *l = malloc(sizeof(struct list));
  l->buf         = malloc(INITIAL_ARRAYLIST_SIZE * sizeof(void *));
  l->allocated   = INITIAL_ARRAYLIST_SIZE;
  l->used        = 0;

  return l;
}

// ----------------------------------------------------------------------------

int
list_add(struct list *l, void *data)
{
  // grow the underlying array if necessary
  if (l->used >= l->allocated) {
    l->allocated *= 2;
    l->buf = realloc(l->buf, l->allocated * sizeof(void *));
    fail_if(l->buf == NULL, "realloc");
  }

  int index = l->used;
  l->used++;
  l->buf[index] = data;
  return index;
}

// ----------------------------------------------------------------------------

int
list_add_int(struct list *l, int data)
{
  return list_add(l, (void *) (intptr_t) data);
}

// ----------------------------------------------------------------------------

void *
list_get(struct list *l, int index)
{
  if (index < 0 || index >= l->used) {
    fail("invalid index %d (size = %d)\n", index, l->used);
  }

  return l->buf[index];
}

// ----------------------------------------------------------------------------

int
list_get_int(struct list *l, int index) {
  return (int) (intptr_t) list_get(l, index);
}

// ----------------------------------------------------------------------------

void *
list_set(struct list *l, int index, void *data)
{
  if (index < 0 || index >= l->used) {
    fail("invalid index %d (size = %d)\n", index, l->used);
  }

  void *previous = l->buf[index];
  l->buf[index] = data;
  return previous;
}

// ----------------------------------------------------------------------------

int
list_set_int(struct list *l, int index, int data)
{
  return (int) (intptr_t) list_set(l, index, (void *) (intptr_t) data);
}
// ----------------------------------------------------------------------------

int
list_size(struct list *l)
{
  return l->used;
}

// ----------------------------------------------------------------------------

void
list_destroy(struct list *l)
{
  free(l);
}
