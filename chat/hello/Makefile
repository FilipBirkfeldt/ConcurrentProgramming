# -----------------------------------------------------------------------------

CFLAGS  = -std=c99 -O2 -g -Wall -Wextra -Werror -Wfatal-errors
CC      = gcc
LD      = gcc

# -----------------------------------------------------------------------------

.PHONY: all clean
.INTERMEDIATE: hello.o

all: hello

clean:
	$(RM) hello.o hello

hello: hello.o
	$(LD) hello.o -o hello
