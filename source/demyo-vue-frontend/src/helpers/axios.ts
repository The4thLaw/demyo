import { apiRoot } from '@/myenv'
import axios from 'axios'

function baselineUrl(url: string) {
	return apiRoot + url
}

export async function axiosGet<T>(url: string, data: T | any | undefined = undefined, defaultValue: T | undefined = undefined): Promise<T> {
	let def: T
	if (defaultValue === undefined) {
		def = data
		data = {}
	} else {
		def = defaultValue
	}

	let response
	try {
		response = await axios.get(baselineUrl(url), { params: data })
	} catch (e) {
		console.warn(`Failed to get the resource at ${url}`, e)
		return def
	}
	return response.data
}

export async function axiosPost<T>(url: string, data: T | any, defaultValue: T | undefined = undefined): Promise<T> {
	let def: T
	if (defaultValue === undefined) {
		def = data
		data = {}
	} else {
		def = defaultValue
	}

	let response
	try {
		response = await axios.post(baselineUrl(url), data)
	} catch (e) {
		console.warn(`Failed to post the data at ${url}`, data, e)
		return def
	}
	return response.data
}

export async function axiosPut<T>(url: string, data: any, defaultValue: T): Promise<T> {
	let response
	try {
		response = await axios.put(baselineUrl(url), data)
	} catch (e) {
		console.warn(`Failed to post the data at ${url}`, data, e)
		return defaultValue
	}
	return response.data
}

export async function axiosDelete<T>(url: string, defaultValue: T): Promise<T> {
	let response
	try {
		response = await axios.delete(baselineUrl(url))
	} catch (e) {
		console.warn(`Failed to delete the resource at ${url}`, e)
		return Promise.resolve(defaultValue)
	}
	return response.data
}
