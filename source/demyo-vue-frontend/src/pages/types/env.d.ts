/* eslint-disable @typescript-eslint/naming-convention */
/// <reference types="vite/client" />

// See https://vitejs.dev/guide/env-and-mode.html#intellisense-for-typescript

interface ImportMetaEnv {
	/** The endpoint to use if the context root cannot be retrieved from the data attributes in the DOM. */
	readonly VITE_CONTEXT_ROOT_FALLBACK: string
	/** The endpoint to use if the API endpoint cannot be retrieved from the data attributes in the DOM. */
	readonly VITE_API_FALLBACK_ENDPOINT: string

	/** The fallback reader configuration value for the page size in text indexes. */
	readonly VITE_DEF_CFG_RDR_PAGESIZEFORTEXT: number
	/** The fallback reader configuration value for the page size in card indexes. */
	readonly VITE_DEF_CFG_RDR_PAGESIZEFORCARDS: number
	/** The fallback reader configuration value for number of items per card in card indexes. */
	readonly VITE_DEF_CFG_RDR_SUBITEMSINCARDINDEX: number
	/** The fallback reader configuration value for the page size in image indexes. */
	readonly VITE_DEF_CFG_RDR_PAGESIZEFORIMAGES: number
}

interface ImportMeta {
	readonly env: ImportMetaEnv
}
