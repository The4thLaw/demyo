import { axiosGet } from '@/helpers/axios'

/**
 * Service to manage parameters for the "about" page.
 */
class AboutService {
	getEnvironment() {
		return axiosGet('about/environment', {})
	}
}

export default new AboutService()
