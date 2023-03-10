
package io.trino.poc.evaluator;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.junit.Test;

public class EvaluatorTest {
    @Test
    public void testEvaluate() throws Exception {
        try (BufferAllocator allocator = new RootAllocator()) {
            Evaluator eval = Evaluator.make();
            try (Evaluator.Result result = eval.evaluate(allocator)) {

                System.out.println("hello trino poc");
            }
        }

    }

}
