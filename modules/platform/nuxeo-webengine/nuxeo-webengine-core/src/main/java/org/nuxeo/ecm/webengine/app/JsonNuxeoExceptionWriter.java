/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Thomas Roger
 *
 */
package org.nuxeo.ecm.webengine.app;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * @since 9.3
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonNuxeoExceptionWriter implements MessageBodyWriter<NuxeoException> {

    @Override
    public long getSize(NuxeoException arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
        return NuxeoException.class.isAssignableFrom(arg0);
    }

    @Override
    public void writeTo(NuxeoException nuxeoException, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType mediaType,
            MultivaluedMap<String, Object> arg5, OutputStream outputStream)
            throws IOException, WebApplicationException {
        JsonWebengineWriter.writeException(outputStream, nuxeoException, mediaType);
    }

}
