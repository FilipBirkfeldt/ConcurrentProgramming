//
//  intset:
//
//  Functions for creating and using sets of integers.
//

struct intset;

/**
 * Create a new integer set.
 */
struct intset *
intset_create();

/**
 * Add the value _a_ to the integer set _s_. Returns true if the value
 * could be added, or false if it was already present in the set.
 */
bool
intset_add(struct intset *s, int a);

/**
 * Returns true if the value _a_ is present in the integer set _s_,
 * false otherwise.
 */
bool
intset_contains(struct intset *s, int a);

/**
 * Returns the number of values in the integer set _s_.
 */
int
intset_size(struct intset *s);
