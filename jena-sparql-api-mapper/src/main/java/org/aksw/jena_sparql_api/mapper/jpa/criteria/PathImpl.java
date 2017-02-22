package org.aksw.jena_sparql_api.mapper.jpa.criteria;

import java.util.Collection;
import java.util.Map;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.jena.sparql.expr.Expr;

public class PathImpl<X>
	extends ExpressionImpl<X>
	implements Path<X>
{
	protected Path<?> parentPath;
	protected String attrName;
	protected Class<X> valueType;
	
	public PathImpl(Path<?> parentPath, String attrName, Class<X> valueType) {
		super(null);
		this.parentPath = parentPath;
		this.attrName = attrName;
		this.valueType = valueType;
	}
	
	public PathImpl(Expr expr) {
		super(expr);
	}

	@Override
	public Bindable<X> getModel() {
		return null;
	}

	@Override
	public Path<?> getParentPath() {
		return parentPath;
	}

	@Override
	public <Y> Path<Y> get(SingularAttribute<? super X, Y> attribute) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<X, C, E> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<X, K, V> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Expression<Class<? extends X>> type() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <Y> Path<Y> get(String attributeName) {
		// TODO obtain the target Type
		Class<Y> javaClass = null;
		
		return new PathImpl<Y>(this, attributeName, javaClass);
	}
}
