/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *
 */

package org.nuxeo.runtime.model;

import java.io.Serializable;

import org.nuxeo.common.xmap.registry.Registry;
import org.w3c.dom.Element;

/**
 * A component extension.
 * <p>
 * Extension objects holds extension data as a DOM element.
 * <p>
 * This data can be used by the extension point to extract contribution objects by using
 * {@link org.nuxeo.common.xmap.XMap} XML mapping engine.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public interface Extension extends Serializable {

    /**
     * Gets the component name where this extension should be contributed.
     *
     * @return the target component name
     */
    ComponentName getTargetComponent();

    /**
     * Gets the extension point name where this extension should be contributed.
     *
     * @return the target extension point
     */
    String getExtensionPoint();

    /**
     * Disposes this extension.
     * <p>
     * This will erase any data held by the extension.
     */
    void dispose();

    /**
     * Gets the DOM element held by this extension.
     * <p>
     * The DOM element correspond to the "extension" element in the component XML descriptor.
     *
     * @return the DOM element
     */
    Element getElement();

    /**
     * Sets the DOM element that defines this extension.
     *
     * @param element the extension DOM element
     */
    void setElement(Element element);

    /**
     * Gets the extension contribution objects.
     * <p>
     * These objects are generated by the extension point from the DOM element and then attached to the extension.
     *
     * @return the contribution objects or null if none
     * @deprecated since 11.5: contributions should be retrieved using a {@link Registry} instead.
     */
    @Deprecated(since = "11.5")
    Object[] getContributions();

    /**
     * Sets the contribution objects.
     * <p>
     * This method is used by the extension point to attach the contribution objects to the extension.
     *
     * @param contributions the contribution objects
     * @deprecated since 11.5: contributions should be registered using {@link Registry} instead.
     */
    @Deprecated(since = "11.5")
    void setContributions(Object[] contributions);

    /**
     * Sets the component owning this extension.
     *
     * @param component the component instance owning this extension
     */
    void setComponent(ComponentInstance component);

    /**
     * Gets the component instance owning this extension.
     *
     * @return the component instance owning this extension
     */
    ComponentInstance getComponent();

    /**
     * Gets the context of the component who contributed this extension.
     *
     * @return the extension context
     */
    RuntimeContext getContext();

    /**
     * Identifies the extension inside the contributing component. The id should be unique in the application. It is
     * recommended to use the following name convention for the ID: 'component_name#contribution_name'.
     * <p>
     * The id is never null. If the user is not specifying an ID, one will be generated as follow:
     * componentName#targetExtensionPoint.randomNumber
     */
    String getId();

    /**
     * Gets any comment on this extension.
     * <p>
     * Comments can be used to document extensions.
     * <p>
     * Comments should be short because they are stored in memory.
     */
    String getDocumentation();

    /**
     * Gets the XML representation for this extension.
     */
    String toXML();

}
