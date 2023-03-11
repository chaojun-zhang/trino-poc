package io.trino.velox;

import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.memory.BufferAllocator;

import java.io.IOException;


public class Task implements AutoCloseable {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Task.class);

    private final TaskJniWrapper taskJniWrapper;

    private final long moduleId;

    public static class ColumnBatch implements AutoCloseable {
        private final ArrowArray array;
        private final ArrowSchema schema;

        public ColumnBatch(ArrowArray array, ArrowSchema schema) {
            this.array = array;
            this.schema = schema;
        }

        public ArrowArray getArray() {
            return array;
        }

        public ArrowSchema getSchema() {
            return schema;
        }

        @Override
        public void close() throws IOException {
            array.close();
            schema.close();
        }
    }


    private Task(TaskJniWrapper taskJniWrapper) {
        this.taskJniWrapper = taskJniWrapper;
        this.moduleId = taskJniWrapper.buildTask();
    }

    public static Task make() {
        TaskJniWrapper wrapper = JniLoader.getInstance().getWrapper();
        logger.debug("Created module for the task");

        return new Task(wrapper);
    }

    public ColumnBatch nextBatch(BufferAllocator allocator) {
        ArrowArray arrowArray = ArrowArray.allocateNew(allocator);
        ArrowSchema arrowSchema = ArrowSchema.allocateNew(allocator);
        taskJniWrapper.nextBatch(moduleId, arrowSchema.memoryAddress(), arrowArray.memoryAddress());
        return new ColumnBatch(arrowArray, arrowSchema);
    }

    public boolean isFinished() {
        return taskJniWrapper.isFinished(moduleId);
    }

    @Override
    public void close() throws Exception {
        taskJniWrapper.closeTask(moduleId);
    }
}
