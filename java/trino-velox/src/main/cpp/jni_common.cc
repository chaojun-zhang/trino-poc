#include <jni.h>
#include <iostream>

#include "arrow_c.h"
#include "id_to_module_map.h"
#include "velox_task.h"

#include "io_trino_velox_TaskJniWrapper.h"

// module maps
trino::velox::IdToModuleMap<std::shared_ptr<trino::velox::TpchQ6Task>> task_modules_;

static jint JNI_VERSION = JNI_VERSION_1_6;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  JNIEnv* env;
  if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK) {
    return JNI_ERR;
  }
  return JNI_VERSION;
}

void JNI_OnUnload(JavaVM* vm, void* reserved) {}

JNIEXPORT jlong JNICALL
Java_io_trino_velox_TaskJniWrapper_buildTask(JNIEnv* env, jobject object) {
    auto task = trino::velox::TpchQ6Task::Make();
    return task_modules_.Insert(task);
}

JNIEXPORT void JNICALL
Java_io_trino_velox_TaskJniWrapper_nextBatch(JNIEnv* env, jobject object, jlong module_id, jlong c_schema_ptr, jlong c_array_ptr) {
    struct ArrowSchema* c_schema = reinterpret_cast<struct ArrowSchema*>(c_schema_ptr);
    struct ArrowArray* c_array = reinterpret_cast<struct ArrowArray*>(c_array_ptr);
    auto task = task_modules_.Lookup(module_id);
    task->nextBatch(c_schema,c_array);
}

JNIEXPORT jboolean JNICALL
Java_io_trino_velox_TaskJniWrapper_isFinished(JNIEnv* env, jobject object, jlong module_id) {
    auto task = task_modules_.Lookup(module_id);
    bool result = task->isFinished();
    return result;
}

JNIEXPORT void JNICALL
Java_io_trino_velox_TaskJniWrapper_closeTask(JNIEnv* env, jobject object, jlong module_id) {
   task_modules_.Erase(module_id);
}

