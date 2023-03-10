include_directories(${CMAKE_CURRENT_BINARY_DIR} ${CMAKE_CURRENT_SOURCE_DIR}
                    ${JNI_INCLUDE_DIRS} ${JNI_HEADERS_DIR})

add_jar(trino_java_jni_velox_jar
        src/main/java/io/trino/poc/evaluator/JniWrapper.java
        GENERATE_NATIVE_HEADERS
        trino_java_jni_velox_headers)


add_library(trino_java_jni_velox SHARED
            src/main/cpp/jni_common.cc)
set_property(TARGET trino_java_jni_velox PROPERTY OUTPUT_NAME "velox_jni")
target_link_libraries(trino_java_jni_velox
                      trino_java_jni_velox_headers
                      jni)


if(CMAKE_VERSION VERSION_LESS 3.18)
  if(APPLE OR WIN32)
    set(CXX_LINKER_SUPPORTS_VERSION_SCRIPT FALSE)
  else()
    set(CXX_LINKER_SUPPORTS_VERSION_SCRIPT TRUE)
  endif()
else()
  include(CheckLinkerFlag)
  check_linker_flag(CXX
                    "-Wl,--version-script=${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/symbols.map"
                    CXX_LINKER_SUPPORTS_VERSION_SCRIPT)
endif()

if(CXX_LINKER_SUPPORTS_VERSION_SCRIPT)
  set_target_properties(trino_java_jni_velox
                        PROPERTIES LINK_FLAGS
                                   "-Wl,--version-script=${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/symbols.map"
  )
endif()

install(TARGETS trino_java_jni_velox
        LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
        RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR})
