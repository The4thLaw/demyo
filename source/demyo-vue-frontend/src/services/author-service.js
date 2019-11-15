import axios from 'axios'
import { apiRoot } from '@/myenv'

export default {
	async findForIndex() {
		let response = await axios.get(apiRoot + 'authors/')
		return response.data
	}
}
