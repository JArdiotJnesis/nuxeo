/*
 * (C) Copyright 2009-2015 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Alexandre Russel
 *     Florent Guillaume
 */
package org.nuxeo.ecm.platform.routing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.core.test.annotations.RepositoryInit;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingConstants;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.HotDeployer;

@Deploy("org.nuxeo.ecm.platform.filemanager")
@Deploy("org.nuxeo.ecm.platform.query.api")
@Deploy("org.nuxeo.ecm.platform.routing.core")
@Deploy("org.nuxeo.ecm.platform.routing.core.test")
@RepositoryConfig(init = TestDocumentRoutingServiceImport.ImportRouteRepositoryInit.class, cleanup = Granularity.METHOD)
public class TestDocumentRoutingServiceImport extends DocumentRoutingTestCase {

    protected static final String TMP_PATH_PROP = "nuxeo.routing.test.tmp.path";

    protected File tmp;

    @Inject
    protected HotDeployer hotDeployer;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        File runtimeHome = Framework.getRuntime().getHome();
        Framework.getResourceLoader().addURL(runtimeHome.toURI().toURL());
        tmp = File.createTempFile("nuxeoRoutingTest", ".zip", runtimeHome);
        Path rpath = Paths.get(runtimeHome.getAbsolutePath()).relativize(Paths.get(tmp.getAbsolutePath()));
        Framework.getProperties().put(TMP_PATH_PROP, rpath.toString());

        // create a ZIP for the contrib
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(tmp))) {
            URL url = getClass().getResource("/routes/myRoute");
            File dir = new File(url.toURI().getPath());
            zipTree("", dir, false, zout);
            zout.finish();
        }
        hotDeployer.deploy("org.nuxeo.ecm.platform.routing.core.test:OSGI-INF/test-document-routing-model-contrib.xml");
    }

    protected void zipTree(String prefix, File root, boolean includeRoot, ZipOutputStream zout) throws IOException {
        if (includeRoot) {
            prefix += root.getName() + '/';
            zipDirectory(prefix, zout);
        }
        for (String name : root.list()) {
            File file = new File(root, name);
            if (file.isDirectory()) {
                zipTree(prefix, file, true, zout);
            } else {
                if (name.endsWith("~") || name.endsWith("#") || name.endsWith(".bak")) {
                    continue;
                }
                name = prefix + name;
                zipFile(name, file, zout);
            }
        }
    }

    protected void zipDirectory(String entryName, ZipOutputStream zout) throws IOException {
        ZipEntry zentry = new ZipEntry(entryName);
        zout.putNextEntry(zentry);
        zout.closeEntry();
    }

    protected void zipFile(String entryName, File file, ZipOutputStream zout) throws IOException {
        ZipEntry zentry = new ZipEntry(entryName);
        zentry.setTime(file.lastModified());
        zout.putNextEntry(zentry);
        try (FileInputStream in = new FileInputStream(file)) {
            IOUtils.copy(in, zout);
        }
        zout.closeEntry();
    }

    @After
    public void tearDown() {
        if (tmp != null) {
            tmp.delete();
            tmp = null;
        }
        Framework.getProperties().remove(TMP_PATH_PROP);
    }

    public static class ImportRouteRepositoryInit implements RepositoryInit {

        @Override
        public void populate(CoreSession session) {
            // create an initial route to test that is override at import
            DocumentModel root = createDocumentModel(session, "document-route-models-root", "DocumentRouteModelsRoot",
                    "/");
            assertNotNull(root);
            DocumentModel route = createDocumentModel(session, "myRoute", "DocumentRoute",
                    "/document-route-models-root/");
            route.setPropertyValue("dc:coverage", "test");
            route = session.saveDocument(route);
            // set ACL to test that the ACLs are kept
            ACP acp = route.getACP();
            ACL acl = acp.getOrCreateACL("testrouting");
            acl.add(new ACE("testusername", "Write", true));
            acp.addACL(acl);
            route.setACP(acp, true);
            route = session.saveDocument(route);

            assertNotNull(route);
            assertEquals("test", route.getPropertyValue("dc:coverage"));

            DocumentModel node = createDocumentModel(session, "myNode", "RouteNode",
                    "/document-route-models-root/myRoute");
            assertNotNull(node);
        }

        protected DocumentModel createDocumentModel(CoreSession session, String name, String type, String path) {
            DocumentModel doc = session.createDocumentModel(path, name, type);
            doc.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, name);
            return session.createDocument(doc);
        }

    }

    @Test
    public void testImportRouteModel() {
        // re-import routes created by test repository init after initial import
        service.importAllRouteModels(session);

        DocumentModel modelsRoot = session.getDocument(new PathRef("/document-route-models-root/"));
        assertNotNull(modelsRoot);
        DocumentModel route = session.getDocument(new PathRef("/document-route-models-root/myRoute"));
        assertNotNull(route);

        String routeDocId = service.getRouteModelDocIdWithId(session, "myRoute");
        DocumentModel doc = session.getDocument(new IdRef(routeDocId));
        DocumentRoute model = doc.getAdapter(DocumentRoute.class);

        assertEquals(route.getId(), model.getDocument().getId());
        // test that document was overriden but the ACLs were kept
        ACL newAcl = route.getACP().getACL("testrouting");
        assertNotNull(newAcl);
        assertEquals(1, newAcl.getACEs().length);
        assertEquals("testusername", newAcl.getACEs()[0].getUsername());

        // Oracle makes no difference between null and blank
        assertTrue(StringUtils.isBlank((String) route.getPropertyValue("dc:coverage")));
        DocumentModel node;
        try {
            node = session.getDocument(new PathRef("/document-route-models-root/myRoute/myNode"));
        } catch (DocumentNotFoundException e) {
            node = null;
        }
        assertNull(node);
        assertEquals("DocumentRoute", route.getType());
        DocumentModel step1 = session.getDocument(new PathRef("/document-route-models-root/myRoute/Step1"));
        assertNotNull(step1);
        assertEquals("RouteNode", step1.getType());
        DocumentModel step2 = session.getDocument(new PathRef("/document-route-models-root/myRoute/Step2"));
        assertNotNull(step2);
        assertEquals("RouteNode", step2.getType());
    }

}
