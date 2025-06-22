package com.amartha.billing.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JSONHelperTest {

    static class Dummy {
        public String name;
        public int value;

        // Default constructor needed for Jackson
        public Dummy() {
        }

        public Dummy(String name, int value) {
            this.name = name;
            this.value = value;
        }

        // equals and hashCode for assertion
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Dummy dummy)) return false;
            return value == dummy.value && name.equals(dummy.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode() * 31 + value;
        }
    }

    @Test
    void toJson_shouldSerializeObject() {
        Dummy dummy = new Dummy("test", 42);
        String json = JSONHelper.toJson(dummy);
        assertTrue(json.contains("\"name\":\"test\""));
        assertTrue(json.contains("\"value\":42"));
    }

    @Test
    void fromJson_shouldDeserializeJsonString() {
        String json = "{\"name\":\"test\",\"value\":42}";
        Dummy dummy = JSONHelper.fromJson(json, Dummy.class);
        Assertions.assertNotNull(dummy);
        assertEquals("test", dummy.name);
        assertEquals(42, dummy.value);
    }

    @Test
    void toJson_shouldReturnEmptyJsonOnError() {
        Object obj = (Runnable) () -> {}; // Jackson cannot serialize lambdas
        String json = JSONHelper.toJson(obj);
        assertEquals("{}", json);
    }
}