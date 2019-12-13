<template>
	<v-container>
		<AppTasks v-model="appTasksMenu">
			<AppTask :label="$t('quickTasks.edit.image')" :to="`/images/${image.id}/edit`" icon="mdi-pencil" />
			<AppTask
				:label="$t('quickTasks.delete.image')"
				:confirm="$t('quickTasks.delete.image.confirm')"
				icon="mdi-account-minus"
				@cancel="appTasksMenu = false"
				@confirm="deleteImage"
			/>
			<!-- TODO: icons ! -->
		</AppTasks>

		<SectionCard :loading="mainLoading" class="c-ImageView__image">
			<img :src="imageUrl">
			<p>{{ image.description }}</p>
		</SectionCard>
		<!--
			TODO: dependencies
		-->
	</v-container>
</template>

<script>
import AlbumTextList from '@/components/AlbumTextList'
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/app-tasks'
import { getEncodedImageName } from '@/helpers/images'
import imageService from '@/services/image-service'

export default {
	name: 'ImageView',

	components: {
		AlbumTextList,
		AppTask,
		AppTasks,
		SectionCard
	},

	metaInfo() {
		return {
			title: this.image.identifyingName
		}
	},

	data() {
		return {
			mainLoading: true,
			image: {},
			appTasksMenu: false
		}
	},

	computed: {
		imageUrl() {
			// TODO: should match the context root. Also check other occurrences
			return '/images/' + this.image.id + '/file/' + getEncodedImageName(this.image)
		}
	},

	watch: {
		'$route': 'fetchData'
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.mainLoading = true
			const id = parseInt(this.$route.params.id, 10)

			this.image = await imageService.findById(id)
			this.mainLoading = false
		},

		deleteImage() {
			deleteStub(this,
				() => imageService.deleteModel(this.image.id),
				'quickTasks.delete.image.confirm.done',
				'/images/')
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
