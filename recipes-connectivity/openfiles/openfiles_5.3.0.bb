SECTION = "console/network"

S = "${WORKDIR}/git"
LICENSE = "CC-BY-ND-3.0 & Proprietary"
LIC_FILES_CHKSUM = "file://${S}/NOTICE;md5=c902fa3f368adf19a134813000affbe5 \
                    file://${COREBASE}/meta/files/common-licenses/CC-BY-ND-3.0;md5=009338acda935b3c3a3255af957e6c14 \
                    file://${COREBASE}/meta/files/common-licenses/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28 "

SRC_URI = "git://github.com/connectedway/openfiles.git;protocol=https;branch=5.3"
SRCREV = "${AUTOREV}"

OF_TYPE ?= "base"
OVERRIDES:append = ":${OF_TYPE}"

DEPENDS = " \
    make-native \
"

DEPENDS:append:smb = " \
    krb5 \
    openssl \
"

RDEPENDS_${PN}:smb = " \
    krb5 \
    openssl \
"

PACKAGES = "${PN} ${PN}-dev ${PN}-test ${PN}-dbg ${PN}-staticdev"

inherit cmake

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
"

EXTRA_OECMAKE:append:smb = " \
    -DOPENFILE_NAMING=./build/naming.cfg \
    -DOPENFILE_PLATFORM=./build/linux-platform.cfg \
    -DOPENFILE_BEHAVIOR=./build/linux-behavior.cfg \
    -DOPENFILE_SIZING=./build/sizing.cfg \
    -DOPENFILE_DEBUG=./build/debug.cfg \
    -DOPENFILE_SMB=./build/smbclient.cfg \
    -DOPENFILE_CIPHER=./build/openssl.cfg \
    -DOPENFILE_JNI=./build/nojni.cfg \
    -DSMB_CONFIG=./configs/default.cfg \
    -DSMB_CONFIG1=./configs/deprecated.cfg \
"

EXTRA_OECMAKE:append:base = " \
    -DOPENFILE_NAMING=./build/naming.cfg \
    -DOPENFILE_PLATFORM=./build/linux-platform.cfg \
    -DOPENFILE_BEHAVIOR=./build/linux-behavior.cfg \
    -DOPENFILE_SIZING=./build/sizing.cfg \
    -DOPENFILE_DEBUG=./build/nodebug.cfg \
    -DOPENFILE_SMB=./build/nosmb.cfg \
    -DOPENFILE_CIPHER=./build/openssl.cfg \
    -DOPENFILE_JNI=./build/nojni.cfg \
"

do_unpack[network] = "1"
do_configure[network] = "1"

python do_unpack:append() {

    from bb.fetch2 import runfetchcmd

    s=d.getVar("S")
    # basecmd = d.getVar("FETCHCMD_git") or "git -c core.fsyncobjectfiles=0"
    # basecmd = d.getVar("FETCHCMD_git") or "git"
    basecmd = d.getVar("FETCHCMD_git") or "git -c core.fsync=objects,derived-metadata,reference" 
    runfetchcmd(f"{basecmd} submodule update --init of_core", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_core_cheap", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_core_binheap", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_core_linux", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_core_fs_bookmarks", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_core_fs_linux", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_core_fs_pipe", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init Unity", d, quiet=True, workdir=s)
}  

python do_unpack:append:smb() {
    from bb.fetch2 import runfetchcmd

    s=d.getVar("S")
    basecmd = d.getVar("FETCHCMD_git") or "git -c core.fsync=objects,derived-metadata,reference"
    runfetchcmd(f"{basecmd} submodule update --init of_smb", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_smb_fs", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_smb_client", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_smb_browser", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_security", d, quiet=True, workdir=s)
    runfetchcmd(f"{basecmd} submodule update --init of_netbios", d, quiet=True, workdir=s)
}  

do_install:append() {
   install -d ${D}/${sysconfdir}		    
   install -m 0644 ${S}/configs/linux_nodebug-smbclient.xml ${D}/${sysconfdir}/openfiles.xml
}

FILES_${PN} = " \
    /usr/local/lib64/libof_core_shared.so.1.0.1 \
    /usr/local/lib64/libof_core_shared.so.1 \    
    ${sysconfdir}/openfiles.xml \
"

FILES_${PN}:append:smb = " \
    /usr/local/lib64/libof_smb_shared.so.1.0.1 \
    /usr/local/lib64/libof_smb_shared.so.1 \
    /usr/local/lib64/libof_netbios_shared.so.1.0.1 \
    /usr/local/lib64/libof_netbios_shared.so.1 \
"

FILES_${PN}-test = " \
    /usr/local/bin/openfiles/test_timer \
    /usr/local/bin/openfiles/test_iovec \
    /usr/local/bin/openfiles/test_perf \
    /usr/local/bin/openfiles/test_fs_linux \
    /usr/local/bin/openfiles/test_waitq \
    /usr/local/bin/openfiles/test_thread \
    /usr/local/bin/openfiles/test_path \
    /usr/local/bin/openfiles/test_event \
    /usr/local/bin/openfiles/test_dg \
    /usr/local/bin/openfiles/test_stream \
    /usr/local/bin/openfiles/test_all \
"

FILES_${PN}-test:append:smb = " \
    /usr/local/bin/openfiles/test_fs_smb \
    /usr/local/bin/openfiles/test_name \
"

FILES_${PN}-dev = " \
    /usr/local/lib/libof_core_shared.so \
    /usr/local/include/ofc \
"

FILES_${PN}-dev:append:smb = " \
    /usr/local/lib64/libof_smb_shared.so \
    /usr/local/lib64/libof_netbios.so \
"

FILES_${PN}-staticdev = " \
    /usr/local/lib64/libof_core_static.a \
"

FILES_${PN}-staticdev:append:smb = " \
    /usr/local/lib64/libof_smb_static.a \
    /usr/local/lib64/libof_netbios_static.a \
"

FILES_${PN}-dbg = " \
    /usr/src \
    /usr/local/bin/openfiles/.debug \
    /usr/local/lib64/.debug \
"
