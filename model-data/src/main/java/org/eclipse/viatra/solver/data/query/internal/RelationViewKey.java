package org.eclipse.viatra.solver.data.query.internal;

import java.util.Objects;
import java.util.UUID;

import org.eclipse.viatra.query.runtime.matchers.context.common.BaseInputKeyWrapper;
import org.eclipse.viatra.solver.data.query.relationView.RelationView;

public class RelationViewKey<D> extends BaseInputKeyWrapper<RelationView<D>>{
	private final String uniqueName;
	
	
	public RelationViewKey(RelationView<D> wrappedKey) {
		super(wrappedKey);
		this.uniqueName = wrappedKey.getRepresentation().getName() + "-"+UUID.randomUUID();
	}

	@Override
	public String getPrettyPrintableName() {
		return wrappedKey.getRepresentation().getName();
	}

	@Override
	public String getStringID() {
		return uniqueName;
	}

	@Override
	public int getArity() {
		return wrappedKey.getRepresentation().getSymbol().getArity();
	}

	@Override
	public boolean isEnumerable() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(uniqueName);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RelationViewKey))
			return false;
		RelationViewKey<?> other = (RelationViewKey<?>) obj;
		return Objects.equals(uniqueName, other.uniqueName);
	}
	
	
}