SECTION = "console/network"

S = "${WORKDIR}/git"
LICENSE = "CC-BY-ND-3.0 & Proprietary"
LIC_FILES_CHKSUM = "file://${S}/NOTICE;md5=c902fa3f368adf19a134813000affbe5 \
                    file://${COREBASE}/meta/files/common-licenses/CC-BY-ND-3.0;md5=009338acda935b3c3a3255af957e6c14 \
                    file://${COREBASE}/meta/files/common-licenses/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28 "

SRC_URI = "git://github.com/connectedway/openfiles.git;protocol=https;branch=main"
SRCREV = "${AUTOREV}"

OF_TYPE ?= "base"
OVERRIDES:append = ":${OF_TYPE}"

DEPENDS = " \
    make-native \
"

DEPENDS:append:smb = " \
    krb5 \
    mbedtls \
"

RDEPENDS_${PN}:smb = " \
    krb5 \
    mbedtls \
"

PACKAGES = "${PN} ${PN}-dev ${PN}-test ${PN}-dbg ${PN}-staticdev"

inherit cmake

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DMBEDTLS_ROOT_DIR=${STAGING_DIR_TARGET}/usr \
"

EXTRA_OECMAKE:append:smb = " \
    -DOPENFILE_CONFIG=./configs/yocto-smbfs \
"

EXTRA_OECMAKE:append:base = " \
    -DOPENFILE_CONFIG=./configs/linux \
"

python do_unpack:append() {

    from bb.fetch2 import runfetchcmd

    s=d.getVar("S")
    basecmd = d.getVar("FETCHCMD_git") or "git -c core.fsyncobjectfiles=0"
    runfetchcmd(f"{basecmd} submodule init", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_core", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_core_cheap", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_core_binheap", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_core_linux", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_core_fs_bookmarks", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_core_fs_linux", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_core_fs_pipe", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update Unity", d, quiet=True, workdir=s)
}  

python do_unpack:append:smb() {
    from bb.fetch2 import runfetchcmd

    s=d.getVar("S")
    runfetchcmd(f"{basecmd} submodule init", d, quiet=True, workdir=s)
    basecmd = d.getVar("FETCHCMD_git") or "git -c core.fsyncobjectfiles=0"
    runfetchcmd(f"{basecmd} submodule update of_smb", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_smb_fs", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_smb_client", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_smb_browser", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update of_security", d, quiet=True, workdir=s)
}  

do_install:append() {
   install -d ${D}/${sysconfdir}		    
   install -m 0644 ${S}/configs/linux_debug.xml ${D}/${sysconfdir}/openfiles.xml
}

FILES_${PN} = " \
    /usr/lib/libof_core_shared.so.1.0.1 \
    /usr/lib/libof_core_shared.so.1 \    
    ${sysconfdir}/openfiles.xml \
"

FILES_${PN}:append:smb = " \
    /usr/lib/libof_smb_shared.so.1.0.1 \
    /usr/lib/libof_smb_shared.so.1 \
"

FILES_${PN}-test = " \
    /usr/bin/openfiles/test_fs_smb \
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

FILES_${PN}-test:append:smb = " \
    /usr/bin/openfiles/test_fs_smb \
"

FILES_${PN}-dev = " \
    /usr/lib/libof_core_shared.so \
    /usr/include/ofc \
"

FILES_${PN}-dev:append:smb = " \
    /usr/lib/libof_smb_shared.so \
"

FILES_${PN}-staticdev = " \
    /usr/lib/libof_core_static.a \
"

FILES_${PN}-staticdev:append:smb = " \
    /usr/lib/libof_smb_static.a \
"

FILES_${PN}-dbg = " \
    /usr/src \
    /usr/bin/openfiles/.debug \
    /usr/lib/.debug \
"
