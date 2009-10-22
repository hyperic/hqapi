/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class to convert Java objects to XML and vice versa.
 */
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
