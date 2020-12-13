cmake_minimum_required(VERSION 3.10.2)

file(GLOB bzip bzip2/*.c)

include_directories(src/main/cpp/bzip2)

add_library(
        patch
        SHARED
        ${bzip}
        bspatch.c
        patch.cpp
)


find_library(
        log-lib
        log
)

target_link_libraries(
        patch
        ${log-lib}
)
