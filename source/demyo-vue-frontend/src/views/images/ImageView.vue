<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask :label="$t('quickTasks.edit.image')" :to="`/images/${image.id}/edit`" icon="mdi-pencil" />
			<AppTask
				:label="$t('quickTasks.delete.image')"
				:confirm="$t('quickTasks.delete.image.confirm')"
				icon="mdi-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteImage"
			/>
		</AppTasks>

		<SectionCard :loading="mainLoading" class="c-ImageView__image">
			<img :src="imageUrl">
			<p>{{ image.description }}</p>
		</SectionCard>

		<SectionCard
			v-if="dependenciesLoading || hasDependencies"
			:loading="dependenciesLoading"
			:title="$t('page.Image.usedIn')"
		>
			<v-list>
				<template v-for="(value, key) in parsedDependencies">
					<v-list-group
						v-if="value.length > 0"
						:key="key"
						:value="true"
						sub-group
					>
						<template v-slot:activator>
							<v-list-item-content>
								<v-list-item-title v-text="$t('page.Image.usedIn.' + key)" />
							</v-list-item-content>
						</template>

						<v-list-item v-for="item in value" :key="item.id" :to="`/${key}/${item.id}/view`">
							<v-list-item-content class="pl-4">
								<v-list-item-title>
									<template v-if="key === 'albums' && item.series">
										{{ item.series.name }} -
									</template>
									{{ item.identifyingName }}
								</v-list-item-title>
							</v-list-item-content>
						</v-list-item>
					</v-list-group>
				</template>
			</v-list>
		</SectionCard>
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/actions'
import { getEncodedImageName } from '@/helpers/images'
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

	metaInfo() {
		return {
			title: this.image.identifyingName
		}
	},

	data() {
		return {
			mainLoading: true,
			dependenciesLoading: true,
			image: {},
			dependencies: {},
			appTasksMenu: false
		}
	},

	computed: {
		imageUrl() {
			// TODO: should match the context root. Also check other occurrences
			return '/images/' + this.image.id + '/file/' + getEncodedImageName(this.image)
		},

		hasDependencies() {
			return this.dependencies.albumCovers ||
				this.dependencies.albumOtherImages ||
				this.dependencies.authors ||
				this.dependencies.collections ||
				this.dependencies.derivatives ||
				this.dependencies.publishers
		},

		parsedDependencies() {
			let covs = this.dependencies.albumCovers || []
			let other = this.dependencies.albumOtherImages || []
			// This won't work if an image is used twice in an Album, although that hardly makes any sense
			return {
				albums: [ ...covs, ...other ],
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
}
</style>
