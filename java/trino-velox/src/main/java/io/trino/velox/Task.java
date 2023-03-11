package io.trino.velox;

import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.c.Data;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.BitVector;
import org.apache.arrow.vector.Float8Vector;
import org.apache.arrow.vector.VectorSchemaRoot;


public class Task implements AutoCloseable {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Task.class);

    private final TaskJniWrapper taskJniWrapper;

    private final long moduleId;
    private final boolean debug;

    private Task(TaskJniWrapper taskJniWrapper, boolean debug) {
        this.taskJniWrapper = taskJniWrapper;
        this.moduleId = taskJniWrapper.buildTask();
        this.debug = debug;
    }

    public static Task make(boolean debug) {
        logger.debug("Created module for the task");

        TaskJniWrapper wrapper = JniLoader.getInstance().getWrapper();
        return new Task(wrapper, debug);

    }

    public VectorSchemaRoot nextBatch(BufferAllocator allocator) {
        try (ArrowArray arrowArray = ArrowArray.allocateNew(allocator); ArrowSchema arrowSchema = ArrowSchema.allocateNew(allocator)) {
            taskJniWrapper.nextBatch(moduleId, arrowSchema.memoryAddress(), arrowArray.memoryAddress());
            if (debug) {
                return testNextBatch(allocator);
            } else {
                return Data.importVectorSchemaRoot(allocator, arrowArray, arrowSchema, null);
            }
        }
    }

    private VectorSchemaRoot testNextBatch(BufferAllocator allocator) {
        BigIntVector vec1 = new BigIntVector("bigint", allocator);
        BitVector vec2 = new BitVector("bool1", allocator);
        Float8Vector vec3 = new Float8Vector("double", allocator);
        BitVector vec4 = new BitVector("bool2", allocator);
        VectorSchemaRoot vsr = VectorSchemaRoot.of(vec1, vec2, vec3, vec4);
        vsr.allocateNew();
        vec1.setSafe(0, 2000);
        vec2.setSafe(0, 1);
        vec3.setSafe(0, 3000.20);
        vec4.setSafe(0, 1);
        vsr.setRowCount(1);
        return vsr;
    }

    public boolean isFinished() {
        return taskJniWrapper.isFinished(moduleId);
    }

    @Override
    public void close() throws Exception {
        taskJniWrapper.closeTask(moduleId);
    }
}
