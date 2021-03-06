/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and others.
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
 */
package org.nuxeo.ecm.platform.routing.api;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.routing.api.helper.ActionableValidator;

/**
 * An actionable object is an object that can be validated or refused.
 *
 * @see ActionableValidator
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 * @deprecated since 5.9.2 - Use only routes of type 'graph'
 */
@Deprecated
public interface ActionableObject {

    /**
     * The operation chain id if the action is refused.
     */
    String getRefuseOperationChainId();

    /**
     * The operation chain id if the action is validated.
     */
    String getValidateOperationChainId();

    /**
     * The step that represent the action.
     */
    DocumentRouteStep getDocumentRouteStep(CoreSession session);

    /**
     * The documents processed by the action.
     */
    DocumentModelList getAttachedDocuments(CoreSession session);

}
