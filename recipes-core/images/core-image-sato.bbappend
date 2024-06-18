
OVERRIDES:append = ":${OF_TYPE}"

IMAGE_INSTALL:append = " \
    openfiles \
    openfiles-test \
"

IMAGE_INSTALL:append:smb = " \
    smbcp \
    krb5-user \
"
