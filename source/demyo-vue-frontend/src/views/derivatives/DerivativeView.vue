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
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.series')" :value="derivative.series.id">
							<router-link :to="`/series/${derivative.series.id}/view`">
								{{ derivative.series.identifyingName }}
							</router-link>
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.album')" :value="derivative.album.id">
							<router-link :to="`/albums/${derivative.album.id}/view`">
								{{ derivative.album.identifyingName }}
							</router-link>
						</FieldValue>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.artist')" :value="derivative.artist.id">
							<router-link :to="`/authors/${derivative.artist.id}/view`">
								{{ derivative.artist.identifyingName }}
							</router-link>
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6">
						<FieldValue :label="$t('field.Derivative.source')" :value="derivative.source.id">
							<router-link :to="`/derivativeSources/${derivative.source.id}/view`">
								{{ derivative.source.identifyingName }}
							</router-link>
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div class="fieldSet">
				<v-row>
					<v-col cols="12" md="6">
					</v-col>
				</v-row>
				#@mdlGridCell([6,4,4])
		#entityFieldLinkedModel('field.Derivative.type' $derivative.type)
		#entityFieldText('field.Derivative.colours' $derivative.colours)
	#end
	#@mdlGridCell([6,4,4])
		#if ($derivative.width && $derivative.height && $derivative.depth)
			#entityFieldText('field.Derivative.size' "#number($derivative.width) x #number($derivative.height) x #number($derivative.depth)")
		#elseif ($derivative.width && $derivative.height)
			#entityFieldText('field.Derivative.size' "#number($derivative.width) x #number($derivative.height)")
		#else
			#entityFieldNumber('field.Derivative.width' $derivative.width)
			#entityFieldNumber('field.Derivative.height' $derivative.height)
			#entityFieldNumber('field.Derivative.depth' $derivative.depth)
		#end
	#end
			</div>
		</SectionCard>
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import DnDImage from '@/components/DnDImage'
import FieldValue from '@/components/FieldValue'
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

	methods: {
		async fetchData() {
			this.derivative = await derivativeService.findById(this.parsedId)

			if (!this.derivative.series) {
				this.derivative.series = {}
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
