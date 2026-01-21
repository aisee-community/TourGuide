cmake_minimum_required(VERSION 3.22.1)
project(uartreader)

add_library(uartreader SHARED
        uartreader.cpp
)

find_library(log-lib log)

target_link_libraries(uartreader
        ${log-lib}
)