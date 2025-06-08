// src/main/java/com/mif/movieInsideForum/Annotation/ObjectIdSetToStringSerializer.java
package com.mif.movieInsideForum.Annotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.Set;

public class ObjectIdSetToStringSerializer extends JsonSerializer<Set<ObjectId>> {
    @Override
    public void serialize(Set<ObjectId> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeStartArray();
            for (ObjectId objectId : value) {
                gen.writeString(objectId.toHexString());
            }
            gen.writeEndArray();
        } else {
            gen.writeNull();
        }
    }
}