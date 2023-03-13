
package io.trino.velox;

import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.c.Data;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.BitVector;
import org.apache.arrow.vector.Float8Vector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.junit.Test;

public class TaskTest {
    @Test
    public void testNextBatch() throws Exception {
        try (BufferAllocator allocator = new RootAllocator(); Task task = Task.make()) {
            int batchIdx = 0;
            while (!task.isFinished()) {
                try (VectorSchemaRoot batch = task.nextBatch(allocator)) {
                    System.out.println("fetch batch " + batchIdx++ + " from backend");
                }
            }
            System.out.println("fetch batch finished");
        }
    }

    @Test
    public void testBatch() throws Exception {
        try (BufferAllocator allocator = new RootAllocator()){
            BigIntVector vec1 = new BigIntVector("bigint", allocator);
            BitVector vec2 = new BitVector("bool1", allocator);
            Float8Vector vec3 = new Float8Vector("double", allocator);
            BitVector vec4 = new BitVector("bool2", allocator);
            try(VectorSchemaRoot vsr = VectorSchemaRoot.of(vec1, vec2, vec3, vec4)){
                vsr.allocateNew();
                vec1.setSafe(0, 1191);
                vec2.setSafe(0, 1);
                vec3.setSafe(0, 4697874936305107645L);
                vec4.setSafe(0, 1);
                vsr.setRowCount(1);


                // Consumer allocates empty structures
                try (ArrowSchema consumerArrowSchema = ArrowSchema.allocateNew(allocator);
                     ArrowArray consumerArrowArray = ArrowArray.allocateNew(allocator)) {

                    // Producer creates structures from existing memory pointers
                    try (ArrowSchema arrowSchema = ArrowSchema.wrap(consumerArrowSchema.memoryAddress());
                         ArrowArray arrowArray = ArrowArray.wrap(consumerArrowArray.memoryAddress())) {
                        // Producer exports vector into the C Data Interface structures
                        Data.exportVectorSchemaRoot(allocator, vsr, null, arrowArray, arrowSchema);
                    }

                    // Consumer imports vector
                    VectorSchemaRoot vectorSchemaRoot = Data.importVectorSchemaRoot(allocator, consumerArrowArray, consumerArrowSchema, null);
                    vectorSchemaRoot.clear();
                }
            }


        }

    }
}
