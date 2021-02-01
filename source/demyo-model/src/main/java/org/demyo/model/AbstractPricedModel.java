package org.demyo.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.demyo.model.util.PreSave;

/**
 * An {@link AbstractModel} for which detailed prices are tracked.
 * 
 * @param <P> The type of price
 * @param <M> The type of model
 */
@MappedSuperclass
public abstract class AbstractPricedModel<P extends AbstractPrice<P, M>, M extends AbstractPricedModel<P, M>>
		extends AbstractModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPricedModel.class);

	/** The date of acquisition. */
	@Column(name = "acquisition_date")
	private Date acquisitionDate;

	/** The price the album was purchased for. */
	@Column(name = "purchase_price")
	@Min(0)
	private BigDecimal purchasePrice;

	/**
	 * The prices as a transient list, which is easier for Spring.
	 */
	@Valid
	@Transient
	protected List<P> priceList;

	/**
	 * Gets the date of acquisition.
	 * 
	 * @return the date of acquisition
	 */
	public Date getAcquisitionDate() {
		return acquisitionDate;
	}

	/**
	 * Sets the date of acquisition.
	 * 
	 * @param acquisitionDate the new date of acquisition
	 */
	public void setAcquisitionDate(Date acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}

	/**
	 * Gets the price the album was purchased for.
	 * 
	 * @return the price the album was purchased for
	 */
	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}

	/**
	 * Sets the price the album was purchased for.
	 * 
	 * @param purchasePrice the new price the album was purchased for
	 */
	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

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
			setPricesRaw(new TreeSet<>());
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
		if (!Hibernate.isInitialized(getPrices())) {
			return Collections.emptyList();
		}
		LOGGER.debug("prices are initialized");

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
				setPricesRaw(new TreeSet<>());
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
