
package io.trino.velox;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.junit.Test;

public class TaskTest {
    @Test
    public void testEvaluate() throws Exception {
        try (BufferAllocator allocator = new RootAllocator(); Task task = Task.make()) {
            int batchIdx =0;
            while (!task.isFinished()) {
                try (Task.ColumnBatch batch = task.nextBatch(allocator)) {
                    System.out.println("fetch batch " + batchIdx++ + " from backend");
                }
            }

        }
    }

}
