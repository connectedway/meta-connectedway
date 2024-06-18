meta-connectedway
=================

# Introduction

This layer provides metadata that extends the base
poky distroy and openembedded layer with support for Connected Way's
Open Files.

This layer is qualified against the pyro, dunfell, hardknott, and scarthgap
releases of Yocto.

# Contents

mbedtls recipe for v3.2.1.  Openfiles depends on v3.2.1 for CCM support.
The default version of mbedtls supported by hardknott is v2.25.0.  Therefore
we provide our own recipe.

openfiles recipe for v5.1.1.  This will build openfiles for a yocto distro.
The following packages can be deployed:
- openfiles: Deploys libof_smb and libof_core shared libraries along with
/etc/openfiles.xml
- openfiles-test: Deploys test binaries on the target.
- openfiles-dev: Deploys files needed to build dependent recipes (smbcp)
- openfiles-staticdev: Deploys static libraries to build dependent
recipes (not currently used)
- openfiles-dbg: Deploys debug sources, binaries, and libraries

smbcp recipe:  This is an example application that utilizes the openfiles
smb v2/v3 client.  It depends on the the openfiles package as well as krb5
and mbedtls.  It is intended that this package will provide an example of
how embedded linux applications can utilize openfiles to access remote
directories using SMBv2/v3.

A script in scripts/runqemu.openfiles which will bring up a qemu VM
with the right parameters.  

Configuration files in conf that will setup the build environment.

# Supported Distros

There are two supported yocto distributions of poky:
- pyro
- hardknott
- dunfell
- scarthgap

Others are likely supported but not currently qualified.

Currently, we our head of integration is scarthgap.  By head of integration,
we mean that although the other releases had been supported with
the 5.3 release of OpenFiles, Scarthgap has been integrated with 5.3.  Other
branches may need to be refreshed.  If other branches are required,
please request support with Connected Way.

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
$ git checkout scarthgap

$ cd ../meta-openembedded
$ git checkout scarthgap
```

Set up a build environment

```
$ cd ..
$ source oe-init-build-env build-scarthgap
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
    openfiles-test \
"
```

If you have not integrated with kerberos in your own build and
you wish to have access to kinit, you will need to also include
krb5-user.  You may also wish to include the Connected Way smbcp
utility.:

```
IMAGE_INSTALL:append:smb = " \
    smbcp \
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
