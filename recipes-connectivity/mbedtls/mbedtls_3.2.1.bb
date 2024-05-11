SUMMARY = "Lightweight crypto and SSL/TLS library"
DESCRIPTION = "mbedtls is a lean open source crypto library          \
for providing SSL and TLS support in your programs. It offers        \
an intuitive API and documented header files, so you can actually    \
understand what the code does. It features:                          \
                                                                     \
 - Symmetric algorithms, like AES, Blowfish, Triple-DES, DES, ARC4,  \
   Camellia and XTEA                                                 \
 - Hash algorithms, like SHA-1, SHA-2, RIPEMD-160 and MD5            \
 - Entropy pool and random generators, like CTR-DRBG and HMAC-DRBG   \
 - Public key algorithms, like RSA, Elliptic Curves, Diffie-Hellman, \
   ECDSA and ECDH                                                    \
 - SSL v3 and TLS 1.0, 1.1 and 1.2                                   \
 - Abstraction layers for ciphers, hashes, public key operations,    \
   platform abstraction and threading                                \
"

HOMEPAGE = "https://polarssl.org"
BUGTRACKER = "https://github.com/polarssl/polarssl/issues"

S = "${WORKDIR}/git"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SECTION = "libdevel"

SRC_URI = "git://github.com/Mbed-TLS/mbedtls.git;protocol=https;branch=main;tag=v${PV}"

#SRC_URI[md5sum] = "a6ed92fc377ef60f7c24d42b900e0dad"
#SRC_URI[sha256sum] = "f5beb43e850283915e3e0f8d37495eade3bfb5beedfb61e7b8da70d4c68edb82"
#SRCREV = "869298bffeea13b205343361b7a7daf2b210e33d"

DEPENDS = "openssl python3"
RDEPENDS_${PN} += "libcrypto"
PROVIDES += "polarssl"
RPROVIDES_${PN} = "polarssl"
EXTRA_OECMAKE = " \
    -DUSE_SHARED_POLARSSL_LIBRARY=on \
    -DUSE_SHARED_MBEDTLS_LIBRARY=on \
    -DLIB_INSTALL_DIR=${baselib} \
    -DENABLE_TESTING=off \
"

inherit cmake

do_install:append() {
  rm -rf ${D}/usr/cmake
}