SUMMARY = "Example Copy Applications for Openfiles"
SECTION = "console/network"
DEPENDS = "openfiles krb5 openssl"
HOMEPAGE = "http://www.connectedway.com/"
LICENSE = "CC-BY-ND-3.0 & Proprietary"

S = "${WORKDIR}/git"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=624d9e67e8ac41a78f6b6c2c55a83a2b"

SRC_URI = "git://github.com/connectedway/smbcp.git;protocol=https;branch=5.3"
SRCREV = "${AUTOREV}"

EXTRA_OEMAKE += "'CC=${CC}'"

#EXTRA_OEMAKE += "'CFLAGS=${CFLAGS} -I${STAGING_INCDIR}' \
#                 'LDFLAGS=${LDFLAGS} -L${STAGING_LIBDIR} -L${STAGING_BASELIBDIR} -L${STAGING_DIR}' \
#                 'CC=${CC}"

do_install () {
    oe_runmake install DESTDIR=${D} BINDIR=${bindir}/openfiles ROOT=${ROOT_HOME}
}

FILES:${PN} = " \
    ${bindir}/openfiles/smbcp \
    ${bindir}/openfiles/smbrm \
    ${bindir}/openfiles/smbfree \
    ${bindir}/openfiles/smbls \
    ${ROOT_HOME}/test/conftest.py \
    ${ROOT_HOME}/test/test_dfs.py \
    ${ROOT_HOME}/test/dfs_iptables.py \
"    
