package dev.xtoy.common.text;

import dev.xtoy.common.text.serializer.XTextSerializer;
import dev.xtoy.common.text.serializer.XTextSerializerFactory;
import dev.xtoy.common.text.serializer.XTextTypeEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class XYamlSerializerTest {
    private final XTextSerializer processor = XTextSerializerFactory.getSerializer(XTextTypeEnum.YAML);

    private record Person(String name, Integer age, LocalDateTime createTime) {}

    @Test
    void serializeAndDeserialize() {
        Person person = new Person("Charlie", 30, LocalDateTime.now());
        String yaml = processor.serialize(person);
        assert yaml != null;
        assert yaml.contains("name: \"Charlie\"");
        assert yaml.contains("age: 30");

        Person deserializedPerson = processor.deserialize(yaml, Person.class);
        assert deserializedPerson != null;
        assert deserializedPerson.name().equals(person.name());
        assert deserializedPerson.age().equals(person.age());
        assert deserializedPerson.createTime().equals(person.createTime());
    }
}
