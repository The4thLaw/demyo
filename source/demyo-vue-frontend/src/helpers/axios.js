import axios from 'axios'
import { apiRoot } from '@/myenv'

function baselineUrl(url) {
	return apiRoot + url
}

export async function axiosGet(url, defaultValue) {
	let response
	try {
		response = await axios.get(baselineUrl(url))
	} catch (e) {
		console.warn(`Failed to get the resource at ${url}`, e)
		return defaultValue
	}
	return response.data
}

export async function axiosDelete(url, defaultValue) {
	let response
	try {
		response = await axios.delete(baselineUrl(url))
	} catch (e) {
		console.warn(`Failed to delete the resource at ${url}`, e)
		return defaultValue
	}
	return response.data
}
