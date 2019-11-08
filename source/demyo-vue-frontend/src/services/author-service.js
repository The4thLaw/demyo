import axios from 'axios'
import { apiRoot } from '@/myenv'

export default {
	async findForIndex() {
		// TODO: must configure CORS, use a proxy
		let response = await axios.get(apiRoot + 'authors/')
		console.log(response.data)
		return response.data
	}
}
