/*
 * (C) Copyright 2006-2007 Nuxeo SA (http://nuxeo.com/) and others.
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
 */
package org.nuxeo.ecm.platform.preview.adapter;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.common.xmap.registry.XEnable;
import org.nuxeo.common.xmap.registry.XRegistry;
import org.nuxeo.common.xmap.registry.XRegistryId;

/**
 * Descriptor for contributed Preview Adapter factories.
 *
 * @author tiry
 */
@XObject(value = "previewAdapter")
@XRegistry(enable = false, compatWarnOnMerge = true)
public class AdapterFactoryDescriptor {

    @XNode("@name")
    @XRegistryId
    private String name;

    @XNode(value = XEnable.ENABLE, fallback = "@enabled")
    @XEnable
    private boolean enabled;

    @XNode("typeName")
    private String typeName;

    @XNode("class")
    private Class<? extends PreviewAdapterFactory> adapterClass;

    public Class<? extends PreviewAdapterFactory> getAdapterClass() {
        return adapterClass;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

}
