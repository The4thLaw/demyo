// Configure the API root
var bodyApiRoot = document.body.dataset.apiroot
if (bodyApiRoot === undefined) {
	bodyApiRoot = process.env.VUE_APP_API_FALLBACK_ENDPOINT
}
console.log('API root is', bodyApiRoot)
export const apiRoot = bodyApiRoot
