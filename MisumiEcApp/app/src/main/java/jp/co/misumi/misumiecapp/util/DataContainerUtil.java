package jp.co.misumi.misumiecapp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;


import jp.co.misumi.misumiecapp.data.DataContainer;


/**
 * ViewUtil
 */
public class DataContainerUtil {


    public static byte[] convDataContainer(DataContainer dataContainer){

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			GZIPOutputStream gzipOut = new GZIPOutputStream(baos, 32768);
			DeflaterOutputStream gzipOut = new DeflaterOutputStream(baos, new Deflater(Deflater.BEST_SPEED), 32768);
			ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
			objectOut.writeObject(dataContainer);
			objectOut.close();
			byte[] bytes = baos.toByteArray();

	jp.co.misumi.misumiecapp.AppLog.e("bytes: "+ bytes.length);

	        return bytes;
		} catch (Exception e) {
		}

        return null;
    }


    public static DataContainer reconvDataContainer(byte[] bytes){

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//			GZIPInputStream gzipIn = new GZIPInputStream(bais, 32768);
			InflaterInputStream gzipIn = new InflaterInputStream(bais);
			ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
			DataContainer dataContainer = (DataContainer) objectIn.readObject();
			objectIn.close();

			return dataContainer;

		} catch (Exception e) {
		}

		return null;
    }

}

