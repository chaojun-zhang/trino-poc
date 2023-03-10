cmake_minimum_required(VERSION 3.11)
message(STATUS "Building using CMake version: ${CMAKE_VERSION}")

# find_package() uses <PackageName>_ROOT variables.
# https://cmake.org/cmake/help/latest/policy/CMP0074.html
if(POLICY CMP0074)
  cmake_policy(SET CMP0074 NEW)
endif()

project(trino-poc)

if("${CMAKE_CXX_STANDARD}" STREQUAL "")
  set(CMAKE_CXX_STANDARD 17)
endif()
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Build
find_package(Java REQUIRED)
find_package(JNI REQUIRED)

include(UseJava)

add_library(jni INTERFACE IMPORTED)
set_target_properties(jni PROPERTIES INTERFACE_INCLUDE_DIRECTORIES "${JNI_INCLUDE_DIRS}")


add_subdirectory(trino-velox)

