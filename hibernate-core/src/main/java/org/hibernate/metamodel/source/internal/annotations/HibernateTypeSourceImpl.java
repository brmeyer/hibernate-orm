/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
package org.hibernate.metamodel.source.internal.annotations;

import java.util.Map;

import org.hibernate.internal.util.ValueHolder;
import org.hibernate.metamodel.reflite.spi.JavaTypeDescriptor;
import org.hibernate.metamodel.source.internal.annotations.attribute.PersistentAttribute;
import org.hibernate.metamodel.source.internal.annotations.attribute.PluralAttributeElementDetails;
import org.hibernate.metamodel.source.spi.HibernateTypeSource;

/**
 * @author Hardy Ferentschik
 * @author Strong Liu
 */
public class HibernateTypeSourceImpl implements HibernateTypeSource {
	private final ValueHolder<String> nameHolder;
	private final ValueHolder<Map<String, String>> parameterHolder;
	private final JavaTypeDescriptor javaType;

	public HibernateTypeSourceImpl(final PersistentAttribute attribute) {
		this.nameHolder = new ValueHolder<String>(
				new ValueHolder.DeferredInitializer<String>() {
					@Override
					public String initialize() {
						return attribute.getHibernateTypeResolver().getExplicitHibernateTypeName();
					}
				}
		);
		this.parameterHolder = new ValueHolder<Map<String, String>>(
				new ValueHolder.DeferredInitializer<Map<String, String>>() {
					@Override
					public Map<String, String> initialize() {
						return attribute.getHibernateTypeResolver().getExplicitHibernateTypeParameters();
					}
				}
		);
		this.javaType = attribute.getBackingMember().getType().getErasedType();
	}

	public HibernateTypeSourceImpl(final PluralAttributeElementDetails element) {
		this.nameHolder = new ValueHolder<String>(
				new ValueHolder.DeferredInitializer<String>() {
					@Override
					public String initialize() {
						return element.getTypeResolver().getExplicitHibernateTypeName();
					}
				}
		);
		this.parameterHolder = new ValueHolder<Map<String, String>>(
				new ValueHolder.DeferredInitializer<Map<String, String>>() {
					@Override
					public Map<String, String> initialize() {
						return element.getTypeResolver().getExplicitHibernateTypeParameters();
					}
				}
		);
		this.javaType = element.getJavaType();
	}

	@Override
	public String getName() {
		return nameHolder.getValue();
	}

	@Override
	public Map<String, String> getParameters() {
		return parameterHolder.getValue();
	}

	@Override
	public JavaTypeDescriptor getJavaType() {
		return javaType;
	}
}


