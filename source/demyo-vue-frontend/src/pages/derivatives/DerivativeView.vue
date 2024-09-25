<template>
	<v-container fluid>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.derivative')"
				:to="`/derivatives/${derivative.id}/edit`"
				icon="mdi-image-frame dem-overlay-edit"
			/>
			<!--
			Adding an @click="appTasksMenu = false" causes the dialog to instantly
			disappear because the AppTask isn't rendered anymore
			-->
			<AppTask
				:label="$t('quickTasks.delete.derivative')"
				:confirm="$t('quickTasks.delete.derivative.confirm')"
				icon="mdi-image-frame dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
			<AppTask
				:label="$t('quickTasks.add.images.to.derivative')"
				icon="mdi-camera dem-overlay-add"
				@click="appTasksMenu = false; dndDialog = true"
			/>
		</AppTasks>
		<DnDImage v-model="dndDialog" other-images-label="field.Derivative.images" @save="saveDndImages" />

		<SectionCard :loading="loading" :image="derivative.mainImage" :title="derivative.identifyingName">
			<div class="dem-fieldset">
				<v-row>
					<v-col v-if="derivative.series.id" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.series')">
							<ModelLink :model="derivative.series" view="SeriesView" />
						</FieldValue>
					</v-col>
					<v-col v-if="derivative.album.id" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.album')">
							<ModelLink :model="derivative.album" view="AlbumView" />
						</FieldValue>
					</v-col>
				</v-row>
				<v-row>
					<v-col v-if="derivative.artist.id" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.artist')">
							<ModelLink :model="derivative.artist" view="AuthorView" />
						</FieldValue>
					</v-col>
					<v-col v-if="derivative.source.id" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.source')">
							<ModelLink :model="derivative.source" view="DerivativeSourceView" />
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div class="dem-fieldset">
				<v-row>
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.type')">
							<ModelLink :model="derivative.type" view="DerivativeTypeView" />
						</FieldValue>
					</v-col>
					<v-col v-if="derivative.colours" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.colours')">
							{{ derivative.colours }}
						</FieldValue>
					</v-col>
				</v-row>
				<v-row>
					<v-col v-if="sizeSpec" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.size')">
							{{ sizeSpec }}
						</FieldValue>
					</v-col>
					<template v-if="!sizeSpec">
						<v-col cols="12" md="4">
							<FieldValue v-if="derivative.width" :label="$t('field.Derivative.width')">
								{{ derivative.width }}
							</FieldValue>
						</v-col>
						<v-col cols="12" md="4">
							<FieldValue v-if="derivative.height" :label="$t('field.Derivative.height')">
								{{ derivative.height }}
							</FieldValue>
						</v-col>
						<v-col cols="12" md="4">
							<FieldValue v-if="derivative.depth" :label="$t('field.Derivative.depth')">
								{{ derivative.depth }}
							</FieldValue>
						</v-col>
					</template>
				</v-row>
			</div>

			<div class="dem-fieldset">
				<v-row>
					<v-col cols="12" md="6" xl="3">
						<template v-if="derivative.number && derivative.total">
							<FieldValue :label="$t('field.Derivative.numberOverTotal')">
								{{ $n(derivative.number) }} / {{ $n(derivative.total) }}
							</FieldValue>
						</template>
						<template v-if="!derivative.number || !derivative.total">
							<FieldValue v-if="derivative.number" :label="$t('field.Derivative.number')">
								{{ derivative.number }}
							</FieldValue>
							<FieldValue v-if="derivative.total" :label="$t('field.Derivative.total')">
								{{ derivative.total }}
							</FieldValue>
						</template>
					</v-col>
					<v-col cols="12" md="6" xl="3">
						<FieldValue :label="$t('field.Derivative.signed.view')">
							{{ $t('field.Derivative.signed.value.' + derivative.signed) }}
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6" xl="3">
						<FieldValue :label="$t('field.Derivative.authorsCopy.view')">
							{{ $t('field.Derivative.authorsCopy.value.' + derivative.authorsCopy) }}
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6" xl="3">
						<FieldValue :label="$t('field.Derivative.restrictedSale.view')">
							{{ $t('field.Derivative.restrictedSale.value.' + derivative.restrictedSale) }}
						</FieldValue>
					</v-col>
					<v-col cols="12">
						<FieldValue v-if="derivative.description" :label="$t('field.Derivative.description')">
							<!-- eslint-disable-next-line vue/no-v-html -->
							<div v-html="derivative.description" />
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div
				v-if="derivative.purchasePrice || derivative.acquisitionDate || hasPrices"
				class="dem-fieldset"
			>
				<v-row>
					<v-col v-if="derivative.acquisitionDate" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.acquisitionDate')">
							{{ $d(new Date(derivative.acquisitionDate), 'long') }}
						</FieldValue>
					</v-col>
					<v-col v-if="derivative.purchasePrice" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.purchasePrice')">
							{{ qualifiedPurchasePrice }}
						</FieldValue>
					</v-col>
					<v-col v-if="hasPrices" cols="12" md="6">
						<PriceTable :prices="derivative.prices" model-name="Derivative" />
					</v-col>
				</v-row>
			</div>
		</SectionCard>

		<SectionCard v-if="hasImages" :loading="loading" :title="$t('page.Derivative.gallery')">
			<GalleryIndex :items="derivative.images">
				<template #default="slotProps">
					<router-link :to="`/images/${slotProps.item.id}/view`">
						{{ slotProps.item.identifyingName }}
					</router-link>
				</template>
			</GalleryIndex>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useCurrency } from '@/composables/currency'
import { useSimpleView } from '@/composables/model-view'
import derivativeService from '@/services/derivative-service'
import { useUiStore } from '@/stores/ui'
import { useI18n } from 'vue-i18n'

const dndDialog = ref(false)

async function fetchData(id: number): Promise<Derivative> {
	return derivativeService.findById(id)
}

const {model: derivative, appTasksMenu, deleteModel, loading, loadData} = useSimpleView(fetchData, derivativeService,
	'quickTasks.delete.derivative.confirm.done', 'DerivariveIndex')

const hasPrices = computed(() => derivative.value.prices?.length > 0)
const hasImages = computed(() => derivative.value.images?.length > 1)
const sizeSpec = computed(() => {
	if (derivative.value.width && derivative.value.height && derivative.value.depth) {
		return `${derivative.value.width} x ${derivative.value.height} x ${derivative.value.depth}`
	}

	if (derivative.value.width && derivative.value.height) {
		return `${derivative.value.width} x ${derivative.value.height}`
	}

	return null
})

const { qualifiedPrice: qualifiedPurchasePrice } = useCurrency(derivative.value.purchasePrice)

const uiStore = useUiStore()
const i18n = useI18n()
async function saveDndImages(data: FilePondData) {
	const ok = await derivativeService.saveFilepondImages(derivative.value.id, data.otherImages)
	if (ok) {
		uiStore.showSnackbar(i18n.t('draganddrop.snack.confirm'))
		loadData()
	} else {
		uiStore.showSnackbar(i18n.t('core.exception.api.title'))
	}
}
</script>
