package org.hyperic.hq.hqapi1;

import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import java.io.InputStream;
import java.io.OutputStream;

public class XmlUtil {

    public static <T> T deserialize(Class<T> res, InputStream is)
        throws JAXBException
    {
        String pkg = res.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(pkg);
        Unmarshaller u = jc.createUnmarshaller();
        u.setEventHandler(new DefaultValidationEventHandler());
        return res.cast(u.unmarshal(is));
    }

    public static void serialize(Object o, OutputStream os, Boolean format)
        throws JAXBException
    {
        String pkg = o.getClass().getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(pkg);
        Marshaller m = jc.createMarshaller();
        m.setEventHandler(new DefaultValidationEventHandler());
        m.setProperty("jaxb.formatted.output", format);
        m.marshal(o, os);
    }
}
