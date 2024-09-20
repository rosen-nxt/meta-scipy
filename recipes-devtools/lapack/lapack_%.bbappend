DEPENDS:remove:class-native = "libgfortran"

EXTRA_OECMAKE:append = " -DCBLAS=ON"

BBCLASSEXTEND = "native nativesdk"
