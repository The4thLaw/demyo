<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.derivativeType')"
				:to="`/derivativeTypes/${type.id}/edit`"
				icon="mdi-brush dem-overlay-edit"
			/>
			<AppTask
				v-if="derivativeCount === 0"
				:label="$t('quickTasks.delete.derivativeType')"
				:confirm="$t('quickTasks.delete.derivativeType.confirm')"
				icon="mdi-brush dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="type.identifyingName">
			<v-btn
				v-if="derivativeCount > 0"
				:to="{ name: 'DerivativeIndex', query: { withType: type.id } }"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.DerivativeType.viewDerivatives', derivativeCount) }}
			</v-btn>
			<v-alert
				v-if="derivativeCount === 0"
				border="start" type="info" text class="my-4"
			>
				{{ $t('page.DerivativeType.noDerivatives') }}
			</v-alert>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import typeService from '@/services/derivative-type-service'

const derivativeCount = ref(-1)

async function fetchData(id: number): Promise<DerivativeType> {
	const typeP = typeService.findById(id)
	derivativeCount.value = await typeService.countDerivatives(id)
	return typeP
}

const { model: type, deleteModel, appTasksMenu, loading } = useSimpleView(fetchData, typeService,
	'quickTasks.delete.derivativeType.confirm.done', 'TypeIndex')
</script>
