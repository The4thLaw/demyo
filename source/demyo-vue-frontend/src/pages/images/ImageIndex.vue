<template>
	<div>
		<GalleryIndex :items="modelList" :keyboard-navigation="true" @page-change="scrollToTop">
			<template #default="slotProps">
				<router-link :to="`/images/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</GalleryIndex>
		<Fab to="/images/new" icon="mdi-plus" />
	</div>
</template>

<script setup>
import { useSimpleIndex } from '@/composables/model-index'
import imageService from '@/services/image-service'

const { modelList } = useSimpleIndex(imageService)
</script>
