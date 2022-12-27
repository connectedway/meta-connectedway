meta-connectedway
=================

# Introduction

This layer provides metadata that allows integration of Connected Way's
products with the Yocto Project build system.

This layer is qualified against the pyro and hardknott releases of Yocto.

# Contents

mbedtls recipe for v3.2.1.  Openfiles depends on v3.2.1 for CCM support.
the default version of mbedtls supported by hardknott is v2.25.0.  Therefore
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

cmake recipe for version 3.22.3:  Openfiles depends on cmake version > 3.20.
the default cmake provided by hardknott is 3.19.

smbcp recipe:  This is an example application that utilizes the openfiles
smb v2/v3 client.  It depends on the the openfiles package as well as krb5
and mbedtls.  It is intended that this package will provide an example of
how embedded linux applications can utilize openfiles to access remote
directories using SMBv2/v3.

A script in scripts/runqemu.openfiles which will bring up a qemu VM
with the right parameters.  

# Supported Distros

There are two supported yocto distributions of poky:
- pyro
- hardknott

If you are checking out a poky branch based on the pyro release (for
example, pyro-20.04 in the connectedway poky fork), you should checkout
the pyro branch of meta-connectedway.  If you are checking out a poky branch
based on the hardknott release (can be direct from git.yoctoproject.org or
from the connectedway fork) you should set the meta-connectedway branch to
hardknott.

# Background

The intent of the meta-connectedway layer is to support and demonstrate
Connected Way's Open Files SMB client framework on embedded linux platforms.
Specifcally, we want to demonstrate support on Linux 4.9 and Linux 5.10
kernels.

Linux 4.9 is supported by the poky Pyro distribution and Linux 5.10 is
supported by the poky Hardknott distribution.  Connected Way has forked
the Yocto Project's poky repository branches pyro and hardknott and have
hosted this fork on github.

Our intent was also to demonstrate openfiles support on Arm64 (aarch64)
platforms.  There is no machine specific software within openfiles so the
machine architecture is somewhat arbitrary, but we use Apple Mac Book Pros
based on Apple's M1 chipset (a 64 bit Arm processor).  Because of this, we
want a Ubuntu linux host distribution that supports arm64.  The only two
Ubuntu distributions that support arm64 are 20.04 and 21.04.

Through experimentation, we discovered that the pyro distribution of poky
will not easily run on either Ubuntu 21.04 or 20.04 although with select
patches to the native recipes, the pyro distribution can be made to run on
Ubuntu 20.04.  We have integrated the required patches to the pyro distribution
to support a Ubuntu 20.04 host onto the branch pyro-20.04.  Description of
these changes will be discussed below.

We also utilize authentication and cryptography support provided by the
meta-openembedded layer.

## Ubuntu 20.04 Support in Pyro

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

## Qemu Workaround for arm64.

The qemu support for the aarch64 platform is deficient for applications that
utilize kernel random number support (the kernel getrandom syscall).  This
causes the linux guest to hang when initializing the random number generator.
Openfiles utilizes mbedtls for crypto support.  mbedtls uses the getrandom
syscall and so would end up hanging.  Patching qemu to support the getrandom
syscall was non-trivial.  We worked around this issue by overriding the
default entropy initialization of mbedtls within the openfiles security layer.
This will only affect targets named qemuarm64.  

# Deploying Openfiles in Yocto Project's Poky Distribution

