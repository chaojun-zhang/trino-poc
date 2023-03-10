#include <jni.h>
#include <iostream>

#include "io_trino_poc_evaluator_JniWrapper.h"
static jint JNI_VERSION = JNI_VERSION_1_6;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  JNIEnv* env;
  if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK) {
    return JNI_ERR;
  }
  return JNI_VERSION;
}

void JNI_OnUnload(JavaVM* vm, void* reserved) {

}


JNIEXPORT void JNICALL
Java_io_trino_poc_evaluator_JniWrapper_evaluate(JNIEnv* env, jobject object, jlong c_schema_ptr, jlong c_array_ptr) {
    struct ArrowSchema* c_schema = reinterpret_cast<struct ArrowSchema*>(c_schema_ptr);
    struct ArrowArray* c_array = reinterpret_cast<struct ArrowArray*>(c_array_ptr);
    //TODO (MAYAN)

    std::cout << "get output from velox" << std::endl;
    //auto vector = q6task->next();
    //exportVectorToArrowArray
    return;
}

