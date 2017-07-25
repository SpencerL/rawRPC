package rpc.codec;

import com.sun.corba.se.pept.encoding.InputObject;

import java.io.*;

/**
 * Created by lee on 7/25/17.
 */
public class JavaCodec {
    public static byte[] encode(Object  obj){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] arr = null;
        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            arr  = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bos.close();
            } catch (IOException e) {}
        }
        return arr;

    }

    public static Object decode(byte[] arr){
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Object obj = null;
        try {
            ObjectInput in = new ObjectInputStream(bis);
            obj = in.readObject();
        }
        catch (IOException e) { e.printStackTrace();}
        catch (ClassNotFoundException e) { e.printStackTrace();}
        finally {
            try {
                bis.close();
            } catch (IOException e) {}
        }
        return obj;
    }
}
