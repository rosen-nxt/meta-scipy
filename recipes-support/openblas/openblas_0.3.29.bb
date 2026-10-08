DESCRIPTION = "OpenBLAS is an optimized BLAS library based on GotoBLAS2 1.13 BSD version."
HOMEPAGE = "http://www.openblas.net/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5adf4792c949a00013ce25d476a2abc0"

inherit siteinfo

SRCREV = "8795fc7985635de1ecf674b87e2008a15097ffab"
SRC_URI = "git://github.com/OpenMathLib/OpenBLAS.git;protocol=https;branch=release-0.3.0"

S = "${WORKDIR}/git"

# expected defaults: lapack(e) from fortran sources (scipy needs fortran anyways), pthread
PACKAGECONFIG ??= "lapack lapacke fortran"

# LAPACK *can* be built from fortran sources, or use an (older) provided C-translated fallback
# Both will provide the LAPACK (fortran) ABI.
# ref: https://www.openmathlib.org/OpenBLAS/docs/install
PACKAGECONFIG[fortran] = "NOFORTRAN=0,NOFORTRAN=1,libgfortran"
PACKAGECONFIG[lapack] = "NO_LAPACK=0,NO_LAPACK=1"
PACKAGECONFIG[lapacke] = "NO_LAPACKE=0,NO_LAPACKE=1"
PACKAGECONFIG[openmp] = "USE_OPENMP=1,USE_OPENMP=0,gcc-runtime,libgomp"
PACKAGECONFIG[ilp64] = "INTERFACE64=1,INTERFACE64=0"

OPENBLAS_TARGET ??= "GENERIC"
OPENBLAS_TARGET:x86 ?= "ATOM"
OPENBLAS_TARGET:arm ?= "ARMV7"
OPENBLAS_TARGET:aarch64 ?= "ARMV8"

RPROVIDES:${PN} = "${@bb.utils.filter('PACKAGECONFIG', 'lapack', d)}"

# ref: http://www.openmathlib.org/OpenBLAS/docs/build_system/
EXTRA_OEMAKE += " \
	TARGET=${OPENBLAS_TARGET} \
	BINARY=${SITEINFO_BITS} \
	HOSTCC='${BUILD_CC}' \
	CC='${CC}' \
	FC='${FC}' \
	PREFIX=${exec_prefix} \
	CROSS_SUFFIX=${HOST_PREFIX} \
	DESTDIR=${D} \
	NUM_THREADS=64 \
	${PACKAGECONFIG_CONFARGS} \
"

do_compile() {
	oe_runmake libs shared
}

do_install() {
	oe_runmake install
	rmdir ${D}${bindir}
}

FILES:${PN}     = "${libdir}/*"
FILES:${PN}-dev = "${includedir} ${libdir}/lib${PN}.so ${libdir}/pkgconfig ${libdir}/cmake"

# removes compile warnings about unsupported -W flags when using poky
FC:remove = "${SECURITY_STRINGFORMAT}"

BBCLASSEXTEND = "nativesdk"
