//  -------------------------------------------------------------------------
//
//  list:
//
//  Functions for creating, using, and destroying lists. Inspired by
//  Java's ArrayList.
//
//  -------------------------------------------------------------------------

struct list;

/**
 * Create a new empty list.
 */
struct list *
list_create();

/**
 * Add a pointer element to the list l (in the last position).
 */
int
list_add(struct list *l, void *data);

/**
 * Add an int element to the list l (in the last position).
 */
int
list_add_int(struct list *l, int data);

/**
 * Returns the element at position _index_ in the list l, as a pointer.
 */
void *
list_get(struct list *l, int index);

/**
 * Returns the element at position _index_ in the list l, as an int.
 */
int
list_get_int(struct list *l, int index);

/**
 * Sets the element at position _index_ in the list l, as a pointer.
 * The previous value is returned.
 */
void *
list_set(struct list *l, int index, void *data);

/**
 * Sets the element at position _index_ in the list l, as an int.
 * The previous value is returned.
 */
int
list_set_int(struct list *l, int index, int data);

/**
 * Returns the number of elements in the list l.
 */
int
list_size(struct list *l);

/**
 * Disposes of the memory allocated for the list.
 */
void
list_destroy(struct list *l);
