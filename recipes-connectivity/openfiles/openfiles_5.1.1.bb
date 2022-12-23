SECTION = "console/network"

S = "${WORKDIR}/git"
LICENSE = "CC-BY-ND-3.0 & Proprietary"
LIC_FILES_CHKSUM = "file://${S}/NOTICE;md5=c902fa3f368adf19a134813000affbe5 \
                    file://${COREBASE}/meta/files/common-licenses/CC-BY-ND-3.0;md5=009338acda935b3c3a3255af957e6c14 \
                    file://${COREBASE}/meta/files/common-licenses/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28 "

SRC_URI = "gitsm://git@github.com/connectedway/openfiles.git;protocol=ssh;branch=main"
SRCREV = "${AUTOREV}"

DEPENDS += " \
    krb5 \
    mbedtls \
    make-native \
"

RDEPENDS_${PN} = " \
    krb5 \
    mbedtls \
"

inherit cmake

EXTRA_OECMAKE = " \
    -DOPENFILE_CONFIG=./configs/linux-smbfs \
    -DCMAKE_MAKE_PROGRAM=${TMPDIR}/hosttools/make \
    -DMBEDTLS_ROOT_DIR=${STAGING_DIR_TARGET}/usr \
"

do_install_append() {
   install -d ${D}/${sysconfdir}		    
   install -m 0644 ${S}/configs/linux_debug.xml ${D}/${sysconfdir}/openfiles.xml
}

PACKAGES =+ ""

PACKAGESPLITFUNCS_prepend = ""

RDEPENDS_${PN} += ""

FILES_${PN} = " \
    /usr/bin \
    /usr/lib \
    ${sysconfdir}/openfiles.xml \
"

FILES_${PN}-base = ""

FILES_${PN}-ctdb-tests = ""

FILES_${BPN}-common = ""

FILES_${PN} += ""

FILES_${PN}-testsuite = ""

FILES_registry-tools = ""

FILES_smbclient = ""

