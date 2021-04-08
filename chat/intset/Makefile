CFILES  = example.c intset.c unittest.c
HFILES  = intset.h
OFILES  = $(CFILES:.c=.o)
PROGS   = example unittest

# -----------------------------------------------------------------------------

CFLAGS  = -std=c99 -O2 -g -Wall -Wextra -Werror -Wfatal-errors
CC      = gcc
LD      = gcc

# -----------------------------------------------------------------------------

.PHONY: all clean
.INTERMEDIATE: $(OFILES)

all: $(PROGS)

clean:
	$(RM) $(OFILES) $(PROGS)

$(OFILES): $(HFILES)

$(PROGS): $(OFILES)
	$(LD) intset.o -pthread $@.o -o $@
