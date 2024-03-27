<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.image')" :to="`/images/${image.id}/edit`"
				icon="mdi-camera dem-overlay-edit"
			/>
			<AppTask
				:label="$t('quickTasks.delete.image')"
				:confirm="$t('quickTasks.delete.image.confirm')"
				icon="mdi-camera dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteImage"
			/>
		</AppTasks>

		<SectionCard :loading="mainLoading" class="c-ImageView__image">
			<img
				:src="imageUrl"
				:alt="image.description"
			>
			<p>{{ image.description }}</p>
		</SectionCard>

		<SectionCard
			v-if="dependenciesLoading || hasDependencies"
			:loading="dependenciesLoading"
			:title="$t('page.Image.usedIn')"
		>
			<v-list>
				<template v-for="(value, modelType) in parsedDependencies">
					<v-list-group
						v-if="value.length > 0"
						:key="modelType"
						:value="true"
					>
						<template #activator="{ props }">
							<v-list-item v-bind="props" :title="$t('page.Image.usedIn.' + modelType)" color="primary" />
						</template>

						<v-list-item v-for="item in value" :key="item.id" :to="`/${modelType}/${item.id}/view`">
							<template #title>
								<template v-if="modelType === 'albums' && item.series">
									{{ item.series.name }} -
								</template>
								{{ item.identifyingName }}
							</template>
						</v-list-item>
					</v-list-group>
				</template>
			</v-list>
		</SectionCard>
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask.vue'
import AppTasks from '@/components/AppTasks.vue'
import SectionCard from '@/components/SectionCard.vue'
import { deleteStub } from '@/helpers/actions'
import { getBaseImageUrl } from '@/helpers/images'
import modelViewMixin from '@/mixins/model-view'
import imageService from '@/services/image-service'

export default {
	name: 'ImageView',

	components: {
		AppTask,
		AppTasks,
		SectionCard
	},

	mixins: [modelViewMixin],

	data() {
		return {
			mainLoading: true,
			dependenciesLoading: true,
			image: {},
			dependencies: {},
			appTasksMenu: false
		}
	},

	head() {
		return {
			title: this.image.identifyingName
		}
	},

	computed: {
		imageUrl() {
			return getBaseImageUrl(this.image)
		},

		hasDependencies() {
			return this.dependencies.albumCovers
				|| this.dependencies.albumOtherImages
				|| this.dependencies.authors
				|| this.dependencies.collections
				|| this.dependencies.derivatives
				|| this.dependencies.publishers
		},

		parsedDependencies() {
			const covs = this.dependencies.albumCovers || []
			const other = this.dependencies.albumOtherImages || []
			// This won't work if an image is used twice in an Album, although that hardly makes any sense
			return {
				albums: [...covs, ...other],
				authors: this.dependencies.authors || [],
				collections: this.dependencies.collections || [],
				derivatives: this.dependencies.derivatives || [],
				publishers: this.dependencies.publishers || []
			}
		}
	},

	methods: {
		async fetchData() {
			this.mainLoading = true

			this.image = await imageService.findById(this.parsedId)
			this.mainLoading = false

			this.dependencies = await imageService.getImageDependencies(this.parsedId)
			this.dependenciesLoading = false
		},

		deleteImage() {
			deleteStub(this,
				() => imageService.deleteModel(this.image.id),
				'quickTasks.delete.image.confirm.done',
				'ImageIndex')
		}
	}
}
</script>

<style lang="less">
.c-ImageView__image {
	text-align: center;

	p {
		margin-top: 32px;
	}

	img {
		max-width: 100%;
	}
}
</style>
