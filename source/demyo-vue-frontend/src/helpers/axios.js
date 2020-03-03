import axios from 'axios'
import { isUndefined } from 'lodash'
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

export async function axiosPost(url, data, defaultValue) {
	if (isUndefined(defaultValue)) {
		defaultValue = data
		data = {}
	}

	let response
	try {
		response = await axios.post(baselineUrl(url), data)
	} catch (e) {
		console.warn(`Failed to post the data at ${url}`, data, e)
		return defaultValue
	}
	return response.data
}

export async function axiosPut(url, data, defaultValue) {
	let response
	try {
		response = await axios.put(baselineUrl(url), data)
	} catch (e) {
		console.warn(`Failed to post the data at ${url}`, data, e)
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
