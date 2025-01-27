SUMMARY = "SciPy: Scientific Library for Python"
HOMEPAGE = "https://www.scipy.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5f477c3073ea2d02a70a764319f5f873"

inherit pkgconfig pypi python_mesonpy

SRC_URI += "file://0001-scipy-1.14.1-use-unversioned-numpy.patch"
SRC_URI[sha256sum] = "5a275584e726026a5699459aa72f828a610821006228e841b94275c4a7c08417"

DEPENDS += " \
	python3-numpy-native \
	python3-pybind11-native \
	python3-pythran-native \
	python3-gast-native \
	python3-beniget-native \
	python3-ply-native \
"

DEPENDS:append:class-target = " \
	python3-numpy \
"

RDEPENDS:${PN} += " \
	python3-numpy \
"

PACKAGECONFIG ?= "lapack pythran"

PACKAGECONFIG[openblas] = "-Dblas=openblas -Dlapack=openblas,,openblas,openblas"
PACKAGECONFIG[lapack] = "-Dblas=lapack -Dlapack=lapack,,lapack,lapack"
PACKAGECONFIG[f77] = "-Duse-g77-abi=true,,,"
PACKAGECONFIG[pythran] = "-Duse-pythran=true,-Duse-pythran=false,python3-pythran-native,python3-pythran"

CLEANBROKEN = "1"

export LAPACK = "${STAGING_LIBDIR}"
export BLAS = "${STAGING_LIBDIR}"

F90:class-native = "${FC}"
F90:class-target = "${TARGET_PREFIX}gfortran"
export F90

export F77 = "${TARGET_PREFIX}gfortran"

BBCLASSEXTEND = "native nativesdk"
