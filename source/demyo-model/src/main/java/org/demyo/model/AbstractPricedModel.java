package org.demyo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.demyo.model.util.PreSave;

import org.apache.commons.collections.CollectionUtils;

/**
 * An {@link AbstractModel} for which detailed prices are tracked.
 * 
 * @param <P> The type of price
 * @param <M> The type of model
 */
@MappedSuperclass
public abstract class AbstractPricedModel<P extends AbstractPrice<P, M>, M extends AbstractPricedModel<P, M>>
		extends AbstractModel {
	/**
	 * The prices as a transient list, which is easier for Spring.
	 */
	@Valid
	@Transient
	protected List<P> priceList;

	/**
	 * Gets the prices applicable to the Model.
	 * 
	 * @return the prices applicable to the Model
	 */
	public abstract SortedSet<P> getPrices();

	/**
	 * Sets the prices to the right variable directly. The implementing method should not try to pre-process the result,
	 * as this is done by {@link #setPrices(SortedSet)}
	 * 
	 * @param prices The prices to set
	 */
	protected abstract void setPricesRaw(SortedSet<P> prices);

	/**
	 * Returns <code>this</code>.
	 * 
	 * @return The current object, typed as expected
	 */
	protected abstract M self();

	/**
	 * Sets the prices applicable to the Model.
	 * 
	 * @param prices the new prices applicable to the Model
	 */
	public void setPrices(SortedSet<P> prices) {
		if (prices == null && this.getPrices() == null) {
			return;
		}

		// To keep the delete-orphan, preserve any Set set by the entity manager
		if (this.getPrices() == null) {
			setPricesRaw(new TreeSet<P>());
		} else {
			this.getPrices().clear();
		}
		if (prices != null) {
			this.getPrices().addAll(prices);
			setModelInPrices();
		}
		priceList = null;
	}

	/**
	 * Gets the prices as a list. Use this only for MVC binding.
	 * 
	 * @see #setPricesFromList()
	 * @return The same as {@link #getPrices()}, but as a List.
	 */
	@Transient
	public List<P> getPriceList() {
		if (priceList == null) {
			priceList = new ArrayList<>();
			if (!CollectionUtils.isEmpty(getPrices())) {
				priceList.addAll(getPrices());
			}
		}
		return priceList;
	}

	/**
	 * Sets the prices from the list modified by the MVC binder. Automatically called by the model services.
	 * 
	 * @see #getPriceList()
	 */
	@PreSave
	public void setPricesFromList() {
		if (priceList != null) {
			if (getPrices() == null) {
				setPricesRaw(new TreeSet<P>());
			}
			getPrices().clear();
			getPrices().addAll(priceList);
			setModelInPrices();
		}
	}

	/**
	 * Sets the Model reference in the child prices to the exact same entity as the parent.
	 * <p>
	 * Must be performed before saving to ensure that Hibernate can merge in one go, but it can also be done more
	 * frequently. The cost is not huge.
	 * </p>
	 */
	private void setModelInPrices() {
		if (getPrices() != null) {
			for (P price : getPrices()) {
				price.setModel(self());
			}
		}
	}
}
