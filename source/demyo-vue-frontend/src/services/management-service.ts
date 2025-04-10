import { apiRoot } from '@/myenv'
import readerService from '@/services/reader-service'
import axios from 'axios'

/**
 * Service to handle search requests.
 */
class ManagementService {
	async doImport(file: File): Promise<boolean> {
		console.debug('Going to upload', file)

		const data = new FormData()
		data.append('importFile', file)

		try {
			const response = await axios.post(`${apiRoot}manage/import`, data, {
				headers: {
					// eslint-disable-next-line @typescript-eslint/naming-convention
					'Content-Type': 'multipart/form-data'
				}
			})
			return response.data as boolean
		} catch (e) {
			console.warn('Failed to import:', e)
		} finally {
			// Always reload the reader
			void readerService.init()
		}
		return false
	}
}

export default new ManagementService()
