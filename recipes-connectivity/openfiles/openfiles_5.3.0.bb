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
    -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
    -DOPENFILE_NAMING=./build/naming.cfg \
    -DOPENFILE_PLATFORM=./build/linux-platform.cfg \
    -DOPENFILE_BEHAVIOR=./build/linux-behavior.cfg \
    -DOPENFILE_SIZING=./build/sizing.cfg \
    -DOPENFILE_DEBUG=./build/nodebug.cfg \
    -DOPENFILE_SMB=./build/smbclient.cfg \
    -DOPENFILE_CIPHER=./build/openssl.cfg \
    -DOPENFILE_JNI=./build/nojni.cfg \
    -DSMB_CONFIG=./configs/default.cfg \
    -DSMB_CONFIG1=./configs/deprecated.cfg \
"

EXTRA_OECMAKE:append:base = " \
    -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
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
   install -m 0644 ${S}/configs/linux-nodebug-smbclient.xml ${D}/${sysconfdir}/openfiles.xml
   install -d ${D}/${sysconfdir}/profile.d
   install -m 0755 ${S}/scripts/openfiles_path.sh ${D}/${sysconfdir}/profile.d
   install -d ${D}/${ROOT_HOME}
   install -m 0755 ${S}/scripts/krb5.conf ${D}/${ROOT_HOME}/krb5.conf
   install -m 0755 ${S}/scripts/resolv.conf ${D}/${ROOT_HOME}/resolv.conf
}

FILES:${PN} = " \
    /usr/lib/libof_core_shared.so.1.0.1 \
    /usr/lib/libof_core_shared.so.1 \    
    ${sysconfdir}/openfiles.xml \
    ${sysconfdir}/profile.d/openfiles_path.sh \
    ${ROOT_HOME}/resolv.conf \
    ${ROOT_HOME}/krb5.conf \
"

FILES:${PN}:append:smb = " \
    /usr/lib/libof_smb_shared.so.1.0.1 \
    /usr/lib/libof_smb_shared.so.1 \
    /usr/lib/libof_netbios_shared.so.1.0.1 \
    /usr/lib/libof_netbios_shared.so.1 \
"

FILES:${PN}-test = " \
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

FILES:${PN}-test:append:smb = " \
    /usr/bin/openfiles/test_fs_smb \
    /usr/bin/openfiles/test_name \
"

FILES:${PN}-dev = " \
    /usr/lib/libof_core_shared.so \
    /usr/include/ofc \
"

FILES:${PN}-dev:append:smb = " \
    /usr/lib/libof_smb_shared.so \
    /usr/lib/libof_netbios_shared.so \
    /usr/include/of_smb \
"

FILES:${PN}-staticdev = " \
    /usr/lib/libof_core_static.a \
"

FILES:${PN}-staticdev:append:smb = " \
    /usr/lib/libof_smb_static.a \
    /usr/lib/libof_netbios_static.a \
"

FILES:${PN}-dbg = " \
    /usr/src \
    /usr/bin/openfiles/.debug \
    /usr/lib/.debug \
"
