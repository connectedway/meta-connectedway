SECTION = "console/network"

S = "${WORKDIR}/git"
LICENSE = "CC-BY-ND-3.0 & Proprietary"
LIC_FILES_CHKSUM = "file://${S}/NOTICE;md5=c902fa3f368adf19a134813000affbe5 \
                    file://${COREBASE}/meta/files/common-licenses/CC-BY-ND-3.0;md5=009338acda935b3c3a3255af957e6c14 "

SRC_URI = "gitsm://git@github.com/connectedway/openfiles.git;protocol=ssh;branch=main"
SRCREV = "${AUTOREV}"

DEPENDS += " \
    make-native \
"

RDEPENDS_${PN} = " \
"

PACKAGES = "${PN} ${PN}-dev ${PN}-test ${PN}-dbg ${PN}-staticdev"

inherit cmake

#EXTRA_OECMAKE = " \
#    -DCMAKE_BUILD_TYPE=Release \
#    -DCMAKE_MAKE_PROGRAM=${TMPDIR}/hosttools/make \
#"

do_install_append() {
   install -d ${D}/${sysconfdir}		    
   install -m 0644 ${S}/configs/linux_debug.xml ${D}/${sysconfdir}/openfiles.xml
}

FILES_${PN} = " \
    /usr/lib/libof_core_shared.so.1.0.1 \
    /usr/lib/libof_core_shared.so.1 \    
    ${sysconfdir}/openfiles.xml \
"

FILES_${PN}-test = " \
    /usr/bin/openfiles/test_timer \
    /usr/bin/openfiles/test_iovec \
    /usr/bin/openfiles/test_perf \
    /usr/bin/openfiles/test_fs_linux \
    /usr/bin/openfiles/test_waitq \
    /usr/bin/openfiles/test_thread \
    /usr/bin/openfiles/test_path \
    /usr/bin/openfiles/test_event \
    /usr/bin/openfiles/test_dg \
    /usr/bin/openfiles/test_stream \
    /usr/bin/openfiles/test_all \
"

FILES_${PN}-dev = " \
    /usr/lib/libof_core_shared.so \
    /usr/include/ofc \
"

FILES_${PN}-staticdev = " \
    /usr/lib/libof_core_static.a \
"

FILES_${PN}-dbg = " \
    /usr/src \
    /usr/bin/openfiles/.debug \
    /usr/lib/.debug \
"
