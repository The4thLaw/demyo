<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.derivativeType')"
				:to="`/derivativeTypes/${type.id}/edit`"
				icon="mdi-brush dem-overlay-edit"
			/>
			<AppTask
				v-if="count === 0"
				:label="$t('quickTasks.delete.derivativeType')"
				:confirm="$t('quickTasks.delete.derivativeType.confirm')"
				icon="mdi-brush dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteType"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="type.identifyingName">
			<v-btn
				v-if="count > 0"
				:to="{ name: 'DerivativeIndex', query: { withType: type.id } }"
				color="accent" class="my-4" small outlined
			>
				{{ $tc('page.DerivativeType.viewDerivatives', count) }}
			</v-btn>
			<v-alert
				v-if="count === 0"
				border="left" type="info" text class="my-4"
			>
				{{ $t('page.DerivativeType.noDerivatives') }}
			</v-alert>
		</SectionCard>
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import typeService from '@/services/derivative-type-service'

export default {
	name: 'DerivativeTypeView',

	components: {
		AppTask,
		AppTasks,
		SectionCard
	},

	mixins: [modelViewMixin],

	metaInfo() {
		return {
			title: this.type.identifyingName
		}
	},

	data() {
		return {
			type: {},
			count: -1,
			appTasksMenu: false
		}
	},

	methods: {
		async fetchData() {
			const typeP = typeService.findById(this.parsedId)
			this.count = await typeService.countDerivatives(this.parsedId)
			this.type = await typeP // Resolve calls in parallel
		},

		deleteType() {
			deleteStub(this,
				() => typeService.deleteModel(this.type.id),
				'quickTasks.delete.derivativeType.confirm.done',
				'DerivativeTypeIndex')
		}
	}
}
</script>
