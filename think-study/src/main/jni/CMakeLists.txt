cmake_minimum_required(VERSION 3.10.2)


add_library(
        reflect
        SHARED
        reflect.cpp
)

find_library(
        log-lib
        log
)



target_link_libraries(
        reflect
        ${log-lib}
)
