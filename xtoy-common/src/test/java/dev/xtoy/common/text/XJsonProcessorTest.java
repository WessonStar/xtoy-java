package dev.xtoy.common.text;

import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XJsonProcessorTest {
    private final XJsonProcessor processor = XJsonProcessor.DEFAULT;

    private record Person(String name, Integer age, LocalDateTime createTime) {}

    @Test
    void deserializeList() {
        String json = "[{\"name\":\"Alice\"},{\"name\":\"Bob\"}]";
        List<Person> people = processor.deserializeList(json, Person.class);
        assertNotNull(people);
        assertEquals(2, people.size());
        assertEquals("Alice", people.get(0).name());
        assertEquals("Bob", people.get(1).name());
    }

    @Test
    void deserializeStringMap() {
        String json = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        InputStream jsonStream = new java.io.ByteArrayInputStream(json.getBytes());
        Map<String, String> map = processor.deserializeStringMap(jsonStream);
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
    }

    @Test
    void serialize() {
        Person person = new Person("Charlie", 30, LocalDateTime.now());
        String json = processor.serialize(person);
        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"Charlie\""));
        assertTrue(json.contains("\"age\":30"));
    }

    @Test
    void deserialize() throws IOException {
        String json = "{\"name\":\"Charlie\",\"age\":30,\"createTime\":\"2000-01-01T12:00:30\"}";
        Person person = processor.deserialize(json, Person.class);
        assertNotNull(person);
        assertEquals("Charlie", person.name());
        assertEquals(30, person.age());
        assertEquals(LocalDateTime.of(2000, 1, 1, 12, 0, 30), person.createTime());

        Person person1 = processor.deserialize(json, new TypeReference<Person>() {
        });
        assertNotNull(person1);
        assertEquals("Charlie", person1.name());
        assertEquals(30, person1.age());

        InputStream jsonStream = new java.io.ByteArrayInputStream(json.getBytes());
        Person person2 = processor.deserialize(jsonStream, Person.class);
        assertNotNull(person2);
        assertEquals("Charlie", person2.name());
        assertEquals(30, person2.age());

        jsonStream.reset();
        Person person3 = processor.deserialize(jsonStream, new TypeReference<Person>() {
        });
        assertNotNull(person3);
        assertEquals("Charlie", person3.name());
        assertEquals(30, person3.age());
    }
}