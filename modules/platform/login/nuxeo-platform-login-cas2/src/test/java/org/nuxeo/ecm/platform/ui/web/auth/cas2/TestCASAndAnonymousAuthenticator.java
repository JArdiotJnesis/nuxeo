/*
 * (C) Copyright 2006-2009 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Nuxeo - initial API and implementation
 *     Academie de Rennes - proxy CAS support
 *
 */

package org.nuxeo.ecm.platform.ui.web.auth.cas2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.security.Principal;

import javax.security.auth.login.LoginContext;

import org.junit.Test;
import org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants;
import org.nuxeo.ecm.platform.ui.web.auth.simple.AbstractAuthenticator;
import org.nuxeo.runtime.test.runner.Deploy;

/**
 * @author Benjamin JALON
 */
@Deploy("org.nuxeo.ecm.platform.login.cas2")
@Deploy("org.nuxeo.ecm.platform.login.cas2.test:OSGI-INF/login-cas-and-anonymous-contrib.xml")
public class TestCASAndAnonymousAuthenticator extends AbstractAuthenticator {

    protected static final String TICKET_KEY = "ticket";

    @Test
    public void testAuthenticatedToCAS() throws Exception {

        initRequest();
        doAuthenticationToCasServer(CAS_USER);

        naf.doFilter(request, response, chain);

        String loginError = (String) request.getAttribute(NXAuthConstants.LOGIN_ERROR);
        LoginContext loginContext = (LoginContext) request.getAttribute(NXAuthConstants.LOGINCONTEXT_KEY);
        assertNull(loginError);
        assertNotNull(loginContext);
        assertEquals(CAS_USER, ((Principal) loginContext.getSubject().getPrincipals().toArray()[0]).getName());
    }

    @Test
    public void testNotAuthenticatedToCAS() throws Exception {
        initRequest();

        naf.doFilter(request, response, chain);

        String loginError = (String) request.getAttribute(NXAuthConstants.LOGIN_ERROR);
        LoginContext loginContext = (LoginContext) request.getAttribute(NXAuthConstants.LOGINCONTEXT_KEY);
        assertNull(loginError);
        assertNotNull(loginContext);
        assertEquals("Anonymous", ((Principal) loginContext.getSubject().getPrincipals().toArray()[0]).getName());
    }

    protected void doAuthenticationToCasServer(String username) {
        String casTicket = username;
        when(request.getParameter(eq(TICKET_KEY))).thenReturn(casTicket);
    }

}
