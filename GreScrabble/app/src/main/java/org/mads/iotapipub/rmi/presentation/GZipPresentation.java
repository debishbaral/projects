package org.mads.iotapipub.rmi.presentation;/*
package org.mads.iotapipub.rmi.presentation;

import org.mads.iotapipub.rmi.description.IRemoteMessage;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


*/
/**
 * GZip presentation to compact data using GZip I/O streams.
 * @see DefaultPresentation
 *//*

public class GZipPresentation implements IPresentation {

    public IRemoteMessage readObject(Object obj) {
        IRemoteMessage remoteMessage = null;
        GZIPInputStream gzipInputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream((byte[]) obj);
            gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int byt;
            while ((byt = gzipInputStream.read()) != -1) {
                byteArrayOutputStream.write(byt);
            }

            gzipInputStream.close();

            byte[] extractedObj = byteArrayOutputStream.toByteArray();

            byteArrayInputStream = new ByteArrayInputStream(extractedObj);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            remoteMessage = (IRemoteMessage) objectInputStream.readUnshared();
            objectInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Can't read message", e);
        } finally {
            if (gzipInputStream != null)
                try {
                    gzipInputStream.close();
                } catch (IOException e) {
                    //Oops! unable to close. Nothing can be done
                }

            if (objectInputStream != null)
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    //Oops! unable to close. Nothing can be done
                }
        }
        return remoteMessage;
    }

    public Object prepareWrite(IRemoteMessage message) {
        Object objectToWrite = message;

        ObjectOutputStream objectOutputStream = null;
        GZIPOutputStream gzipOutputStream = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            //objectOutputStream.reset(); //not important as objectStreams are always reset on new instance

            objectOutputStream.writeUnshared(message);
            byte[] byteObj = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.reset();

            gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(byteObj);
            gzipOutputStream.finish();
            byteObj = byteArrayOutputStream.toByteArray();

            objectToWrite = byteObj;
        } catch (Exception e) {
            throw new RuntimeException("Can't prepare message", e);
        } finally {
            if (gzipOutputStream != null)
                try {
                    gzipOutputStream.close();
                } catch (IOException e) {
                    //we are closing need not do anything
                }

            if (objectOutputStream != null)
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    //we are closing the outputStream, need not do anything
                }
        }
        return objectToWrite;
    }
}
*/
