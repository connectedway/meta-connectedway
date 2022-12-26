SUMMARY = "Example Copy Applications for Openfiles"
SECTION = "console/network"
DEPENDS = "openfiles krb5 mbedtls"
HOMEPAGE = "http://www.connectedway.com/"
LICENSE = "BSD"

SRC_URI = " \
    file://COPYING \
    file://ofcp.c \
    file://Makefile \
"

S = "${WORKDIR}"

LIC_FILES_CHKSUM = "file://COPYING;md5=624d9e67e8ac41a78f6b6c2c55a83a2b"

do_install () {
    oe_runmake install DESTDIR=${D} BINDIR=${bindir}/openfiles
}

FILES_${PN} = " \
    ${bindir}/openfiles/ofcp \
"    
