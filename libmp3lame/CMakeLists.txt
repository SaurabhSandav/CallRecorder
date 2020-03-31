cmake_minimum_required(VERSION 3.10.2)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DANDROID_STL=gnustl_static -DANDROID_TOOLCHAIN=clang -DSTDC_HEADERS -w")

add_library( # Specifies the name of the library.
             mp3lame

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/mp3lame/bitstream.c
             src/main/cpp/mp3lame/fft.c
             src/main/cpp/mp3lame/id3tag.c
             src/main/cpp/mp3lame/mpglib_interface.c
             src/main/cpp/mp3lame/presets.c
             src/main/cpp/mp3lame/quantize.c
             src/main/cpp/mp3lame/reservoir.c
             src/main/cpp/mp3lame/tables.c
             src/main/cpp/mp3lame/util.c
             src/main/cpp/mp3lame/VbrTag.c
             src/main/cpp/mp3lame/encoder.c
             src/main/cpp/mp3lame/gain_analysis.c
             src/main/cpp/mp3lame/lame.c
             src/main/cpp/mp3lame/newmdct.c
             src/main/cpp/mp3lame/psymodel.c
             src/main/cpp/mp3lame/quantize_pvt.c
             src/main/cpp/mp3lame/set_get.c
             src/main/cpp/mp3lame/takehiro.c
             src/main/cpp/mp3lame/vbrquantize.c
             src/main/cpp/mp3lame/version.c
             src/main/cpp/mp3lame/wrapper.c )

include_directories(src/main/cpp/mp3lame/)

find_library( # Defines the name of the path variable that stores the
              # location of the NDK library.
              log-lib

              # Specifies the name of the NDK library that
              # CMake needs to locate.
              log )

# Links your native library against one or more other native libraries.
target_link_libraries( # Specifies the target library.
                       mp3lame

                       # Links the log library to the target library.
                       ${log-lib} )
