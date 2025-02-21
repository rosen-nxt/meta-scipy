DESCRIPTION = "OpenBLAS is an optimized BLAS library based on GotoBLAS2 1.13 BSD version."
HOMEPAGE = "http://www.openblas.net/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5adf4792c949a00013ce25d476a2abc0"

inherit siteinfo

RDEPENDS:${PN} += "libgomp"

SRCREV = "8795fc7985635de1ecf674b87e2008a15097ffab"
SRC_URI = "git://github.com/OpenMathLib/OpenBLAS.git;protocol=https;branch=release-0.3.0"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "lapack"
# LAPACK requires libgfortran
PACKAGECONFIG[lapack] = ",,libgfortran"
PACKAGECONFIG[lapacke] = ",,libgfortran"
PACKAGECONFIG[openmp] = "USE_OPENMP=1,USE_OPENMP=0,libgomp"

OPENBLAS_TARGET ??= "GENERIC"
OPENBLAS_TARGET:x86 ?= "ATOM"
OPENBLAS_TARGET:arm ?= "ARMV7"
OPENBLAS_TARGET:aarch64 ?= "ARMV8"

RPROVIDES:${PN} = "${@bb.utils.filter('PACKAGECONFIG', 'lapack', d)}"

# ref: http://www.openmathlib.org/OpenBLAS/docs/build_system/
EXTRA_OEMAKE += " \
	TARGET=${OPENBLAS_TARGET} \
	BINARY=${SITEINFO_BITS} \
	${@bb.utils.contains('PACKAGECONFIG', 'lapack', '', 'NO_LAPACK=1', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'lapacke', '', 'NO_LAPACKE=1', d)} \
	USE_OPENMP=${@bb.utils.contains('PACKAGECONFIG', 'openmp', '1', '0', d)} \
	HOSTCC='${BUILD_CC}' \
	CC='${CC}' \
	PREFIX=${exec_prefix} \
	CROSS_SUFFIX=${HOST_PREFIX} \
	DESTDIR=${D} \
	NUM_THREADS=64 \
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

BBCLASSEXTEND = "nativesdk"
