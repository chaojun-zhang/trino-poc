package io.trino.poc.evaluator;

import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.memory.BufferAllocator;

import java.io.Closeable;
import java.io.IOException;


public class Evaluator {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Evaluator.class);

    private JniWrapper wrapper;

    public static class Result implements Closeable {
        private ArrowArray array;
        private ArrowSchema schema;

        public Result(ArrowArray array, ArrowSchema schema) {
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


    private Evaluator(JniWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public static Evaluator make() {
        JniWrapper wrapper = JniLoader.getInstance().getWrapper();
        logger.debug("Created module for the evaluator");

        return new Evaluator(wrapper);
    }

    public Result evaluate(BufferAllocator allocator) {
        ArrowArray arrowArray = ArrowArray.allocateNew(allocator);
        ArrowSchema arrowSchema = ArrowSchema.allocateNew(allocator);
        wrapper.evaluate(arrowSchema.memoryAddress(), arrowArray.memoryAddress());
        return new Result(arrowArray, arrowSchema);
    }


}
