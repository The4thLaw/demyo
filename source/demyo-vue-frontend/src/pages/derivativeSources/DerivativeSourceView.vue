<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.derivativeSource')"
				:to="`/derivativeSources/${source.id}/edit`"
				icon="mdi-tooltip-account dem-overlay-edit"
			/>
			<AppTask
				v-if="derivCount === 0"
				:label="$t('quickTasks.delete.derivativeSource')"
				:confirm="$t('quickTasks.delete.derivativeSource.confirm')"
				icon="mdi-tooltip-account dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="source.identifyingName">
			<v-row>
				<v-col v-if="source.owner" cols="12" md="6">
					<FieldValue :value="source.owner" label-key="field.DerivativeSource.owner" type="text" />
				</v-col>
				<v-col cols="12" md="6">
					<FieldValue :label="$t('field.DerivativeSource.active.view')">
						{{ $t(`field.DerivativeSource.active.value.${source.active}`) }}
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
					<FieldValue :value="source.website" label-key="field.DerivativeSource.website" type="url" />
				</v-col>
				<v-col v-if="source.email" cols="12" md="6">
					<FieldValue :label="$t('field.DerivativeSource.email')">
						<a :href="'mailto:' + source.email">{{ source.email }}</a>
					</FieldValue>
				</v-col>
			</v-row>
			<FieldValue :value="source.history" label-key="field.DerivativeSource.history" type="rich-text" />
			<v-btn
				v-if="derivCount > 0"
				:to="{ name: 'DerivativeIndex', query: { withSource: source.id } }"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.DerivativeSource.viewDerivatives', derivCount) }}
			</v-btn>
			<v-alert
				v-if="derivCount === 0"
				border="start" type="info" text class="my-4"
				variant="outlined"
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

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import sourceService from '@/services/derivative-source-service'

const derivCount = ref(-1)

async function fetchData(id: number): Promise<DerivativeSource> {
	const sourceP = sourceService.findById(id)
	derivCount.value = await sourceService.countDerivatives(id)
	return sourceP // Resolve calls in parallel
}

const { model: source, loading, appTasksMenu, deleteModel }
	= useSimpleView(fetchData, sourceService,
		'quickTasks.delete.derivativeSource.confirm.done', 'DerivativeSourceIndex')

const phoneLink = computed(() => {
	if (!source.value.phoneNumber) {
		return ''
	}

	return 'tel:' + source.value.phoneNumber.replaceAll(/[ /-]/g, '')
})

const mapUrl = computed(() => {
	const encoded = encodeURI(`${source.value.name}, ${source.value.address}`)
	return 'https://maps.google.com/maps?t=&z=15&ie=UTF8&iwloc=&output=embed&q=' + encoded
})
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
