/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.common.lucene.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.RAMDirectory;
import org.elasticsearch.common.lucene.Lucene;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@Test
public class MultiPhrasePrefixQueryTests {

    @Test public void simpleTests() throws Exception {
        IndexWriter writer = new IndexWriter(new RAMDirectory(), new IndexWriterConfig(Lucene.VERSION, Lucene.STANDARD_ANALYZER));
        Document doc = new Document();
        doc.add(new Field("field", "aaa bbb ccc ddd", Field.Store.NO, Field.Index.ANALYZED));
        writer.addDocument(doc);
        IndexReader reader = IndexReader.open(writer, true);
        IndexSearcher searcher = new IndexSearcher(reader);

        MultiPhrasePrefixQuery query = new MultiPhrasePrefixQuery();
        query.add(new Term("field", "aa"));
        assertThat(Lucene.count(searcher, query, 0), equalTo(1l));

        query = new MultiPhrasePrefixQuery();
        query.add(new Term("field", "aaa"));
        query.add(new Term("field", "bb"));
        assertThat(Lucene.count(searcher, query, 0), equalTo(1l));

        query = new MultiPhrasePrefixQuery();
        query.setSlop(1);
        query.add(new Term("field", "aaa"));
        query.add(new Term("field", "cc"));
        assertThat(Lucene.count(searcher, query, 0), equalTo(1l));

        query = new MultiPhrasePrefixQuery();
        query.setSlop(1);
        query.add(new Term("field", "xxx"));
        assertThat(Lucene.count(searcher, query, 0), equalTo(0l));
    }
}