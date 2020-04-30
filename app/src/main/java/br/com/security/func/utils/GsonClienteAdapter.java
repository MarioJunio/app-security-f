package br.com.security.func.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import br.com.security.func.models.orm.Cliente;

/**
 * Created by mariomartins on 18/09/17.
 */

public class GsonClienteAdapter implements JsonSerializer<Cliente>, JsonDeserializer<Cliente> {

    @Override
    public Cliente deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Cliente(Long.parseLong(json.toString()));
    }

    @Override
    public JsonElement serialize(Cliente src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getId());
    }
}
