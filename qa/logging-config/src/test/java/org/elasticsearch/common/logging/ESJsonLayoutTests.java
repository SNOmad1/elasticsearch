/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */
package org.elasticsearch.common.logging;

import org.elasticsearch.test.ESTestCase;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;

public class ESJsonLayoutTests extends ESTestCase {
    @BeforeClass
    public static void initNodeName() {
        JsonLogsTestSetup.init();
    }

    public void testEmptyType() {
        expectThrows(IllegalArgumentException.class, () -> ESJsonLayout.newBuilder().build());
    }

    public void testLayout() {

        ESJsonLayout server = ESJsonLayout.newBuilder().setType("server").build();
        String conversionPattern = server.getPatternLayout().getConversionPattern();

        assertThat(
            conversionPattern,
            Matchers.equalTo(
                "{"
                    + "\"type\": \"server\", "
                    + "\"timestamp\": \"%d{yyyy-MM-dd'T'HH:mm:ss,SSSZZ}\", "
                    + "\"level\": \"%p\", "
                    + "\"component\": \"%c{1.}\", "
                    + "\"cluster.name\": \"${sys:es.logs.cluster_name}\", "
                    + "\"node.name\": \"%node_name\", "
                    + "\"message\": \"%notEmpty{%enc{%marker}{JSON} }%enc{%.-10000m}{JSON}\""
                    + "%notEmpty{, %node_and_cluster_id }"
                    + "%notEmpty{, %trace_id }"
                    + "%exceptionAsJson }"
                    + System.lineSeparator()
            )
        );
    }

    public void testLayoutWithAdditionalFields() {
        ESJsonLayout server = ESJsonLayout.newBuilder().setType("server").setESMessageFields("x-opaque-id,someOtherField").build();
        String conversionPattern = server.getPatternLayout().getConversionPattern();

        assertThat(
            conversionPattern,
            Matchers.equalTo(
                "{"
                    + "\"type\": \"server\", "
                    + "\"timestamp\": \"%d{yyyy-MM-dd'T'HH:mm:ss,SSSZZ}\", "
                    + "\"level\": \"%p\", "
                    + "\"component\": \"%c{1.}\", "
                    + "\"cluster.name\": \"${sys:es.logs.cluster_name}\", "
                    + "\"node.name\": \"%node_name\", "
                    + "\"message\": \"%notEmpty{%enc{%marker}{JSON} }%enc{%.-10000m}{JSON}\""
                    + "%notEmpty{, \"x-opaque-id\": \"%ESMessageField{x-opaque-id}\"}"
                    + "%notEmpty{, \"someOtherField\": \"%ESMessageField{someOtherField}\"}"
                    + "%notEmpty{, %node_and_cluster_id }"
                    + "%notEmpty{, %trace_id }"
                    + "%exceptionAsJson }"
                    + System.lineSeparator()
            )
        );
    }

    public void testLayoutWithAdditionalFieldOverride() {
        ESJsonLayout server = ESJsonLayout.newBuilder().setType("server").setESMessageFields("message").build();
        String conversionPattern = server.getPatternLayout().getConversionPattern();

        assertThat(
            conversionPattern,
            Matchers.equalTo(
                "{"
                    + "\"type\": \"server\", "
                    + "\"timestamp\": \"%d{yyyy-MM-dd'T'HH:mm:ss,SSSZZ}\", "
                    + "\"level\": \"%p\", "
                    + "\"component\": \"%c{1.}\", "
                    + "\"cluster.name\": \"${sys:es.logs.cluster_name}\", "
                    + "\"node.name\": \"%node_name\""
                    + "%notEmpty{, \"message\": \"%ESMessageField{message}\"}"
                    + "%notEmpty{, %node_and_cluster_id }"
                    + "%notEmpty{, %trace_id }"
                    + "%exceptionAsJson }"
                    + System.lineSeparator()
            )
        );
    }
}
