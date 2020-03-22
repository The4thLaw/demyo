import axios from 'axios'
import { apiRoot } from '@/myenv'

/**
 * Service to handle search requests.
 */
class ManagementService {
	async doImport(file) {
		console.debug('Going to upload', file)

		let data = new FormData()
		data.append('importFile', file)

		try {
			let response = await axios.post(`${apiRoot}manage/import`, data, {
				headers: {
					'Content-Type': 'multipart/form-data'
				}
			})
			return response.data
		} catch (e) {
			console.warn('Failed to import:', e)
		}
		return false
	}
}

export default new ManagementService()
