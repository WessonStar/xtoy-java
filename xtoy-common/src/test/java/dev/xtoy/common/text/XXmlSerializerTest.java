package dev.xtoy.common.text;

import dev.xtoy.common.text.serializer.XTextSerializer;
import dev.xtoy.common.text.serializer.XTextSerializerFactory;
import dev.xtoy.common.text.serializer.XTextTypeEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class XXmlSerializerTest {
    private final XTextSerializer processor = XTextSerializerFactory.getSerializer(XTextTypeEnum.XML);

    private record Person(String name, Integer age, LocalDateTime createTime) {}

    @Test
    void serializeAndDeserialize() {
        Person person = new Person("Charlie", 30, LocalDateTime.now());
        String xml = processor.serialize(person);
        assert xml != null;
        assert xml.contains("<name>Charlie</name>");
        assert xml.contains("<age>30</age>");

        Person deserializedPerson = processor.deserialize(xml, Person.class);
        assert deserializedPerson != null;
        assert deserializedPerson.name().equals(person.name());
        assert deserializedPerson.age().equals(person.age());
        assert deserializedPerson.createTime().equals(person.createTime());
    }
}
