include openfiles.inc

DEPENDS += " \
    krb5 \
    mbedtls \
"

RDEPENDS_${PN} = " \
    krb5 \
    mbedtls \
"

EXTRA_OECMAKE += " \
    -DOPENFILE_CONFIG=./configs/yocto-smbfs \
"

do_install_append() {
   install -d ${D}/${sysconfdir}		    
   install -m 0644 ${S}/configs/linux_debug.xml ${D}/${sysconfdir}/openfiles.xml
}

FILES_${PN} += " \
    /usr/lib/libof_smb_shared.so.1.0.1 \
    /usr/lib/libof_smb_shared.so.1 \
"

FILES_${PN}-test += " \
    /usr/bin/openfiles/test_fs_smb \
"

FILES_${PN}-dev += " \
    /usr/lib/libof_smb_shared.so \
"

FILES_${PN}-staticdev += " \
    /usr/lib/libof_smb_static.a \
"

