SUMMARY = "Example Copy Applications for Openfiles"
SECTION = "console/network"
DEPENDS = "openfiles krb5 mbedtls"
HOMEPAGE = "http://www.connectedway.com/"
LICENSE = "BSD-4"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = " \
    file://ofcp.c \
    file://Makefile \
"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS} -I/usr/include/openfiles -L/usr/lib/openfiles'"

do_install () {
    oe_runmake install DESTDIR=${D} BINDIR=${bindir}/openfiles
}

FILES_${PN} = " \
    ${bindir}/openfiles/ofcp \
"    
