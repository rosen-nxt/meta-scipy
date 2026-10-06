SUMMARY = "SciPy: Scientific Library for Python"
HOMEPAGE = "https://www.scipy.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5f477c3073ea2d02a70a764319f5f873"

inherit pkgconfig pypi python_mesonpy

SRC_URI += "file://0001-scipy-1.13.1-use-unversioned-numpy.patch"
SRC_URI[sha256sum] = "095a87a0312b08dfd6a6155cbbd310a8c51800fc931b8c0b84003014b874ed3c"

DEPENDS += " \
	libgfortran \
	python3-numpy-native \
	python3-pybind11-native \
	python3-pythran-native \
	python3-ply-native \
"

DEPENDS:append:class-target = " \
	python3-numpy \
	python3-pybind11 \
	python3-pythran \
"

RDEPENDS:${PN} += " \
	python3-numpy \
"

PACKAGECONFIG ?= "openblas"

PACKAGECONFIG[openblas] = "-Dblas=openblas -Dlapack=openblas,,openblas,openblas,,lapack"
PACKAGECONFIG[lapack] = "-Dblas=lapack -Dlapack=lapack,,lapack,lapack,,openblas"
PACKAGECONFIG[f77] = "-Duse-g77-abi=true,,,"

CLEANBROKEN = "1"

do_write_config:append:class-target() {
	# numpy and pythran static libs must fetched from target sysroot site-packages, not sysroot-native
	# https://docs.scipy.org/doc/scipy-1.13.1/building/cross_compilation.html
	cat >${WORKDIR}/meson-${PN}.cross <<EOF
[constants]
sitepkg = '${STAGING_DIR_HOST}${PYTHON_SITEPACKAGES_DIR}/'

[properties]
numpy-include-dir = sitepkg + 'numpy/core/include'
pythran-include-dir = sitepkg + 'pythran'
EOF
}

MESON_CROSS_FILE:append:class-target = " --cross-file ${WORKDIR}/meson-${PN}.cross"

# removes compile warnings about unsupported flags when using poky
FC:remove = "${SECURITY_STRINGFORMAT}"

F90:class-native = "${FC}"
F90:class-target = "${TARGET_PREFIX}gfortran"
export F90

export F77 = "${TARGET_PREFIX}gfortran"

BBCLASSEXTEND = "native nativesdk"
