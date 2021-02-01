/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.docgen.refcard

import org.neo4j.cypher.docgen.RefcardTest
import org.neo4j.cypher.docgen.tooling.{DocsExecutionResult, QueryStatisticsTestSupport}
import org.neo4j.graphdb.Transaction

class ConstraintTest extends RefcardTest with QueryStatisticsTestSupport {
  val graphDescription = List("A:Person KNOWS B:Person")
  val title = "CONSTRAINT"
  override val linkId = "administration/constraints"

  override def assert(tx:Transaction, name: String, result: DocsExecutionResult): Unit = {
    name match {
      case "create-unique-property-constraint" =>
        assertStats(result, uniqueConstraintsAdded = 1)
        assert(result.toList.size === 0)
      case "create-named-unique-property-constraint" =>
        assertStats(result, uniqueConstraintsAdded = 1)
        assert(result.toList.size === 0)
      case "create-node-property-existence-constraint" =>
        assertStats(result, existenceConstraintsAdded = 1)
        assert(result.toList.size === 0)
      case "create-named-node-property-existence-constraint" =>
        assertStats(result, existenceConstraintsAdded = 1)
        assert(result.toList.size === 0)
      case "create-relationship-property-existence-constraint" =>
        assertStats(result, existenceConstraintsAdded = 1)
        assert(result.toList.size === 0)
      case "create-named-relationship-property-existence-constraint" =>
        assertStats(result, existenceConstraintsAdded = 1)
        assert(result.toList.size === 0)
      case "create-node-key-constraint" =>
        assertStats(result, nodekeyConstraintsAdded = 1)
        assert(result.toList.size === 0)
      case "create-named-node-key-constraint" =>
        assertStats(result, nodekeyConstraintsAdded = 1)
        assert(result.toList.size === 0)
      case "drop-named-constraint" =>
        assertStats(result, namedConstraintsRemoved = 1)
        assert(result.toList.size === 0)
      case "match" =>
        assertStats(result, nodesCreated = 0)
        assert(result.toList.size === 1)
    }
  }

  override val properties: Map[String, Map[String, Any]] = Map(
    "A" -> Map("name" -> "Alice", "firstname" -> "Alice", "surname" -> "Johnson"),
    "B" -> Map("name" -> "Bobo", "firstname" -> "Bobo", "surname" -> "Baumann"))

  override def parameters(name: String): Map[String, Any] =
    name match {
      case "parameters=aname" =>
        Map("value" -> "Alice")
      case _ =>
        Map()
    }

  def text = """
###assertion=create-unique-property-constraint
//

CREATE CONSTRAINT ON (p:Person)
       ASSERT p.name IS UNIQUE
###

Create a unique property constraint on the label `Person` and property `name`.
If any other node with that label is updated or created with a `name` that
already exists, the write operation will fail.
This constraint will create an accompanying index.

###assertion=create-named-unique-property-constraint
//

CREATE CONSTRAINT uniqueness ON (p:Person)
       ASSERT p.age IS UNIQUE
###

Create a unique property constraint on the label `Person` and property `age` with the name `uniqueness`.
If any other node with that label is updated or created with a `age` that
already exists, the write operation will fail.
This constraint will create an accompanying index.

###assertion=create-node-property-existence-constraint
//

CREATE CONSTRAINT ON (p:Person)
       ASSERT exists(p.name)
###

(★) Create a node property existence constraint on the label `Person` and property `name`.
If a node with that label is created without a `name`, or if the `name` property is
removed from an existing node with the `Person` label, the write operation will fail.

###assertion=create-named-node-property-existence-constraint
//

CREATE CONSTRAINT node_exists ON (p:Person)
       ASSERT exists(p.surname)
###

(★) Create a node property existence constraint on the label `Person` and property `surname` with the name `node_exists`.
If a node with that label is created without a `surname`, or if the `surname` property is
removed from an existing node with the `Person` label, the write operation will fail.

###assertion=create-relationship-property-existence-constraint
//

CREATE CONSTRAINT ON ()-[l:LIKED]-()
       ASSERT exists(l.when)
###

(★) Create a relationship property existence constraint on the type `LIKED` and property `when`.
If a relationship with that type is created without a `when`, or if the `when` property is
removed from an existing relationship with the `LIKED` type, the write operation will fail.

###assertion=create-named-relationship-property-existence-constraint
//

CREATE CONSTRAINT relationship_exists ON ()-[l:LIKED]-()
       ASSERT exists(l.since)
###

(★) Create a relationship property existence constraint on the type `LIKED` and property `since` with the name `relationship_exists`.
If a relationship with that type is created without a `since`, or if the `since` property is
removed from an existing relationship with the `LIKED` type, the write operation will fail.

""".concat(if (!versionFenceAllowsThisTest("3.2.9")) "" else """
###assertion=create-node-key-constraint
//

CREATE CONSTRAINT ON (p:Person)
      ASSERT (p.firstname, p.surname) IS NODE KEY
###

(★) Create a node key constraint on the label `Person` and properties `firstname` and `surname`.
If a node with that label is created without both `firstname` and `surname`
or if the combination of the two is not unique,
or if the `firstname` and/or `surname` labels on an existing node with the `Person` label
is modified to violate these constraints, the write operation will fail.

###assertion=create-named-node-key-constraint
//

CREATE CONSTRAINT node_key ON (p:Person)
      ASSERT (p.name, p.surname) IS NODE KEY
###

(★) Create a node key constraint on the label `Person` and properties `name` and `surname` with the name `node_key`.
If a node with that label is created without both `name` and `surname`
or if the combination of the two is not unique,
or if the `name` and/or `surname` labels on an existing node with the `Person` label
is modified to violate these constraints, the write operation will fail.

""").concat("""
###assertion=drop-named-constraint
//

DROP CONSTRAINT uniqueness
###

Drop the constraint with the name `uniqueness`.
""")
}
