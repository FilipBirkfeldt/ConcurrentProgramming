#include <stdbool.h>
#include <stdio.h>

#include "intset.h"

int
main()
{
  struct intset *s = intset_create();

  intset_add(s, 7);
  intset_add(s, 4);

  for (int i = 0; i < 10; i++) {
    if (intset_contains(s, i)) {
      printf("%d is in the set\n", i);
    } else {
      printf("%d is not in the set\n", i);
    }
  }
}
