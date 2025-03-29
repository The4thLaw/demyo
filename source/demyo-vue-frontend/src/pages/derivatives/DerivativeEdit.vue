<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Derivative.origin')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="derivative.series.id" :items="allSeries" label-key="field.Derivative.series"
							:loading="seriesLoading" refreshable clearable
							:rules="rules.albumOrSeries" @update:modelValue="loadAlbums(); formRef.validate()" @refresh="loadSeries"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="derivative.album.id" :items="relatedAlbums" :loading="relatedAlbumsLoading"
							label-key="field.Derivative.album" refreshable clearable
							:rules="rules.albumOrSeries" @update:modelValue="formRef.validate()" @refresh="loadAlbums"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="derivative.artist.id" :items="authors" :loading="authorsLoading"
							label-key="field.Derivative.artist" refreshable @refresh="loadAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="derivative.source.id" :items="sources" :loading="sourcesLoading"
							label-key="field.Derivative.source" refreshable @refresh="loadSources"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Derivative.format')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="derivative.type.id" :items="types" :loading="typesLoading"
							label-key="field.Derivative.type" :rules="rules.type"
							refreshable @refresh="loadTypes"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="derivative.colours" :label="$t('field.Derivative.colours')"
							:rules="rules.colours" type="number" inputmode="numeric"
						/>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="derivative.width" :label="$t('field.Derivative.width')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="derivative.height" :label="$t('field.Derivative.height')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="derivative.depth" :label="$t('field.Derivative.depth')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Derivative.description')" :loading="loading">
				<v-row>
					<v-col cols="12" sm="6" lg="2">
						<v-text-field
							v-model="derivative.number" :label="$t('field.Derivative.number')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" sm="6" lg="2">
						<v-text-field
							v-model="derivative.total" :label="$t('field.Derivative.total')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" sm="6" md="4" lg="2">
						<v-checkbox v-model="derivative.signed" :label="$t('field.Derivative.signed.edit')" />
					</v-col>
					<v-col cols="12" sm="6" md="4" lg="3">
						<v-checkbox v-model="derivative.authorsCopy" :label="$t('field.Derivative.authorsCopy.edit')" />
					</v-col>
					<v-col cols="12" sm="6" md="4" lg="3">
						<v-checkbox
							v-model="derivative.restrictedSale" :label="$t('field.Derivative.restrictedSale.edit')"
						/>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Derivative.description') }}</label>
						<RichTextEditor v-model="derivative.description" />
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="derivative.images" :items="images" :loading="imagesLoading"
							multiple clearable
							label-key="field.Derivative.images" refreshable @refresh="loadImages"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Derivative.acquisition')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="derivative.acquisitionDate" :label="$t('field.Derivative.acquisitionDate')"
							type="date"
						/>
						<CurrencyField v-model="derivative.purchasePrice" label-key="field.Derivative.purchasePrice" />
					</v-col>
					<PriceManagement v-model="derivative" model-name="Derivative" cols="12" md="6" />
				</v-row>
			</SectionCard>

			<FormActions v-if="!loading" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleEdit } from '@/composables/model-edit'
import {
	useRefreshableAuthors, useRefreshableDerivativeSources, useRefreshableDerivativeTypes,
	useRefreshableImages, useRefreshableSeries
} from '@/composables/refreshable-models'
import { getParsedRouteParam } from '@/helpers/route'
import { integer, mandatory } from '@/helpers/rules'
import derivativeService from '@/services/derivative-service'
import seriesService from '@/services/series-service'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'

const i18n = useI18n()
const route = useRoute()

const {authors, authorsLoading, loadAuthors} = useRefreshableAuthors()
const {sources, sourcesLoading, loadSources} = useRefreshableDerivativeSources()
const {types, typesLoading, loadTypes} = useRefreshableDerivativeTypes()
const {images, imagesLoading, loadImages} = useRefreshableImages()
const {series: allSeries, seriesLoading, loadSeries} = useRefreshableSeries()

const relatedAlbums = ref([] as Album[])
const relatedAlbumsLoading = ref(false)

async function loadAlbums(derivativeParam?: Partial<Derivative>) {
	if (!derivativeParam) {
		derivativeParam = derivative.value
	}
	relatedAlbumsLoading.value = true
	relatedAlbums.value = await seriesService.findAlbumsForList(derivativeParam.series?.id)
	if (derivativeParam.album?.id) {
		// If the current album ID is not in the returned list, clear it
		if (!relatedAlbums.value.find(val => val.id === derivativeParam.album?.id)) {
			derivativeParam.album.id = undefined as unknown as number
		}
	}
	relatedAlbumsLoading.value = false
}

async function fetchData(id: number|undefined): Promise<Partial<Derivative>> {
	if (id) {
		const derivative = await derivativeService.findById(id)
		loadAlbums(derivative)
		return Promise.resolve(derivative)
	}

	const derivative: Partial<Derivative> = {
		series: {} as Series,
		album: {} as Album,
		artist: {} as Author,
		source: {} as DerivativeSource,
		type: {} as DerivativeType,
		prices: []
	}

	const toSeries = getParsedRouteParam(route.query.toSeries)
	if (toSeries && derivative.series) {
		derivative.series.id = toSeries
	}

	const toAlbum = getParsedRouteParam(route.query.toAlbum)
	if (toAlbum && derivative.album) {
		derivative.album.id = toAlbum
	}

	const toArtist = getParsedRouteParam(route.query.toArtist)
	if (toArtist && derivative.artist) {
		derivative.artist.id = toArtist
	}


	loadAlbums(derivative)

	return Promise.resolve(derivative)
}

const {model: derivative, loading, save, reset, formRef} = useSimpleEdit(fetchData, derivativeService,
	[loadAuthors, loadImages, loadSources, loadTypes, loadSeries],
	'title.add.derivative', 'title.edit.derivative', 'DerivativeView')

function oneNotNull() {
	// In addition to this rule, an @input handler forces full form validation when one of the relevant
	// input changes. This guarantees that errors on other fields are cleared when only one changes
	if (!derivative.value.series?.id && !derivative.value.album?.id) {
		return i18n.t('validation.Derivative.albumOrSeries')
	}
	return true
}

const rules = {
	type: [
		mandatory()
	],
	colours: [
		integer()
	],
	albumOrSeries: [
		oneNotNull
	]
}
</script>
