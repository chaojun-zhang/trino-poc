
package io.trino.poc.evaluator;

public class JniWrapper {

    native void evaluate(long schemaAddress, long dataAddress);
}
