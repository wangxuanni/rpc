package rpc;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * @description:
 * @author: wangxuanni
 * @create: 2019-09-06 16:36
 **/

public class ProtostuffAdapter {
    private static RuntimeSchema<Model> schema = RuntimeSchema.createFrom(Model.class);

    public static Model getModel(byte[] bytes) {
        Model model = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, model, schema);
        System.out.println(model.toString());
        return model;
    }

    public static byte[] getByte(Model model) {
        byte[] bytes = ProtostuffIOUtil.toByteArray(model, schema,
                LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        return bytes;
    }

}
