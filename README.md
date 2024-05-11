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
the default cmake provided by hardknott and dunfell is 3.19.

smbcp recipe:  This is an example application that utilizes the openfiles
smb v2/v3 client.  It depends on the the openfiles package as well as krb5
and openssl.  It is intended that this package will provide an example of
how embedded linux applications can utilize openfiles to access remote
directories using SMBv2/v3.

A script in scripts/runqemu.openfiles which will bring up a qemu VM
with the right parameters.  

Configuration files in conf that will setup the build environment.

# Supported Distros

There are two supported yocto distributions of poky:
- pyro
- dunfell
- hardknott

Others are likely supported but not currently qualified.

If you are checking out a poky branch based on the pyro release and
not using the repo tool (for example, pyro-20.04 in the connectedway
poky fork), you should checkout the pyro branch of meta-connectedway.
If you are checking out a poky branch based on the hardknott release
(can be direct from git.yoctoproject.org or from the connectedway
fork) you should set the meta-connectedway branch to hardknott.

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

# Maintenance

Please see the MAINTAINERS file for information on contacting the
maintainers of this layer, as well as instructions for submitting patches. 
