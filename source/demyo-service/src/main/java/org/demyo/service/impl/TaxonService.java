package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.dao.IModelRepo;
import org.demyo.dao.ITaxonRepo;
import org.demyo.model.Taxon;
import org.demyo.model.enums.TaxonType;
import org.demyo.service.ITaxonService;

/**
 * Implements the contract defined by {@link ITaxonService}.
 */
@Service
public class TaxonService extends AbstractModelService<Taxon> implements ITaxonService {
	@Autowired
	private ITaxonRepo repo;

	/**
	 * Default constructor.
	 */
	public TaxonService() {
		super(Taxon.class);
	}

	@Override
	public List<Taxon> findAllForIndex() {
		return repo.findAllWithUsageCounts();
	}

	@Async
	@Override
	@Transactional(readOnly = true)
	public CompletableFuture<List<Taxon>> quickSearch(String query, boolean exact) {
		return quickSearch(query, exact, repo);
	}

	@Override
	protected IModelRepo<Taxon> getRepo() {
		return repo;
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public void convertType(long id) {
		Taxon taxon = repo.getReferenceById(id);
		if (taxon.getType() == TaxonType.TAG) {
			taxon.setType(TaxonType.GENRE);
		} else {
			taxon.setType(TaxonType.TAG);
		}
		save(taxon);
	}
}
