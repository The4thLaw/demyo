<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.derivativeType')"
				:to="`/derivativeTypes/${type.id}/edit`"
				icon="mdi-pencil"
			/>
			<AppTask
				:label="$t('quickTasks.delete.derivativeType')"
				:confirm="$t('quickTasks.delete.derivativeType.confirm')"
				icon="mdi-account-minus"
				@cancel="appTasksMenu = false"
				@confirm="deleteType"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="type.identifyingName">
			<v-btn
				:to="{ name: 'DerivativeIndex', query: { withType: type.id } }"
				color="accent" class="my-4" small outlined
			>
				{{ $t('page.DerivativeType.viewDerivatives') }}
			</v-btn>
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
			appTasksMenu: false
		}
	},

	methods: {
		async fetchData() {
			this.type = await typeService.findById(this.parsedId)
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
