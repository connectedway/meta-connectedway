all: ofcp

ofcp: ofcp.o
	$(CC) $(LDFLAGS) -o $@ $< -Wl,--no-as-needed -lof_smb_shared -lof_core_shared -lmbedtls -lkrb5 -lgssapi_krb5 

%.o: %.c
	$(CC) -g -c $(CFLAGS) -o $@ $< 

clean:
	rm -f ofcp.o ofcp

install:
	install -d $(DESTDIR)/$(BINDIR)
	install -m 755 ofcp $(DESTDIR)/$(BINDIR)
