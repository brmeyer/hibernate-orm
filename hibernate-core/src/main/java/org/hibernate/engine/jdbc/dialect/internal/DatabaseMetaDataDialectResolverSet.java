/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
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
package org.hibernate.engine.jdbc.dialect.internal;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolver;

/**
 * Implements the DatabaseMetaDataDialectResolver as a chain, allowing multiple delegate DatabaseMetaDataDialectResolver
 * implementations to coordinate resolution
 *
 * @author Steve Ebersole
 */
public class DatabaseMetaDataDialectResolverSet implements DatabaseMetaDataDialectResolver {
	private List<DatabaseMetaDataDialectResolver> delegateResolvers;

	/**
	 * Constructs a DatabaseInfoDialectResolverSet
	 */
	public DatabaseMetaDataDialectResolverSet() {
		this( new ArrayList<DatabaseMetaDataDialectResolver>() );
	}

	/**
	 * Constructs a DatabaseInfoDialectResolverSet
	 *
	 * @param delegateResolvers The set of delegate resolvers
	 */
	public DatabaseMetaDataDialectResolverSet(List<DatabaseMetaDataDialectResolver> delegateResolvers) {
		this.delegateResolvers = delegateResolvers;
	}

	/**
	 * Constructs a DatabaseInfoDialectResolverSet
	 *
	 * @param delegateResolvers The set of delegate resolvers
	 */
	@SuppressWarnings("UnusedDeclaration")
	public DatabaseMetaDataDialectResolverSet(DatabaseMetaDataDialectResolver... delegateResolvers) {
		this( Arrays.asList( delegateResolvers ) );
	}

	@Override
	public Dialect resolve(DatabaseMetaData databaseMetaData) {
		for ( DatabaseMetaDataDialectResolver resolver : delegateResolvers ) {
			final Dialect dialect = resolver.resolve( databaseMetaData );
			if ( dialect != null ) {
				return dialect;
			}
		}
		return null;
	}

	/**
	 * Add a resolver at the end of the underlying resolver list.  The resolver added by this method is at lower
	 * priority than any other existing resolvers.
	 *
	 * @param resolver The resolver to add.
	 */
	public void addResolver(DatabaseMetaDataDialectResolver resolver) {
		delegateResolvers.add( resolver );
	}

	/**
	 * Add a resolver at the beginning of the underlying resolver list.  The resolver added by this method is at higher
	 * priority than any other existing resolvers.
	 *
	 * @param resolver The resolver to add.
	 */
	@SuppressWarnings("UnusedDeclaration")
	public void addResolverAtFirst(DatabaseMetaDataDialectResolver resolver) {
		delegateResolvers.add( 0, resolver );
	}
}
