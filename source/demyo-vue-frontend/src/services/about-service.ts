import { axiosGet } from '@/helpers/axios'

/**
 * Service to manage parameters for the "about" page.
 */
class AboutService {
	/** @return The environment settings */
	async getEnvironment(): Promise<Record<string, string>> {
		return axiosGet('about/environment', {})
	}
}

export default new AboutService()
