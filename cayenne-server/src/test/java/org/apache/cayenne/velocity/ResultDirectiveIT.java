/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.velocity;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.MockOperationObserver;
import org.apache.cayenne.access.jdbc.SQLTemplateAction;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.dba.JdbcAdapter;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.query.CapsStrategy;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.testdo.testmap.Artist;
import org.apache.cayenne.unit.di.server.CayenneProjects;
import org.apache.cayenne.unit.di.server.ServerCase;
import org.apache.cayenne.unit.di.server.UseServerRuntime;
import org.junit.Test;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test for Result directive to check if we could use ResultDirective
 * optionally.
 */
@UseServerRuntime(CayenneProjects.TESTMAP_PROJECT)
public class ResultDirectiveIT extends ServerCase {

	@Inject
	private ServerRuntime runtime;

	@Inject
	private JdbcAdapter dbAdapter;

	@Inject
	private DataNode node;

    @Test
	public void testWithoutResultDirective() throws Exception {
		String sql = "SELECT ARTIST_ID, ARTIST_NAME FROM ARTIST";
		Map<String, Object> artist = insertArtist();
		Map<String, Object> selectResult = selectForQuery(sql);

		assertEquals(artist.get("ARTIST_ID"), selectResult.get("ARTIST_ID"));
		assertEquals(artist.get("ARTIST_NAME"), selectResult.get("ARTIST_NAME"));
	}

    @Test
	public void testWithOnlyResultDirective() throws Exception {
		String sql = "SELECT #result('ARTIST_ID' 'java.lang.Integer')," + " #result('ARTIST_NAME' 'java.lang.String')"
				+ " FROM ARTIST";
		Map<String, Object> artist = insertArtist();
		Map<String, Object> selectResult = selectForQuery(sql);

		assertEquals(artist.get("ARTIST_ID"), selectResult.get("ARTIST_ID"));
		assertEquals(artist.get("ARTIST_NAME"), selectResult.get("ARTIST_NAME").toString().trim());
	}

    @Test
	public void testWithMixedDirectiveUse1() throws Exception {
		String sql = "SELECT ARTIST_ID," + " #result('ARTIST_NAME' 'java.lang.String')" + " FROM ARTIST";
		Map<String, Object> artist = insertArtist();
		Map<String, Object> selectResult = selectForQuery(sql);

		assertEquals(artist.get("ARTIST_ID"), selectResult.get("ARTIST_ID"));
		assertEquals(artist.get("ARTIST_NAME"), selectResult.get("ARTIST_NAME").toString().trim());
	}

    @Test
	public void testWithMixedDirectiveUse2() throws Exception {
		String sql = "SELECT #result('ARTIST_ID' 'java.lang.Integer')," + " ARTIST_NAME " + " FROM ARTIST";
		Map<String, Object> artist = insertArtist();
		Map<String, Object> selectResult = selectForQuery(sql);

		assertEquals(artist.get("ARTIST_ID"), selectResult.get("ARTIST_ID"));
		assertEquals(artist.get("ARTIST_NAME"), selectResult.get("ARTIST_NAME"));
	}

	private Map<String, Object> selectForQuery(String sql) {
		SQLTemplate template = new SQLTemplate(Artist.class, sql);
		template.setColumnNamesCapitalization(CapsStrategy.UPPER);
		MockOperationObserver observer = new MockOperationObserver();
		runtime.getDataDomain().performQueries(Collections.singletonList(template), observer);

		List<Map<String, Object>> data = observer.rowsForQuery(template);
		assertEquals(1, data.size());
		Map<String, Object> row = data.get(0);
		return row;
	}

	/**
	 * Inserts one Artist
	 */
	private Map<String, Object> insertArtist() throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 1);
		parameters.put("name", "ArtistToTestResult");
		String templateString = "INSERT INTO ARTIST (ARTIST_ID, ARTIST_NAME, DATE_OF_BIRTH) "
				+ "VALUES (#bind($id), #bind($name), #bind($dob))";

		SQLTemplate template = new SQLTemplate(Object.class, templateString);

		template.setParams(parameters);

		SQLTemplateAction action = new SQLTemplateAction(template, node);

		Connection c = runtime.getDataDomain().getDataNodes().iterator().next().getDataSource().getConnection();
		try {
			MockOperationObserver observer = new MockOperationObserver();
			action.performAction(c, observer);

			int[] batches = observer.countsForQuery(template);
			assertNotNull(batches);
			assertEquals(1, batches.length);
			assertEquals(1, batches[0]);
		} finally {
			c.close();
		}

		MockOperationObserver observer = new MockOperationObserver();
		SelectQuery query = new SelectQuery(Artist.class);
		runtime.getDataDomain().performQueries(Collections.singletonList(query), observer);

		List<?> data = observer.rowsForQuery(query);
		assertEquals(1, data.size());
		DataRow row = (DataRow) data.get(0);
		return row;
	}
}
