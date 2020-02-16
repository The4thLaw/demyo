<template>
	<v-container fluid>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.derivative')"
				:to="`/derivatives/${derivative.id}/edit`"
				icon="mdi-pencil"
			/>
			<!--
			Adding an @click="appTasksMenu = false" causes the dialog to instantly
			disappear because the AppTask isn't rendered anymore
			-->
			<AppTask
				:label="$t('quickTasks.delete.derivative')"
				:confirm="$t('quickTasks.delete.derivative.confirm')"
				icon="mdi-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteDerivative"
			/>
			<AppTask
				:label="$t('quickTasks.add.images.to.derivative')"
				icon="mdi-camera"
				@click="appTasksMenu = false; dndDialog = true"
			/>
		</AppTasks>
		<DnDImage v-model="dndDialog" other-images-label="field.Derivative.images" @save="saveDndImages" />

		<SectionCard :loading="loading" :image="derivative.mainImage" :title="derivative.identifyingName">
			<div class="fieldSet">
				<v-row>
					<v-col v-if="derivative.series.id" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.series')" :value="derivative.series.id">
							<ModelLink :model="derivative.series" view="SeriesView" />
						</FieldValue>
					</v-col>
					<v-col v-if="derivative.album.id" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.album')" :value="derivative.album.id">
							<ModelLink :model="derivative.album" view="AlbumView" />
						</FieldValue>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.artist')" :value="derivative.artist.id">
							<ModelLink :model="derivative.artist" view="AuthorView" />
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.source')" :value="derivative.source.id">
							<ModelLink :model="derivative.source" view="DerivativeSourceView" />
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div class="fieldSet">
				<v-row>
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.type')" :value="derivative.type.id">
							<ModelLink :model="derivative.type" view="DerivativeTypeView" />
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.colours')" :value="derivative.colours">
							{{ derivative.colours }}
						</FieldValue>
					</v-col>
					<v-col v-if="sizeSpec" cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.size')" :value="true">
							{{ sizeSpec }}
						</FieldValue>
					</v-col>
					<template v-if="!sizeSpec">
						<v-col cols="12" md="4">
							<FieldValue :label="$t('field.Derivative.width')" :value="derivative.width">
								{{ derivative.width }}
							</FieldValue>
						</v-col>
						<v-col cols="12" md="4">
							<FieldValue :label="$t('field.Derivative.height')" :value="derivative.height">
								{{ derivative.height }}
							</FieldValue>
						</v-col>
						<v-col cols="12" md="4">
							<FieldValue :label="$t('field.Derivative.depth')" :value="derivative.depth">
								{{ derivative.depth }}
							</FieldValue>
						</v-col>
					</template>
				</v-row>
			</div>

			<div class="fieldSet">
				<v-row>
					<v-col cols="12" md="6" xl="3">
						<template v-if="derivative.number && derivative.total">
							<FieldValue :label="$t('field.Derivative.numberOverTotal')" :value="true">
								{{ derivative.number }} / {{ derivative.total }}
							</FieldValue>
						</template>
						<template v-if="!derivative.number || !derivative.total">
							<FieldValue :label="$t('field.Derivative.number')" :value="derivative.number">
								{{ derivative.number }}
							</FieldValue>
							<FieldValue :label="$t('field.Derivative.total')" :value="derivative.total">
								{{ derivative.total }}
							</FieldValue>
						</template>
					</v-col>
					<v-col cols="12" md="6" xl="3">
						<FieldValue :label="$t('field.Derivative.signed.view')" :value="true">
							{{ $t('field.Derivative.signed.value.' + derivative.signed) }}
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6" xl="3">
						<FieldValue :label="$t('field.Derivative.authorsCopy.view')" :value="true">
							{{ $t('field.Derivative.authorsCopy.value.' + derivative.authorsCopy) }}
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6" xl="3">
						<FieldValue :label="$t('field.Derivative.restrictedSale.view')" :value="true">
							{{ $t('field.Derivative.restrictedSale.value.' + derivative.restrictedSale) }}
						</FieldValue>
					</v-col>
					<v-col cols="12">
						<FieldValue :label="$t('field.Derivative.description')" :value="derivative.description">
							<div v-html="derivative.description" />
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div
				v-if="derivative.purchasePrice || derivative.acquisitionDate || derivative.prices.length > 0"
				class="fieldSet"
			>
				<v-row>
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.acquisitionDate')" :value="derivative.acquisitionDate">
							{{ $d(new Date(derivative.acquisitionDate), 'long') }}
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.purchasePrice')" :value="derivative.purchasePrice">
							{{ derivative.purchasePrice }}
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6">
						<FieldValue
							:label="$t('field.Derivative.prices.history')"
							:value="derivative.prices.length > 0"
						>
							<v-simple-table>
								<template v-slot:default>
									<thead>
										<tr>
											<th>{{ $t('field.Derivative.prices.date') }}</th>
											<th>{{ $t('field.Derivative.prices.price') }}</th>
										</tr>
									</thead>
									<tbody>
										<tr v-for="(price, index) in derivative.prices" :key="index">
											<td>{{ $d(new Date(price.date), 'long') }}</td>
											<td>{{ price.price }}</td>
										</tr>
									</tbody>
								</template>
							</v-simple-table>
						</FieldValue>
					</v-col>
				</v-row>
			</div>
		</SectionCard>

		<SectionCard v-if="derivative.images.length > 1" :loading="loading" :title="$t('page.Derivative.gallery')">
			<GalleryIndex :items="derivative.images">
				<template v-slot:default="slotProps">
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
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import derivativeService from '@/services/derivative-service'

export default {
	name: 'DerivativeView',

	components: {
		AppTask,
		AppTasks,
		DnDImage,
		FieldValue,
		GalleryIndex,
		ModelLink,
		SectionCard
	},

	mixins: [modelViewMixin],

	metaInfo() {
		return {
			title: this.derivative.identifyingName
		}
	},

	data() {
		return {
			appTasksMenu: false,
			dndDialog: false,
			derivative: {
				series: {}
			}
		}
	},

	computed: {
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

			if (!this.derivative.series) {
				this.derivative.series = {}
			}

			if (!this.derivative.prices) {
				this.derivative.prices = []
			}
		},

		async saveDndImages(data) {
			let ok = await derivativeService.saveFilepondImages(this.derivative.id, data.otherImages)
			if (ok) {
				this.$store.dispatch('ui/showSnackbar', this.$t('draganddrop.snack.confirm'))
				this.fetchDataInternal()
			} else {
				this.$store.dispatch('ui/showSnackbar', this.$t('core.exception.api.title'))
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
