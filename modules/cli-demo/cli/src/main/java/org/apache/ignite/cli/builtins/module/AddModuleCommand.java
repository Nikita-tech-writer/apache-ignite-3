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

package org.apache.ignite.cli.builtins.module;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.ignite.cli.AbstractCliCommand;
import org.apache.ignite.cli.CliPathsConfigLoader;

@Singleton
public class AddModuleCommand extends AbstractCliCommand {

    private final CliPathsConfigLoader cliPathsConfigLoader;
    private final ModuleManager moduleManager;

    @Inject
    public AddModuleCommand(
        CliPathsConfigLoader cliPathsConfigLoader,
        ModuleManager moduleManager) {
        this.cliPathsConfigLoader = cliPathsConfigLoader;
        this.moduleManager = moduleManager;
    }


    public void addModule(String moduleName, boolean cli) {
        moduleManager.setOut(out);
        moduleManager.addModule(moduleName, cliPathsConfigLoader.loadIgnitePathsOrThrowError(), cli);
    }
}