Follow the instructions in [Yocto Quick Start](https://docs.yoctoproject.org/brief-yoctoprojectqs/index.html).  Instead of cloning the Yocto Project's
poky repository, clone:

```
$ mkdir yocto
$ cd yocto
$ git clone https://github.com/connectedway/poky.git
```

Then clone other required layers:

```
$ cd poky
$ git clone https://github.com/connectedway/meta-connectedway.git
$ git clone http://git.openembedded.org/meta-openembedded.git
```

Then follow the directions for the particular version of poky you are building.

## Preparing Openfiles on a pyro distribution

Within the poky repository, set the branch:

```
$ cd yocto/poky
$ git checkout pyro-20.04
```

Then checkout the pyro branch in meta-connectedway:

```
$ cd yocto/poky/meta-connectedway
$ git checkout pyro
```

Then checkout the pyro branch of meta-openembedded:

```
$ cd yocto/poky/meta-openembedded
$ git checkout pyro
```

## Configuring a Build Environment for a Pyro Distribution

Initialize the build environment

```
$ cd yocto/poky
$ source oe-init-build-env
```

Then configure the build by editing `yocto/poky/build/conf/local.conf` and
make the following changes:

1. Select the correct target MACHINE.  We use qemuarm64:
```
MACHINE ?= "qemuarm64"
```

2. Set the tmp directory for the pyro release:
```
TMPDIR = "${TOPDIR}/tmp-pyro"
```

3. Disable uninative (unsupported on Ubuntu 20.04)
```
INHERIT_remove = "uninative"
```

4. Select the 4.9 Linux kernel
```
PREFERRED_VERSION_linux-yocto = "4.9%"
```

5. Declare additional packages to install into target file system
```
IMAGE_INSTALL_append = " \
    openfiles \
    openfiles-test \
    smbcp \
"
```

## Preparing Openfiles on a hardknott distribution

Within the poky repository, set the branch:

```
$ cd yocto/poky
$ git checkout hardknott
```

Then checkout the hardknott branch in meta-connectedway:

```
$ cd yocto/poky/meta-connectedway
$ git checkout hardknott
```

Then checkout the hardknott branch of meta-openembedded:

```
$ cd yocto/poky/meta-openembedded
$ git checkout hardknott
```

## Configuring a Build Environment for a Hardknott Distribution

Initialize the build environment

```
$ cd yocto/poky
$ source oe-init-build-env
```

Then configure the build by editing `yocto/poky/build/conf/local.conf` and
make the following changes:

1. Select the correct target MACHINE.  We use qemuarm64:
```
MACHINE ?= "qemuarm64"
```

2. Set the tmp directory for the hardknott release:
```
TMPDIR = "${TOPDIR}/tmp-hardknott"
```

3. Disable uninative (unsupported on Ubuntu 20.04)
```
INHERIT_remove = "uninative"
```

4. Select the 5.10 Linux kernel
```
PREFERRED_VERSION_linux-yocto = "5.10%"
```

5. Declare additional packages to install into target file system
```
IMAGE_INSTALL_append = " \
    openfiles \
    openfiles-test \
    smbcp \
"
```

## Add Layers to Yocto's build environment

Whether building a pyro or a hardknott distro, the following layers
must be added to the builds bblayer.conf file.

Edit `yocto/poky/build/conf/bblayers.conf` and add the following:

```
  <workspace>/poky/meta-openembedded/meta-oe \
  <workspace>/poky/meta-openembedded/meta-networking \
  <workspace>/poky/meta-openembedded/meta-python \
  <workspace>/poky/meta-connectedway \
```

Where <workspace> is the location of your build.  We have been using `yocto`
in previous examples.  Also note: The bblayers.conf file will already be
populated with layers that are required for the poky distribution.

# Building A Poky Distribution With OpenFiles

If all the previous steps were successful, building a poky distribution with
openfiles should essentially be one command:

First, ensure you're build environment is set:

```
$ cd yocto/poky
$ source oe-init-build-env
```

Then issue the following command:

```
bitbake core-image-minimal
```

This will take some time.  It should complete successfully although there
may be some bitbake warnings.  If you are concerned about the warnings,
or if you run into errors during the build, please contact your support at
Connected Way for clarification.  

Once the build is complete, you can run your embedded linux image in qemu
by executing the following command:

```
../meta-connectedway/scripts/runqemu.openfiles
```

This script will start the qemu environment with arguments we have determined
provide the best experience.  Once the system boots up, you can log in with
the username root:

```
Poky (Yocto Project Reference Distro) 3.3.6 qemuarm64 /dev/ttyAMA0

qemuarm64 login: root
root@qemuarm64:~#
```

# Explore the File System

Openfiles and openfile artificts will be installed in the following locations:

- /usr/bin/openfiles/smbcp: an openfiles smb aware copy utility (see below)
- /usr/bin/openfiles/test_dg: a Datagram loopback test application
- /usr/bin/openfiles/test_event: A openfiles event test
- /usr/bin/openfiles/test_iovec: Tests of vector based messages
- /usr/bin/openfiles/test_path: A path manipulation test
- /usr/bin/openfiles/test_perf: A test of the performance measurement facility
- /usr/bin/openfiles/test_stream: A TCP loopback test application
- /usr/bin/openfiles/test_thread: A test of openfiles threading
- /usr/bin/openfiles/test_timer: A test of openfiles timers
- /usr/bin/openfiles/test_waitq: A test of openfiles wait queues.
- /usr/bin/openfiles/test_fs_linux: A test of local file I/O
- /usr/bin/openfiles/test_fs_smb: A test of remote smb file I/O
- /usr/bin/openfiles/test_all: An aggregate of all tests
- /usr/lib/libof_core_shared.so.1*: The openfiles framework
- /usr/lib/libof_smb_shared.so.1*: The openfiles smb support

NOTE: The /usr/bin/openfiles/smbcp binary is installed by the smbcp package
NOTE: The /usr/bin/openfiles/test_* are installed by the openfiles-test package

NOTE: To run test_fs_linux, you must first do:

```
$ mkdir -p /tmp/openfiles/tmp
```

NOTE: segmentation fault on exit of test applications
NOTE: segmentation fault on test_fs_smb

# smbcp Example Application

Open Files supports numerous deployment scenarios:
- statically linked RTOS deployments
- dynamically linked applications
- statically linked applications
- JNI based java applications

The test_* applications are all statically linked applications.  The smbcp
executable is a dynamically linked application.  The application itself
is built seperately from the openfiles framework and is simply linked with
the framework on the target system.

The syntax of the smbcp utility is:

```
$ smbcp [-a] <source> <destination>
```

Where -a signifies that the copy operation should be done asynchronously
using multiple overlapped buffers.  The absense of -a specifies that the
copy is done synchronously.  Only one I/O is outstanding at a time in
syncronous mode.

smbcp can copy a file from local or remote locations to a file that resides
locally or remotely.  A file specification is of the form:

```
[//username:password:domain@server/share/]path.../file
```

Where the presence of a leading `//` signifies a remote location.  The
absence of a leading `//` signifies a local location.

With a remote URL, a username, password, server, and share are all required.
while the :domain is optional and used only for active directory managed
locatoins.  Path can be any depth as long as the total file path is less than
256 bytes.  For example:

```
$ smbcp //me:secret@remote/share/subdirectory/picture.jpg ./picture.jpg
```

This will access the server named 'remote' and log in using the username
'me' and the password 'secret'.  It will implicitly mount the remote share
named 'share' and will access the file 'subdirectory/picture.jpg' relative
to the share.  It will copy the file locally to the filename picture.jpg.

The source path and destination path can be either local or remote so
copies from a remote to a local, from a local to a remote, from a local to
a local, and from a remote to a remote are all possible.

The output of a smbcp session from a remote location to a remote location is
below:

```
root@qemuarm64:~# /usr/bin/openfiles/smbcp -a //rschmitt:happy@192.168.1.206/spi
ritcloud/lizards.jpg //rschmitt:happy@192.168.1.206/spiritcloud/lizard_copy.jpg
1435756250 OpenFiles (main) 5.0 1
1435756250 Loading /etc/openfiles.xml
1435756254 Device Name: localhost
1435756257 OpenFiles SMB (main) 5.0 1
Copying //rschmitt:happy@192.168.1.206/spiritcloud/lizards.jpg to //rschmitt:happy@192.168.1.206/spiritcloud/lizard_copy.jpg: [ok]
Total Allocated Memory 616, Max Allocated Memory 794571
```

The first line is the invocation of the command.  We wish to perform an
asynchronous copy from a remote file to a remote file.  The username is
rschmitt, the password is happy, the server is specified with an IP address
(although a DNS name is acceptable).  The share is named spiritcloud.  The
file we are copying from is called lizards.jpg.  The destination is the same
user, password, remote, and share but with a target filename of
lizards_copy.jpg.  The following lines are information messages
which can be disabled.  At the completion is heap statistics.

## smbcp Implementation

The implementatoin of the smbcp application and Yocto recipe is relevant for
integrating customer applications with the Yocto based openfiles framework.

### smbcp Recipe

The smbcp recipe is located within the meta-connectdway layer at:

```
meta-connectedway/recipes-extended/smbcp/smbcp_1.0.0.bb
```

The `DEPENDS` variable declares that smbcp is dependent on the openfiles
package, krb5, and mbedtls.  This insures that those three packages will be
built and installed into the target file system along with the smbcp package.

The `SRC_URI` declares three files as part of the package: COPYING, smbcp.c,
and a Makefile.  These could be packaged as a tar file, or within a git repo
but we opted to included these files in the smbcp subdirectory of the recipe.

Most of the recipe is simply default bitbake magic.  Bitbake will automatically
treat a package that contains a Makefile as a makefile project and will
invoke that makefile to compile and link the application.  The do_install
task will explicity invoke make to install the executables in the destination
staging area defined by 'D' and into a install directory of /usr/bin/openfiles.

Lastly, we have declared the packaging of the recipe to contain the smbcp
binary within the /usr/bin/openfiles directory.

### smbcp Makefile

The makefile is a basic make.  The smbcp application consists of one object
file, smbcp.o.  The object file is compiled using bitbakes default CFLAGS
which includes a specification of the sysroot which will contain the necessary
openfiles header files.  The executable is linked with five shared libraries:
- libof_smb_shared
- libof_core_shared
- libmbedtls
- libkrb5
- libgssapi_krb5

We specify the linker option `--no-as-needed` which directs the linker to
include the libraries whether they are explicity referenced in the object
files or not.  This is needed if we wish to utilize implicit library
constructors.  As a conveniece to openfile application developers, we will
initialize the stack implicitly upon library load rather than requiring the
developer to initialize the stack within the application itself.

### smbcp Source Code

This readme will not go through line by line within the smbcp.c source file.
Rather, we'll call out relevant sections.

An openfiles application can use standard C libraries and with respect to
smbcp, we use four.  The wchar.h file is included so we can convert ASCII
characters to wide characters used as part of the openfiles API.  NOTE that
openfiles exposes an ascii API as well but wide characters is recommended.

There are six openfiles header files used by this application.  Openfiles
provides a robust set of APIs for many services.  We recommend viewing
the (openfiles api documentation)[http://www.connectedway.com/openfiles] for
a detail.  The Openfiles recipe installs a subset of the available APIs into
the yocto sysroot.  If you find that particular headers are not available in
your sysroot, Connected Way support will be glad to export them for you.

A brief description of the headers used:

- ofc/config.h - This provides constants that the openfiles framework was
built with.  This will have defines for the maximum buffer size, whether
smb I/O is supported and more.
- ofc/handle.h - This defines a generic handle type used to refer to
openfile objects.  Handles are used throughout openfiles and can refer to
files, queues, events, sockets and more.
- ofc/types.h - This defines the basic types used by openfiles.
- ofc/file.h - The API used by the openfiles file system.
- ofc/waitset.h - This allows aggregation of waitable objects.  This is used
by the asynchronous file copy to manage asynchronous completion of multiple
buffers.
- ofc/queue.h - This is a robust abstraction of a doubly linked list.  It
is purely a convenience facility but is heavily used throughout the openfiles
framework and within the smbcp application to maintain lists of buffers.

In the simple case, this is the full set of APIs needed to interact with
the openfiles framework.  The main set of APIs is contained in the header
`file.h`.  For the most part, the file API of Open Files is based on the
Windows File APIs.  

The smbcp.c source file contains code for both the asynchronous file copy
and synchronous file copy modes.  The asynchronous code supports arbitrary
queue depth of arbitrary buffer size (up to a max of OFC_MAX_IO).  If you
have questions on the operation of the asynchrous copy, please contact
Connected Way support.  The entry point to the asynchronous file copy is

```
static OFC_DWORD copy_async(OFC_CTCHAR *rfilename, OFC_CTCHAR *wfilename)
```

The synchronous file copy is much simpler.  It simply opens up the source
and destination files and then enters a loop where a buffer is read from the
read file, and written to the write file.  The loop continues till an
eof on the read file or an error on either.  The entry point of the synchronous
copy is:

```
static OFC_DWORD copy_sync(OFC_CTCHAR *rfilename, OFC_CTCHAR *wfilename)
```

The main entry point parses the arguments, converts the ascii file names
into wide character file names and calls the respective asynchronous or
synchronous entry points.

The APIs used by smbcp are:

APIs for Synchronous File Copy:

- OfcCreateFile - Create or Open a Local or Remote file.
- OfcReadFile - Read from a Local or Remote File
- OfcWriteFile - Write to a Local or Remote File
- OfcGetLasstError - Get the last error code encountered
- OfcCloseHandle - Close a Local or Remote File
- ofc_get_error_string - Convert a last error into a string

APIs for Asynchronous File Copy:

- OfcSetOverlappedOffset - Set the offset for an overlapped buffer
- OfcGetOverlappedResult - Get the result of an overlapped i/o
- OfcDestroyOverlapped - Destroy an overlapped buffer
- OfcCreateOverlapped - Create an overlapped buffer
- OfcReadFile - Read from a local or remote file
- OfcWriteFile - Write to a local or remote file
- OfcCreateFile - Create or Open a local or remote file
- OfcCloseHandle - Close a local or remote file
- OfcGetLastError - Get the last error code encountered
- ofc_waitset_add - Add an object to a list of waitable objects
- ofc_waitset_remove - Remove an object from a list of waitable objects
- ofc_waitset_destroy - Destroy the list of waitable objects
- ofc_waitset_create - Create a list of waitable objects
- ofc_waitset_wait - Wait for a buffer completion
- ofc_handle_get_app - Get a buffer association
- ofc_dequeue - Dequeue a buffer from a buffer list
- ofc_queue_create - Create a buffer list
- ofc_enqueue - Queue a buffer to a buffer list
- ofc_queue_first - Get the first item on the buffer list
- ofc_queue_next - Get the subsequent item on a buffer list
- ofc_get_error_string - Convert a last error into a string

It is clear from the list of APIs used between synchronous and asynchronous
file copy that the asynchronous mode is more complex but it can offer
considerable performance improvements

This is the full list of APIs required for either mode.  This should help in
understanding the level of effort in integrating openfiles with a customer
application.

There are a few features of Open Files which allow this simple set of APIs:

- Implicit Framework Initialization and Destruction.  No explicit
initialization required although explicit and static initialization is
supported.
- Implicit Remote Connections.  Connections to remotes do not need to be
explicitly established.  Remote URLs are examined and if the URL is to a
remote that does not have a connection, a connection will be established
and authenticated.  Connections are closed when idle.
- Implicit Share Mounting.  Mounting of a share is performed implicity when
accessing a file through the URL.  No explicit mounting or unmounting of
a share is required.  Shares are dismounted when idle.
- Integrated local and remote file APIs.  No need to be aware of whether a
file is local or remote.  The framework handles the difference internally.

# Maintenance

Please see the MAINTAINERS file for information on contacting the
maintainers of this layer, as well as instructions for submitting patches. 
