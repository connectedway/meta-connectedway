all: ofcp

ofcp: ofcp.o
    $(CC) $(CFLAGS) -o $@ $<

%.o: %.c
    $(CC) $(CFLAGS) -o $@ $<
