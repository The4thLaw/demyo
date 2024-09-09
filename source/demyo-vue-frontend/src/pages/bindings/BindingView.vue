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
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.Binding.viewAlbums', count) }}
			</v-btn>
			<v-alert
				v-if="count === 0"
				border="start" type="info" text class="my-4"
			>
				{{ $t('page.Binding.noAlbums') }}
			</v-alert>
		</SectionCard>
	</v-container>
</template>

<script>
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import bindingService from '@/services/binding-service'

// TODO: Vue 3: Try to extract such simple views in a composable
export default {
	name: 'BindingView',

	mixins: [modelViewMixin],

	data() {
		return {
			binding: {},
			count: -1,
			appTasksMenu: false
		}
	},

	head() {
		return {
			title: this.binding.identifyingName
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
