/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
 * third-party contributors as indicated by either @author tags or express
 * copyright attribution statements applied by the authors.  All
 * third-party contributions are distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.metamodel.spi.relational;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;

/**
 * Support for writing {@link Constraint} implementations
 *
 * @todo do we need to support defining these on particular schemas/catalogs?
 *
 * @author Steve Ebersole
 * @author Gail Badner
 * @author Brett Meyer
 */
public abstract class AbstractConstraint implements Constraint {
	private final TableSpecification table;
	private String name;
	private final Map<Identifier, Column> columnMap = new LinkedHashMap<Identifier, Column>();
	
	// TODO: We might be able to combine name and generatedName, but for now
	// stick with the convention of separating logical and physical names.
	private String generatedName;

	protected AbstractConstraint(TableSpecification table, String name) {
		this.table = table;
		this.name = name;
	}

	@Override
	public TableSpecification getTable() {
		return table;
	}

	/**
	 * Returns the constraint name, or null if the name has not been set.
	 *
	 * @return the constraint name, or null if the name has not been set
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets a constraint name that is unique across
	 * all database objects.
	 *
	 * @param name - the unique constraint name; must be non-null.
	 *
	 * @throws IllegalArgumentException if name is null.
	 * @throws IllegalStateException if this constraint already has a non-null name.
	 */
	public void setName(String name) {
		if ( name == null ) {
			throw new IllegalArgumentException( "name must be non-null." );
		}
		if ( this.name != null ) {
			throw new IllegalStateException(
					String.format(
							"This constraint already has a name (%s) and cannot be renamed to (%s).",
							this.name,
							name
					)
			);
		}
		this.name = name;
	}

	protected abstract String getGeneratedNamePrefix();


	public String getExportedName() {
		// should really generate names (if not supplied) after metamodel is complete rather than waiting like this.
		return name != null ? name : generateName();
	}

	protected String generateName() {
		if ( generatedName == null ) {
			generatedName = StringHelper.randomFixedLengthHex( getGeneratedNamePrefix() );
		}
		return generatedName;
	}

	protected int generateConstraintColumnListId() {
		return table.generateColumnListId( getColumns() );
	}

	public List<Column> getColumns() {
		return Collections.unmodifiableList( new ArrayList<Column>( columnMap.values() ) );
	}

	public int getColumnSpan() {
		return columnMap.size();
	}

	protected Map<Identifier, Column> internalColumnAccess() {
		return columnMap;
	}

	public void addColumn(Column column) {
		internalAddColumn( column );
	}

	protected void internalAddColumn(Column column) {
//		if ( column.getTable() != getTable() ) {
//			throw new AssertionFailure(
//					String.format(
//							"Unable to add column to constraint; tables [%s, %s] did not match",
//							column.getTable().toLoggableString(),
//							getTable().toLoggableString()
//					)
//			);
//		}
		columnMap.put( column.getColumnName(), column );
	}

	protected boolean isCreationVetoed(Dialect dialect) {
		return false;
	}

	protected abstract String sqlConstraintStringInAlterTable(Dialect dialect);

	public String[] sqlDropStrings(Dialect dialect) {
		if ( isCreationVetoed( dialect ) ) {
			return null;
		}
		else {
			return new String[] {
					new StringBuilder()
						.append( "alter table " )
						.append( getTable().getQualifiedName( dialect ) )
						.append( " drop constraint " )
						.append( dialect.quote( getExportedName() ) )
						.toString()
			};
		}
	}

	public String[] sqlCreateStrings(Dialect dialect) {
		if ( isCreationVetoed( dialect ) ) {
			return null;
		}
		else {
			return new String[] {
					new StringBuilder( "alter table " )
							.append( getTable().getQualifiedName( dialect ) )
							.append( sqlConstraintStringInAlterTable( dialect ) )
							.toString()
			};
		}
	}
}
