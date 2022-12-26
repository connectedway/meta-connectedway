all: ofcp

ofcp: ofcp.o
	$(CC) $(LDFLAGS) -o $@ $< -lof_smb_shared -lof_core_shared -lkrb5 -lgssapi_krb5 

%.o: %.c
	$(CC) -c $(CFLAGS) -o $@ $<

clean:
	rm -f ofcp.o ofcp

install:
	install -d $(DESTDIR)/$(BINDIR)
	install -m 755 ofcp $(DESTDIR)/$(BINDIR)
