<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.binding')"
				:to="`/bindings/${binding.id}/edit`"
				icon="mdi-brush dem-overlay-edit"
			/>
			<AppTask
				v-if="count === 0"
				:label="$t('quickTasks.delete.binding')"
				:confirm="$t('quickTasks.delete.binding.confirm')"
				icon="mdi-brush dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteBinding"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="binding.identifyingName">
			<v-btn
				v-if="count > 0"
				:to="{ name: 'AlbumIndex', query: { withBinding: binding.id } }"
				color="accent" class="my-4" small outlined
			>
				{{ $tc('page.Binding.viewAlbums', count) }}
			</v-btn>
			<v-alert
				v-if="count === 0"
				border="left" type="info" text class="my-4"
			>
				{{ $t('page.Binding.noAlbums') }}
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
import bindingService from '@/services/binding-service'

export default {
	name: 'BindingView',

	components: {
		AppTask,
		AppTasks,
		SectionCard
	},

	mixins: [modelViewMixin],

	metaInfo() {
		return {
			title: this.binding.identifyingName
		}
	},

	data() {
		return {
			binding: {},
			count: -1,
			appTasksMenu: false
		}
	},

	methods: {
		async fetchData() {
			const bindingP = bindingService.findById(this.parsedId)
			this.count = await bindingService.countAlbums(this.parsedId)
			this.binding = await bindingP // Resolve calls in parallel
		},

		deleteBinding() {
			deleteStub(this,
				() => bindingService.deleteModel(this.binding.id),
				'quickTasks.delete.binding.confirm.done',
				'BindingIndex')
		}
	}
}
</script>
