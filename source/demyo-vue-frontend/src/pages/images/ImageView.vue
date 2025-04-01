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
				@confirm="deleteModel"
			/>
		</AppTasks>

		<SectionCard :loading="mainLoading" class="c-ImageView__image">
			<img
				v-if="imageUrl"
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
								<template v-if="modelType === 'albums' && hasSeries(item)">
									{{ (item as Album).series.identifyingName }} -
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

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import { getBaseImageUrl } from '@/helpers/images'
import imageService from '@/services/image-service'

const mainLoading = ref(true)
const dependenciesLoading = ref(true)
const dependencies = ref<Image | undefined>(undefined)

async function fetchData(id: number): Promise<Image> {
	mainLoading.value = true
	const imageP = imageService.findById(id)
	mainLoading.value = false

	dependenciesLoading.value = true
	dependencies.value = await imageService.getImageDependencies(id)
	dependenciesLoading.value = false

	return imageP
}

const { model: image, loading, appTasksMenu, deleteModel }
	= useSimpleView(fetchData, imageService,
		'quickTasks.delete.image.confirm.done', 'ImageIndex')

const imageUrl = computed(() => getBaseImageUrl(image.value))

const hasDependencies = computed(() =>
	dependencies.value?.albumCovers
		|| dependencies.value?.albumOtherImages
		|| dependencies.value?.authors
		|| dependencies.value?.collections
		|| dependencies.value?.derivatives
		|| dependencies.value?.publishers
)

function hasSeries(item: IModel) {
	return (item as Album).series
}

const parsedDependencies = computed(() => {
	const covs: Album[] = dependencies.value?.albumCovers || []
	const other: Album[] = dependencies.value?.albumOtherImages || []
	// This won't work if an image is used twice in an Album, although that hardly makes any sense
	return {
		albums: [...covs, ...other],
		authors: dependencies.value?.authors || [] as Author[],
		collections: dependencies.value?.collections || [] as Collection[],
		derivatives: dependencies.value?.derivatives || [] as Derivative[],
		publishers: dependencies.value?.publishers || [] as Publisher[]
	}
})
</script>

<style lang="scss">
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
