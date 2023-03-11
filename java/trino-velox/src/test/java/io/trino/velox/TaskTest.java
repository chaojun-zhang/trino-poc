
package io.trino.velox;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.junit.Test;

public class TaskTest {
    @Test
    public void testNextBatch() throws Exception {
        try (BufferAllocator allocator = new RootAllocator(); Task task = Task.make(true)) {
            int batchIdx = 0;
            while (!task.isFinished()) {
                try (VectorSchemaRoot batch = task.nextBatch(allocator)) {
                    System.out.println("fetch batch " + batchIdx++ + " from backend");
                }
            }
            System.out.println("fetch batch finished");
        }
    }

}
