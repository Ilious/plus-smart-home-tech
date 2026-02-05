package ru.yandex.practicum.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private final DatumReader<T> reader;

    private final DecoderFactory decoderFactory;

    public BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.reader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        try {
            if (bytes == null)
                return null;

            int start = 0;
            if (bytes.length >= 5 && bytes[0] == 0x00) {
                start = 5;
            }

            BinaryDecoder decoder = decoderFactory.binaryDecoder(
                    bytes,
                    start,
                    bytes.length - start,
                    null);

            return this.reader.read(null, decoder);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing data from topic [" + topic + "]", e);
        }
    }
}