<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.reader')"
				:to="`/readers/${reader.id}/edit`"
				icon="mdi-account dem-overlay-edit"
			/>
			<AppTask
				v-if="mayDelete"
				:label="$t('quickTasks.delete.reader')"
				:confirm="$t('quickTasks.delete.reader.confirm')"
				icon="mdi-account dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteType"
			/>
		</AppTasks>

		<SectionCard :loading="loading">
			<h1 class="display-1">
				<LetterIcon :letter="reader.name.charAt(0)" :color="reader.colour" />
				{{ reader.identifyingName }}
			</h1>
			<v-btn
				:to="`/readers/${reader.id}/favourites`"
				color="accent" class="my-4 me-4" small outlined
			>
				{{ $t('page.Reader.actions.favourites') }}
			</v-btn>
			<v-btn
				:to="`/readers/${reader.id}/readingList`"
				color="accent" class="my-4" small outlined
			>
				{{ $t('page.Reader.actions.readingList') }}
			</v-btn>
		</SectionCard>
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import LetterIcon from '@/components/LetterIcon'
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import readerService from '@/services/reader-service'

export default {
	name: 'ReaderView',

	components: {
		AppTask,
		AppTasks,
		LetterIcon,
		SectionCard
	},

	mixins: [modelViewMixin],

	metaInfo() {
		return {
			title: this.reader.identifyingName
		}
	},

	data() {
		return {
			reader: {},
			mayDelete: false,
			appTasksMenu: false
		}
	},

	methods: {
		async fetchData() {
			const readerP = readerService.findById(this.parsedId)
			this.mayDelete = await readerService.mayDeleteReader()
			this.reader = await readerP // Resolve calls in parallel
		},

		deleteType() {
			deleteStub(this,
				() => readerService.deleteModel(this.reader.id),
				'quickTasks.delete.reader.confirm.done',
				'ReaderIndex')
		}
	}
}
</script>
