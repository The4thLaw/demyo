package org.demyo.service;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.IModel;
import org.demyo.model.Image;

public interface IFilePondModelService {
	/**
	 * Recovers and saves model-related images from FilePond.
	 * @param <M> The model type.
	 * @param modelId The model internal ID.
	 * @param mainFilePondId The FilePond ID of the main image. Can be null or blank.
	 * @param otherFilePondIds The FilePond ID of the other images. Can be null or empty.
	 * @param mainLabel The label to use as base for the main image.
	 * @param otherLabel The label to use as base for the other images.
	 * @param mainSetter The setter for the recovered main image.
	 * @param otherSetter The setter for the recovered other images.
	 * @param service The model service.
	 * @param modelName The getter for the model name.
	 * @throws DemyoException In case of isse while recovering.
	 */
	<M extends IModel> void recoverFromFilePond(
			long modelId,
			String mainFilePondId, String[] otherFilePondIds,
			String mainLabel, String otherLabel,
			BiConsumer<M, Image> mainSetter, BiConsumer<M, List<Image>> otherSetter,
			IModelService<M> service, Function<M, String> modelName)
			throws DemyoException;

}
