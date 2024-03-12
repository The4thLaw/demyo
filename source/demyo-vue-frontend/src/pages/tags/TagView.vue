<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.tag')"
				:to="`/tags/${tag.id}/edit`"
				icon="mdi-tag dem-overlay-edit"
			/>
			<!-- Note that we can always delete tags -->
			<AppTask
				:label="$t('quickTasks.delete.tag')"
				:confirm="$t('quickTasks.delete.tag.confirm')"
				icon="mdi-tag dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteTag"
			/>
		</AppTasks>

		<SectionCard :loading="loading">
			<h1 v-if="!loading" class="text-h4">
				<span class="d-Tag" :style="style">
					{{ tag.identifyingName }}
				</span>
			</h1>
			<FieldValue v-if="tag.description" :label="$t('field.Tag.description')">
				<!-- eslint-disable-next-line vue/no-v-html -->
				<div v-html="tag.description" />
			</FieldValue>
			<v-btn
				v-if="count > 0"
				:to="{ name: 'AlbumIndex', query: { withTag: tag.id } }"
				color="accent" class="my-4" small outlined
			>
				{{ $tc('page.Tag.viewAlbums', count) }}
			</v-btn>
			<v-alert
				v-if="count === 0"
				border="left" type="info" text class="my-4"
			>
				{{ $t('page.Tag.noAlbums') }}
			</v-alert>
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
import tagService from '@/services/tag-service'

export default {
	name: 'TagView',

	components: {
		AppTask,
		AppTasks,
		FieldValue,
		SectionCard
	},

	mixins: [modelViewMixin],

	metaInfo() {
		return {
			title: this.tag.identifyingName
		}
	},

	data() {
		return {
			tag: {},
			count: -1,
			appTasksMenu: false
		}
	},

	computed: {
		style() {
			const style = {}
			if (this.tag.fgColour) {
				style.color = this.tag.fgColour
			}
			if (this.tag.bgColour) {
				style['background-color'] = this.tag.bgColour
			}
			return style
		}
	},

	methods: {
		async fetchData() {
			const tagP = tagService.findById(this.parsedId)
			this.count = await tagService.countAlbums(this.parsedId)
			this.tag = await tagP // Resolve calls in parallel
		},

		deleteTag() {
			deleteStub(this,
				() => tagService.deleteModel(this.tag.id),
				'quickTasks.delete.tag.confirm.done',
				'TagIndex')
		}
	}
}
</script>
