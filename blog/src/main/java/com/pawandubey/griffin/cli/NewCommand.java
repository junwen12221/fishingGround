/*
 * Copyright 2015 Pawan Dubey pawandubey@outlook.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pawandubey.griffin.cli;

import com.pawandubey.griffin.Griffin;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.pawandubey.griffin.Configurator.LINE_SEPARATOR;

/**
 *
 * @author Pawan Dubey pawandubey@outlook.com
 */
public class NewCommand implements GriffinCommand {
    @Argument(usage = "从给定的路径创建网站骨架", metaVar = "<path>")
    public List<String> args = new ArrayList<>();//

    Path filePath;

    @Option(name = "--help", aliases = {"-h"}, handler = BooleanOptionHandler.class, usage = "寻找关于这个命令的帮助")
    private boolean help = false;

    @Option(name = "-name", aliases = {"-n"}, metaVar = "<folder_name>", usage = "这个名字的目录将被创建")
    private String name = "griffin";

    /**
     * Executes the command
     */
    @Override
    public void execute() {
        try {

            if (help || args.isEmpty()) {
                System.out.println("Scaffold out a new Griffin directory structure.");
                System.out.println("usage: griffin new [option] <path>");
                System.out.println("Options: " + LINE_SEPARATOR);
                CmdLineParser parser = new CmdLineParser(this, ParserProperties.defaults().withUsageWidth(120));
                parser.printUsage(System.out);
                return;
            }
            else {
                filePath = Paths.get(args.get(0));
            }
            Griffin griffin = new Griffin(filePath.resolve(name));
            griffin.initialize(filePath, name);
            System.out.println("成功建立网站");
        }
        catch (IOException | URISyntaxException ex) {
            Logger.getLogger(NewCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
