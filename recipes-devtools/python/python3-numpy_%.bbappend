# switch to more flexible meson build system
inherit pkgconfig python_mesonpy

# use openblas for target builds (there is no openblas-native due to missing native gfortran)
DEPENDS:append:class-target = " openblas"
EXTRA_OEMESON:append:class-target = " -Dblas=openblas -Dlapack=openblas -Dallow-noblas=false"
