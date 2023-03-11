
package io.trino.velox;

public class TaskJniWrapper {

    native long buildTask();

    native void nextBatch(long taskModuleId, long schemaAddress, long dataAddress);

    native boolean isFinished(long taskModuleId);

    native void closeTask(long taskModuleId);
}
