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
package com.pawandubey.griffin.model;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * 定义一个接口,用于被所有内容类型实现,这些类型将需要被传递到解释器
 * Defines an interface to be implemented by all content types which need to be
 * parsed by the Parser.
 *
 * @author Pawan Dubey pawandubey@outlook.com
 */
public interface Parsable extends Serializable {

    public String getContent();

    public void setContent(String content);

    public String getExcerpt();

    public String getAuthor();

    public Path getFeaturedImage();

    public String getTitle();

    public LocalDate getDate();

    public Path getLocation();

    public String getSlug();

    public String getLayout();

    public String getPermalink();

    public List<String> getTags();
}
