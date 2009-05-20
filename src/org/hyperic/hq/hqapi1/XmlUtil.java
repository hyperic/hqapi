package org.hyperic.hq.hqapi1;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import java.io.InputStream;
import java.io.OutputStream;

public class XmlUtil {

    private static JAXBContext CTX;

    static {
        try {
            CTX = JAXBContext.newInstance("org.hyperic.hq.hqapi1.types");
        } catch (JAXBException e) {
            // Not going to happen
            System.out.println("Error initializing context: " + e.getMessage());
        }
    }

    public static <T> T deserialize(Class<T> res, InputStream is)
        throws JAXBException
    {
        Unmarshaller u = CTX.createUnmarshaller();
        u.setEventHandler(new DefaultValidationEventHandler());
        return res.cast(u.unmarshal(is));
    }

    public static void serialize(Object o, OutputStream os, Boolean format)
        throws JAXBException
    {
        Marshaller m = CTX.createMarshaller();
        m.setEventHandler(new DefaultValidationEventHandler());
        m.setProperty("jaxb.formatted.output", format);
        m.marshal(o, os);
    }
}
