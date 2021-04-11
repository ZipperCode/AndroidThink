cmake_minimum_required(VERSION 3.10.2)

#
#include_directories(src/main/cpp/bzip2)
#
add_library(
        dex_helper
        SHARED
        dex_helper.cpp
        log.h
)


find_library(
        log-lib
        log
)

target_link_libraries(
        dex_helper
        ${log-lib}
)
