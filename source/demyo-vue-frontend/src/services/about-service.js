import { axiosGet } from '@/helpers/axios'

/**
 * Service to manage parameters for the "about" page.
 */
class AboutService {
	/** @return {Promis<Object.<string, string>>} the environment settings */
	getEnvironment() {
		return axiosGet('about/environment', {})
	}
}

export default new AboutService()
