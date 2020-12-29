/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.configuration.presentation.json;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.ignite.configuration.ConfigurationPresentation;
import org.apache.ignite.configuration.ConfigurationProperty;
import org.apache.ignite.configuration.Configurator;
import org.apache.ignite.configuration.internal.DynamicConfiguration;
import org.apache.ignite.configuration.internal.selector.BaseSelectors;

/** */
public class JsonPresentation implements ConfigurationPresentation<String> {
    /** */
    private final JsonConverter converter = new JsonConverter();

    /** */
    private final Map<String, Configurator<? extends DynamicConfiguration<?, ?, ?>>> configsMap;

    /** */
    public JsonPresentation(Map<String, Configurator<? extends DynamicConfiguration<?, ?, ?>>> configsMap) {
        this.configsMap = configsMap;
    }

    /** {@inheritDoc} */
    @Override public String present() {
        Map<String, ?> preparedMap = configsMap.entrySet().stream().collect(Collectors.toMap(
            e -> e.getKey(),
            e -> e.getValue().getRoot().value()
        ));

        return converter.convertTo(preparedMap);
    }

    /** {@inheritDoc} */
    @Override public String presentByPath(String path) {
        if (path.isEmpty())
            return present();

        String root = path.contains(".") ? path.substring(0, path.indexOf('.')) : path;

        Configurator<? extends DynamicConfiguration<?, ?, ?>> configurator = configsMap.get(root);

        ConfigurationProperty<Object, Object> prop = configurator.getInternal(BaseSelectors.find(path));

        return converter.convertTo(prop.value());
    }
}
