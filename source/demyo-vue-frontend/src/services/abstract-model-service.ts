/* eslint-disable @typescript-eslint/no-dynamic-delete */
/* eslint-disable @typescript-eslint/no-unsafe-return */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
import { axiosDelete, axiosGet, axiosPost, axiosPut } from '@/helpers/axios'

// I couldn't figure out how to map the keys to specific types so some "as any" casts are needed below
interface ServiceConfig<M extends IModel> {
	sanitizeHtml?: (keyof M)[]
	fillMissingObjects?: (keyof M)[]
	sanitizeObjects?: (keyof M)[]
	fillMissingArrays?: (keyof M)[]
	sanitizeArrays?: (keyof M)[]

}

/**
 * Base class for Model API services.
 */
class AbstractModelService<M extends IModel> {
	protected readonly basePath: string
	private readonly config: ServiceConfig<M>

	/**
	 * Constructor
	 * @param basePath The base path for API URLs, relative to the API root
	 * @param config An optional configuration object
	 */
	constructor(basePath: string, config?: ServiceConfig<M>) {
		this.basePath = basePath
		this.config = config ?? {}
	}

	async findForIndex(): Promise<M[]> {
		return axiosGet(this.basePath, [])
	}

	async findForList(): Promise<M[]> {
		return axiosGet(this.basePath, { view: 'minimal' }, [])
	}

	/**
	 * Finds a Model by its ID.
	 * @param id The Model ID
	 * @return The Model
	 */
	async findById(id: number): Promise<M> {
		const model: M = await axiosGet(this.basePath + id, {})
		return this.fillMissingData(model)
	}

	/**
	 * Fills any data that is required by the frontend but missing from server-side data.
	 * @param model The model to fill
	 */
	fillMissingData(model: M): M {
		if (model instanceof Promise) {
			console.error('You need to resolve the Promises before calling fillMissingData')
		}

		if (this.config.fillMissingObjects) {
			this.config.fillMissingObjects.forEach(prop => {
				if (!model[prop]) {
					model[prop] = {
						id: undefined
					} as any
				}
			})
		}

		if (this.config.fillMissingArrays) {
			this.config.fillMissingArrays.forEach(prop => {
				if (!model[prop]) {
					model[prop] = [] as any
				}
			})
		}

		return model
	}

	/**
	 * Finds a set of Models by their IDs.
	 * @param ids The Model IDs
	 * @return The Models
	 */
	async findMultipleById(ids: number[]): Promise<M[]> {
		const idString = ids.join(',')
		return axiosGet(`${this.basePath}/batch/${idString}`, {})
	}

	async save(model: M): Promise<number> {
		this.sanitizeArrays(model)
		this.sanitizeObjects(model)
		this.sanitizeHtml(model)

		if (model.id) {
			return axiosPut(this.basePath + model.id, model, -1)
		}
		return axiosPost(this.basePath, model, -1)
	}

	/**
	 * Sanitizes the sub-properties of a Model that link to multiple other Models.
	 * Transforms arrays of integers in arrays of objects.
	 * @param model The Model.
	 */
	private sanitizeArrays(model: M): void {
		if (this.config.sanitizeArrays) {
			this.config.sanitizeArrays.forEach(prop => {
				if (Array.isArray(model[prop])) {
					model[prop] = model[prop].map(v => Number.isInteger(v) ? { id: v } : v) as any
				}
			})
		}
	}

	/**
	 * Sanitizes the sub-properties of a Model that link to a single other Model.
	 * @param model The Model.
	 */
	private sanitizeObjects(model: M): void {
		if (this.config.sanitizeObjects) {
			// In the case of clearable model link fields, the id can be set to false
			// In such cases, we should clear the object completely
			this.config.sanitizeObjects.forEach(prop => {
				const subModel = model[prop] as IModel
				if (!subModel.id) {
					delete model[prop]
				}
			})
		}
	}

	/**
	 * Sanitizes the HTML properties of a Model, clearing empty paragraphs left behind by the RTE.
	 * @param model The Model.
	 * @private
	 */
	sanitizeHtml(model: M): void {
		if (this.config.sanitizeHtml) {
			// Rich text could leave empty <p></p> markup if the user deletes the text
			this.config.sanitizeHtml.forEach(prop => {
				if (model[prop] === '<p></p>') {
					model[prop] = null as any
				}
			})
		}
	}

	/**
	 * Deletes a Model.
	 * @param id The Model ID
	 */
	async deleteModel(id: number): Promise<boolean> {
		return axiosDelete(this.basePath + id, false)
	}
}

export default AbstractModelService
