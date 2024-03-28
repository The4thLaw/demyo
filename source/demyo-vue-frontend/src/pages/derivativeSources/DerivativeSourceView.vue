<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.derivativeSource')"
				:to="`/derivativeSources/${source.id}/edit`"
				icon="mdi-tooltip-account dem-overlay-edit"
			/>
			<AppTask
				v-if="count === 0"
				:label="$t('quickTasks.delete.derivativeSource')"
				:confirm="$t('quickTasks.delete.derivativeSource.confirm')"
				icon="mdi-tooltip-account dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteSource"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="source.identifyingName">
			<v-row>
				<v-col v-if="source.owner" cols="12" md="6">
					<FieldValue :label="$t('field.DerivativeSource.owner')">
						{{ source.owner }}
					</FieldValue>
				</v-col>
				<v-col v-if="source.phoneNumber" cols="12" md="6">
					<FieldValue :label="$t('field.DerivativeSource.phoneNumber')">
						<a :href="phoneLink">{{ source.phoneNumber }}</a>
					</FieldValue>
				</v-col>
			</v-row>
			<v-row>
				<v-col v-if="source.website" cols="12" md="6">
					<FieldValue :label="$t('field.DerivativeSource.website')">
						<a :href="source.website">{{ source.website }}</a>
					</FieldValue>
				</v-col>
				<v-col v-if="source.email" cols="12" md="6">
					<FieldValue :label="$t('field.DerivativeSource.email')">
						<a :href="'mailto:' + source.email">{{ source.email }}</a>
					</FieldValue>
				</v-col>
			</v-row>
			<FieldValue v-if="source.history" :label="$t('field.DerivativeSource.history')">
				<!-- eslint-disable-next-line vue/no-v-html -->
				<div v-html="source.history" />
			</FieldValue>
			<v-btn
				v-if="count > 0"
				:to="{ name: 'DerivativeIndex', query: { withSource: source.id } }"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.DerivativeSource.viewDerivatives', count) }}
			</v-btn>
			<v-alert
				v-if="count === 0"
				border="start" type="info" text class="my-4"
			>
				{{ $t('page.DerivativeSource.noDerivatives') }}
			</v-alert>
		</SectionCard>

		<SectionCard v-if="loading || source.address" :loading="loading" :title="$t('field.DerivativeSource.address')">
			<div class="dem-multiline-value" v-text="source.address" />
			<div class="v-DerivativeSourceView__map">
				<!-- Thanks to https://www.embedgooglemap.net -->
				<div class="v-DerivativeSourceView__mapCanvas">
					<iframe
						:title="$t('field.DerivativeSource.address')"
						:src="mapUrl" class="v-DerivativeSourceView__mapFrame"
					/>
				</div>
			</div>
		</SectionCard>
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask.vue'
import AppTasks from '@/components/AppTasks.vue'
import FieldValue from '@/components/FieldValue.vue'
import SectionCard from '@/components/SectionCard.vue'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import sourceService from '@/services/derivative-source-service'

export default {
	name: 'DerivativeSourceView',

	components: {
		AppTask,
		AppTasks,
		FieldValue,
		SectionCard
	},

	mixins: [modelViewMixin],

	data() {
		return {
			source: {},
			count: -1,
			appTasksMenu: false
		}
	},

	head() {
		return {
			title: this.source.identifyingName
		}
	},

	computed: {
		phoneLink() {
			if (!this.source.phoneNumber) {
				return ''
			}

			return 'tel:' + this.source.phoneNumber.replaceAll(/[ /-]/g, '')
		},

		mapUrl() {
			const encoded = encodeURI(`${this.source.name}, ${this.source.address}`)
			return 'https://maps.google.com/maps?t=&z=15&ie=UTF8&iwloc=&output=embed&q=' + encoded
		}
	},

	methods: {
		async fetchData() {
			const sourceP = sourceService.findById(this.parsedId)
			this.count = await sourceService.countDerivatives(this.parsedId)
			this.source = await sourceP // Resolve calls in parallel
		},

		deleteSource() {
			deleteStub(this,
				() => sourceService.deleteModel(this.source.id),
				'quickTasks.delete.derivativeSource.confirm.done',
				'DerivativeSourceIndex')
		}
	}
}
</script>

<style lang="scss">
.v-DerivativeSourceView__map {
	position: relative;
	text-align: right;
	margin-top: 1em;
	width: 100%;
}

.v-DerivativeSourceView__mapCanvas {
	overflow: hidden;
	background: none !important;
}

.v-DerivativeSourceView__mapCanvas,
.v-DerivativeSourceView__mapFrame {
	width: 100%;
}

.v-DerivativeSourceView__map,
.v-DerivativeSourceView__mapCanvas,
.v-DerivativeSourceView__mapFrame {
	height: 3000px;
	max-height: 80vh;
}

.v-DerivativeSourceView__mapFrame {
	overflow: hidden;
	border: 0;
	margin: 0;
}
</style>
