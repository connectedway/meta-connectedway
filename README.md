meta-connectedway
=================

# Introduction

This layer provides metadata that extends the base
poky distroy and openembedded layer with support for Connected Way's
Open Files.

This layer is qualified against the pyro, dunfell, and hardknott
releases of Yocto.

# Contents

openfiles recipe for v5.3.0.  This will build openfiles for a yocto distro.
The following packages can be deployed:
- openfiles: Deploys libof_smb and libof_core shared libraries along with
/etc/openfiles.xml
- openfiles-test: Deploys test binaries on the target.
- openfiles-dev: Deploys files needed to build dependent recipes (smbcp)
- openfiles-staticdev: Deploys static libraries to build dependent
recipes (not currently used)
- openfiles-dbg: Deploys debug sources, binaries, and libraries

cmake recipe for version 3.22.3:  Openfiles depends on cmake version > 3.20.
the default cmake provided by hardknott 3.19 and dunfell is 3.16.

smbcp recipe:  This is an example application that utilizes the openfiles
smb v2/v3 client.  It depends on the the openfiles package as well as krb5
and openssl.  It is intended that this package will provide an example of
how embedded linux applications can utilize openfiles to access remote
directories using SMBv2/v3.

A script in scripts/runqemu.openfiles which will bring up a qemu VM
with the right parameters.  

Configuration files in conf that will setup the build environment.

# Supported Distros

There are three supported yocto distributions of poky:

- pyro
- dunfell
- hardknott

Currently, we our head of integration is dunfell.  By head of integration,
we mean that although the other releases had been supported with
the 5.1 release of OpenFiles, Dunfell has been integrated with 5.3.  Other
branches likely need to be refreshed.  If other branches are required,
please request support with Connected Way.

# Ubuntu 20.04 Support in Pyro

The changes we made to the Pyro distribution in order to support a poky build
under Ubuntu 20.04 can be seen by running the git command:

```
git diff pyro pyro-20.04
```

Specifically, these chages are:
- fixed a python compatibility issue in buildstats.bbclass
- fixed compatibility issues in glib-2.0
- fixed compatibility issues in glibc
- fixed compatibility issues in bison
- fixed compatibility issue in elfutils
- fixed compatibility issue i m4
- fixed compatibility issues with make
- fixed compatibility issues with perl
- fixed compatibility issues with qemu
- fixed compatibility issues with libgpg

These changes affect only the build of native tools.  They do not result in
target system changes.  The changes are only needed for the pyro distro.
The hardknott distro is compatible with Ubuntu 20.04.

# Qemu Workaround for arm64.

The qemu support for the aarch64 platform is deficient for applications that
utilize kernel random number support (the kernel getrandom syscall).  This
causes the linux guest to hang when initializing the random number generator.
Openfiles utilizes mbedtls for crypto support.  mbedtls uses the getrandom
syscall and so would end up hanging.  Patching qemu to support the getrandom
syscall was non-trivial.  We worked around this issue by overriding the
default entropy initialization of mbedtls within the openfiles security layer.
This will only affect targets named qemuarm64.  

# MbedTls

OpenFiles has been integrate with mbedtls version 3.2.1.  Dunfell has
support or mbedtls 2.16.6.  The meta-connectedway layer provides support
for mbedtls 3.2.1.  The default cipher stack for OpenFiles is openssl
although we support both mbedtls and gnutls support as well.  Dunfell is
integrated with openssl 1.1.1.  We support that as well as openssl 3.3.1
which is integrated in later versions of Yocto.

# OpenEmbedded

OpenFiles depends on the openembedded layers primarily for kerberos
although we can utilize python for testing.  

# Integration with Yocto

Integration with Yocto is straightforward.

Peform a clone of meta-connectedway:

```
$ cd poky
$ git clone https://github.com/connectedway/meta-connectedway.git
```

If you do not have meta-openembedded already in your environment,
clone meta-openembedded:

```
$ git clone git://git.openembedded.org/meta-openembedded
```

Set the branches

```
$ cd meta-connectedway
$ git checkout dunfell
$
$ cd ../meta-openembedded
$ git checkout dunfell
```

Set up a build environment

```
$ cd ..
$ source oe-init-build-env
```

Update bblayers

```
$ <your editor> conf/bblayers.conf
```

Add the following layers to your bblayers.conf.  Replace <layer-dir>
with the path to your layer directory.

```
  <layer-dir>/meta-openembedded/meta-oe \
  <layer-dir>/meta-openembedded/meta-networking \
  <layer-dir>/meta-openembedded/meta-python \
  <layer-dir>/meta-connectedway \
```

Update your local.conf

```
$ <your editor> conf/local.conf
```

Add the following near the end of the configuration file

```
OF_TYPE = "smb"
```

That's it.  Now you can begin a build.

```
bitbake <image>
```

We provide pre-integration with core-image-minimal and core-image-sato.

If you are building some other image, add the follow to your
local.conf

```
IMAGE_INSTALL:append = " \
    openfiles \
    smbcp \
"
```

If you have not integrated with kerberos in your own build and
you wish to have access to kinit, you will need to also include:

```
IMAGE_INSTALL:append = " \
    krb5-user \
"
```

# NOTES

The OpenFiles Recipe installs a krb5.conf file that is hard coded and
resides in the openfiles git repo in the path `scripts/krb5.conf`.  This
file will be installed in the root home directory on the target.  You
will need to edit this file and install in /etc/krb5.conf before you run
your system.

In order to use kerberos, you need to have your DNS name resolution
resolving to the DNS that is used by your active directory domain.  We
provided a `resolv.conf` in the openfiles repository in `scripts/resolv.conf`
This will be installed in the root home directory on the target. You
may have other ways to update the dns configuration on the target.
The current process we use is upon booting a new image, we perform
the following:

```
$ cp /home/root/krb5.conf /etc/krb5.conf
$ cp /home/root/resolv.conf /etc/resolv.conf
```

# Maintenance

Please see the MAINTAINERS file for information on contacting the
maintainers of this layer, as well as instructions for submitting patches. 
