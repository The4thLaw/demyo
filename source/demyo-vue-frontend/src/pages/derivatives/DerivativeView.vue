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
				@confirm="deleteDerivative"
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
							{{ derivative.purchasePrice | price(currency) }}
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

<script>
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import DnDImage from '@/components/DnDImage'
import FieldValue from '@/components/FieldValue'
import GalleryIndex from '@/components/GalleryIndex'
import ModelLink from '@/components/ModelLink'
import PriceTable from '@/components/PriceTable'
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/actions'
import i18nMixin from '@/mixins/i18n'
import modelViewMixin from '@/mixins/model-view'
import derivativeService from '@/services/derivative-service'
import { useUiStore } from '@/stores/ui'

export default {
	name: 'DerivativeView',

	components: {
		AppTask,
		AppTasks,
		DnDImage,
		FieldValue,
		GalleryIndex,
		ModelLink,
		PriceTable,
		SectionCard
	},

	mixins: [i18nMixin, modelViewMixin],

	metaInfo() {
		return {
			title: this.derivative.identifyingName
		}
	},

	data() {
		return {
			uiStore: useUiStore(),

			appTasksMenu: false,
			dndDialog: false,
			derivative: {
				series: {},
				album: {},
				artist: {},
				source: {}
			}
		}
	},

	computed: {
		hasPrices() {
			return this.derivative.prices && this.derivative.prices.length > 0
		},

		hasImages() {
			return this.derivative.images && this.derivative.images.length > 1
		},

		sizeSpec() {
			if (this.derivative.width && this.derivative.height && this.derivative.depth) {
				return `${this.derivative.width} x ${this.derivative.height} x ${this.derivative.depth}`
			}

			if (this.derivative.width && this.derivative.height) {
				return `${this.derivative.width} x ${this.derivative.height}`
			}

			return null
		}
	},

	methods: {
		async fetchData() {
			this.derivative = await derivativeService.findById(this.parsedId)
		},

		async saveDndImages(data) {
			const ok = await derivativeService.saveFilepondImages(this.derivative.id, data.otherImages)
			if (ok) {
				this.uiStore.showSnackbar(this.$t('draganddrop.snack.confirm'))
				this.fetchDataInternal()
			} else {
				this.uiStore.showSnackbar(this.$t('core.exception.api.title'))
			}
		},

		deleteDerivative() {
			deleteStub(this,
				() => derivativeService.deleteModel(this.derivative.id),
				'quickTasks.delete.derivative.confirm.done',
				'DerivariveIndex')
		}
	}
}
</script>
